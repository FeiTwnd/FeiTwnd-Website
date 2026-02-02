package cc.feitwnd.mapper;

import cc.feitwnd.dto.AdminChangePasswordDTO;
import cc.feitwnd.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper {
    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员
     */
    @Select("select * from admin where username = #{username}")
    Admin getByUsername(String username);

    /**
     * 根据id查询管理员
     * @param adminId 管理员id
     * @return 管理员
     */
    @Select("select * from admin where id = #{adminId}")
    Admin getById(Long adminId);

    /**
     * 修改管理员信息
     * @param admin
     */
    void update(Admin admin);
}
