package cc.feitwnd.controller.blog;

import cc.feitwnd.properties.WebsiteProperties;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ArticleService;
import cc.feitwnd.service.PersonalInfoService;
import cc.feitwnd.service.RssFeedService;
import cc.feitwnd.vo.BlogArticleVO;
import cc.feitwnd.vo.PersonalInfoVO;
import cc.feitwnd.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 博客端 RSS Feed 接口
 */
@Slf4j
@RestController("blogRssFeedController")
@RequestMapping("/blog")
public class RssFeedController {

    @Autowired
    private RssFeedService rssFeedService;

    /**
     * 生成 RSS 2.0 Feed XML
     */
    @GetMapping(value = "/rss", produces = MediaType.APPLICATION_XML_VALUE)
    @Cacheable(value = "rssFeed", key = "'xml'")
    public String rssFeed() {
        String xml = rssFeedService.generateRssFeed();
        return xml;
    }
}
