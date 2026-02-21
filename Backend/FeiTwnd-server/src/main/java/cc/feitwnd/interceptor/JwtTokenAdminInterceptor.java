package cc.feitwnd.interceptor;

import cc.feitwnd.constant.JwtClaimsConstant;
import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.constant.StatusConstant;
import cc.feitwnd.context.BaseContext;
import cc.feitwnd.exception.GuestReadOnlyException;
import cc.feitwnd.exception.NotLoginException;
import cc.feitwnd.exception.UnauthorizedException;
import cc.feitwnd.properties.JwtProperties;
import cc.feitwnd.service.TokenService;
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
    @Autowired
    private TokenService tokenService;

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

        // 如果令牌为空，抛出未登录异常
        if(StringUtils.isEmpty(token)){
            throw new NotLoginException(MessageConstant.NOT_LOGIN);
        }

        // 校验令牌
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);
            Long adminId = Long.valueOf(claims.get(JwtClaimsConstant.ADMIN_ID).toString());
            Integer role = Integer.valueOf(claims.get(JwtClaimsConstant.ADMIN_ROLE).toString());
            log.info("jwt校验,当前管理员id：{}, role: {}", adminId, role);

            // 检测令牌是否在服务端存在
            if(!tokenService.isValidToken(adminId, token)){
                throw new UnauthorizedException(MessageConstant.NOT_AUTHORIZED);
            }

            // 游客账号(role=0)只允许GET查询操作，禁止增删改
            if(role.equals(StatusConstant.DISABLE) && !"GET".equalsIgnoreCase(request.getMethod())){
                throw new GuestReadOnlyException(MessageConstant.GUEST_READ_ONLY);
            }

            BaseContext.setCurrentId(adminId);
            BaseContext.setCurrentRole(role);
            // 通过，放行
            return true;
        }catch (GuestReadOnlyException ex){
            throw ex;
        }
        catch (Exception ex) {
            // 校验失败，抛出未授权异常
            throw new UnauthorizedException(MessageConstant.NOT_AUTHORIZED);
        }
    }

    /**
     * 后置处理 - 清理ThreadLocal
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        try {
            // 清理ThreadLocal，防止虚拟线程复用导致adminId串用
            BaseContext.removeCurrentId();
            BaseContext.removeCurrentRole();
        } catch (Exception e) {
            log.error("清理ThreadLocal失败", e);
        }
    }
}
