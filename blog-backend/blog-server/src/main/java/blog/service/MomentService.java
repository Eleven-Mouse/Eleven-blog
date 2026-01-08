package blog.service;

import blog.dto.MomentDTO;
import blog.dto.MomentQueryDTO;
import blog.vo.MomentVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MomentService
{


    List<MomentVO> listAllMoments();

    void createMoment(MomentDTO momentDTO);

    MomentVO getMomentById(Long id);

    void deleteMoment(Long id);

    void updateMoment(Integer id, MomentDTO momentDTO);

    /**
     * 统计动态总数
     */
    Long countTotal();
}
