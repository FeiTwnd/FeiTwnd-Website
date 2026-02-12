package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.dto.MusicPageQueryDTO;
import cc.feitwnd.entity.Music;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端音乐接口
 */
@Slf4j
@RestController("adminMusicController")
@RequestMapping("/admin/music")
public class MusicController {

    @Autowired
    private MusicService musicService;

    /**
     * 分页查询音乐列表
     * @param musicPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getMusicList(MusicPageQueryDTO musicPageQueryDTO) {
        log.info("获取音乐列表,{}", musicPageQueryDTO);
        PageResult pageResult = musicService.pageQuery(musicPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询音乐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Music> getById(@PathVariable Long id) {
        log.info("根据ID查询音乐,{}", id);
        Music music = musicService.getById(id);
        return Result.success(music);
    }

    /**
     * 添加音乐
     * @param music
     * @return
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "music")
    public Result addMusic(@RequestBody Music music) {
        log.info("添加音乐,{}", music);
        musicService.addMusic(music);
        return Result.success();
    }

    /**
     * 更新音乐
     * @param music
     * @return
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "music", targetId = "#music.id")
    public Result updateMusic(@RequestBody Music music) {
        log.info("更新音乐,{}", music);
        musicService.updateMusic(music);
        return Result.success();
    }

    /**
     * 批量删除音乐
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "music", targetId = "#ids")
    public Result deleteMusic(@RequestParam List<Long> ids) {
        log.info("批量删除音乐,{}", ids);
        musicService.batchDelete(ids);
        return Result.success();
    }
}
