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
    /**
     * CDN 加速域名（可选）：配置后上传返回的 URL 使用该域名，配合私有 Bucket + CDN 私有回源防盗刷
     */
    private String cdnDomain;
}
