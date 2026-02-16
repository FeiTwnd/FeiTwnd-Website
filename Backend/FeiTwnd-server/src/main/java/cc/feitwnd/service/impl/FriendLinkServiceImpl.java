package cc.feitwnd.service.impl;

import cc.feitwnd.entity.FriendLinks;
import cc.feitwnd.mapper.FriendLinkMapper;
import cc.feitwnd.service.FriendLinkService;
import cc.feitwnd.vo.FriendLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FriendLinkServiceImpl implements FriendLinkService {

    @Autowired
    private FriendLinkMapper friendLinkMapper;

    /**
     * 获取所有友链
     * @return
     */
    @Cacheable(value = "friendLinks", key = "'all'")
    public List<FriendLinks> getAllFriendLink() {
        List<FriendLinks> friendLinkList = friendLinkMapper.getAllFriendLink();
        if(friendLinkList != null && friendLinkList.size() > 0){
            return friendLinkList;
        }
        return Collections.emptyList();
    }

    /**
     * 添加友链
     * @param friendLink
     */
    @CacheEvict(value = "friendLinks", allEntries = true)
    public void addFriendLink(FriendLinks friendLink) {
        friendLinkMapper.insert(friendLink);
    }

    /**
     * 批量删除友链
     * @param ids
     */
    @CacheEvict(value = "friendLinks", allEntries = true)
    public void batchDelete(List<Long> ids) {
        friendLinkMapper.batchDelete(ids);
    }

    /**
     * 修改友链
     * @param friendLink
     */
    @CacheEvict(value = "friendLinks", allEntries = true)
    public void updateFriendLink(FriendLinks friendLink) {
        friendLinkMapper.update(friendLink);
    }

    /**
     * 博客端获取可见的友链
     * @return
     */
    @Cacheable(value = "friendLinks", key = "'visible'")
    public List<FriendLinkVO> getVisibleFriendLink() {
        List<FriendLinks> friendLinkList = friendLinkMapper.getVisibleFriendLink();
        if(friendLinkList != null && friendLinkList.size() > 0){
            List<FriendLinkVO> friendLinkVOList = friendLinkList.stream().map(friendLink -> FriendLinkVO.builder()
                    .id(friendLink.getId())
                    .name(friendLink.getName())
                    .url(friendLink.getUrl())
                    .avatarUrl(friendLink.getAvatarUrl())
                    .description(friendLink.getDescription())
                    .sort(friendLink.getSort())
                    .build()).toList();
            return friendLinkVOList;
        }
        return Collections.emptyList();
    }
}
