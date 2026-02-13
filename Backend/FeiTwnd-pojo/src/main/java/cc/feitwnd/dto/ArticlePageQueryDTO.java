package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章分页查询DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticlePageQueryDTO {

    // 页码
    private int page;

    // 每页显示数量
    private int pageSize;

    // 文章标题（模糊搜索）
    private String title;

    // 分类ID
    private Long categoryId;

    // 是否发布,0-草稿，1-已发布
    private Integer isPublished;
}
