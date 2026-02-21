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
     * 异步处理：IP地理位置查询 + 访客地理信息更新 + 浏览记录写入
     * 接收 visitorId 而非 Visitors 对象引用，避免主线程与异步线程共享可变对象导致竞态条件
     */
    @Async("taskExecutor")
    public void processGeoAndRecordViewAsync(Long visitorId, String ip, String userAgent,
                                              String pagePath, String referer, String pageTitle) {
        try {
            // 耗时操作：IP地理位置查询
            Map<String, String> geoInfo = IpUtil.getGeoInfo(ip);

            String country = geoInfo.get("country");
            String province = geoInfo.get("province");
            String city = geoInfo.get("city");

            // 仅在地理位置有效时更新
            if (country != null && !country.isEmpty()) {
                // 从数据库重新读取访客记录，确保拿到最新数据
                Visitors current = visitorMapper.findById(visitorId);
                if (current != null) {
                    boolean geoChanged = !equalsNullSafe(current.getCountry(), country)
                            || !equalsNullSafe(current.getProvince(), province)
                            || !equalsNullSafe(current.getCity(), city);

                    if (geoChanged) {
                        // 仅更新地理位置字段，避免与主线程的访问计数产生竞态
                        Visitors geoUpdate = Visitors.builder()
                                .id(visitorId)
                                .country(country)
                                .province(province)
                                .city(city)
                                .longitude(geoInfo.get("longitude"))
                                .latitude(geoInfo.get("latitude"))
                                .build();
                        visitorMapper.updateById(geoUpdate);
                    }
                }
            }

            // 写入浏览记录
            Views view = Views.builder()
                    .visitorId(visitorId)
                    .pagePath(pagePath)
                    .referer(referer)
                    .pageTitle(pageTitle)
                    .ipAddress(ip)
                    .userAgent(userAgent)
                    .viewTime(LocalDateTime.now())
                    .build();
            viewMapper.insert(view);

            log.debug("异步处理访客记录完成: visitorId={}, ip={}", visitorId, ip);
        } catch (Exception e) {
            log.error("异步处理访客记录失败: visitorId={}, ip={}, ex={}", visitorId, ip, e.getMessage());
        }
    }

    private boolean equalsNullSafe(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}
