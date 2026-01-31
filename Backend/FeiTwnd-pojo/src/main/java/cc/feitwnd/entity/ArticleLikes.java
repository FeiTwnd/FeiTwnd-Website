package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章点赞
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLikes implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 文章ID
    private Long articleId;

    // 访客ID
    private Long visitorId;

    // 点赞时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime likeTime;
}
