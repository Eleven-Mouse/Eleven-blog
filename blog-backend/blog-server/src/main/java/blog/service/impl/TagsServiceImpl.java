package blog.service.impl;

import blog.dto.TagsDTO;
import blog.entity.Tags;
import blog.mapper.ArticleMapper;
import blog.mapper.TagsMapper;
import blog.service.TagsService;
import blog.vo.ArticleVO;
import blog.vo.CategoryVO;
import blog.vo.TagsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TagsServiceImpl implements TagsService
{
    @Autowired
    private TagsMapper tagsMapper;


    @Override
    @Transactional
    public void insertTags(TagsDTO tagsDTO)
    {
        // 1. 先检查标签名是否已存在
        int count = tagsMapper.countByName(tagsDTO.getName());
        if (count > 0)
        {
            throw new RuntimeException("标签名 '" + tagsDTO.getName() + "' 已存在");
        }

        // 2. 创建新标签
        Tags tags = new Tags();
        BeanUtils.copyProperties(tagsDTO, tags);

        tags.setCreateTime(LocalDateTime.now());
        tags.setUpdateTime(LocalDateTime.now());


        tagsMapper.insert(tags);

        log.info("标签创建成功：{}", tags.getName());
    }

    @Override
    @Transactional
    public void deleteTag(Long id)
    {
        tagsMapper.delete(id);
    }

    @Override
    public List<TagsVO> selectAll()
    {
        return  tagsMapper.selectTagListWithCount();
    }

    @Override
    public void update(TagsDTO tagsDTO)
    {
        // 1. 检查是否有其他标签使用了这个名字
        int count = tagsMapper.countByNameExcludeId(tagsDTO.getName(), tagsDTO.getId());
        if (count > 0) {
            throw new RuntimeException("标签名 '" + tagsDTO.getName() + "' 已存在");
        }

        // 2. 更新标签
        Tags tags = new Tags();
        BeanUtils.copyProperties(tagsDTO, tags);

        tagsMapper.update(tags);

        log.info("标签更新成功：{}", tags.getName());
    }

    @Override
    public List<Map<String, Object>> getTagStatistics()
    {
        return tagsMapper.getTagStatistics();
    }
}
