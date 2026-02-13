package cc.feitwnd.service.impl;

import cc.feitwnd.mapper.ArticleCategoryMapper;
import cc.feitwnd.mapper.ArticleMapper;
import cc.feitwnd.mapper.ViewMapper;
import cc.feitwnd.mapper.VisitorMapper;
import cc.feitwnd.service.ReportService;
import cc.feitwnd.vo.BlogReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                .build();
    }
}
