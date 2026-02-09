package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.exception.UploadFileErrorException;
import cc.feitwnd.properties.ImageProperties;
import cc.feitwnd.service.CommonService;
import cc.feitwnd.utils.AliOssUtil;
import cc.feitwnd.utils.ImageCompressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private ImageCompressUtil imageCompressUtil;
    @Autowired
    private ImageProperties imageProperties;

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
            // 获取文件字节数组
            byte[] bytes = file.getBytes();

            // 如果是图片，先压缩再上传
            if(aliOssUtil.getFileCategory(extension)=="image"){
                bytes = imageCompressUtil.compress(file);
                extension = imageProperties.getOutPutFormat();
            }

            // 获取uuid文件名
            String uuidFileName = UUID.randomUUID()+"."+extension;
            // 上传文件
            String fileUrl =aliOssUtil.upload(bytes,extension,uuidFileName);

            return fileUrl;

        } catch (IOException e) {
            throw new UploadFileErrorException(MessageConstant.UPLOAD_FAILED);
        }
    }
}
