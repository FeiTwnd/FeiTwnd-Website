package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MD 文件导入解析结果 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleImportVO {

    // 文章标题
    private String title;

    // URL 标识
    private String slug;

    // 摘要
    private String summary;

    // 封面图
    private String coverImage;

    // 分类名称
    private String category;

    // 标签名称列表
    private List<String> tags;

    // Markdown 正文
    private String contentMarkdown;
}
