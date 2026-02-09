package cc.feitwnd.utils;

import cc.feitwnd.properties.ImageProperties;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 图片压缩工具类
 */
@Component
@Slf4j
public class ImageCompressUtil {

    @Autowired
    private ImageProperties imageProperties;

    // 支持的图片格式
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "tif"
    );

    /**
     * 图片压缩
     * @param file
     * @return
     */
    public byte[] compress(MultipartFile file) throws IOException {
        // 如果不需要压缩，直接返回原文件字节
        if(!shouldCompress(file)){
           return file.getBytes();
        }

        // 记录原文件信息
        long originalSize = file.getSize();
        String originalName = file.getOriginalFilename();

        log.info("开始压缩: {} ({}KB)", originalName, originalSize / 1024);

        // 第一次压缩
        byte[] compressedBytes = compressWithQuality(file.getBytes(), imageProperties.getQuality());

        // 如果还是太大，递归压缩
        int attempts = 0;
        double currentQuality = imageProperties.getQuality();

        while (isOversized(compressedBytes) && attempts < 10) {
            // 逐步降低质量
            currentQuality = Math.max(0.3, currentQuality - 0.05);
            compressedBytes = compressWithQuality(compressedBytes, currentQuality);
            attempts++;
        }

        // 记录压缩后信息
        long compressedSize = compressedBytes.length;
        double ratio = 1.0 - (double) compressedSize / originalSize;

        log.info("压缩完成: {} ({}KB -> {}KB, 压缩率: {}, 质量: {})",
                originalName,
                originalSize / 1024,
                compressedSize / 1024,
                String.format("%.2f",ratio),
                String.format("%.2f", currentQuality));

        return compressedBytes;
    }

    /**
     * 使用指定质量压缩图片
     */
    private byte[] compressWithQuality(byte[] inputBytes, double quality) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Thumbnails.of(inputStream)
                    .scale(1.0)  // 保持原尺寸
                    .outputFormat(imageProperties.getOutPutFormat())
                    .outputQuality(quality)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }

    private boolean shouldCompress(MultipartFile file) throws IOException {
        // 检查是否开启图片压缩
        if(!imageProperties.isEnabled()){
            return false;
        }
        // 检查文件类型
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            return false;
        }
        String extension = getFileExtension(originalName).toLowerCase();
        if (!SUPPORTED_FORMATS.contains(extension)) {
            return false;
        }

        // 检查文件大小, 如果没超过限制，不压缩
        if(!isOversized(file.getBytes())){
            return false;
        }

        return true;
    }

    /**
     * 检查是否超过限制大小
     */
    private boolean isOversized(byte[] data) {
        int sizeKb = data.length / 1024;
        return sizeKb > imageProperties.getMaxSizeKb();
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}
