package cc.feitwnd.service.impl;

import cc.feitwnd.dto.MusicDTO;
import cc.feitwnd.dto.MusicPageQueryDTO;
import cc.feitwnd.entity.Music;
import cc.feitwnd.mapper.MusicMapper;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.MusicService;
import cc.feitwnd.vo.MusicVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {

    @Autowired
    private MusicMapper musicMapper;

    /**
     * 添加音乐
     * @param music
     */
    public void addMusic(MusicDTO musicDTO) {
        Music music = new Music();
        BeanUtils.copyProperties(musicDTO, music);
        musicMapper.insert(music);
    }

    /**
     * 分页查询音乐列表
     * @param musicPageQueryDTO
     * @return
     */
    public PageResult pageQuery(MusicPageQueryDTO musicPageQueryDTO) {
        PageHelper.startPage(musicPageQueryDTO.getPage(), musicPageQueryDTO.getPageSize());
        Page<Music> page = musicMapper.pageQuery(musicPageQueryDTO);
        long total = page.getTotal();
        List<Music> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 更新音乐
     * @param music
     */
    public void updateMusic(MusicDTO musicDTO) {
        Music music = new Music();
        BeanUtils.copyProperties(musicDTO, music);
        musicMapper.update(music);
    }

    /**
     * 批量删除音乐
     * @param ids
     */
    public void batchDelete(List<Long> ids) {
        musicMapper.batchDelete(ids);
    }

    /**
     * 根据ID查询音乐
     * @param id
     * @return
     */
    public Music getById(Long id) {
        return musicMapper.getById(id);
    }

    /**
     * 获取所有可见的音乐
     * @return
     */
    public List<MusicVO> getAllVisibleMusic() {
        List<Music> musicList = musicMapper.getAllVisibleMusic();
        if(musicList != null && !musicList.isEmpty()) {
            // 转换为VO
            List<MusicVO> musicVOList = musicList.stream().map(music -> MusicVO.builder()
                    .id(music.getId())
                    .title(music.getTitle())
                    .artist(music.getArtist())
                    .duration(music.getDuration())
                    .coverImage(music.getCoverImage())
                    .musicUrl(music.getMusicUrl())
                    .lyricUrl(music.getLyricUrl())
                    .hasLyric(music.getHasLyric())
                    .lyricType(music.getLyricType())
                    .build()
            ).toList();
            return musicVOList;
        }
        return Collections.emptyList();
    }
}
