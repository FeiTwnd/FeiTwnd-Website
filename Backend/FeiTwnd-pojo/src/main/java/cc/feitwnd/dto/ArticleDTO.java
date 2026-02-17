package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 50, message = "文章标题不能超过50字")
    private String title;

    // URL标识
    @NotBlank(message = "URL标识不能为空")
    @Size(max = 50, message = "URL标识不能超过50字")
    private String slug;

    // 文章摘要
    @Size(max = 255, message = "文章摘要不能超过255字")
    private String summary;

    // 封面图片url
    private String coverImage;

    // Markdown内容
    @NotBlank(message = "文章内容不能为空")
    private String contentMarkdown;

    // 分类ID
    @NotNull(message = "文章分类不能为空")
    private Long categoryId;

    // 是否发布,0-否（草稿），1-是
    private Integer isPublished;

    // 标签ID列表
    private List<Long> tagIds;
}
