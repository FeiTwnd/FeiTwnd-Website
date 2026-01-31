package cc.feitwnd.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feitwnd.alioss")
@Data
public class AliOssProperties {
    /**
     * 阿里云 Endpoint
     */
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}
