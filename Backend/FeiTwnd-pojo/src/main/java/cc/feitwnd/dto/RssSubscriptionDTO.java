package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RssSubscriptionDTO {

    // 访客ID
    private Long visitorId;

    // 昵称
    private String nickname;

    // 邮箱
    private String email;
}
