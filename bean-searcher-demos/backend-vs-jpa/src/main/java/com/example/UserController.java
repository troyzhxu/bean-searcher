package com.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户检索接口（JPA 实现）。
 *
 * 对比 Bean Searcher 版本：Bean Searcher 只需一行
 *     return beanSearcher.search(User.class, User::getAge);
 * 框架自动完成「参数解析 → 动态条件 → 分页 → 排序 → 统计」；
 * 而 JPA（Spring Data）需要我们手写：参数解析（buildQuery）、动态条件（Specification / toPredicates）、
 * 三条查询（findAll / count / 年龄求和的 Criteria 查询）、响应组装，以及 CSV 导出的流式输出。
 * 这正是 Bean Searcher 帮开发者省掉的样板代码。
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // sumAge 需要 SUM(age) 聚合，而 JpaSpecificationExecutor 只有 find/count/exists，
    // 没有 sum/avg/max/min，所以必须用 EntityManager + Criteria API（Spring Data 内部也是这么实现 count 的）
    @PersistenceContext
    private final EntityManager em;

    @GetMapping("/index")
    public SearchResult index(@RequestParam Map<String, String> params) {
        Map<String, Object> query = buildQuery(params);

        Specification<User> spec = (root, q, cb) ->
                cb.and(toPredicates(cb, root, query).toArray(new Predicate[0]));

        int page = (Integer) query.getOrDefault("page", 0);
        int size = (Integer) query.getOrDefault("size", 5);

        List<User> list = userRepository.findAll(spec, PageRequest.of(page, size, buildSort(query))).getContent();
        long total = userRepository.count(spec);
        int sumAge = sumAge(query);

        return new SearchResult(list, total, List.of(sumAge));
    }

    /**
     * 导出 CSV。模拟 Bean Searcher 导出：分批写入并间隔刷新，
     * 使前端点击导出后立即开始下载，无需等待全部数据查完。
     */
    @GetMapping("/export")
    public void export(@RequestParam Map<String, String> params, HttpServletResponse response) throws Exception {
        Map<String, Object> query = buildQuery(params);

        Specification<User> spec = (root, q, cb) ->
                cb.and(toPredicates(cb, root, query).toArray(new Predicate[0]));

        List<User> all = userRepository.findAll(spec); // 导出查全部，不分页

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"users.csv\"");

        int batchSize = 5;
        long batchDelayMs = 500;

        try (OutputStream os = response.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            writer.write('\uFEFF'); // Excel 打开中文不乱码
            writer.write("ID,姓名,年龄,性别,部门,入职时间\r\n");

            int count = 0;
            for (User u : all) {
                writer.write(csvRow(u));
                writer.write("\r\n");
                if (++count % batchSize == 0) {
                    writer.flush();
                    Thread.sleep(batchDelayMs); // 模拟大数据量，边查边下
                }
            }
            writer.flush();
        }
    }

    // ===================== 以下为 JPA 版需自行处理的"样板代码" =====================

    /**
     * 将前端传来的 Bean Searcher 风格参数，转换为查询条件 Map。
     * 这一步在 Bean Searcher 中由框架（ReqParamFilter + 内建参数解析）自动完成。
     *
     * 参数命名统一为 {field}-{suffix}，遍历原始请求参数后按中划线拆分：
     * - {field}-op   : 操作符，缺省时使用各字段默认操作符
     * - {field}-ic   : 忽略大小写（布尔）
     * - {field}-0/-1 : 区间起止值（数值字段转 Integer，其余保持字符串）
     * 无中划线的普通参数（name / department / sort / order / page / size）原样透传。
     */
    private Map<String, Object> buildQuery(Map<String, String> p) {
        Map<String, Object> q = new HashMap<>();

        // 各字段默认操作符（与 Bean Searcher 语义一致）
        Map<String, String> opDefaults = Map.of(
                "name", "in",
                "age", "eq",
                "department", "in",
                "entryDate", "bt"
        );
        // 数值型区间字段：对应的 -0 / -1 需转换为 Integer（其余如 entryDate 保持字符串）
        Set<String> numericFields = Set.of("age");

        for (Map.Entry<String, String> e : p.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            int hyphen = key.indexOf('-');
            if (hyphen < 0) {
                // 无中划线的普通参数
                if ("page".equals(key) || "size".equals(key)) {
                    q.put(key, parseInt(value, "page".equals(key) ? 0 : 5));
                } else if (value != null && !value.isEmpty()) {
                    q.put(key, value);
                }
                continue;
            }
            // 含中划线的参数：name-op / age-0 / department-ic / entryDate-1 ...
            String prefix = key.substring(0, hyphen);
            String suffix = key.substring(hyphen + 1);
            String camel = toCamel(key);
            switch (suffix) {
                case "op" -> q.put(camel, defaultIfBlank(value, opDefaults.get(prefix)));
                case "ic" -> q.put(camel, Boolean.parseBoolean(value));
                case "0", "1" -> {
                    if (numericFields.contains(prefix)) {
                        putIntIfPresent(q, camel, value);
                    } else {
                        putIfPresent(q, camel, value);
                    }
                }
                default -> q.put(camel, value);
            }
        }

        // 分页（Bean Searcher 的 page 从 0 开始，size 为每页条数）
        // page / size 缺省也写入，保证查询中分页不为 null
        int page = (Integer) q.getOrDefault("page", 0);
        int size = (Integer) q.getOrDefault("size", 5);
        q.put("page", page);
        q.put("size", size);
        q.put("offset", page * size);
        return q;
    }

    /**
     * 把解析后的查询条件 Map 翻译为 JPA Criteria 断言（Predicate）。
     * 复刻 Bean Searcher 的参数语义：name / age / department / entryDate 的 eq/in/sw/ew/bt/gt/lt/ge/le。
     */
    private List<Predicate> toPredicates(CriteriaBuilder cb, Root<User> root, Map<String, Object> q) {
        List<Predicate> preds = new ArrayList<>();

        // 姓名：name-op = eq / in(默认,包含) / sw(开头) / ew(结尾)；name-ic 忽略大小写
        String name = (String) q.get("name");
        if (name != null && !name.isEmpty()) {
            String op = (String) q.getOrDefault("nameOp", "in");
            boolean ic = Boolean.TRUE.equals(q.get("nameIc"));
            Expression<String> col = ic ? cb.lower(root.get("name").as(String.class)) : root.get("name").as(String.class);
            String v = ic ? name.toLowerCase() : name;
            preds.add(likePredicate(cb, col, v, op));
        }

        // 年龄：age-op = eq / gt / lt / ge / le / bt(区间)
        if (q.get("age0") != null) {
            Integer age0 = (Integer) q.get("age0");
            String op = (String) q.getOrDefault("ageOp", "eq");
            Expression<Integer> col = root.get("age").as(Integer.class);
            preds.add(switch (op) {
                case "gt" -> cb.greaterThan(col, age0);
                case "lt" -> cb.lessThan(col, age0);
                case "ge" -> cb.greaterThanOrEqualTo(col, age0);
                case "le" -> cb.lessThanOrEqualTo(col, age0);
                case "bt" -> {
                    Integer age1 = (Integer) q.get("age1");
                    yield age1 != null ? cb.between(col, age0, age1) : cb.greaterThanOrEqualTo(col, age0);
                }
                default -> cb.equal(col, age0);
            });
        }

        // 部门：department-op 同姓名；department-ic 忽略大小写。
        // department 来自 User 的 @ManyToOne Department dept，通过 root.get("dept").get("name") 走关联路径
        String department = (String) q.get("department");
        if (department != null && !department.isEmpty()) {
            String op = (String) q.getOrDefault("departmentOp", "in");
            boolean ic = Boolean.TRUE.equals(q.get("departmentIc"));
            Expression<String> col = ic ? cb.lower(root.get("dept").get("name").as(String.class)) : root.get("dept").get("name").as(String.class);
            String v = ic ? department.toLowerCase() : department;
            preds.add(likePredicate(cb, col, v, op));
        }

        // 入职日期：entryDate-op = bt / gt / lt / ge / le
        String entryDate0 = (String) q.get("entryDate0");
        if (entryDate0 != null && !entryDate0.isEmpty()) {
            LocalDateTime from = parseDateTime(entryDate0);
            if (from != null) { // 解析失败则忽略该条件，避免空指针
                String op = (String) q.getOrDefault("entryDateOp", "bt");
                Expression<LocalDateTime> col = root.get("entryDate").as(LocalDateTime.class);
                preds.add(switch (op) {
                    case "gt" -> cb.greaterThan(col, from);
                    case "lt" -> cb.lessThan(col, from);
                    case "ge" -> cb.greaterThanOrEqualTo(col, from);
                    case "le" -> cb.lessThanOrEqualTo(col, from);
                    case "bt" -> {
                        String entryDate1 = (String) q.get("entryDate1");
                        yield (entryDate1 != null && !entryDate1.isEmpty())
                                ? cb.between(col, from, parseDateTime(entryDate1))
                                : cb.greaterThanOrEqualTo(col, from);
                    }
                    default -> cb.greaterThanOrEqualTo(col, from);
                });
            }
        }

        return preds;
    }

    private Predicate likePredicate(CriteriaBuilder cb, Expression<String> col, String v, String op) {
        return switch (op) {
            case "eq" -> cb.equal(col, v);
            case "sw" -> cb.like(col, v + "%");
            case "ew" -> cb.like(col, "%" + v);
            default -> cb.like(col, "%" + v + "%"); // in（包含）
        };
    }

    /**
     * 年龄求和（对应 Bean Searcher search(User.class, User::getAge) 的 summaries）。
     * 用 EntityManager 的 Criteria 查询，复用 toPredicates 的同一批条件。
     */
    private int sumAge(Map<String, Object> q) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Number> cq = cb.createQuery(Number.class);
        Root<User> root = cq.from(User.class);
        // root.get("age").as(Long.class) 明确为 Expression<Long>，使 cb.sum 选中返回 Expression<Long> 的重载
        // （SQL SUM 实际返回 BIGINT，对应 Long），避免运行期 Integer/Long 类型转换异常
        Expression<Long> sumExpr = cb.sum(root.get("age").as(Long.class));
        cq.select(cb.coalesce(sumExpr, 0L));
        cq.where(toPredicates(cb, root, q).toArray(new Predicate[0]));
        Number sum = em.createQuery(cq).getSingleResult();
        return sum == null ? 0 : sum.intValue();
    }

    /**
     * 由 sort / order 参数构造 Spring Data 的 Sort。
     * department 需映射为关联路径 dept.name（Spring Data 支持实体关联路径的 dotted 写法）。
     */
    private Sort buildSort(Map<String, Object> q) {
        String sort = (String) q.get("sort");
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }
        String prop = "department".equals(sort) ? "dept.name" : sort;
        Sort.Direction dir = "desc".equalsIgnoreCase((String) q.getOrDefault("order", ""))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, prop);
    }

    /**
     * 中划线命名转驼峰命名：name-op -> nameOp，entryDate-0 -> entryDate0。
     */
    private String toCamel(String key) {
        String[] parts = key.split("-");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
            }
        }
        return sb.toString();
    }

    private void putIfPresent(Map<String, Object> q, String key, String value) {
        if (value != null && !value.isEmpty()) {
            q.put(key, value);
        }
    }

    private void putIntIfPresent(Map<String, Object> q, String key, String value) {
        if (value != null && !value.isEmpty()) {
            q.put(key, Integer.valueOf(value));
        }
    }

    private String defaultIfBlank(String value, String fallback) {
        return (value == null || value.isEmpty()) ? fallback : value;
    }

    private int parseInt(String value, int fallback) {
        if (value == null || value.isEmpty()) return fallback;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * 解析前端传入的日期字符串（支持 "yyyy-MM-dd HH:mm" 与 "yyyy-MM-dd"）。
     */
    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            if (s.contains(" ")) {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            return LocalDateTime.parse(s + "T00:00:00");
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String csvRow(User u) {
        return String.join(",",
                csvCell(u.getId()),
                csvCell(u.getName()),
                csvCell(u.getAge()),
                csvCell(u.getGenderName()),
                csvCell(u.getDepartment()),
                csvCell(formatEntry(u.getEntryDate()))
        );
    }

    private String csvCell(Object value) {
        if (value == null) return "";
        String s = value.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private String formatEntry(LocalDateTime t) {
        return t == null ? "" : t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
