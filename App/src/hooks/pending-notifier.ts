import Constants from 'expo-constants';
import { useEffect, useRef, useState } from 'react';
import { AppState } from 'react-native';

import { getOverview } from '@/api/report';
import { useSession } from '@/lib/session';

const POLL_INTERVAL_MS = 60000;
type NotificationsModule = typeof import('expo-notifications');

/**
 * 前台轮询待审核数，数量增长时发本地通知。
 * 冷启动不通知（基线归零），仅通知"变化量"避免打扰。
 */
export function PendingNotifier() {
  const session = useSession();
  const [notificationsEnabled, setNotificationsEnabled] = useState(false);
  const notifications = useRef<NotificationsModule | null>(null);
  const baseline = useRef(0);

  useEffect(() => {
    if (session !== 'ok' || Constants.expoGoConfig) return;

    let active = true;
    void import('expo-notifications')
      .then((module) => {
        if (!active) return;
        notifications.current = module;
        module.setNotificationHandler({
          handleNotification: async () => ({
            shouldShowBanner: true,
            shouldShowList: true,
            shouldPlaySound: false,
            shouldSetBadge: false,
          }),
        });
        return module.requestPermissionsAsync();
      })
      .then((permission) => {
        if (active && permission) setNotificationsEnabled(permission.status === 'granted');
      })
      .catch(() => {});

    return () => {
      active = false;
    };
  }, [session]);

  useEffect(() => {
    const notificationModule = notifications.current;
    if (session !== 'ok' || !notificationModule || !notificationsEnabled) return;

    const check = () => {
      getOverview()
        .then((ov) => {
          const total = ov.pendingCommentCount + ov.pendingMessageCount;
          const prev = baseline.current;
          baseline.current = total;
          if (prev > 0 && total > prev) {
            notificationModule.scheduleNotificationAsync({
              content: {
                title: '有新的待审核内容',
                body: `新增 ${total - prev} 条评论/留言待审核`,
              },
              trigger: null,
            }).catch(() => {});
          }
        })
        .catch(() => {});
    };

    check();
    const timer = setInterval(check, POLL_INTERVAL_MS);

    const sub = AppState.addEventListener('change', (state) => {
      if (state === 'active') check();
    });

    return () => {
      clearInterval(timer);
      sub.remove();
    };
  }, [notificationsEnabled, session]);

  return null;
}
