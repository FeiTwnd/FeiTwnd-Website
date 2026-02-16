package cc.feitwnd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RSS订阅DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RssSubscriptionDTO {

    // 访客ID
    @NotNull(message = "访客ID不能为空")
    private Long visitorId;

    // 昵称
    @Size(max = 30, message = "昵称不能超过30字")
    private String nickname;

    // 邮箱
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
