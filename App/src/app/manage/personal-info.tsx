import { Stack } from 'expo-router';
import { useEffect, useState } from 'react';
import { Alert, ScrollView, StyleSheet, TextInput, View } from 'react-native';

import { getPersonalInfo, updatePersonalInfo } from '@/api/site';
import { ImageField } from '@/components/image-field';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Btn, Input, useColors } from '@/components/ui';
import { Spacing } from '@/constants/theme';
import { ApiError } from '@/lib/api-client';
import type { PersonalInfo } from '@/lib/types';

export default function PersonalInfoScreen() {
  const theme = useColors();
  const [form, setForm] = useState<PersonalInfo | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    getPersonalInfo()
      .then((p) => setForm({ ...p, avatar: p.avatar?.trim() }))
      .catch((e) => Alert.alert('加载失败', e instanceof ApiError ? e.message : '网络错误'));
  }, []);

  const set = (key: keyof PersonalInfo, value: string) =>
    setForm((p) => (p ? ({ ...p, [key]: value } as PersonalInfo) : p));

  const save = async () => {
    const f = form;
    if (!f || !f.nickname?.trim() || !f.tag?.trim()) {
      Alert.alert('提示', '昵称和标签为必填');
      return;
    }
    setSaving(true);
    try {
      // 只提交接口定义的字段，避免把 createTime/updateTime 回传
      await updatePersonalInfo({
        id: f.id,
        nickname: f.nickname,
        tag: f.tag,
        description: f.description,
        avatar: f.avatar,
        website: f.website,
        email: f.email,
        github: f.github,
        location: f.location,
      });
      Alert.alert('保存成功');
    } catch (e) {
      Alert.alert('保存失败', e instanceof ApiError ? e.message : '网络错误');
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: '个人信息' }} />
      <ThemedView style={styles.container}>
        <ScrollView contentContainerStyle={styles.body} keyboardShouldPersistTaps="handled">
          <ImageField label="头像" value={form?.avatar} onChange={(v) => set('avatar', v)} />
          <Input label="昵称" value={form?.nickname ?? ''} onChangeText={(v) => set('nickname', v)} />
          <Input label="标签" value={form?.tag ?? ''} onChangeText={(v) => set('tag', v)} />
          <View style={styles.fieldCol}>
            <ThemedText type="small" themeColor="textSecondary">个人简介</ThemedText>
            <TextInput
              multiline
              placeholder="一句话介绍自己"
              placeholderTextColor={theme.textSecondary}
              value={form?.description ?? ''}
              onChangeText={(v) => set('description', v)}
              style={[styles.textarea, { backgroundColor: theme.backgroundElement, color: theme.text }]}
            />
          </View>
          <Input label="个人网站" value={form?.website ?? ''} onChangeText={(v) => set('website', v)} />
          <Input label="邮箱" value={form?.email ?? ''} onChangeText={(v) => set('email', v)} />
          <Input label="GitHub" value={form?.github ?? ''} onChangeText={(v) => set('github', v)} />
          <Input label="所在地" value={form?.location ?? ''} onChangeText={(v) => set('location', v)} />
          <Btn label="保存" onPress={save} loading={saving} />
        </ScrollView>
      </ThemedView>
    </>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  body: { padding: Spacing.three, gap: Spacing.three, paddingBottom: Spacing.five },
  fieldCol: { gap: Spacing.one },
  textarea: {
    minHeight: 80,
    borderRadius: Spacing.two,
    padding: Spacing.three,
    fontSize: 15,
    textAlignVertical: 'top',
  },
});
