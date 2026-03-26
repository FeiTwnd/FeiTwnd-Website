package cc.feitwnd.service;

import cc.feitwnd.constant.JwtClaimsConstant;
import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.exception.UnauthorizedException;
import cc.feitwnd.properties.JwtProperties;
import cc.feitwnd.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 访客令牌服务：签发与校验访客身份令牌
 */
@Component
public class VisitorTokenService {

    public static final String VISITOR_TOKEN_HEADER = "X-Visitor-Token";
    public static final String VISITOR_FP_HEADER = "X-Visitor-Fingerprint";

    // 访客令牌默认有效期：30天
    private static final long VISITOR_TOKEN_TTL = 30L * 24 * 60 * 60 * 1000;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 为访客签发令牌（绑定访客ID+指纹）
     */
    public String generateToken(Long visitorId, String fingerprint) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.VISITOR_ID, visitorId);
        claims.put(JwtClaimsConstant.VISITOR_FINGERPRINT, fingerprint);
        return JwtUtil.createJWT(jwtProperties.getSecretKey(), VISITOR_TOKEN_TTL, claims);
    }

    /**
     * 从请求中解析并校验访客令牌，返回可信visitorId
     */
    public Long resolveVisitorId(HttpServletRequest request) {
        String token = request.getHeader(VISITOR_TOKEN_HEADER);
        String fingerprint = request.getHeader(VISITOR_FP_HEADER);
        if (StringUtils.isBlank(token) || StringUtils.isBlank(fingerprint)) {
            throw new UnauthorizedException(MessageConstant.VISITOR_TOKEN_INVALID);
        }

        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);
            Object idObj = claims.get(JwtClaimsConstant.VISITOR_ID);
            Object fpObj = claims.get(JwtClaimsConstant.VISITOR_FINGERPRINT);
            if (idObj == null || fpObj == null) {
                throw new UnauthorizedException(MessageConstant.VISITOR_TOKEN_INVALID);
            }

            String tokenFingerprint = String.valueOf(fpObj);
            if (!fingerprint.equals(tokenFingerprint)) {
                throw new UnauthorizedException(MessageConstant.VISITOR_TOKEN_INVALID);
            }

            return Long.valueOf(String.valueOf(idObj));
        } catch (UnauthorizedException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnauthorizedException(MessageConstant.VISITOR_TOKEN_INVALID);
        }
    }
}
