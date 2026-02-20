package cc.feitwnd.mapper;

import cc.feitwnd.dto.RssSubscriptionPageQueryDTO;
import cc.feitwnd.entity.RssSubscriptions;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RssSubscriptionMapper {
    /**
     * 插入RSS订阅
     * @param rssSubscriptions
     */
    void insert(RssSubscriptions rssSubscriptions);

    /**
     * 分页查询RSS订阅
     * @param rssSubscriptionPageQueryDTO
     * @return
     */
    Page<RssSubscriptions> pageQuery(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO);

    /**
     * 更新RSS订阅
     * @param rssSubscriptions
     */
    void update(RssSubscriptions rssSubscriptions);

    /**
     * 删除RSS订阅
     * @param id
     */
    @Delete("delete from rss_subscriptions where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量删除RSS订阅
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据ID查询RSS订阅
     * @param id
     * @return
     */
    @Select("select * from rss_subscriptions where id = #{id}")
    RssSubscriptions getById(Long id);

    /**
     * 获取所有激活的订阅
     * @return
     */
    @Select("select * from rss_subscriptions where is_active = 1 order by subscribe_time desc")
    List<RssSubscriptions> getAllActiveSubscriptions();

    /**
     * 根据邮箱查询订阅
     * @param email
     * @return
     */
    @Select("select * from rss_subscriptions where email = #{email}")
    RssSubscriptions getByEmail(String email);

    /**
     * 检查访客是否有激活的订阅
     * @param visitorId
     * @return
     */
    @Select("select count(*) > 0 from rss_subscriptions where visitor_id = #{visitorId} and is_active = 1")
    boolean hasActiveByVisitorId(Long visitorId);

    /**
     * 根据访客ID获取激活的订阅记录
     * @param visitorId
     * @return
     */
    @Select("select * from rss_subscriptions where visitor_id = #{visitorId} and is_active = 1 limit 1")
    RssSubscriptions getActiveByVisitorId(Long visitorId);
}
