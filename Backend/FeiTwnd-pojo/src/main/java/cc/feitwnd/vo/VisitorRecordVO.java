package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorRecordVO {
    // 给前端返回访客标识

    // 设备指纹
    private String visitorFingerprint;
    // 当前会话ID
    private String sessionId;
    // 访客在数据库中的ID
    private Long visitorId;
    // 是否是新访客
    private Boolean isNewVisitor;
}
