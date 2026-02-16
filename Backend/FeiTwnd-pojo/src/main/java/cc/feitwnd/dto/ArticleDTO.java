package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章创建/更新DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleDTO {

    // 文章ID（更新时使用）
    private Long id;

    // 文章标题
    private String title;

    // URL标识
    private String slug;

    // 文章摘要
    private String summary;

    // 封面图片url
    private String coverImage;

    // Markdown内容
    private String contentMarkdown;

    // 分类ID
    private Long categoryId;

    // 是否发布,0-否（草稿），1-是
    private Integer isPublished;

    // 标签ID列表
    private List<Long> tagIds;
}
