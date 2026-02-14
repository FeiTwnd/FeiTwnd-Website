package cc.feitwnd.service;

import cc.feitwnd.entity.Visitors;

/**
 * 异步访客服务（地理位置查询、访客记录更新、浏览记录写入）
 */
public interface AsyncVisitorService {

    /**
     * 异步处理访客地理位置更新和浏览记录写入
     * @param visitor 已创建/已查出的访客对象
     * @param ip 客户端IP
     * @param userAgent 用户代理
     * @param pagePath 页面路径
     * @param referer 来源URL
     * @param pageTitle 页面标题
     */
    void processGeoAndRecordViewAsync(Visitors visitor, String ip, String userAgent,
                                      String pagePath, String referer, String pageTitle);
}
