package pub.zgq.community.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pub.zgq.community.entity.LoginTicket;
import pub.zgq.community.entity.User;
import pub.zgq.community.service.UserService;
import pub.zgq.community.util.CookieUtil;
import pub.zgq.community.util.HostHoler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


/**
 * @Author 孑然
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHoler hostHoler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从Cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            // 查询登录凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 判断凭证是否有效
            // 不为空 且 状态是0有效  没有过期
            if (loginTicket != null && loginTicket.getStatus() ==0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户信息
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有user 存储容器中会有线程安全问题 request域对象太底层不建议使用
                hostHoler.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHoler.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHoler.clear();
    }
}
