package blog.service;

import blog.dto.FriendLinkDTO;
import blog.dto.FriendLinkQueryDTO;
import blog.vo.FriendLinkVO;

import java.util.List;

/**
 * 友链服务接口
 *
 * @author Eleven
 * @version 1.0
 */
public interface FriendLinkService {

    /**
     * 创建友链
     */
    void createFriendLink(FriendLinkDTO friendLinkDTO);

    /**
     * 更新友链
     */
    void updateFriendLink(FriendLinkDTO friendLinkDTO);

    /**
     * 删除友链
     */
    void deleteFriendLink(Long id);

    /**
     * 根据ID查询友链
     */
    FriendLinkVO getFriendLinkById(Long id);

    /**
     * 查询所有友链
     */
    List<FriendLinkVO> listAllFriendLinks();

    /**
     * 根据条件查询友链
     */
    List<FriendLinkVO> listFriendLinks(FriendLinkQueryDTO queryDTO);



    /**
     * 统计友链总数
     */
    Long countTotal();
}

