package cc.feitwnd.controller.blog;

import cc.feitwnd.annotation.RateLimit;
import cc.feitwnd.dto.RssSubscriptionDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.RssSubscriptionService;
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

    /**
     * 添加RSS订阅
     * @param rssSubscriptionDTO
     * @return
     */
    @PostMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
            timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result addSubscription(@Valid @RequestBody RssSubscriptionDTO rssSubscriptionDTO) {
        log.info("添加RSS订阅,{}", rssSubscriptionDTO);
        rssSubscriptionService.addSubscription(rssSubscriptionDTO);
        return Result.success();
    }

    /**
     * 取消RSS订阅（访客端）
     * @param email
     * @return
     */
    @PutMapping("/unsubscribe")
    public Result unsubscribe(@RequestParam String email) {
        log.info("取消RSS订阅,{}", email);
        rssSubscriptionService.unsubscribeByEmail(email);
        return Result.success();
    }
}
