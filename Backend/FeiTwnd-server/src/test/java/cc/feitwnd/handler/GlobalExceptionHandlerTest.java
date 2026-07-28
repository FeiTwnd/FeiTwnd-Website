package cc.feitwnd.handler;

import cc.feitwnd.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 校验数据库约束异常在不同包装路径下都能返回精确的友好信息：
 * 1. Spring 已翻译（DataIntegrityViolationException）
 * 2. SQL 层直接抛出（SQLIntegrityConstraintViolationException）
 * 3. 未被 Spring 翻译、被普通 RuntimeException 包裹后落到兜底 Exception 分支
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private static final String SLUG_DUP_MSG =
            "Duplicate entry 'what-is-slug' for key 'articles.slug'";

    @Test
    void dataIntegrityViolation_returnsSlugMessage() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "could not execute statement",
                new SQLIntegrityConstraintViolationException(SLUG_DUP_MSG));

        Result result = handler.exceptionHandler(ex);

        assertEquals(0, result.getCode());
        assertEquals("Slug 已存在", result.getMsg());
    }

    @Test
    void sqlConstraintViolation_returnsSlugMessage() {
        SQLIntegrityConstraintViolationException ex =
                new SQLIntegrityConstraintViolationException(SLUG_DUP_MSG);

        Result result = handler.exceptionHandler(ex);

        assertEquals("Slug 已存在", result.getMsg());
    }

    @Test
    void untranslatedPersistenceException_fallbackStillDetectsConstraint() {
        // 模拟 MyBatis 未被 Spring 翻译的场景：普通 RuntimeException 包裹 SQL 约束异常，
        // 顶层类型只是 RuntimeException -> 命中兜底 Exception 分支
        RuntimeException persistenceLike = new RuntimeException(
                "\n### Error updating database.  Cause: " + SLUG_DUP_MSG,
                new SQLIntegrityConstraintViolationException(SLUG_DUP_MSG));

        Result result = handler.exceptionHandler((Exception) persistenceLike);

        // 兜底分支应遍历 cause 链识别出约束冲突，返回精确信息而非"未知错误"
        assertEquals("Slug 已存在", result.getMsg());
    }

    @Test
    void foreignKeyConstraint_returnsFriendlyMessage() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "could not execute statement",
                new SQLIntegrityConstraintViolationException(
                        "Cannot delete or update a parent row: a foreign key constraint fails"));

        Result result = handler.exceptionHandler(ex);

        assertEquals("操作失败：存在关联数据，请先处理关联项", result.getMsg());
    }

    @Test
    void configKeyDuplicate_returnsConfigMessage() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "insert failed",
                new SQLIntegrityConstraintViolationException(
                        "Duplicate entry 'site_name' for key 'system_config.config_key'"));

        assertEquals("配置键已存在", handler.exceptionHandler(ex).getMsg());
    }

    @Test
    void genuineUnknownException_returnsDiagnosableMessage() {
        Result result = handler.exceptionHandler(new IllegalStateException("boom"));

        // 非约束异常：兜底返回带异常类型的可诊断信息
        assertEquals(0, result.getCode());
        assertTrue(result.getMsg().contains("IllegalStateException"),
                "兜底信息应包含异常类型便于排查，实际：" + result.getMsg());
    }
}
