package cc.feitwnd.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章列表VO（不含正文内容）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 文章标题
    private String title;

    // URL标识
    private String slug;

    // 文章摘要
    private String summary;

    // 封面图片url
    private String coverImage;

    // 分类ID
    private Long categoryId;

    // 分类名称
    private String categoryName;

    // 浏览次数
    private Long viewCount;

    // 点赞次数
    private Long likeCount;

    // 评论数
    private Long commentCount;

    // 字数统计
    private Long wordCount;

    // 预计阅读时间（分钟）
    private Long readingTime;

    // 是否发布
    private Integer isPublished;

    // 是否置顶
    private Integer isTop;

    // 发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
