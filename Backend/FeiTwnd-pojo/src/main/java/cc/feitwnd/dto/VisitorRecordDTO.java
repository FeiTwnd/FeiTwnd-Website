package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访客访问记录DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorRecordDTO {

    // 访问路径
    @NotBlank(message = "访问路径不能为空")
    @Size(max = 500, message = "访问路径过长")
    private String pagePath;

    // 页面标题
    @Size(max = 200, message = "页面标题过长")
    private String pageTitle;

    // 来源页面
    @Size(max = 500, message = "来源页面过长")
    private String referer;

    // 前端收集浏览器信息,用于制作访客指纹

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
