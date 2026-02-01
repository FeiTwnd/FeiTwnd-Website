package cc.feitwnd.service;

import org.springframework.web.multipart.MultipartFile;

public interface CommonService {
    /**
     * 文件上传
     * @param file 文件
     */
    String uploadFile(MultipartFile file);
}
