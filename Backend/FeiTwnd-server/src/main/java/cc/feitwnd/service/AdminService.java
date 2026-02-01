package cc.feitwnd.service;

import cc.feitwnd.dto.AdminLoginDTO;
import cc.feitwnd.dto.AdminLoginOutDTO;
import cc.feitwnd.vo.AdminLoginVO;

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
     * 管理员退出登录
     * @param adminLoginOutDTO
     */
    void loginOut(AdminLoginOutDTO adminLoginOutDTO);
}
