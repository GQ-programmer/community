package pub.zgq.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pub.zgq.community.entity.Comment;
import pub.zgq.community.entity.User;
import pub.zgq.community.service.CommentService;
import pub.zgq.community.util.HostHoler;

import java.util.Date;

/**
 * @Author 孑然
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private HostHoler hostHoler;
    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/isLogin", method = RequestMethod.GET)
    @ResponseBody
    public User isLogin(){
        User user = hostHoler.getUser();
        return user;
    }

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment, Model model){
        User user = hostHoler.getUser();
        comment.setUserId(user.getId());
        // 有效帖
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);


        // 还跳转当前请求的页面
        return "redirect:/discuss/detail/" + discussPostId;
    }


}
