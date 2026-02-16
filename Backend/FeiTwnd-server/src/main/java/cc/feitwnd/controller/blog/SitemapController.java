package cc.feitwnd.controller.blog;

import cc.feitwnd.properties.WebsiteProperties;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.ArticleService;
import cc.feitwnd.vo.BlogArticleVO;
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
 * 博客端 Sitemap 接口
 */
@Slf4j
@RestController("blogSitemapController")
@RequestMapping("/blog")
public class SitemapController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private WebsiteProperties websiteProperties;

    private static final DateTimeFormatter W3C_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 动态生成站点地图 XML
     */
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        log.info("生成Sitemap");

        String BLOG_BASE_URL = websiteProperties.getBlog();

        // 获取所有已发布文章（取足够多，最多500条）
        PageResult pageResult = articleService.getPublishedPage(1, 500);
        @SuppressWarnings("unchecked")
        List<BlogArticleVO> articles = (List<BlogArticleVO>) pageResult.getRecords();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // 首页
        xml.append("  <url>\n");
        xml.append("    <loc>").append(BLOG_BASE_URL).append("</loc>\n");
        xml.append("    <changefreq>daily</changefreq>\n");
        xml.append("    <priority>1.0</priority>\n");
        xml.append("  </url>\n");

        // 文章页面
        if (articles != null) {
            for (BlogArticleVO article : articles) {
                xml.append("  <url>\n");
                xml.append("    <loc>").append(BLOG_BASE_URL).append("/article/").append(article.getSlug()).append("</loc>\n");
                if (article.getPublishTime() != null) {
                    xml.append("    <lastmod>").append(article.getPublishTime().format(W3C_DATE_FMT)).append("</lastmod>\n");
                }
                xml.append("    <changefreq>weekly</changefreq>\n");
                xml.append("    <priority>0.8</priority>\n");
                xml.append("  </url>\n");
            }
        }

        // 归档页
        xml.append("  <url>\n");
        xml.append("    <loc>").append(BLOG_BASE_URL).append("/archive</loc>\n");
        xml.append("    <changefreq>weekly</changefreq>\n");
        xml.append("    <priority>0.6</priority>\n");
        xml.append("  </url>\n");

        // 友链页
        xml.append("  <url>\n");
        xml.append("    <loc>").append(BLOG_BASE_URL).append("/friends</loc>\n");
        xml.append("    <changefreq>monthly</changefreq>\n");
        xml.append("    <priority>0.5</priority>\n");
        xml.append("  </url>\n");

        // 留言板
        xml.append("  <url>\n");
        xml.append("    <loc>").append(BLOG_BASE_URL).append("/message</loc>\n");
        xml.append("    <changefreq>daily</changefreq>\n");
        xml.append("    <priority>0.5</priority>\n");
        xml.append("  </url>\n");

        xml.append("</urlset>\n");

        return xml.toString();
    }
}
