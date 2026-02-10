package cc.feitwnd.utils;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * Markdown工具类
 */
public class MarkdownUtil {

    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    /**
     * Markdown转HTML（带XSS防护）
     * @param markdown Markdown文本
     * @return 安全的HTML文本
     */
    public static String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        
        // 1. 解析Markdown
        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        
        // 2. 安全过滤（防止XSS）
        return sanitizeHtml(html);
    }

    /**
     * 安全的HTML过滤
     */
    private static String sanitizeHtml(String html) {
        Safelist safelist = Safelist.relaxed()
                .addTags("code", "pre", "hr")  // 允许的标签
                .addAttributes("code", "class")  // 允许code标签的class属性（用于语法高亮）
                .addProtocols("a", "href", "http", "https", "mailto")  // 只允许安全协议
                .addAttributes("a", "target", "rel")  // 允许target和rel属性
                .addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer");  // 自动加安全属性
        
        // 清理HTML
        return Jsoup.clean(html, safelist);
    }
}
