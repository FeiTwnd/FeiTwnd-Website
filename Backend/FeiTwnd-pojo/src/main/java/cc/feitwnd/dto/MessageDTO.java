package cc.feitwnd.dto;

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
    private String content;

    // 根留言ID,null是一级留言
    private Long rootId;

    // 父留言ID,null是一级留言
    private Long parentId;

    // 父留言昵称
    private String parentNickname;

    // 访客ID
    private Long visitorId;

    // 昵称
    private String nickname;

    // 邮箱或qq
    private String emailOrQq;

    // 是否使用markdown，0-否，1-是
    private Integer isMarkdown;

    // 是否匿名，0-否，1-是
    private Integer isSecret;

    // 有回复是否通知，0-否，1-是
    private Integer isNotice;
}
