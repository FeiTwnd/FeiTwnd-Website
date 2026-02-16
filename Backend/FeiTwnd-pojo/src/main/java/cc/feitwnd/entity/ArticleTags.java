package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章标签
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTags implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 标签名称
    private String name;

    // URL标识
    private String slug;

    // 标签颜色
    private String color;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
