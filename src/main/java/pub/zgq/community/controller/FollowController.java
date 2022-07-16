package pub.zgq.community.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsFileUploadSupport;
import pub.zgq.community.entity.Page;
import pub.zgq.community.entity.User;
import pub.zgq.community.service.FollowService;
import pub.zgq.community.service.LikeService;
import pub.zgq.community.service.UserService;
import pub.zgq.community.util.CommunityConstant;
import pub.zgq.community.util.CommunityUtil;
import pub.zgq.community.util.HostHoler;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author 孑然
 */
@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHoler hostHoler;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHoler.getUser();
        // TODO 待优化返回提示
        if (user != null) {
            followService.follow(user.getId(), entityType, entityId);
            return CommunityUtil.getJSONString(0, "已关注!");
        }
        return null;
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHoler.getUser();
        // TODO 待优化返回提示
        if (user != null) {
            followService.unfollow(user.getId(), entityType, entityId);
            return CommunityUtil.getJSONString(0, "已取消关注!");
        }
        return null;
    }

    /**
     * 关注列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        // 封装Page
        page.setPath("/followees/" + userId);
        page.setLimit(5);
        page.setRows((int) followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USE));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (!userList.isEmpty()) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                boolean hasFollowed = hasFollowed(u.getId());
                map.put("hasFollowed", hasFollowed);

                // 此时map中的键包含 user、followeeTime、hasFollowed
            }
        }
        model.addAttribute("users", userList);
        return "/site/followee";
    }

    private boolean hasFollowed(int userId) {
        if (hostHoler.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHoler.getUser().getId(), ENTITY_TYPE_USE, userId);
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        // 封装Page
        page.setPath("/followers/" + userId);
        page.setLimit(5);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USE, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (!userList.isEmpty()) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                boolean hasFollowed = hasFollowed(u.getId());
                map.put("hasFollowed", hasFollowed);

                // 此时map中的键包含 user、followerTime、hasFollowed
            }
        }
        model.addAttribute("users", userList);
        return "/site/follower";
    }
}
