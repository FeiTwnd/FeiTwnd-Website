package cc.feitwnd.controller.blog;

import cc.feitwnd.dto.RssSubscriptionDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.RssSubscriptionService;
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
    public Result addSubscription(@RequestBody RssSubscriptionDTO rssSubscriptionDTO) {
        log.info("添加RSS订阅,{}", rssSubscriptionDTO);
        rssSubscriptionService.addSubscription(rssSubscriptionDTO);
        return Result.success();
    }
}
