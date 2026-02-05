package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorRecordDTO {

    // 访问路径
    private String pagePath;
    // 页面标题
    private String pageTitle;
    // 来源页面
    private String referer;

    // 前端收集浏览器信息

    // 屏幕分辨率 "1920x1080"
    private String screen;
    // 时区 "Asia/Shanghai"
    private String timezone;
    // 语言 "zh-CN"
    private String language;
    // 平台 "Win32"
    private String platform;
    // 是否支持Cookie
    private Boolean cookiesEnabled;

    // 设备信息

    // 设备内存
    private Integer deviceMemory;
    // CPU核心数
    private Integer hardwareConcurrency;
}
