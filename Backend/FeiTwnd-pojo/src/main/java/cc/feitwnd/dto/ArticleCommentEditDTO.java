package cc.feitwnd.dto;

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
    private Long id;

    // 访客ID（用于验证身份）
    private Long visitorId;

    // 编辑后的内容
    private String content;

    // 是否使用markdown
    private Integer isMarkdown;
}
