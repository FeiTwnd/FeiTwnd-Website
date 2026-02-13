package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 博客端访客提交文章评论DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentDTO implements Serializable {
    private Long articleId;
    private Long rootId;
    private Long parentId;
    private String parentNickname;
    private String content;
    private Long visitorId;
    private String nickname;
    private String emailOrQq;
    private Integer isMarkdown;
    private Integer isSecret;
    private Integer isNotice;
}
