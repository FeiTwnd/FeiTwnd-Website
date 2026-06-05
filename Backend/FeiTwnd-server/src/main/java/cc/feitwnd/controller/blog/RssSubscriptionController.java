package cc.feitwnd.controller.blog;

import cc.feitwnd.annotation.RateLimit;
import cc.feitwnd.dto.RssSubscriptionDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.RssSubscriptionService;
import cc.feitwnd.service.VisitorTokenService;
import cc.feitwnd.vo.RssSubscriptionStatusVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 博客端RSS订阅接口
 */
@Slf4j
@RestController("blogRssSubscriptionController")
@RequestMapping("/blog/rssSubscription")
public class RssSubscriptionController {

    @Autowired
    private RssSubscriptionService rssSubscriptionService;
    @Autowired
    private VisitorTokenService visitorTokenService;

    /**
     * 添加RSS订阅（需token验证访客身份）
     * @param rssSubscriptionDTO
     * @return
     */
    @PostMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
            timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result addSubscription(@Valid @RequestBody RssSubscriptionDTO rssSubscriptionDTO,
                                  HttpServletRequest request) {
        // 验证访客身份，覆盖DTO中的visitorId，防止伪造
        Long visitorId = visitorTokenService.resolveVisitorId(request);
        rssSubscriptionDTO.setVisitorId(visitorId);
        log.info("添加RSS订阅,{}", rssSubscriptionDTO);
        rssSubscriptionService.addSubscription(rssSubscriptionDTO);
        return Result.success();
    }

    /**
     * 取消RSS订阅（访客端，需token验证）
     */
    @PutMapping("/unsubscribe")
    public Result unsubscribe(HttpServletRequest request) {
        Long visitorId = visitorTokenService.resolveVisitorId(request);
        log.info("取消RSS订阅: visitorId={}", visitorId);
        rssSubscriptionService.unsubscribeByVisitorId(visitorId);
        return Result.success();
    }

    /**
     * 检查访客订阅状态（需token验证）
     */
    @GetMapping("/check")
    public Result<RssSubscriptionStatusVO> checkSubscription(HttpServletRequest request) {
        Long visitorId = visitorTokenService.resolveVisitorId(request);
        log.info("检查订阅状态: visitorId={}", visitorId);
        RssSubscriptionStatusVO status = rssSubscriptionService.getSubscriptionStatus(visitorId);
        return Result.success(status);
    }
}
