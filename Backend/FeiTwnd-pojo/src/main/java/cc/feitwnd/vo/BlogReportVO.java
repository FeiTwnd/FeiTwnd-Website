package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogReportVO {

    // 总浏览量
    private Integer viewTotalCount;

    // 今日浏览量
    private Integer viewTodayCount;

    // 总访客数
    private Integer visitorTotalCount;

    // 总文章分类数
    private Integer categoryTotalCount;

    // 总文章标签数
    private Integer tagTotalCount;

    // 总文章数
    private Integer articleTotalCount;
}
