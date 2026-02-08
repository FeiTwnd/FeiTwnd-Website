package cc.feitwnd.controller.admin;

import cc.feitwnd.dto.*;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.AdminService;
import cc.feitwnd.vo.AdminLoginVO;
import cc.feitwnd.vo.AdminVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端管理员接口
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
     * 获取管理员信息
     */
    @GetMapping
    public Result<AdminVO> getAdminInfo() {
        AdminVO adminVO = adminService.getAdminById();
        return Result.success(adminVO);
    }

    /**
     * 管理员退出登录
     */
    @PostMapping("/logout")
    public Result logout(@RequestBody AdminLogoutDTO adminLogoutDTO) {
        adminService.logout(adminLogoutDTO);
        return Result.success();
    }

    /**
     * 管理员修改密码
     */
    @PutMapping("/changePassword")
    public Result changePassword(@RequestBody AdminChangePasswordDTO adminChangePasswordDTO) throws Exception {
        log.info("管理员修改密码：{}", adminChangePasswordDTO);
        adminService.changePassword(adminChangePasswordDTO);
        return Result.success();
    }

    /**
     * 管理员更改昵称
     */
    @PutMapping("/changeNickname")
    public Result changeNickname(@RequestBody AdminChangeNicknameDTO adminChangeNicknameDTO) {
        log.info("管理员更改昵称：{}", adminChangeNicknameDTO);
        adminService.changeNickname(adminChangeNicknameDTO);
        return Result.success();
    }

    /**
     * 管理员换绑邮箱
     */
    @PutMapping("/changeEmail")
    public Result changeEmail(@RequestBody AdminChangeEmailDTO adminChangeEmailDTO) {
        log.info("管理员换绑邮箱：{}", adminChangeEmailDTO);
        adminService.changeEmail(adminChangeEmailDTO);
        return Result.success();
    }
}
