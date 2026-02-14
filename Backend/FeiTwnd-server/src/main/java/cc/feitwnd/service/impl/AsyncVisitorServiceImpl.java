package cc.feitwnd.service.impl;

import cc.feitwnd.entity.Views;
import cc.feitwnd.entity.Visitors;
import cc.feitwnd.mapper.ViewMapper;
import cc.feitwnd.mapper.VisitorMapper;
import cc.feitwnd.service.AsyncVisitorService;
import cc.feitwnd.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 异步访客服务实现（地理位置查询和浏览记录写入异步化）
 */
@Service
@Slf4j
public class AsyncVisitorServiceImpl implements AsyncVisitorService {

    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private ViewMapper viewMapper;

    /**
     * 异步处理：IP地理位置查询 + 访客信息更新 + 浏览记录写入
     */
    @Async("taskExecutor")
    public void processGeoAndRecordViewAsync(Visitors visitor, String ip, String userAgent,
                                              String pagePath, String referer, String pageTitle) {
        try {
            // 耗时操作：IP地理位置查询
            Map<String, String> geoInfo = IpUtil.getGeoInfo(ip);

            // 更新访客地理位置信息
            boolean geoChanged = !equalsNullSafe(visitor.getCountry(), geoInfo.get("country"))
                    || !equalsNullSafe(visitor.getProvince(), geoInfo.get("province"))
                    || !equalsNullSafe(visitor.getCity(), geoInfo.get("city"));

            if (geoChanged) {
                visitor.setCountry(geoInfo.get("country"));
                visitor.setProvince(geoInfo.get("province"));
                visitor.setCity(geoInfo.get("city"));
                visitor.setLongitude(geoInfo.get("longitude"));
                visitor.setLatitude(geoInfo.get("latitude"));
                visitorMapper.updateById(visitor);
            }

            // 写入浏览记录
            Views view = Views.builder()
                    .visitorId(visitor.getId())
                    .pagePath(pagePath)
                    .referer(referer)
                    .pageTitle(pageTitle)
                    .ipAddress(ip)
                    .userAgent(userAgent)
                    .viewTime(LocalDateTime.now())
                    .build();
            viewMapper.insert(view);

            log.debug("异步处理访客记录完成: visitorId={}, ip={}", visitor.getId(), ip);
        } catch (Exception e) {
            log.error("异步处理访客记录失败: visitorId={}, ip={}, ex={}", visitor.getId(), ip, e.getMessage());
        }
    }

    private boolean equalsNullSafe(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}
