package cc.feitwnd.service;

import cc.feitwnd.dto.*;
import cc.feitwnd.vo.AdminLoginVO;
import cc.feitwnd.vo.AdminVO;

public interface AdminService {

    /**
     * 发送验证码
     */
    void sendVerifyCode(String username);

    /**
     * 管理员登录
     * @param adminLoginDTO
     * @return
     */
    AdminLoginVO login(AdminLoginDTO adminLoginDTO) throws Exception;

    /**
     * 获取管理员信息
     * @return
     */
    AdminVO getAdminById();

    /**
     * 管理员退出登录
     * @param adminLogoutDTO
     */
    void logout(AdminLogoutDTO adminLogoutDTO);

    /**
     * 管理员修改密码
     * @param adminChangePasswordDTO
     */
    void changePassword(AdminChangePasswordDTO adminChangePasswordDTO) throws Exception;

    /**
     * 管理员更改昵称
     * @param adminChangeNicknameDTO
     */
    void changeNickname(AdminChangeNicknameDTO adminChangeNicknameDTO);

    /**
     * 管理员换绑邮箱
     * @param adminChangeEmailDTO
     */
    void changeEmail(AdminChangeEmailDTO adminChangeEmailDTO);
}
