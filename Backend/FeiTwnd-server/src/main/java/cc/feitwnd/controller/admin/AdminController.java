package cc.feitwnd.controller.admin;

import cc.feitwnd.dto.AdminLoginDTO;
import cc.feitwnd.dto.AdminLoginOutDTO;
import cc.feitwnd.dto.SendCodeDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.AdminService;
import cc.feitwnd.vo.AdminLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端管理员相关接口
 */
@RestController
@RequestMapping("/admin/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 发送验证码
     */
    @PostMapping("/sendCode")
    public Result sendCode(@RequestBody SendCodeDTO sendCodeDTO) {
        log.info("发送验证码");
        adminService.sendVerifyCode(sendCodeDTO.getUsername());
        return Result.success();
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> AdminLogin(@RequestBody AdminLoginDTO adminLoginDTO) throws Exception {
        log.info("管理员登录：{}", adminLoginDTO);
        AdminLoginVO adminLoginVO = adminService.login(adminLoginDTO);
        return Result.success(adminLoginVO);
    }

    /**
     * 管理员退出登录
     */
    @PostMapping("/logout")
    public Result logout(@RequestBody AdminLoginOutDTO adminLoginOutDTO) {
        adminService.loginOut(adminLoginOutDTO);
        return Result.success();
    }
}
