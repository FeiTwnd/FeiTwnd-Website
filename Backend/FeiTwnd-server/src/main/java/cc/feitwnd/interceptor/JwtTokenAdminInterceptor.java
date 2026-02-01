package cc.feitwnd.interceptor;

import cc.feitwnd.constant.JwtClaimsConstant;
import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.context.BaseContext;
import cc.feitwnd.exception.NotLoginException;
import cc.feitwnd.exception.UnauthorizedException;
import cc.feitwnd.properties.JwtProperties;
import cc.feitwnd.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());

        if(StringUtils.isEmpty(token)){
            throw new NotLoginException(MessageConstant.NOT_LOGIN);
        }

        // 校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);
            Long adminId = Long.valueOf(claims.get(JwtClaimsConstant.ADMIN_ID).toString());
            log.info("当前管理员id：{}", adminId);
            BaseContext.setCurrentId(adminId);
            // 通过，放行
            return true;
        } catch (Exception ex) {
            // 校验失败，抛出未授权异常
            throw new UnauthorizedException(MessageConstant.NOT_AUTHORIZED);
        }
    }
}
