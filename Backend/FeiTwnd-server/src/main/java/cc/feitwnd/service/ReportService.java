package cc.feitwnd.service;

import cc.feitwnd.vo.BlogReportVO;

public interface ReportService {

    /**
     * 获取博客统计数据
     */
    BlogReportVO getBlogReport();
}
