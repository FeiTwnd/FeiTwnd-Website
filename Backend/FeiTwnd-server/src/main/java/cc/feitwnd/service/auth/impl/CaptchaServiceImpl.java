package cc.feitwnd.service.auth.impl;

import cc.feitwnd.service.auth.CaptchaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final Map<String, Integer> store = new ConcurrentHashMap<>();

    @Override
    public void put(String captchaId, int result) {
        store.put(captchaId, result);
    }

    @Override
    public boolean verify(String captchaId, int answer) {
        Integer expected = store.remove(captchaId);
        return expected != null && expected == answer;
    }

    @Override
    @Scheduled(fixedRate = 300_000)
    public void cleanExpired() {
        // ConcurrentHashMap 简单清理：超过 1000 条时全清
        // 生产环境可用 TTL 缓存替代，此处保持轻量
        if (store.size() > 1000) {
            store.clear();
        }
    }
}
