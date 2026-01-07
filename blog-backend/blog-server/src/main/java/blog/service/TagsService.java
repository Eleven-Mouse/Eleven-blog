package blog.service;

import blog.dto.TagsDTO;
import blog.vo.CategoryVO;
import blog.vo.TagsVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface TagsService
{
    void insertTags(TagsDTO tagsDTO);

    void deleteTag(Long id);

    List<TagsVO> selectAll();

    void update(TagsDTO tagsDTO);

    List<Map<String, Object>> getTagStatistics();
}
