package cc.feitwnd.controller.admin;

import cc.feitwnd.result.Result;
import cc.feitwnd.service.ReportService;
import cc.feitwnd.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 管理端统计相关接口
 */
@Slf4j
@RestController("adminReportController")
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 浏览量统计
     */
    @GetMapping("/viewStatistics")
    public Result<ViewReportVO> getViewStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("浏览量统计: {} - {}", begin, end);
        ViewReportVO viewReportVO = reportService.getViewStatistics(begin, end);
        return Result.success(viewReportVO);
    }

    /**
     * 访客统计
     */
    @GetMapping("/visitorStatistics")
    public Result<VisitorReportVO> getVisitorStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("访客统计: {} - {}", begin, end);
        VisitorReportVO visitorReportVO = reportService.getVisitorStatistics(begin, end);
        return Result.success(visitorReportVO);
    }

    /**
     * 访客省份分布统计
     */
    @GetMapping("/provinceDistribution")
    public Result<ProvinceVisitorVO> getProvinceDistribution() {
        log.info("访客省份分布统计");
        ProvinceVisitorVO provinceVisitorVO = reportService.getProvinceDistribution();
        return Result.success(provinceVisitorVO);
    }

    /**
     * 文章访问量排行前十
     */
    @GetMapping("/articleViewTop10")
    public Result<ArticleViewTop10VO> getArticleViewTop10() {
        log.info("文章访问量排行前十");
        ArticleViewTop10VO articleViewTop10VO = reportService.getArticleViewTop10();
        return Result.success(articleViewTop10VO);
    }

    /**
     * 获取总览数据（总访问量、总访客）
     */
    @GetMapping("/overview")
    public Result<AdminOverviewVO> getAdminOverview() {
        log.info("获取管理端总览数据");
        AdminOverviewVO adminOverviewVO = reportService.getAdminOverview();
        return Result.success(adminOverviewVO);
    }
}
