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
            String fileCategory = getFileCategory(extension);

            // 获取uuid文件名
            String uuidFileName = UUID.randomUUID() + "." + extension;
            String fileUrl =aliOssUtil.upload(file.getBytes(),fileCategory,uuidFileName);

            return fileUrl;

        } catch (IOException e) {
            throw new UploadFileErrorException(MessageConstant.UPLOAD_FAILED);
        }
    }

    /**
     * 获取文件分类
     * @param extension
     * @return
     */
    private String getFileCategory(String extension) {
        switch (extension){
            // 图片
            case "jpg":
            case "png":
            case "gif":
            case "bmp":
            case "webp":
            case "jpeg":
            case "svg":
            case "ico":
            case "tiff":
                return "image";

            // 视频
            case "mp4":
            case "avi":
            case "mov":
            case "mkv":
            case "wmv":
            case "flv":
            case "webm":
            case "m4v":
            case "3gp":
                return "video";

            // 音频
            case "mp3":
            case "wav":
            case "wma":
            case "ogg":
            case "aac":
            case "flac":
            case "m4a":
            case "ape":
            case "mid":
            case "midi":
                return "audio";

            // 歌词
            case "lrc":
            case "lrcx":
            case "krc":
            case "qrc":
            case "trc":
            case "ksc":
                return "lyric";

            // 文档
            case "txt":
            case "md":
            case "rtf":
                return "text";

            case "pdf":
                return "pdf";

            case "doc":
            case "docx":
            case "dot":
            case "dotx":
                return "word";

            case "xls":
            case "xlsx":
            case "xlt":
            case "xltx":
                return "excel";

            // 压缩文件
            case "zip":
            case "rar":
            case "7z":
            case "tar":
            case "gz":
            case "bz2":
                return "archive";

            // 字体
            case "ttf":
            case "otf":
            case "woff":
            case "woff2":
            case "eot":
                return "font";

            default:
                return "other";
        }
    }
}
