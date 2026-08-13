import { DarkTheme, DefaultTheme, Stack, ThemeProvider } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { useEffect } from 'react';
import { useColorScheme } from 'react-native';

import { Colors } from '@/constants/theme';
import { PendingNotifier } from '@/hooks/pending-notifier';
import { setUnauthorizedHandler } from '@/lib/api-client';
import { setSession, useSession } from '@/lib/session';
import { clearSession, getToken } from '@/lib/storage';

export default function RootLayout() {
  const colorScheme = useColorScheme();
  const colors = Colors[colorScheme === 'dark' ? 'dark' : 'light'];
  const session = useSession();

  useEffect(() => {
    getToken().then((token) => setSession(token ? 'ok' : 'none'));
    setUnauthorizedHandler(() => {
      clearSession();
      setSession('none');
    });
  }, []);

  // 会话初始化完成前不渲染，避免已登录用户短暂闪现登录页（替代原开屏遮罩）
  if (session === 'loading') {
    return null;
  }

  return (
    <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
      <StatusBar style="auto" />
      <PendingNotifier />
      <Stack
        screenOptions={{
          headerShown: false,
          headerStyle: { backgroundColor: colors.background },
          headerTintColor: colors.text,
          headerTitleStyle: { color: colors.text, fontWeight: '600' },
          headerShadowVisible: false,
        }}>
        <Stack.Screen name="(tabs)" />
        <Stack.Screen name="login" />
      </Stack>
    </ThemeProvider>
  );
}
