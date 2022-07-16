package pub.zgq.community.controller;

import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pub.zgq.community.entity.User;
import pub.zgq.community.service.LikeService;
import pub.zgq.community.util.CommunityUtil;
import pub.zgq.community.util.HostHoler;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 孑然
 */
@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHoler hostHoler;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        // 获得当前用户
        User user = hostHoler.getUser();
        // TODO: 2022/7/14 对未登录进行优化
        if (user != null) {
            // 对当前实体进行 点赞
            likeService.like(user.getId(), entityType, entityId, entityUserId);

            // 查询当前实体的 点赞数量
            long likeCount = likeService.findEntityLikeCount(entityType, entityId);
            // 当前点赞状态
            int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

            // 封装返回结果
            Map<String, Object> map = new HashMap<>();
            map.put("likeCount", likeCount);
            map.put("likeStatus", likeStatus);
            return CommunityUtil.getJSONString(0, null, map);
        }
        return null;
    }
}
