package blog.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ViewCountService
{
    void incrementViewCount(Long id,String ip);

    Integer getViewCount(Long id);
    Map<Object,Object> getAllViewCounts();
}
