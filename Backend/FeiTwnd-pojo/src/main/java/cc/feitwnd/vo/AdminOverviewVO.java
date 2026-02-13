package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理端总览统计VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminOverviewVO {

    // 总浏览量
    private Integer totalViewCount;

    // 总访客数
    private Integer totalVisitorCount;
}
