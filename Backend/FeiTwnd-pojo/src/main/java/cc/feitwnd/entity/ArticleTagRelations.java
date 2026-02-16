package cc.feitwnd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文章-标签关联
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagRelations implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 文章ID
    private Long articleId;

    // 标签ID
    private Long tagId;
}
