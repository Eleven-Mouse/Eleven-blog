package blog.service.impl;

import blog.dto.MomentDTO;
import blog.dto.MomentQueryDTO;

import blog.entity.Moment;
import blog.mapper.MomentMapper;
import blog.service.MomentService;
import blog.vo.MomentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MomentServiceImpl implements MomentService
{
    @Autowired
    private MomentMapper momentMapper;


    @Override
    public List<MomentVO> listAllMoments()
    {
        log.info("查询所有动态");
        return momentMapper.selectAll();
    }

    @Override
    @Transactional
    public void createMoment(MomentDTO momentDTO)
    {
        log.info("创建动态，参数：{}", momentDTO);

        momentMapper.insert(momentDTO);

        log.info("动态创建成功");
    }

    @Override
    public MomentVO getMomentById(Long id)
    {
        log.info("查询动态详情，ID：{}", id);
        return momentMapper.selectById(id);
    }

    @Override
    @Transactional
    public void deleteMoment(Long id)
    {
        log.info("删除动态，ID：{}", id);
        momentMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateMoment(Integer id, MomentDTO momentDTO)
    {
        Moment moment = new Moment();
        moment.setId(id.longValue());

        BeanUtils.copyProperties(momentDTO, moment);

        if(moment.getStatus() != null && moment.getStatus() == 1)
        {
            MomentVO existMoment = momentMapper.selectById(id.longValue());
            if(existMoment != null && existMoment.getStatus() == 0)
            {
                moment.setPublishTime(LocalDateTime.now());
            }
        }

        moment.setUpdateTime(LocalDateTime.now());

        momentMapper.update(moment);

        log.info("动态更新成功，动态ID：{}，状态：{}", id, moment.getStatus() == 1 ? "已发布" : "草稿");

    }

    @Override
    public Long countTotal()
    {
        log.info("统计动态总数");
        return momentMapper.countTotal();
    }
}

