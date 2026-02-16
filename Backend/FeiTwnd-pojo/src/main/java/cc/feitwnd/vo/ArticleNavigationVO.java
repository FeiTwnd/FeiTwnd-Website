package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文章上下篇导航VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleNavigationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 上一篇
    private BlogArticleVO prevArticle;

    // 下一篇
    private BlogArticleVO nextArticle;
}
