package cc.feitwnd.service.visitor;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 访客令牌服务：签发与校验访客身份令牌
 */
public interface VisitorTokenService {

    String VISITOR_TOKEN_HEADER = "X-Visitor-Token";
    String VISITOR_FP_HEADER = "X-Visitor-Fingerprint";

    /**
     * 为访客签发令牌（绑定访客ID+指纹）
     */
    String generateToken(Long visitorId, String fingerprint);

    /**
     * 从请求中解析并校验访客令牌，返回可信visitorId
     */
    Long resolveVisitorId(HttpServletRequest request);
}
