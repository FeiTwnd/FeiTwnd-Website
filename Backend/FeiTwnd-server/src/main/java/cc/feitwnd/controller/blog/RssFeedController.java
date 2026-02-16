package cc.feitwnd.controller.blog;

import cc.feitwnd.properties.WebsiteProperties;
import cc.feitwnd.service.ArticleService;
import cc.feitwnd.service.PersonalInfoService;
import cc.feitwnd.vo.BlogArticleVO;
import cc.feitwnd.vo.PersonalInfoVO;
import cc.feitwnd.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ArticleService articleService;

    @Autowired
    private PersonalInfoService personalInfoService;

    @Autowired
    private WebsiteProperties websiteProperties;

    private static final DateTimeFormatter RSS_DATE_FMT =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss '+0800'", java.util.Locale.ENGLISH);

    /**
     * 生成 RSS 2.0 Feed XML
     */
    @GetMapping(value = "/rss", produces = MediaType.APPLICATION_XML_VALUE)
    public String rssFeed() {
        log.info("生成RSS Feed");

        String BLOG_BASE_URL = websiteProperties.getBlog();

        // 获取个人信息作为站点信息
        PersonalInfoVO info = personalInfoService.getPersonalInfo();
        String siteName = info != null && info.getNickname() != null ? info.getNickname() + "的博客" : "FeiTwnd Blog";
        String siteDescription = info != null && info.getDescription() != null ? info.getDescription() : "个人博客";

        // 获取最新20篇已发布文章
        PageResult pageResult = articleService.getPublishedPage(1, 20);
        @SuppressWarnings("unchecked")
        List<BlogArticleVO> articles = (List<BlogArticleVO>) pageResult.getRecords();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        xml.append("  <channel>\n");
        xml.append("    <title>").append(escapeXml(siteName)).append("</title>\n");
        xml.append("    <link>").append(BLOG_BASE_URL).append("</link>\n");
        xml.append("    <description>").append(escapeXml(siteDescription)).append("</description>\n");
        xml.append("    <language>zh-CN</language>\n");
        xml.append("    <lastBuildDate>").append(LocalDateTime.now().format(RSS_DATE_FMT)).append("</lastBuildDate>\n");
        xml.append("    <atom:link href=\"").append(BLOG_BASE_URL).append("/rss\" rel=\"self\" type=\"application/rss+xml\"/>\n");

        if (articles != null) {
            for (BlogArticleVO article : articles) {
                xml.append("    <item>\n");
                xml.append("      <title>").append(escapeXml(article.getTitle())).append("</title>\n");
                xml.append("      <link>").append(BLOG_BASE_URL).append("/article/").append(article.getSlug()).append("</link>\n");
                xml.append("      <guid isPermaLink=\"true\">").append(BLOG_BASE_URL).append("/article/").append(article.getSlug()).append("</guid>\n");
                if (article.getSummary() != null) {
                    xml.append("      <description>").append(escapeXml(article.getSummary())).append("</description>\n");
                }
                if (article.getCategoryName() != null) {
                    xml.append("      <category>").append(escapeXml(article.getCategoryName())).append("</category>\n");
                }
                if (article.getPublishTime() != null) {
                    xml.append("      <pubDate>").append(article.getPublishTime().format(RSS_DATE_FMT)).append("</pubDate>\n");
                }
                xml.append("    </item>\n");
            }
        }

        xml.append("  </channel>\n");
        xml.append("</rss>\n");

        return xml.toString();
    }

    /**
     * XML特殊字符转义
     */
    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
