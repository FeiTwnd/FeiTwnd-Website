package cc.feitwnd.mapper;

import cc.feitwnd.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {
    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员
     */
    @Select("select * from admin where username = #{username}")
    Admin getByUsername(String username);
}
