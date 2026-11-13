package cn.zhxu.bs.ex;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link DefaultExportFieldResolver} 单元测试<br>
 * 重点覆盖 {@link Export} 注解属性到 {@link ExportField} 的传递，
 * 普通类与 record 两条解析分支都要各自验证（它们是两段独立的构造代码）。
 *
 * @author Troy.Zhou @ 2026-11-13
 * @since v5.0.0
 */
class DefaultExportFieldResolverTest {

    @Test
    void testResolveOnClass() {
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(User.class);

        assertEquals(2, fields.size());
        // 未配置 width / image 时取默认值
        ExportField plain = fields.get(0);
        assertEquals("姓名", plain.getExName());
        assertEquals(0, plain.getWidth());
        assertFalse(plain.isImage());
        // 配置后如实传递
        ExportField avatar = fields.get(1);
        assertEquals("头像", avatar.getExName());
        assertEquals(20, avatar.getWidth());
        assertTrue(avatar.isImage());
    }

    @Test
    void testResolveOnRecord() {
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(UserRecord.class);

        assertEquals(2, fields.size());
        assertEquals("姓名", fields.get(0).getExName());
        assertEquals(0, fields.get(0).getWidth());
        assertFalse(fields.get(0).isImage());
        assertEquals("头像", fields.get(1).getExName());
        assertEquals(20, fields.get(1).getWidth());
        assertTrue(fields.get(1).isImage());
    }

    /**
     * 普通类：字段上标注
     */
    static class User {

        @Export(name = "姓名", idx = 1)
        String name;

        @Export(name = "头像", idx = 2, width = 20, image = true)
        String avatar;

        User(String name, String avatar) {
            this.name = name;
            this.avatar = avatar;
        }

    }

    /**
     * record 类：组件上标注
     */
    record UserRecord(
            @Export(name = "姓名", idx = 1) String name,
            @Export(name = "头像", idx = 2, width = 20, image = true) String avatar
    ) { }

}
