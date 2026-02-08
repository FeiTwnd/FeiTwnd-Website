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
}
