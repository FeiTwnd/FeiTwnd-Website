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

    // 今日浏览量
    private Integer todayViewCount;

    // 今日新增访客数
    private Integer todayNewVisitorCount;

    // 总文章数
    private Integer totalArticleCount;

    // 总评论数
    private Integer totalCommentCount;

    // 总留言数
    private Integer totalMessageCount;

    // 待审核评论数
    private Integer pendingCommentCount;

    // 待审核留言数
    private Integer pendingMessageCount;
}
