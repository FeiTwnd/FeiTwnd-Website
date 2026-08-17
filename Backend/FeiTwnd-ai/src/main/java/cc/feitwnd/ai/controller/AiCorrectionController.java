package cc.feitwnd.ai.controller;

import cc.feitwnd.extension.ai.AiTypoCorrector;
import cc.feitwnd.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 错别字/病句纠错接口
 *
 * 管理端文章编辑页"AI 纠错"按钮调用：传入当前文章 Markdown 内容，
 * 返回 AI 纠错后的完整 Markdown（由前端逐行 diff 展示并让用户确认）。
 * 未打包 AI 模块时该接口不存在（404），前端通过 /admin/ai/status 探测后隐藏按钮。
 */
@Slf4j
@RestController
@RequestMapping("/admin/ai")
@RequiredArgsConstructor
public class AiCorrectionController {

    private final ObjectProvider<AiTypoCorrector> typoCorrectorProvider;

    /**
     * 对文章内容进行错别字/病句纠错
     * @param body 请求体：{content: 文章 Markdown 内容}
     * @return correctedContent-纠错后的完整 Markdown；AI 未启用或纠错失败时返回错误信息
     */
    @PostMapping("/correct")
    public Result<Map<String, String>> correct(@RequestBody Map<String, String> body) {
        log.info("AI纠错请求");
        String content = body.get("content");
        if (content == null || content.isBlank()) {
            return Result.error("文章内容不能为空");
        }

        AiTypoCorrector corrector = typoCorrectorProvider.getIfAvailable();
        if (corrector == null) {
            return Result.error("AI 纠错功能未启用");
        }

        String corrected = corrector.correctTypo(content);
        if (corrected == null) {
            return Result.error("AI 纠错失败，请稍后重试");
        }

        Map<String, String> data = new HashMap<>();
        data.put("correctedContent", corrected);
        return Result.success(data);
    }
}
