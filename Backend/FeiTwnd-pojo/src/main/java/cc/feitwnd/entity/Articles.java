package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Articles implements Serializable {

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

    // Markdown内容
    private String contentMarkdown;

    // 转换后的HTML内容
    private String contentHtml;

    // 分类ID
    private Long categoryId;

    // 浏览次数
    private Long viewCount;

    // 点赞次数
    private Long likeCount;

    // 评论数
    private Long commentCount;

    // 字数统计
    private Long wordCount;

    // 预计阅读时间，单位：分钟
    private Long readingTime;

    // 是否发布,0-否，1-是
    private Integer isPublished;

    // 是否置顶,0-否，1-是
    private Integer isTop;

    // 发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    // 发布年份
    private Integer publishYear;

    // 发布月份
    private Integer publishMonth;

    // 发布日期
    private Integer publishDay;

    // 发布日期（去掉时间）
    private LocalDate publishDate;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 标签ID列表（非数据库字段，管理端返回时填充）
    private List<Long> tagIds;
}
