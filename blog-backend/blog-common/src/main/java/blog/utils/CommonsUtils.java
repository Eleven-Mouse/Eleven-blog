package blog.utils;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 模板工具类
 * 为 Thymeleaf 模板提供辅助方法
 *
 * @author Eleven
 * @version 1.0
 */
@Component("commons")
public class CommonsUtils {

    /**
     * 格式化日期
     */
    public String fmtdate(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取 Gravatar 头像地址
     */
    public String gravatar(String email) {
        if (email == null || email.isEmpty()) {
            return "https://www.gravatar.com/avatar/default?s=40&d=mp";
        }
        try {
            String hash = md5(email.trim().toLowerCase());
            return "https://www.gravatar.com/avatar/" + hash + "?s=40&d=mp";
        } catch (Exception e) {
            return "https://www.gravatar.com/avatar/default?s=40&d=mp";
        }
    }

    /**
     * MD5 加密
     */
    private String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取网站URL
     */
    public String site_url() {
        return "http://localhost:8080";
    }

    /**
     * 获取网站URL（带路径）
     */
    public String site_url(String path) {
        return "http://localhost:8080" + path;
    }

    /**
     * 截取文章内容
     */
    public String article(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        // 移除 HTML 标签
        String text = content.replaceAll("<[^>]+>", "");
        // 截取前100个字符
        if (text.length() > 100) {
            return text.substring(0, 100) + "...";
        }
        return text;
    }

    /**
     * 随机数生成（用于背景图等）
     */
    public int random(int max, String suffix) {
        return (int) (Math.random() * max) + 1;
    }
}


