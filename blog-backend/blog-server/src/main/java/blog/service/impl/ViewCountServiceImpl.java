package blog.service.impl;

import blog.service.ViewCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ViewCountServiceImpl implements ViewCountService
{
    @Autowired
    private StringRedisTemplate redisTemplate;

    private  static final String VIEW_COUNT_KEY = "article:view_count";

    /**
     * 1. 增加浏览量 (每次调用+1)
     */
    public void incrementViewCount(Long articleId,String ip)
    {
        String ipRecordKey = "article:view:record:" + articleId + ":" + ip;
        Boolean isFirstVisit = redisTemplate.opsForValue()
                .setIfAbsent(ipRecordKey, "1", 10, TimeUnit.MINUTES);


        if (Boolean.TRUE.equals(isFirstVisit))
        {
            redisTemplate.opsForHash().increment(VIEW_COUNT_KEY, String.valueOf(articleId), 1);
        }


    }

    public Integer getViewCount (Long articleId)
    {
        Object count = redisTemplate.opsForHash().get(VIEW_COUNT_KEY,String.valueOf((articleId)));
        if(count != null)
        {
            return Integer.parseInt((String) count);
        }
    return 0;
    }


    public Map<Object,Object> getAllViewCounts()
    {
        return redisTemplate.opsForHash().entries(VIEW_COUNT_KEY);
    }
}
