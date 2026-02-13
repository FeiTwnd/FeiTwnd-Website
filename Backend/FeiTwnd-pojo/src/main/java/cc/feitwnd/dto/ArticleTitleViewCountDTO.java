package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章标题与浏览量DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleTitleViewCountDTO {

    // 文章标题
    private String title;

    // 浏览量
    private Integer viewCount;
}
