package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.entity.FriendLinks;
import cc.feitwnd.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface FriendLinkMapper {
    /**
     * 获取所有友情链接
     * @return
     */
    @Select("select * from friend_links")
    List<FriendLinks> getAllFriendLink();

    /**
     * 添加友情链接
     * @param friendLink
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(FriendLinks friendLink);

    /**
     * 删除友情链接
     * @param id
     */
    @Delete("delete from friend_links where id = #{id}")
    void delete(Long id);

    /**
     * 批量删除友情链接
     * @param ids
     */
    void batchDelete(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(FriendLinks friendLink);

    /**
     * 获取可见友情链接
     * @return
     */
    @Select("select * from friend_links where is_visible = 1")
    List<FriendLinks> getVisibleFriendLink();
}
