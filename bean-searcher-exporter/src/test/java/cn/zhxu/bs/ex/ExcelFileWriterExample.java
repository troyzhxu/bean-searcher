package cn.zhxu.bs.ex;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ExcelFileWriter 使用示例：生成一个本地 Excel 文件 <br>
 * 运行方式：在 IDE 中直接运行 {@link #main}，或先 {@code mvn test-compile} 再带 test classpath 运行；
 * 默认输出到 {@code target/example/员工信息示例.xlsx}，也可传入一个参数指定输出路径。
 *
 * @author Troy.Zhou @ 2026-11-13
 * @since v5.0.0
 */
public class ExcelFileWriterExample {

    /**
     * 导出对象：用 @Export 标注要导出的字段（name 为列名，idx 为列顺序）<br>
     * 用 @ExcelSheet 指定工作表名与表头样式
     */
    @ExcelSheet(name = "员工信息", freezeHeader = true, rowHeight = 50)
    public static class Employee {

        @Export(name = "工号", idx = 1)
        String code;

        @Export(name = "姓名", idx = 2, width = 12)
        String name;

        @Export(name = "部门", idx = 3, width = 14)
        String department;

        @Export(name = "月薪", idx = 4)
        BigDecimal salary;          // 数值单元格，Excel 里可直接求和

        @Export(name = "绩效", idx = 5)
        Double score;               // 数值单元格

        @Export(name = "入职时间", idx = 6, format = "yyyy-MM-dd")
        LocalDate entryDate;        // 配置了 format，按格式写成文本

        @Export(name = "在职", idx = 7)
        Boolean active;             // 写成 TRUE / FALSE

        @Export(name = "备注", idx = 8, width = 40)
        String remark;

        @Export(name = "头像", idx = 9, image = true, width = 10)
        String avatar;              // 网络图片链接，写成 IMAGE 公式，由 Excel 加载

        Employee(String code, String name, String department, BigDecimal salary,
                 Double score, LocalDate entryDate, Boolean active, String remark, String avatar) {
            this.code = code;
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.score = score;
            this.entryDate = entryDate;
            this.active = active;
            this.remark = remark;
            this.avatar = avatar;
        }

    }

    public static void main(String[] args) throws IOException {
        Path file = Path.of(args.length > 0 ? args[0] : "target/example/员工信息示例.xlsx");
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }

        // 1. 解析导出字段（实际项目中由 BeanExporter 内部完成，这里手动解析以脱离数据库演示）
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(Employee.class);
        List<Employee> employees = mockData();

        // 2. 写出文件
        try (OutputStream out = new FileOutputStream(file.toFile())) {
            ExcelFileWriter writer = new ExcelFileWriter(out);
            // 工作表名、表头加粗、冻结首行、行高均由 Employee 上的 @ExcelSheet 注解指定；
            // 各列列宽由字段上的 @Export(width = N) 指定（未指定的列使用 Excel 默认宽度）；
            // 头像列标注了 image = true，写出的是 IMAGE 公式，图片由 Excel 打开时自行加载；
            // 数字是否写成数值单元格由开关控制（默认 true，关闭后数字按文本写出）
            // writer.setNumericCells(false);

            writer.writeStart(Employee.class, fields);
            // 3. 模拟分批次加载数据（每批 10 条），与 DefaultBeanExporter 的分批检索行为一致
            int batchSize = 10;
            for (int i = 0; i < employees.size(); i += batchSize) {
                writer.writeAndFlush(fields, employees.subList(i, Math.min(i + batchSize, employees.size())));
            }
            writer.writeStop(Employee.class, fields);
        }

        System.out.println("已生成 Excel 文件: " + file.toAbsolutePath());
    }

    /**
     * 造一批示例数据（含空值、长文本、特殊字符，方便观察写出效果）
     */
    static List<Employee> mockData() {
        String[] names = {"张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十"};
        String[] depts = {"研发部", "销售部", "市场部", "财务部"};
        List<Employee> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(new Employee(
                    String.format("E%04d", 1001 + i),
                    names[i % names.length] + (i / names.length + 1),
                    depts[i % depts.length],
                    new BigDecimal("12500.50").add(new BigDecimal(i * 100L)),
                    80 + (i * 7) % 20 + 0.5,
                    LocalDate.of(2019, 1, 1).plusMonths(i * 5L),
                    i % 4 != 3,
                    i % 7 == 0 ? null : "备注-" + i + (i % 5 == 0 ? "，含逗号、\"引号\" 与 <特殊字符>" : ""),
                    // 每 6 条留一条空链接，用于观察空值单元格的表现
                    i % 6 == 0 ? null : "https://bs.zhxu.cn/logo.png"
            ));
        }
        return list;
    }

}
