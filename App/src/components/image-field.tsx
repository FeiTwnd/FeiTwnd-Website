import * as ImagePicker from 'expo-image-picker';
import { useState } from 'react';
import { ActivityIndicator, Alert, Image, Pressable, StyleSheet, View } from 'react-native';

import { uploadImage } from '@/api/common';
import { ThemedText } from '@/components/themed-text';
import { useColors } from '@/components/ui';
import { Spacing } from '@/constants/theme';

export function ImageField({ label, value, onChange }: {
  label?: string;
  value?: string;
  onChange: (url: string) => void;
}) {
  const theme = useColors();
  const [uploading, setUploading] = useState(false);
  // 老数据可能带首尾空白/换行（如头像 "\nhttps://…"），RN Image 不会自动 trim，这里规范化后再渲染
  const src = value?.trim() || '';

  const pick = async () => {
    const permission = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (!permission.granted) {
      Alert.alert('需要相册权限', '请在系统设置中允许访问相册后重试');
      return;
    }
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ['images'],
      quality: 0.8,
    });
    if (result.canceled || !result.assets?.[0]) return;

    const asset = result.assets[0];
    setUploading(true);
    try {
      const url = await uploadImage(
        asset.uri,
        asset.fileName ?? 'upload.jpg',
        asset.mimeType ?? 'image/jpeg'
      );
      onChange(url);
    } catch (e) {
      Alert.alert('上传失败', e instanceof Error ? e.message : '网络错误');
    } finally {
      setUploading(false);
    }
  };

  return (
    <View style={styles.wrap}>
      {label ? <ThemedText type="small" themeColor="textSecondary">{label}</ThemedText> : null}
      <Pressable
        onPress={pick}
        disabled={uploading}
        style={[styles.box, { backgroundColor: theme.backgroundElement }]}>
        {uploading ? (
          <ActivityIndicator color={theme.textSecondary} />
        ) : src ? (
          <Image source={{ uri: src }} style={styles.preview} />
        ) : (
          <ThemedText type="small" themeColor="textSecondary">选择图片</ThemedText>
        )}
      </Pressable>
      {src ? (
        <Pressable onPress={() => onChange('')} hitSlop={8}>
          <ThemedText type="small" themeColor="textSecondary">移除图片</ThemedText>
        </Pressable>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { gap: Spacing.one },
  box: {
    width: 96,
    height: 96,
    borderRadius: Spacing.two,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
  },
  preview: { width: '100%', height: '100%' },
});
