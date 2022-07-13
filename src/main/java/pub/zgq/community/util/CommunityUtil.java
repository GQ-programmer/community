package pub.zgq.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author 孑然
 * <p>
 * 工具类
 */
public class CommunityUtil {

    /**
     * 生成随机字符串
     *
     * @return
     */
    public static String generateUUID() {
        //返回随机字符串，并将"-"去掉
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD5加密
     * key  -> 加密后的 key
     *
     * @param key 密码
     * @return
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 转换Json格式
     *
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    //public static void main(String[] args) {
    //    Map<String, Object> map = new HashMap<>();
    //    map.put("name", "张三");
    //    map.put("age", 25);
    //    System.out.println(getJSONString(0, "ok", map));
    //    // {"msg":"ok","code":0,"name":"张三","age":25}
    //}

}
