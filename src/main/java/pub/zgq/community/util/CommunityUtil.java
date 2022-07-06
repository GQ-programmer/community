package pub.zgq.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @Author 孑然
 *
 * 工具类
 */
public class CommunityUtil {

    /**
     * 生成随机字符串
     * @return
     */
    public static String generateUUID(){
        //返回随机字符串，并将"-"去掉
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD5加密
     * key  -> 加密后的 key
     * @param key 密码
     * @return
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
