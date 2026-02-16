package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 访客提交留言DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

    // 留言内容
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 2000, message = "留言内容不能超过2000字")
    private String content;

    // 根留言ID,null是一级留言
    private Long rootId;

    // 父留言ID,null是一级留言
    private Long parentId;

    // 父留言昵称
    @Size(max = 50, message = "父留言昵称不能超过50字")
    private String parentNickname;

    // 访客ID
    @NotNull(message = "访客ID不能为空")
    private Long visitorId;

    // 昵称
    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称不能超过30字")
    private String nickname;

    // 邮箱或qq
    @Size(max = 100, message = "邮箱或QQ号格式不正确")
    private String emailOrQq;

    // 是否使用markdown，0-否，1-是
    private Integer isMarkdown;

    // 是否匿名，0-否，1-是
    private Integer isSecret;

    // 有回复是否通知，0-否，1-是
    private Integer isNotice;
}
