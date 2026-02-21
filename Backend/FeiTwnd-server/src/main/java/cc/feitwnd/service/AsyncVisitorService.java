package cc.feitwnd.service;

/**
 * 异步访客服务（地理位置查询、访客记录更新、浏览记录写入）
 */
public interface AsyncVisitorService {

    /**
     * 异步处理访客地理位置更新和浏览记录写入
     * @param visitorId 访客ID（避免传递共享对象引用导致竞态条件）
     * @param ip 客户端IP
     * @param userAgent 用户代理
     * @param pagePath 页面路径
     * @param referer 来源URL
     * @param pageTitle 页面标题
     */
    void processGeoAndRecordViewAsync(Long visitorId, String ip, String userAgent,
                                      String pagePath, String referer, String pageTitle);
}
