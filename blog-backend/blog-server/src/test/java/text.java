import blog.service.ViewCountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisTest {

    @Autowired
    private ViewCountService viewCountService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testAddViewCount() {
        Long articleId = 10086L;

        // 1. 模拟浏览 +1
        viewCountService.incrementViewCount(articleId);
        System.out.println("执行 +1 完成");

        // 2. 查 Redis
        Boolean hasKey = redisTemplate.hasKey("article:view_count");
        System.out.println("Redis 是否存在 Key: " + hasKey);

        Object val = redisTemplate.opsForHash().get("article:view_count", String.valueOf(articleId));
        System.out.println("当前浏览量: " + val);
    }
}
