package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.exception.UploadFileErrorException;
import cc.feitwnd.service.CommonService;
import cc.feitwnd.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     */
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new UploadFileErrorException(MessageConstant.FILE_EMPTY);
        }
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件后缀
            String extension = fileName.substring(fileName.lastIndexOf(".")+1);

            // 获取uuid文件名
            String uuidFileName = UUID.randomUUID() + "." + extension;
            // 上传文件
            String fileUrl =aliOssUtil.upload(file.getBytes(),extension,uuidFileName);

            return fileUrl;

        } catch (IOException e) {
            throw new UploadFileErrorException(MessageConstant.UPLOAD_FAILED);
        }
    }


}
