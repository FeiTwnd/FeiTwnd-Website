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
}
