package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RSS订阅状态VO（返回给博客前端）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RssSubscriptionStatusVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 是否已订阅 */
    private boolean subscribed;

    /** 订阅时使用的昵称 */
    private String nickname;

    /** 订阅时使用的邮箱 */
    private String email;
}
