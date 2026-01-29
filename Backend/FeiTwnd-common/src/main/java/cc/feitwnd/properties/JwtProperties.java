package cc.feitwnd.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feitwnd.jwt")
@Data
public class JwtProperties {
    /**
     * jwt令牌相关配置
     */
    private String secretKey;
    private String ttl;
    private String tokenName;
}
