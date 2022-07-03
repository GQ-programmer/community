package pub.zgq.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.zgq.community.dao.UserMapper;
import pub.zgq.community.entity.User;

/**
 * @Author 孑然
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
