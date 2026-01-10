package blog.service.impl;

import blog.dto.FriendLinkDTO;
import blog.dto.FriendLinkQueryDTO;
import blog.entity.FriendLink;
import blog.entity.Tags;
import blog.mapper.FriendLinkMapper;
import blog.service.FriendLinkService;
import blog.vo.FriendLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 友链服务实现类
 *
 * @author Eleven
 * @version 1.0
 */
@Service
@Slf4j
public class FriendLinkServiceImpl implements FriendLinkService
{

    @Autowired
    private FriendLinkMapper friendLinkMapper;

    @Override
    @Transactional
    public void createFriendLink(FriendLinkDTO friendLinkDTO)
    {
        log.info("创建友链：{}", friendLinkDTO.getName());

        FriendLink friendLink = new FriendLink();
        BeanUtils.copyProperties(friendLinkDTO, friendLink);

        // 设置默认值
        LocalDateTime now = LocalDateTime.now();
        friendLink.setCreateTime(now);
        friendLink.setUpdateTime(now);
        friendLink.setStatus(1);
        if (friendLink.getViewCount() == null)
        {
            friendLink.setViewCount(0);
        }

        friendLinkMapper.insert(friendLink);
    }

    @Override
    @Transactional
    public void updateFriendLink( FriendLinkDTO friendLinkDTO)
    {
        log.info("更新友链:{}", friendLinkDTO);

        FriendLink friendLink = new FriendLink();
        BeanUtils.copyProperties(friendLinkDTO, friendLink);

        friendLink.setUpdateTime(LocalDateTime.now());

        friendLinkMapper.update(friendLink);

    }

    @Override
    @Transactional
    public void deleteFriendLink(Long id)
    {
        log.info("删除友链，ID：{}", id);

        friendLinkMapper.deleteById(id);
    }

    @Override
    public FriendLinkVO getFriendLinkById(Long id)
    {
        log.info("查询友链详情，ID：{}", id);

        return friendLinkMapper.selectById(id);
    }

    @Override
    public List<FriendLinkVO> listAllFriendLinks()
    {
        log.info("查询所有友链");

        return friendLinkMapper.selectAll();
    }

    @Override
    public List<FriendLinkVO> listFriendLinks(FriendLinkQueryDTO queryDTO)
    {
        log.info("根据条件查询友链：{}", queryDTO);

        List<FriendLinkVO> friendLinks;

        if (queryDTO.getName() != null && !queryDTO.getName().trim().isEmpty())
        {
            friendLinks = friendLinkMapper.selectByName(queryDTO.getName());
        }
        else if (queryDTO.getStatus() != null)
        {
            friendLinks = friendLinkMapper.selectByStatus(queryDTO.getStatus());
        }
        else
        {
            friendLinks = friendLinkMapper.selectAll();
        }

        return friendLinks;
    }



    @Override
    public Long countTotal()
    {
        log.info("统计友链总数");
        return friendLinkMapper.countTotal();
    }
}
