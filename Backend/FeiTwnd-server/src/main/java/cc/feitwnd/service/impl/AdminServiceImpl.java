package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.constant.StatusConstant;
import cc.feitwnd.context.BaseContext;
import cc.feitwnd.dto.*;
import cc.feitwnd.entity.Admin;
import cc.feitwnd.exception.*;
import cc.feitwnd.mapper.AdminMapper;
import cc.feitwnd.properties.VisitorProperties;
import cc.feitwnd.service.*;
import cc.feitwnd.vo.AdminLoginVO;
import cc.feitwnd.vo.AdminVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private VerifyCodeService verifyCodeService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private VisitorProperties visitorProperties;
    @Autowired
    private EncryptPasswordService encryptPasswordService;

    /**
     * 发送验证码
     * @param username
     */
    public void sendVerifyCode(String username) {
        // 验证用户是否存在
        Admin admin = adminMapper.getByUsername(username);
        if(admin == null){
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if(admin.getRole() == StatusConstant.DISABLE){
            // 游客无须邮箱验证码
            throw new VisitorSendCodeException(MessageConstant.VISITOR_VERIFY_CODE_ERROR
                    +visitorProperties.getVerifyCode());
        }
        // 检查是否可以发送验证码
        if(!verifyCodeService.canSendCode()){
            Long cooldown = verifyCodeService.getRemainingCooldown();
            throw new VerifyCodeCoolDownException("验证码冷却中,请等待"+cooldown+"秒后重试");
        }
        // 生成并保存验证码
        String code = verifyCodeService.generateCode();
        String email = admin.getEmail();

        verifyCodeService.saveCode(code);

        // 发送验证码
        emailService.sendVerifyCode(email,code);
    }

    /**
     * 管理员登录
     * @param adminLoginDTO
     * @return
     */
    public AdminLoginVO login(AdminLoginDTO adminLoginDTO) throws Exception {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();
        // 验证用户是否存在
        Admin admin = adminMapper.getByUsername(username);
        if(admin == null){
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 对密码进行加密
        String hashedPassword = encryptPasswordService.hashPassword(password, admin.getSalt());
        // 验证密码是否正确
        if(!hashedPassword.equals(admin.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 区分游客和管理员校验验证码
        if(admin.getRole() == StatusConstant.ENABLE){
            // 管理员需要校验邮箱验证码

            // 检查是否可以校验验证码
            if(!verifyCodeService.canAttempt()){
                Long lockRemainingMinutes = verifyCodeService.getLockRemainingMinutes();
                throw new VerifyCodeLockException(MessageConstant.VERIFY_CODE_LOCK+lockRemainingMinutes+"分钟");
            }

            // 校验验证码是否正确
            boolean isValid = verifyCodeService.verifyCode(adminLoginDTO.getCode());
            if(!isValid){
                Long remainingAttempts = verifyCodeService.getRemainingAttempts();
                throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR
                        +",还可以试"+remainingAttempts+"次");
            }

        }else{
            // 游客直接校验固定验证码
            if(!adminLoginDTO.getCode().equals(visitorProperties.getVerifyCode())){
                throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR
                        +",请输入:"+visitorProperties.getVerifyCode());
            }
        }

        // 生成并存储token
        String token = tokenService.createAndStoreToken(admin.getId());

        return AdminLoginVO.builder()
                .id(admin.getId())
                .token(token)
                .build();
    }

    /**
     * 获取管理员信息
     * @return
     */
    public AdminVO getAdminById() {
        Long adminId = BaseContext.getCurrentId();
        Admin admin = adminMapper.getById(adminId);
        if(admin == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 构造管理员信息
        return AdminVO.builder()
                .id(adminId)
                .nickname(admin.getNickname())
                .email(admin.getEmail())
                .build();
    }

    /**
     * 管理员退出登录
     * @param adminLogoutDTO
     */
    public void logout(AdminLogoutDTO adminLogoutDTO) {
        // 删除Redis中的token
        tokenService.logout(adminLogoutDTO.getId(), adminLogoutDTO.getToken());
    }

    /**
     * 管理员修改密码
     * @param adminChangePasswordDTO
     */
    public void changePassword(AdminChangePasswordDTO adminChangePasswordDTO) throws Exception {
        Long adminId = BaseContext.getCurrentId();
        Admin admin = adminMapper.getById(adminId);
        if(admin == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        // 验证两次输入的新密码是否一致
        if(!adminChangePasswordDTO.getNewPassword().equals(adminChangePasswordDTO.getConfirmNewPassword())){
            throw new PasswordErrorException(MessageConstant.NEW_PASSWORD_NOT_MATCH);
        }
        // 验证旧密码是否正确
        String hashedOldPassword = encryptPasswordService.hashPassword(adminChangePasswordDTO.getOldPassword(), admin.getSalt());
        if(!hashedOldPassword.equals(admin.getPassword())){
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        }
        // 获取加密后的新密码
        String hashedNewPassword = encryptPasswordService.hashPassword(adminChangePasswordDTO.getNewPassword(), admin.getSalt());
        // 验证新密码是否与旧密码一致
        if(hashedNewPassword.equals(admin.getPassword())){
            throw new PasswordErrorException(MessageConstant.NEW_PASSWORD_NOT_CHANGE);
        }
        // 更新管理员信息
        admin.setPassword(hashedNewPassword);
        adminMapper.update(admin);
        // 登出所有设备
        tokenService.logoutAll(adminId);
    }

    /**
     * 管理员更改昵称
     * @param adminChangeNicknameDTO
     */
    public void changeNickname(AdminChangeNicknameDTO adminChangeNicknameDTO) {
        Long adminId = BaseContext.getCurrentId();
        Admin admin = adminMapper.getById(adminId);
        if(admin == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        // 更新昵称
        admin.setNickname(adminChangeNicknameDTO.getNickname());
        adminMapper.update(admin);
    }

    /**
     * 管理员换绑邮箱
     * @param adminChangeEmailDTO
     */
    public void changeEmail(AdminChangeEmailDTO adminChangeEmailDTO) {
        Long adminId = BaseContext.getCurrentId();
        Admin admin = adminMapper.getById(adminId);
        if(admin == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 效验邮箱验证码
        // 检查是否可以校验验证码
        if(!verifyCodeService.canAttempt()){
            Long lockRemainingMinutes = verifyCodeService.getLockRemainingMinutes();
            throw new VerifyCodeLockException(MessageConstant.VERIFY_CODE_LOCK+lockRemainingMinutes+"分钟");
        }

        // 校验验证码是否正确
        boolean isValid = verifyCodeService.verifyCode(adminChangeEmailDTO.getCode());
        if(!isValid){
            Long remainingAttempts = verifyCodeService.getRemainingAttempts();
            throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR
                    +",还可以试"+remainingAttempts+"次");
        }
        // 更新邮箱
        admin.setEmail(adminChangeEmailDTO.getEmail());
        adminMapper.update(admin);
    }
}
