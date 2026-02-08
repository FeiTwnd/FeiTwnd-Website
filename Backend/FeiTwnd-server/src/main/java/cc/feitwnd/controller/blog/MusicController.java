package cc.feitwnd.controller.blog;

import cc.feitwnd.result.Result;
import cc.feitwnd.service.MusicService;
import cc.feitwnd.vo.MusicVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客端音乐接口
 */
@Slf4j
@RestController("blogMusicController")
@RequestMapping("/blog/music")
public class MusicController {

    @Autowired
    private MusicService musicService;

    /**
     * 获取所有可见的音乐
     * @return
     */
    @GetMapping
    public Result<List<MusicVO>> getAllVisibleMusic() {
        log.info("获取所有可见的音乐");
        List<MusicVO> musicVOList = musicService.getAllVisibleMusic();
        return Result.success(musicVOList);
    }
}
