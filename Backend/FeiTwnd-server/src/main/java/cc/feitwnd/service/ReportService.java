package cc.feitwnd.service;

import cc.feitwnd.vo.*;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 获取博客统计数据
     */
    BlogReportVO getBlogReport();

    /**
     * 浏览量统计
     */
    ViewReportVO getViewStatistics(LocalDate begin, LocalDate end);

    /**
     * 访客统计
     */
    VisitorReportVO getVisitorStatistics(LocalDate begin, LocalDate end);

    /**
     * 访客省份分布统计
     */
    ProvinceVisitorVO getProvinceDistribution();

    /**
     * 文章访问量排行前十
     */
    ArticleViewTop10VO getArticleViewTop10();

    /**
     * 获取管理端总览数据
     */
    AdminOverviewVO getAdminOverview();
}
