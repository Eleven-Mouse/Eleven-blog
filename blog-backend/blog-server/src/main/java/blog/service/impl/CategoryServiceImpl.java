package blog.service.impl;

import blog.dto.CategoryDTO;
import blog.entity.Article;
import blog.entity.Category;
import blog.mapper.ArticleMapper;
import blog.mapper.CategoryMapper;
import blog.service.CategoryService;
import blog.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService
{
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleMapper articleMapper;


    @Override
    @Transactional
    public void delete(Long id)
    {
        categoryMapper.delete(id);
    }

    @Override
    @Transactional
    public void insert(CategoryDTO categoryDTO)
    {
        Category category = new Category();

        BeanUtils.copyProperties(categoryDTO,category);

        LocalDateTime now = LocalDateTime.now();
        category.setCreateTime(now);
        category.setUpdateTime(now);

        categoryMapper.insert(category);

    }

    @Override
    @Transactional
    public void update(CategoryDTO categoryDTO)
    {
        Category category = new Category();

        BeanUtils.copyProperties(categoryDTO,category);
        category.setUpdateTime(LocalDateTime.now());

        categoryMapper.update(category);
    }

    @Override
    public List<CategoryVO> selectAll()
    {
        log.info("查询分类列表：");
        return categoryMapper.selectAll();
    }
}
