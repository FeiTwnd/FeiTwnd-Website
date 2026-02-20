package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章分类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCategories implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 分类名称
    private String name;

    // URL标识
    private String slug;

    // 分类描述
    private String description;

    // 排序，越小越靠前
    private Integer sort;

    // 文章数量（非数据库字段，查询时计算）
    private Integer articleCount;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
