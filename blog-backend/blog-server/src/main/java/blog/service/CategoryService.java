package blog.service;

import blog.dto.CategoryDTO;
import blog.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService
{
    void delete(Long id);

    void insert(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);
    
    List<CategoryVO> selectAll();
}
