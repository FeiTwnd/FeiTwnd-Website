package cc.feitwnd.utils;

import cc.feitwnd.properties.ImageProperties;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
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
        // 只读取一次字节，避免同一张图被多次加载进内存
        byte[] originalBytes = file.getBytes();
        String originalName = file.getOriginalFilename();

        // 如果不需要压缩，直接返回原文件字节
        if (!shouldCompress(originalName, originalBytes)) {
            return originalBytes;
        }

        long originalSize = originalBytes.length;
        log.info("开始压缩: {} ({}KB)", originalName, originalSize / 1024);

        // 解码为受控分辨率的图片：超过最长边上限时在解码阶段就按倍数下采样，
        // 直接降低解码内存峰值，防止超大分辨率图片撑爆堆内存
        BufferedImage image = decodeWithLimit(originalBytes);

        // 复用同一张已解码的图片反复编码，而非每次重新解码原始字节；
        // 每次都是从相同像素源单次编码，不会造成累积的二次有损压缩
        double currentQuality = imageProperties.getQuality();
        byte[] compressedBytes = encode(image, currentQuality);

        int attempts = 0;
        while (isOversized(compressedBytes) && attempts < 10) {
            currentQuality = Math.max(0.3, currentQuality - 0.05);
            compressedBytes = encode(image, currentQuality);
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
     * 解码图片，超过最长边上限时在解码阶段按整数倍下采样，降低内存峰值
     */
    private BufferedImage decodeWithLimit(byte[] inputBytes) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(inputBytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                throw new IOException("不支持的图片格式，无法解码");
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(iis);
                int longest = Math.max(reader.getWidth(0), reader.getHeight(0));

                javax.imageio.ImageReadParam param = reader.getDefaultReadParam();
                Integer maxDimension = imageProperties.getMaxDimension();
                if (maxDimension != null && maxDimension > 0 && longest > maxDimension) {
                    int sample = (int) Math.ceil((double) longest / maxDimension);
                    param.setSourceSubsampling(sample, sample, 0, 0);
                }
                return reader.read(0, param);
            } finally {
                reader.dispose();
            }
        }
    }

    /**
     * 使用指定质量编码图片
     */
    private byte[] encode(BufferedImage image, double quality) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(image)
                    .scale(1.0)  // 尺寸已在解码阶段控制
                    .imageType(BufferedImage.TYPE_INT_RGB) // 强制标准RGB，防止WebP色彩空间转换偏绿
                    .outputFormat(imageProperties.getOutPutFormat())
                    .outputQuality(quality)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }

    private boolean shouldCompress(String originalName, byte[] bytes) {
        // 检查是否开启图片压缩
        if (!imageProperties.isEnabled()) {
            return false;
        }
        // 检查文件类型
        if (originalName == null) {
            return false;
        }
        String extension = getFileExtension(originalName).toLowerCase();
        if (!SUPPORTED_FORMATS.contains(extension)) {
            return false;
        }

        // 检查文件大小, 如果没超过限制，不压缩
        if (!isOversized(bytes)) {
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
