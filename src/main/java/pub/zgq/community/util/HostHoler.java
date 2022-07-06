package pub.zgq.community.util;

import org.springframework.stereotype.Component;
import pub.zgq.community.entity.User;

/**
 * @Author 孑然
 *
 * 持有用户的信息，用于代替session对象
 */
@Component
public class HostHoler {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }
    public void clear() {
        users.remove();
    }
}
