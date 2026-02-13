package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 文章归档VO（按年月分组）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleArchiveVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer year;
    private Integer month;
    private List<ArticleArchiveItemVO> articles;
}
