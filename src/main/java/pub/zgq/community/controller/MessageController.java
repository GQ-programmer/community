package pub.zgq.community.controller;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pub.zgq.community.entity.Message;
import pub.zgq.community.entity.Page;
import pub.zgq.community.entity.User;
import pub.zgq.community.service.MessageService;
import pub.zgq.community.service.UserService;
import pub.zgq.community.util.CommunityUtil;
import pub.zgq.community.util.HostHoler;

import java.util.*;

/**
 * @Author 孑然
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHoler hostHoler;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHoler.getUser();
        // 处理分页信息
        page.setPath("/letter/list");
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表信息
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());

        // 封装会话列表信息（每个会话包含 会话信息 当前会话私信数 当前会话未读私信数 当前会话目标用户）
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversations != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLettersCount(message.getConversationId()));
                map.put("unreadCount", messageService.findUnreadLettersCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询未读消息总数
        int letterUnreadCount = messageService.findUnreadLettersCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";

    }

    @RequestMapping(path = "/letter/detail{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        // 封装分页信息
        page.setPath("/letter/detail" + conversationId);
        page.setLimit(5);
        page.setRows(messageService.findLettersCount(conversationId));

        // 查询私信信息
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        // 封装私信信息(包含：私信信息、发送者信息fromUser)
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        // 查询私信目标
        User target = getLetterTarget(conversationId);
        model.addAttribute("target", target);

        // 把未读消息设置已读
        List<Integer> letterIds = getLetterIds(letterList);
        if (!letterIds.isEmpty()) {
            messageService.readMessage(letterIds);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHoler.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                // 判断当前消息是不是别人给当前用户发的
                if (hostHoler.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User taget = userService.findUserByUsername(toName);
        if (taget == null) {
           return CommunityUtil.getJSONString(1, "目标用户不存在!");
        }
        Message message = new Message();
        message.setFromId(hostHoler.getUser().getId());
        message.setToId(taget.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());

        // 插入数据
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }
}
