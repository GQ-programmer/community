package pub.zgq.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pub.zgq.community.dao.DiscussPostMapper;
import pub.zgq.community.entity.Comment;
import pub.zgq.community.entity.DiscussPost;
import pub.zgq.community.entity.Page;
import pub.zgq.community.entity.User;
import pub.zgq.community.service.CommentService;
import pub.zgq.community.service.DiscussPostService;
import pub.zgq.community.service.UserService;
import pub.zgq.community.util.CommunityConstant;
import pub.zgq.community.util.CommunityUtil;
import pub.zgq.community.util.HostHoler;

import java.util.*;

/**
 * @Author 孑然
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHoler hostHoler;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        // 判断当前用户是否登录
        User user = hostHoler.getUser();
        if (user == null) {
            //未登录
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 报错的情况，将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {

        // 查询帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",discussPost);
        // 查询作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());


        // 评论：给帖子的评论
        // 回复: 给评论的评论

        // 评论列表
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        // 评论Vo列表（添人评论者的用户信息User）
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        // 封装评论Vo
        for (Comment comment : commentList) {
            if (comment != null) {
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                // 回复列表
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE
                );
                // 回复Vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyVoList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 此回复的作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标对象
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }

}
