package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.RateLimit;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.system.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

/**
 * 管理端通用接口
 */
@RestController("adminCommonController")
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private CommonService commonService;

    /**
     * 文件上传
     * 上传是写 OSS 的唯一入口，叠加全局维度限流（分钟级），防止盗用凭据后刷存储与流量
     */
    @PostMapping("/upload")
    @RateLimit(type = RateLimit.Type.ENDPOINT, tokens = 20, burstCapacity = 40,
            timeWindow = 1, timeUnit = TimeUnit.MINUTES, message = "上传过于频繁，请稍后再试")
    public Result uploadFile(MultipartFile file){
        log.info("文件上传：{}",file);
        String fileUrl = commonService.uploadFile(file);
        return Result.success(fileUrl);
    }
}
