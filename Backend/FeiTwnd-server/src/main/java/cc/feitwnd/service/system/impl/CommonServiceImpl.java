package cc.feitwnd.service.system.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.exception.UploadFileErrorException;
import cc.feitwnd.properties.ImageProperties;
import cc.feitwnd.service.system.CommonService;
import cc.feitwnd.utils.AliOssUtil;
import cc.feitwnd.utils.ImageCompressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

/**
 * 通用文件上传服务实现
 *
 * 上传接口是写 OSS 的唯一入口，若被滥用会直接造成存储与流量损失。
 * 因此这里做三层防线：扩展名白名单 + 大小上限 + 图片内容魔数校验，
 * 防止上传恶意/超大文件刷 OSS 流量。
 */
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    /** 允许上传的扩展名白名单（仅站点实际会用到：图片、音乐、歌词、文本） */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            // 图片
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "tif", "svg",
            // 音乐
            "mp3", "wav", "wma", "ogg", "aac", "flac", "m4a", "ape", "mid", "midi",
            // 歌词 / 文本
            "lrc", "txt"
    );

    /** 图片扩展名（需做内容魔数校验；ico 无法用 ImageIO 解码故不放行） */
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "tif", "svg"
    );

    /** 单个文件大小上限（字节）。音乐 3-5MB 常见，15MB 兼顾正常资源与防刷 */
    private static final long MAX_FILE_SIZE = 15L * 1024 * 1024;

    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private ImageCompressUtil imageCompressUtil;
    @Autowired
    private ImageProperties imageProperties;

    /**
     * 文件上传
     * @param file 待上传文件
     * @return 可公开访问的文件 URL
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new UploadFileErrorException(MessageConstant.FILE_EMPTY);
        }
        // 大小上限：防止恶意上传超大文件刷 OSS 存储与流量
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new UploadFileErrorException("文件过大，单文件不能超过 15MB");
        }
        try {
            String fileName = file.getOriginalFilename();
            String extension = fileName == null ? ""
                    : fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            // 白名单：拒绝未知扩展名与无扩展名文件
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new UploadFileErrorException("不支持的文件类型：" + (extension.isEmpty() ? "无扩展名" : extension));
            }

            // 图片先做真实内容校验（防伪装扩展名上传恶意内容），再压缩上传
            byte[] uploadBytes = file.getBytes();
            if (IMAGE_EXTENSIONS.contains(extension)) {
                if (!isRealImage(extension, uploadBytes)) {
                    throw new UploadFileErrorException("图片内容校验失败，请上传真实图片文件");
                }
                // 压缩后覆盖为压缩产物（格式转为配置的输出格式），上传压缩后的字节
                uploadBytes = imageCompressUtil.compress(file);
                extension = imageProperties.getOutPutFormat();
            }

            String uuidFileName = UUID.randomUUID() + "." + extension;
            return aliOssUtil.upload(uploadBytes, extension, uuidFileName);
        } catch (IOException e) {
            log.error("文件读取失败: ex={}", e.getMessage());
            throw new UploadFileErrorException(MessageConstant.UPLOAD_FAILED);
        } catch (RuntimeException e) {
            // OSS 上传失败等运行时异常：转业务异常返回给前端，避免 500
            log.error("文件上传失败: ex={}", e.getMessage());
            throw new UploadFileErrorException(MessageConstant.UPLOAD_FAILED);
        }
    }

    /**
     * 校验字节内容是否为真实图片，防止把 HTML/可执行文件改名成图片扩展名上传。
     * svg 是 XML 文本无法用 ImageIO 解码，单独校验是否包含 svg 根标签；
     * 其余位图格式用 ImageIO 实际解码验证。
     * @param extension 扩展名（已小写）
     * @param bytes 文件字节
     * @return 是否为真实图片内容
     */
    private boolean isRealImage(String extension, byte[] bytes) {
        try {
            if ("svg".equals(extension)) {
                String head = new String(bytes, 0, Math.min(bytes.length, 512), StandardCharsets.UTF_8);
                return head.contains("<svg");
            }
            return ImageIO.read(new ByteArrayInputStream(bytes)) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
