package cc.feitwnd.service.impl;

import cc.feitwnd.entity.FriendLinks;
import cc.feitwnd.mapper.FriendLinkMapper;
import cc.feitwnd.service.FriendLinkService;
import cc.feitwnd.vo.FriendLinkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Autowired
    private FriendLinkMapper friendLinkMapper;

    /**
     * 获取所有友链
     */
    public List<FriendLinks> getAllFriendLink() {
        List<FriendLinks> friendLinkList = friendLinkMapper.getAllFriendLink();
        if(friendLinkList != null && friendLinkList.size() > 0){
            return friendLinkList;
        }
        return null;
    }

    /**
     * 添加友链
     * @param friendLink
     */
    public void addFriendLink(FriendLinks friendLink) {
        friendLinkMapper.insert(friendLink);
    }

    /**
     * 删除友链
     * @param id
     */
    public void deleteFriendLink(Long id) {
        friendLinkMapper.delete(id);
    }

    /**
     * 修改友链
     * @param friendLink
     */
    public void updateFriendLink(FriendLinks friendLink) {
        friendLinkMapper.update(friendLink);
    }

    /**
     * 博客端获取可见的友链
     * @return
     */
    public List<FriendLinkVO> getVisibleFriendLink() {
        List<FriendLinks> friendLinkList = friendLinkMapper.getVisibleFriendLink();
        if(friendLinkList != null && friendLinkList.size() > 0){
            List<FriendLinkVO> friendLinkVOList = friendLinkList.stream().map(friendLink -> FriendLinkVO.builder()
                    .id(friendLink.getId())
                    .name(friendLink.getName())
                    .url(friendLink.getUrl())
                    .avatarUrl(friendLink.getAvatarUrl())
                    .description(friendLink.getDescription())
                    .build()).toList();
            return friendLinkVOList;
        }
        return null;
    }
}
