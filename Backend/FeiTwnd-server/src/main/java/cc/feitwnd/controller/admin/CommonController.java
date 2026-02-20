package cc.feitwnd.controller.admin;

import cc.feitwnd.result.Result;
import cc.feitwnd.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
     */
    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file){
        log.info("文件上传：{}",file);
        String fileUrl = commonService.uploadFile(file);
        return Result.success(fileUrl);
    }
}
