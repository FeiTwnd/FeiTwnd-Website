package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.dto.RssSubscriptionDTO;
import cc.feitwnd.dto.RssSubscriptionPageQueryDTO;
import cc.feitwnd.entity.RssSubscriptions;
import cc.feitwnd.exception.BaseException;
import cc.feitwnd.exception.RssSubscriptionException;
import cc.feitwnd.mapper.RssSubscriptionMapper;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.RssSubscriptionService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RssSubscriptionServiceImpl implements RssSubscriptionService {

    @Autowired
    private RssSubscriptionMapper rssSubscriptionMapper;

    /**
     * 添加RSS订阅
     * @param rssSubscriptionDTO
     */
    public void addSubscription(RssSubscriptionDTO rssSubscriptionDTO) {
        // 检查邮箱是否已存在
        RssSubscriptions existingSubscription = rssSubscriptionMapper.getByEmail(rssSubscriptionDTO.getEmail());
        if (existingSubscription != null) {
            // 如果已存在且激活，抛出异常
            if (existingSubscription.getIsActive() == 1) {
                throw new RssSubscriptionException(MessageConstant.RssAlreadyExists);
            }
            // 如果已存在但未激活，重新激活
            existingSubscription.setIsActive(1);
            existingSubscription.setNickname(rssSubscriptionDTO.getNickname());
            existingSubscription.setUnSubscribeTime(null);
            rssSubscriptionMapper.update(existingSubscription);
        } else {
            // 新增订阅
            RssSubscriptions rssSubscriptions = RssSubscriptions.builder()
                    .visitorId(rssSubscriptionDTO.getVisitorId())
                    .nickname(rssSubscriptionDTO.getNickname())
                    .email(rssSubscriptionDTO.getEmail())
                    .isActive(1)
                    .subscribeTime(LocalDateTime.now())
                    .build();
            rssSubscriptionMapper.insert(rssSubscriptions);
        }
    }

    /**
     * 分页查询RSS订阅列表
     * @param rssSubscriptionPageQueryDTO
     * @return
     */
    public PageResult pageQuery(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO) {
        PageHelper.startPage(rssSubscriptionPageQueryDTO.getPage(), rssSubscriptionPageQueryDTO.getPageSize());
        Page<RssSubscriptions> page = rssSubscriptionMapper.pageQuery(rssSubscriptionPageQueryDTO);
        long total = page.getTotal();
        List<RssSubscriptions> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 更新RSS订阅
     * @param rssSubscriptions
     */
    public void updateSubscription(RssSubscriptions rssSubscriptions) {
        rssSubscriptionMapper.update(rssSubscriptions);
    }

    /**
     * 批量删除RSS订阅
     * @param ids
     */
    public void batchDelete(List<Long> ids) {
        rssSubscriptionMapper.batchDelete(ids);
    }

    /**
     * 根据ID查询RSS订阅
     * @param id
     * @return
     */
    public RssSubscriptions getById(Long id) {
        return rssSubscriptionMapper.getById(id);
    }

    /**
     * 获取所有激活的订阅
     * @return
     */
    public List<RssSubscriptions> getAllActiveSubscriptions() {
        return rssSubscriptionMapper.getAllActiveSubscriptions();
    }

    /**
     * 根据邮箱取消订阅
     * @param email
     */
    public void unsubscribeByEmail(String email) {
        RssSubscriptions subscription = rssSubscriptionMapper.getByEmail(email);
        if (subscription == null) {
            throw new RssSubscriptionException(MessageConstant.RssNotFound);
        }
        if (subscription.getIsActive() == 0) {
            throw new RssSubscriptionException(MessageConstant.RssNotFound);
        }
        subscription.setIsActive(0);
        subscription.setUnSubscribeTime(LocalDateTime.now());
        rssSubscriptionMapper.update(subscription);
    }
}
