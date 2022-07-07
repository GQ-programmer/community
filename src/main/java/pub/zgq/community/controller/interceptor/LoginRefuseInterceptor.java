package pub.zgq.community.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pub.zgq.community.annotation.LoginRequired;
import pub.zgq.community.util.HostHoler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Author 孑然
 *
 * 拦截处理 不需登录状态 的请求
 */
@Component
public class LoginRefuseInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHoler hostHoler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (hostHoler.getUser() != null) {
            // 是登录状态 则重定向到首页
            response.sendRedirect(request.getContextPath() + "/index");
            return false;
        }
        return true;
    }
}
