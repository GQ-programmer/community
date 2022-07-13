package pub.zgq.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pub.zgq.community.util.CommunityUtil;

/**
 * @Author 孑然
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {


    // ajax 示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "操作成功");
    }
}
