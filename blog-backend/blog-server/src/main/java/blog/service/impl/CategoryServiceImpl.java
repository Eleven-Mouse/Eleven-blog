package blog.service.impl;

import blog.dto.CategoryDTO;
import blog.entity.Category;
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
import java.util.Locale;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService
{
    @Autowired
    private CategoryMapper categoryMapper;


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

        if (category.getSlug() == null || category.getSlug().trim().isEmpty()) {
            category.setSlug(buildSlug(category.getName()));
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }

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
        if (category.getSlug() != null && category.getSlug().trim().isEmpty()) {
            category.setSlug(null);
        }
        category.setUpdateTime(LocalDateTime.now());

        categoryMapper.update(category);
    }

    @Override
    public List<CategoryVO> selectAll()
    {
        log.info("查询分类列表：");
        return categoryMapper.selectAll();
    }

    @Override
    public CategoryVO selectById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return null;
        }
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    private String buildSlug(String source) {
        if (source == null || source.trim().isEmpty()) {
            return "topic";
        }
        String slug = source
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
        return slug.isEmpty() ? "topic" : slug;
    }
}
