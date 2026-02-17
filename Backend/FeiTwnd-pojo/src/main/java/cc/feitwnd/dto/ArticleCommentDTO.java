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
 * 博客端访客提交文章评论DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentDTO implements Serializable {

    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    private Long rootId;
    private Long parentId;

    @Size(max = 15, message = "父评论昵称不能超过15字")
    private String parentNickname;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容不能超过2000字")
    private String content;

    @NotNull(message = "访客ID不能为空")
    private Long visitorId;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 15, message = "昵称不能超过15字")
    private String nickname;

    @Size(max = 50, message = "邮箱或QQ号不能超过50字")
    private String emailOrQq;

    private Integer isMarkdown;
    private Integer isSecret;
    private Integer isNotice;
}
