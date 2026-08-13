import { Redirect, router } from 'expo-router';
import { useEffect, useState } from 'react';
import { Alert, KeyboardAvoidingView, Platform, Pressable, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import * as authApi from '@/api/auth';
import { Btn, Input, useColors } from '@/components/ui';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing } from '@/constants/theme';
import { ApiError } from '@/lib/api-client';
import { setSession, useSession } from '@/lib/session';
import { clearSession, setAdminId, setToken } from '@/lib/storage';

export default function LoginScreen() {
  const theme = useColors();
  const session = useSession();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [countdown, setCountdown] = useState(0);

  useEffect(() => {
    if (countdown <= 0) return;
    const timer = setTimeout(() => setCountdown((c) => c - 1), 1000);
    return () => clearTimeout(timer);
  }, [countdown]);

  if (session === 'ok') {
    return <Redirect href="/" />;
  }

  const onSendCode = async () => {
    if (!username.trim()) {
      Alert.alert('提示', '请先输入用户名');
      return;
    }
    setSending(true);
    try {
      await authApi.sendCode(username.trim());
      Alert.alert('已发送', '验证码已发送至管理员邮箱');
      setCountdown(60);
    } catch (e) {
      Alert.alert('发送失败', e instanceof ApiError ? e.message : '网络错误，请稍后重试');
    } finally {
      setSending(false);
    }
  };

  const onLogin = async () => {
    if (!username.trim() || !password || !code.trim()) {
      Alert.alert('提示', '请填写用户名、密码和验证码');
      return;
    }
    setLoading(true);
    try {
      const { id, token } = await authApi.login(username.trim(), password, code.trim());
      await setToken(token);
      const profile = await authApi.getProfile();
      if (profile.role === 0) {
        await clearSession();
        throw new ApiError('游客账号不可登录 App，请使用管理员账号');
      }
      await setAdminId(id);
      setSession('ok');
      router.replace('/');
    } catch (e) {
      Alert.alert('登录失败', e instanceof ApiError ? e.message : '网络错误，请检查服务器地址');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : undefined}
          style={styles.safeArea}>
          <View style={styles.brandArea}>
            <ThemedText type="title" style={styles.brand}>FeiTwnd</ThemedText>
            <ThemedText type="smallBold" themeColor="textSecondary">个人博客管理后台</ThemedText>
            <ThemedText type="small" themeColor="textSecondary" style={styles.slogan}>
              内容审核 · 文章管理 · 数据看板
            </ThemedText>
          </View>

          <View style={styles.formArea}>
            <ThemedView type="backgroundElement" style={styles.form}>
              <Input
                label="用户名"
                placeholder="管理员用户名"
                autoCapitalize="none"
                value={username}
                onChangeText={setUsername}
              />
              <Input
                label="密码"
                placeholder="登录密码"
                secureTextEntry
                value={password}
                onChangeText={setPassword}
              />
              <Input
                label="验证码"
                placeholder="邮箱收到的验证码"
                keyboardType="number-pad"
                value={code}
                onChangeText={setCode}
                style={{ paddingRight: 96 }}
              />
              <Pressable
                onPress={onSendCode}
                disabled={sending || countdown > 0}
                style={[styles.codeBtn, { opacity: sending || countdown > 0 ? 0.5 : 1 }]}>
                <Text style={[styles.codeBtnText, { color: theme.text }]}>{countdown > 0 ? `${countdown}s 后重试` : '获取验证码'}</Text>
              </Pressable>
              <Btn label="登录" onPress={onLogin} loading={loading} />
            </ThemedView>
          </View>
        </KeyboardAvoidingView>
      </SafeAreaView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  safeArea: { flex: 1 },
  brandArea: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: Spacing.two,
    paddingHorizontal: Spacing.four,
  },
  brand: {
    fontSize: 44,
    lineHeight: 52,
  },
  slogan: {
    marginTop: Spacing.one,
  },
  formArea: {
    paddingHorizontal: Spacing.three,
    paddingBottom: Spacing.four,
  },
  form: {
    borderRadius: Spacing.three,
    padding: Spacing.four,
    gap: Spacing.three,
  },
  codeBtn: {
    position: 'absolute',
    right: Spacing.four,
    top: 114,
  },
  codeBtnText: {
    fontSize: 14,
    fontWeight: '600',
  },
});
