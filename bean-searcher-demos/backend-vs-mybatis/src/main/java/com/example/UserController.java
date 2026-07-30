package com.example;

import com.example.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户检索接口（MyBatis 实现）。
 *
 * 对比 Bean Searcher 版本：Bean Searcher 只需一行
 *     return beanSearcher.search(User.class, User::getAge);
 * 框架自动完成「参数解析 → 动态 SQL → 分页 → 排序 → 统计」；
 * 而 MyBatis 需要我们手写：参数解析（buildQuery）、三条 SQL（search/count/sumAge）、
 * 响应组装，以及 CSV 导出的流式输出。这正是 Bean Searcher 帮开发者省掉的样板代码。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("/index")
    public SearchResult index(@RequestParam Map<String, String> params) {
        Map<String, Object> query = buildQuery(params);

        List<User> list = userMapper.search(query);
        long total = userMapper.count(query);
        int sumAge = userMapper.sumAge(query);

        return new SearchResult(list, total, List.of(sumAge));
    }

    /**
     * 导出 CSV。模拟 Bean Searcher 导出：分批写入并间隔刷新，
     * 使前端点击导出后立即开始下载，无需等待全部数据查完。
     */
    @GetMapping("/export")
    public void export(@RequestParam Map<String, String> params, HttpServletResponse response) throws Exception {
        Map<String, Object> query = buildQuery(params);
        query.remove("offset"); // 导出查全部，去掉分页

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"users.csv\"");

        int batchSize = 5;
        long batchDelayMs = 500;

        try (OutputStream os = response.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            writer.write('\uFEFF'); // Excel 打开中文不乱码
            writer.write("ID,姓名,年龄,性别,部门,入职时间\r\n");

            List<User> all = userMapper.search(query);
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

    // ===================== 以下为 MyBatis 版需自行处理的"样板代码" =====================

    /**
     * 将前端传来的 Bean Searcher 风格参数，转换为 MyBatis Mapper 可用的查询条件。
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
        // page / size 缺省也写入，保证 Mapper 中 #{size} 不为 null（避免 LIMIT null）
        int page = (Integer) q.getOrDefault("page", 0);
        int size = (Integer) q.getOrDefault("size", 5);
        q.put("page", page);
        q.put("size", size);
        q.put("offset", page * size);
        return q;
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
