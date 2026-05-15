package blog.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.InputStream;

/**
 * 基于 ip2region 的离线IP归属地查询工具
 */
@Slf4j
public class IpRegionUtil {

    private static final byte[] VECTOR_INDEX;

    static {
        try (InputStream is = IpRegionUtil.class.getClassLoader().getResourceAsStream("ip2region.xdb")) {
            if (is == null) {
                throw new RuntimeException("ip2region.xdb 未找到，请确认文件在 classpath 下");
            }
            VECTOR_INDEX = is.readAllBytes();
            log.info("ip2region.xdb 加载成功，大小: {} bytes", VECTOR_INDEX.length);
        } catch (Exception e) {
            throw new RuntimeException("加载 ip2region.xdb 失败", e);
        }
    }

    /**
     * 解析IP归属地，返回如 "广东 深圳" 或 "本地"
     */
    public static String resolve(String ip) {
        log.info("IP归属地解析开始, 原始IP: {}", ip);

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)
                || ip.startsWith("127.") || ip.startsWith("192.168.")
                || ip.startsWith("10.") || ip.startsWith("172.")
                || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")
                || "localhost".equalsIgnoreCase(ip)) {
            log.info("IP为本地/内网地址, 返回: 本地");
            return "本地";
        }

        // IPv6 处理: 提取 IPv4-mapped IPv6 中的真实 IPv4
        if (ip.contains(":")) {
            String mapped = extractIPv4Mapped(ip);
            if (mapped != null) {
                log.info("IPv4-mapped IPv6, 提取IPv4: {} -> {}", ip, mapped);
                ip = mapped;
            } else {
                log.info("纯IPv6地址, 返回: 海外, IP: {}", ip);
                return "海外";
            }
        }

        try {
            Searcher searcher = Searcher.newWithBuffer(VECTOR_INDEX);
            String region = searcher.search(ip);
            searcher.close();
            log.info("ip2region查询结果, IP: {}, 原始: {}", ip, region);
            String result = formatRegion(region);
            log.info("IP归属地最终结果: {}", result);
            return result;
        } catch (Exception e) {
            log.warn("IP归属地解析失败: ip={}, error={}", ip, e.getMessage());
            return "未知";
        }
    }

    /**
     * 将 ip2region 的原始格式转为友好显示
     * 原始格式: 国家|区域|省|市|ISP
     * 目标格式: 省 市 (如 "广东 深圳")，本地IP返回 "本地"
     */
    /**
     * 从 IPv4-mapped IPv6 地址中提取真实 IPv4
     * 支持 ::ffff:x.x.x.x 和 x:x:x:x:x:ffff:x.x.x.x 格式
     */
    private static String extractIPv4Mapped(String ip) {
        // ::ffff:192.168.1.1
        if (ip.startsWith("::ffff:")) {
            return ip.substring(7);
        }
        // 0:0:0:0:0:ffff:192.168.1.1 或 0:0:0:0:0:ffff:c0a8:101
        String lower = ip.toLowerCase();
        if (lower.contains("ffff:")) {
            String after = ip.substring(ip.indexOf("ffff:") + 5);
            // 如果是点分十进制格式直接返回
            if (after.contains(".")) {
                return after;
            }
        }
        return null;
    }

    private static String formatRegion(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "未知";
        }

        String[] parts = raw.split("\\|");
        String province = parts.length > 2 ? parts[2] : "";
        String city = parts.length > 3 ? parts[3] : "";

        // 清除 "省" "市" 后缀以缩短显示
        province = province.replace("省", "").replace("自治区", "");
        city = city.replace("市", "");

        if (province.isEmpty() && city.isEmpty()) {
            // 海外IP，显示国家
            String country = parts.length > 0 ? parts[0] : "";
            return country.isEmpty() ? "未知" : country;
        }

        return province.equals(city) ? province : province + " " + city;
    }
}
