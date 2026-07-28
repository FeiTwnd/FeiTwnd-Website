package cc.feitwnd.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feitwnd.image.compress")
@Data
public class ImageProperties {
    private boolean enabled;
    private Long maxSizeKb;
    private Double quality;
    private String outPutFormat;
    // 图片最长边像素上限，超过则等比缩小，防止大分辨率图片解码时占用巨量内存
    private Integer maxDimension;
}
