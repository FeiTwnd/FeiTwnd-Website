package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员回复文章评论DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentReplyDTO implements Serializable {

    // 文章ID
    private Long articleId;

    // 父评论ID
    private Long parentId;

    // 根评论ID
    private Long rootId;

    // 父评论昵称
    private String parentNickname;

    // 回复内容
    private String content;

    // 是否使用markdown，0-否，1-是
    private Integer isMarkdown;
}
