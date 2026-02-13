package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章访问量排行前十VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleViewTop10VO {

    // 文章标题，以逗号分隔
    private String titleList;

    // 对应文章的浏览量，以逗号分隔
    private String viewCountList;
}
