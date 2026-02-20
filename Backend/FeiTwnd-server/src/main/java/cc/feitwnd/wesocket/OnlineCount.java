package cc.feitwnd.wesocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在线人数统计
 */
@Slf4j
@Component
@ServerEndpoint("/ws/online")
public class OnlineCount {

    // 存放所有连接的会话
    private static final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    // 在线人数计数器
    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 连接建立
     */
    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        int count = onlineCount.incrementAndGet();

        log.info("新连接: {}, 当前在线: {} 人", session.getId(), count);

        // 发送当前在线人数给新用户
        sendMessage(session, String.valueOf(count));

        // 广播更新给所有用户
        broadcastCount();
    }

    /**
     * 收到消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 如果是心跳消息，回复确认
        if ("ping".equals(message)) {
            sendMessage(session, "pong");
        }
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
        int count = onlineCount.decrementAndGet();

        log.info("连接关闭: {}, 当前在线: {} 人", session.getId(), count);

        // 广播更新
        broadcastCount();
    }

    /**
     * 连接出错
     */
    @OnError
    public void onError(Session session, Throwable error) {
        sessions.remove(session.getId());
        log.debug("WebSocket 连接异常: {}", session.getId());
    }

    /**
     * 发送消息给单个用户（使用 getAsyncRemote 避免并发写冲突）
     */
    private void sendMessage(Session session, String message) {
        try {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        } catch (Exception e) {
            log.debug("发送消息失败: {}", session.getId());
        }
    }

    /**
     * 广播在线人数给所有用户
     */
    private void broadcastCount() {
        String message = String.valueOf(onlineCount.get());

        sessions.forEach((id, session) -> {
            try {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                log.debug("广播消息失败: {}", id);
            }
        });
    }
}
