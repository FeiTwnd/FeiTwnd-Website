package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Rss订阅记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RssSubscriptions implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 访客ID
    private Long visitorId;

    // 昵称
    private String nickname;

    // 邮箱
    private String email;

    // 是否激活，0-否，1-是
    private Integer isActive;

    // 订阅时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime subscribeTime;

    // 取消订阅时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime unSubscribeTime;
}
