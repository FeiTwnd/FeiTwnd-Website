package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员回复留言DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReplyDTO implements Serializable {

    // 父留言ID
    private Long parentId;

    // 根留言ID
    private Long rootId;

    // 父留言昵称
    private String parentNickname;

    // 回复内容
    private String content;

    // 是否使用markdown，0-否，1-是
    private Integer isMarkdown;
}
