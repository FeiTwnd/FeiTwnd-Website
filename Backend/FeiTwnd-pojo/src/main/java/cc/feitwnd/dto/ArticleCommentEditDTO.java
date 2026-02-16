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
 * 访客编辑评论DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentEditDTO implements Serializable {

    // 评论ID
    @NotNull(message = "评论ID不能为空")
    private Long id;

    // 访客ID（用于验证身份）
    @NotNull(message = "访客ID不能为空")
    private Long visitorId;

    // 编辑后的内容
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容不能超过2000字")
    private String content;

    // 是否使用markdown
    private Integer isMarkdown;
}
