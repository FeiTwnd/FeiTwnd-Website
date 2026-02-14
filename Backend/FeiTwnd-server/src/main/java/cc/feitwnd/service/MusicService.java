package cc.feitwnd.service;

import cc.feitwnd.dto.MusicPageQueryDTO;
import cc.feitwnd.entity.Music;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.vo.MusicVO;

import java.util.List;

public interface MusicService {
    /**
     * 添加音乐
     * @param music
     */
    void addMusic(Music music);

    /**
     * 分页查询音乐列表
     * @param musicPageQueryDTO
     * @return
     */
    PageResult pageQuery(MusicPageQueryDTO musicPageQueryDTO);

    /**
     * 更新音乐
     * @param music
     */
    void updateMusic(Music music);

    /**
     * 批量删除音乐
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据ID查询音乐
     * @param id
     * @return
     */
    Music getById(Long id);

    /**
     * 获取所有可见的音乐
     * @return
     */
    List<MusicVO> getAllVisibleMusic();
}
