package cc.feitwnd.service;

import cc.feitwnd.dto.RssSubscriptionDTO;
import cc.feitwnd.dto.RssSubscriptionPageQueryDTO;
import cc.feitwnd.entity.RssSubscriptions;
import cc.feitwnd.result.PageResult;

import java.util.List;

public interface RssSubscriptionService {
    /**
     * 添加RSS订阅
     * @param rssSubscriptionDTO
     */
    void addSubscription(RssSubscriptionDTO rssSubscriptionDTO);

    /**
     * 分页查询RSS订阅列表
     * @param rssSubscriptionPageQueryDTO
     * @return
     */
    PageResult pageQuery(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO);

    /**
     * 更新RSS订阅
     * @param rssSubscriptions
     */
    void updateSubscription(RssSubscriptions rssSubscriptions);

    /**
     * 删除RSS订阅
     * @param id
     */
    void deleteSubscription(Long id);

    /**
     * 根据ID查询RSS订阅
     * @param id
     * @return
     */
    RssSubscriptions getById(Long id);

    /**
     * 获取所有激活的订阅
     * @return
     */
    List<RssSubscriptions> getAllActiveSubscriptions();

    /**
     * 根据邮箱取消订阅
     * @param email
     */
    void unsubscribeByEmail(String email);
}
