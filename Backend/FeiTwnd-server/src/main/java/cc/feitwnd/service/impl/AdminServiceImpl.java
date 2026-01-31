package cc.feitwnd.service.impl;

import cc.feitwnd.constant.JwtClaimsConstant;
import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.dto.AdminLoginDTO;
import cc.feitwnd.entity.Admin;
import cc.feitwnd.exception.*;
import cc.feitwnd.mapper.AdminMapper;
import cc.feitwnd.properties.JwtProperties;
import cc.feitwnd.service.AdminService;
import cc.feitwnd.service.EmailService;
import cc.feitwnd.service.VerifyCodeService;
import cc.feitwnd.utils.JwtUtil;
import cc.feitwnd.vo.AdminLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private VerifyCodeService verifyCodeService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 发送验证码
     */
    public void sendVerifyCode(String username) {
        // 验证用户是否存在
        Admin admin = adminMapper.getByUsername(username);
        if(admin == null){
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
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

        // 验证密码是否正确
        String hashedPassword = hashPassword(password, admin.getSalt());
        if(!hashedPassword.equals(admin.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 检查是否可以校验验证码
        if(!verifyCodeService.canAttempt()){
            Long lockRemainingMinutes = verifyCodeService.getLockRemainingMinutes();
            throw new VerifyCodeLockException(MessageConstant.VERIFY_CODE_LOCK+lockRemainingMinutes+"分钟");
        }

        // 校验验证码是否正确
        boolean isValid = verifyCodeService.verifyCode(adminLoginDTO.getCode());
        if(!isValid){
            verifyCodeService.recordAttempt(false);
            throw new VerifyCodeErrorException(MessageConstant.VERIFY_CODE_ERROR);
        }

        // 清空验证码及状态
        verifyCodeService.clearAll();

        // 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID,admin.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);

        return AdminLoginVO.builder()
                .id(admin.getId())
                .nickName(admin.getNickname())
                .token(token)
                .build();
    }

    // 计算密码+盐的哈希
    private String hashPassword(String password, String salt) throws Exception{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String combined = password + salt;
        byte[] hash = md.digest(combined.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

     //将字节数组转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
