package cc.feitwnd.service.impl;

import cc.feitwnd.dto.ArticleTitleViewCountDTO;
import cc.feitwnd.dto.DailyViewCountDTO;
import cc.feitwnd.dto.DailyVisitorCountDTO;
import cc.feitwnd.dto.ProvinceCountDTO;
import cc.feitwnd.mapper.*;
import cc.feitwnd.service.ReportService;
import cc.feitwnd.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ViewMapper viewMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;
    @Autowired
    private ArticleCommentMapper articleCommentMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;

    /**
     * 获取博客统计数据
     */
    public BlogReportVO getBlogReport() {
        return BlogReportVO.builder()
                .viewTotalCount(viewMapper.countTotal())
                .viewTodayCount(viewMapper.countToday())
                .visitorTotalCount(visitorMapper.countTotal())
                .categoryTotalCount(articleCategoryMapper.countTotal())
                .articleTotalCount(articleMapper.countPublished())
                .tagTotalCount(articleTagMapper.countTotal())
                .build();
    }

    /**
     * 浏览量统计
     */
    public ViewReportVO getViewStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        List<DailyViewCountDTO> dailyStats = viewMapper.getDailyViewStats(begin, end);
        Map<LocalDate, Integer> dailyViewMap = dailyStats.stream()
                .collect(Collectors.toMap(DailyViewCountDTO::getDate, DailyViewCountDTO::getCount));

        List<Integer> viewCountList = dateList.stream()
                .map(date -> dailyViewMap.getOrDefault(date, 0))
                .collect(Collectors.toList());

        return ViewReportVO.builder()
                .dateList(String.join(",", dateList.stream().map(LocalDate::toString).collect(Collectors.toList())))
                .viewCountList(String.join(",", viewCountList.stream().map(String::valueOf).collect(Collectors.toList())))
                .build();
    }

    /**
     * 访客统计
     */
    public VisitorReportVO getVisitorStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        List<DailyVisitorCountDTO> dailyStats = visitorMapper.getDailyNewVisitorStats(begin, end);
        Map<LocalDate, Integer> dailyNewVisitorMap = dailyStats.stream()
                .collect(Collectors.toMap(DailyVisitorCountDTO::getDate, DailyVisitorCountDTO::getCount));

        List<Integer> newVisitorCountList = dateList.stream()
                .map(date -> dailyNewVisitorMap.getOrDefault(date, 0))
                .collect(Collectors.toList());

        // 计算累计访客数
        List<Integer> totalVisitorCountList = new ArrayList<>();
        for (int i = 0; i < newVisitorCountList.size(); i++) {
            if (i == 0) {
                totalVisitorCountList.add(newVisitorCountList.get(i));
            } else {
                totalVisitorCountList.add(totalVisitorCountList.get(i - 1) + newVisitorCountList.get(i));
            }
        }

        return VisitorReportVO.builder()
                .dateList(String.join(",", dateList.stream().map(LocalDate::toString).collect(Collectors.toList())))
                .newVisitorCountList(String.join(",", newVisitorCountList.stream().map(String::valueOf).collect(Collectors.toList())))
                .totalVisitorCountList(String.join(",", totalVisitorCountList.stream().map(String::valueOf).collect(Collectors.toList())))
                .build();
    }

    /**
     * 访客省份分布统计
     */
    public ProvinceVisitorVO getProvinceDistribution() {
        List<ProvinceCountDTO> provinceStats = visitorMapper.getProvinceDistribution();

        List<String> provinceList = provinceStats.stream()
                .map(ProvinceCountDTO::getProvince)
                .collect(Collectors.toList());
        List<Integer> countList = provinceStats.stream()
                .map(ProvinceCountDTO::getCount)
                .collect(Collectors.toList());

        return ProvinceVisitorVO.builder()
                .provinceList(String.join(",", provinceList))
                .countList(String.join(",", countList.stream().map(String::valueOf).collect(Collectors.toList())))
                .build();
    }

    /**
     * 文章访问量排行前十
     */
    public ArticleViewTop10VO getArticleViewTop10() {
        List<ArticleTitleViewCountDTO> top10List = articleMapper.getViewTop10();

        List<String> titleList = top10List.stream()
                .map(ArticleTitleViewCountDTO::getTitle)
                .collect(Collectors.toList());
        List<Integer> viewCountList = top10List.stream()
                .map(ArticleTitleViewCountDTO::getViewCount)
                .collect(Collectors.toList());

        return ArticleViewTop10VO.builder()
                .titleList(String.join(",", titleList))
                .viewCountList(String.join(",", viewCountList.stream().map(String::valueOf).collect(Collectors.toList())))
                .build();
    }

    /**
     * 获取管理端总览数据
     */
    public AdminOverviewVO getAdminOverview() {
        return AdminOverviewVO.builder()
                .totalViewCount(viewMapper.countTotal())
                .totalVisitorCount(visitorMapper.countTotal())
                .todayViewCount(viewMapper.countToday())
                .todayNewVisitorCount(visitorMapper.countToday())
                .totalArticleCount(articleMapper.countPublished())
                .totalCommentCount(articleCommentMapper.countTotal())
                .totalMessageCount(messageMapper.countTotal())
                .pendingCommentCount(articleCommentMapper.countPending())
                .pendingMessageCount(messageMapper.countPending())
                .build();
    }

    /**
     * 获取指定日期范围内的日期列表
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
