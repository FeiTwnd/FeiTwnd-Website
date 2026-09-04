package cc.feitwnd.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    /** CDN 加速域名（可选）：配置后返回的访问 URL 使用 CDN 域名而非 OSS 直链，防盗刷需配合私有 Bucket + CDN 私有回源 */
    private String cdnDomain;

    /**
     * 文件上传
     * @param bytes 文件字节数组
     * @param extension 文件后缀
     * @param fileName 文件名
     * @return
     */
    public String upload(byte[] bytes, String extension, String fileName) {

        String objectName = getFileCategory(extension) + "/" + fileName;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            // 上传失败必须向上抛，绝不能静默后返回假 URL，否则前端会持久化一个不存在的链接
            log.error("OSS 上传失败（服务端拒绝）: objectName={}, code={}, message={}, requestId={}",
                    objectName, oe.getErrorCode(), oe.getErrorMessage(), oe.getRequestId());
            throw new RuntimeException("OSS 上传失败: " + oe.getErrorMessage(), oe);
        } catch (ClientException ce) {
            log.error("OSS 上传失败（客户端异常）: objectName={}, message={}",
                    objectName, ce.getMessage());
            throw new RuntimeException("OSS 上传失败: " + ce.getMessage(), ce);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 访问 URL：优先使用 CDN 域名（防盗刷场景，浏览器只接触 CDN），否则退回 OSS 直链
        StringBuilder stringBuilder = new StringBuilder("https://");
        if (cdnDomain != null && !cdnDomain.isBlank()) {
            stringBuilder.append(cdnDomain);
        } else {
            stringBuilder.append(bucketName).append(".").append(endpoint);
        }
        stringBuilder.append("/").append(objectName);

        log.info("文件上传到:{}", stringBuilder.toString());

        return stringBuilder.toString();
    }

    /**
     * 获取文件分类
     * @param extension
     * @return
     */
    public String getFileCategory(String extension) {
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
