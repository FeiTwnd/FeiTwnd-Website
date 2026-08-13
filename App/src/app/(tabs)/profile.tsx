import { router, type Href } from 'expo-router'
import { useEffect, useState } from 'react'
import { Alert, Modal, Pressable, StyleSheet, View } from 'react-native'

import { changeNickname, changePassword, getProfile, logout } from '@/api/auth'
import { ThemedText } from '@/components/themed-text'
import {
  Btn,
  Card,
  Input,
  Screen,
  SectionTitle,
  useColors,
} from '@/components/ui'
import { Spacing } from '@/constants/theme'
import { ApiError } from '@/lib/api-client'
import { setSession } from '@/lib/session'
import { clearSession, getAdminId, getToken } from '@/lib/storage'
import type { AdminVO } from '@/lib/types'

const MENU: { title: string; items: { label: string; route: Href }[] }[] = [
  {
    title: '个人主页',
    items: [{ label: '个人信息', route: '/manage/personal-info' }],
  },
  {
    title: '内容管理',
    items: [
      { label: '社交媒体', route: '/manage/social-media' },
      { label: '经历', route: '/manage/experience' },
      { label: '技能', route: '/manage/skill' },
      { label: '友链', route: '/manage/friend-link' },
    ],
  },
  {
    title: '站点资源',
    items: [
      { label: '音乐', route: '/manage/music' },
      { label: '文章分类', route: '/manage/category' },
      { label: '文章标签', route: '/manage/tag' },
      { label: '城市足迹', route: '/manage/footprint' },
    ],
  },
]

function MenuRow({ label, onPress }: { label: string; onPress: () => void }) {
  return (
    <Pressable
      onPress={onPress}
      style={({ pressed }) => [styles.menuRow, pressed && { opacity: 0.6 }]}
    >
      <ThemedText type="small">{label}</ThemedText>
      <ThemedText type="small" themeColor="textSecondary">
        ›
      </ThemedText>
    </Pressable>
  )
}

export default function ProfileScreen() {
  const theme = useColors()
  const [profile, setProfile] = useState<AdminVO | null>(null)
  const [showPwd, setShowPwd] = useState(false)
  const [showNickname, setShowNickname] = useState(false)
  const [oldPwd, setOldPwd] = useState('')
  const [newPwd, setNewPwd] = useState('')
  const [confirmPwd, setConfirmPwd] = useState('')
  const [nickname, setNickname] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    getProfile()
      .then((p) => {
        setProfile(p)
        setNickname(p.nickname || '')
      })
      .catch((e) =>
        Alert.alert('加载失败', e instanceof ApiError ? e.message : '网络错误'),
      )
  }, [])

  const onSavePassword = async () => {
    if (!oldPwd || !newPwd || !confirmPwd) {
      Alert.alert('提示', '请填写完整')
      return
    }
    if (newPwd !== confirmPwd) {
      Alert.alert('提示', '两次输入的新密码不一致')
      return
    }
    setSaving(true)
    try {
      await changePassword(oldPwd, newPwd, confirmPwd)
      setShowPwd(false)
      setOldPwd('')
      setNewPwd('')
      setConfirmPwd('')
      Alert.alert('修改成功', '密码已更新')
    } catch (e) {
      Alert.alert('修改失败', e instanceof ApiError ? e.message : '网络错误')
    } finally {
      setSaving(false)
    }
  }

  const onSaveNickname = async () => {
    if (!nickname.trim()) {
      Alert.alert('提示', '昵称不能为空')
      return
    }
    setSaving(true)
    try {
      await changeNickname(nickname.trim())
      setProfile((p) => (p ? { ...p, nickname: nickname.trim() } : p))
      setShowNickname(false)
    } catch (e) {
      Alert.alert('修改失败', e instanceof ApiError ? e.message : '网络错误')
    } finally {
      setSaving(false)
    }
  }

  const onLogout = () => {
    Alert.alert('确认退出', '确定退出登录吗？', [
      { text: '取消', style: 'cancel' },
      {
        text: '退出',
        style: 'destructive',
        onPress: async () => {
          const [id, token] = await Promise.all([getAdminId(), getToken()])
          if (id && token) {
            logout(Number(id), token).catch(() => {})
          }
          await clearSession()
          setSession('none')
          router.replace('/login')
        },
      },
    ])
  }

  return (
    <Screen>
      <SectionTitle>账号信息</SectionTitle>
      <Card>
        <View style={styles.row}>
          <ThemedText type="smallBold">{profile?.nickname || '…'}</ThemedText>
        </View>
        <ThemedText type="small" themeColor="textSecondary">
          邮箱：{profile?.email || '…'}
        </ThemedText>
        <View style={styles.actions}>
          <Btn
            label="修改昵称"
            variant="ghost"
            onPress={() => setShowNickname(true)}
            style={styles.actionBtn}
          />
          <Btn
            label="修改密码"
            variant="ghost"
            onPress={() => setShowPwd(true)}
            style={styles.actionBtn}
          />
        </View>
      </Card>

      {MENU.map((group) => (
        <View key={group.title} style={styles.group}>
          <SectionTitle>{group.title}</SectionTitle>
          <Card>
            {group.items.map((it, idx) => (
              <View key={it.label}>
                <MenuRow
                  label={it.label}
                  onPress={() => router.push(it.route)}
                />
                {idx < group.items.length - 1 ? (
                  <View
                    style={[
                      styles.divider,
                      { backgroundColor: theme.backgroundSelected },
                    ]}
                  />
                ) : null}
              </View>
            ))}
          </Card>
        </View>
      ))}

      <Btn
        label="退出登录"
        variant="danger"
        onPress={onLogout}
        style={styles.logout}
      />

      <Modal visible={showPwd} transparent animationType="fade">
        <Pressable style={styles.modalMask} onPress={() => setShowPwd(false)}>
          <Pressable
            style={[styles.modalCard, { backgroundColor: theme.background }]}
            onPress={(e) => e.stopPropagation()}
          >
            <ThemedText type="smallBold">修改密码</ThemedText>
            <Input
              label="旧密码"
              secureTextEntry
              value={oldPwd}
              onChangeText={setOldPwd}
            />
            <Input
              label="新密码（6-32位）"
              secureTextEntry
              value={newPwd}
              onChangeText={setNewPwd}
            />
            <Input
              label="确认新密码"
              secureTextEntry
              value={confirmPwd}
              onChangeText={setConfirmPwd}
            />
            <View style={styles.modalActions}>
              <Btn
                label="取消"
                variant="ghost"
                onPress={() => setShowPwd(false)}
              />
              <Btn label="保存" onPress={onSavePassword} loading={saving} />
            </View>
          </Pressable>
        </Pressable>
      </Modal>

      <Modal visible={showNickname} transparent animationType="fade">
        <Pressable
          style={styles.modalMask}
          onPress={() => setShowNickname(false)}
        >
          <Pressable
            style={[styles.modalCard, { backgroundColor: theme.background }]}
            onPress={(e) => e.stopPropagation()}
          >
            <ThemedText type="smallBold">修改昵称</ThemedText>
            <Input label="昵称" value={nickname} onChangeText={setNickname} />
            <View style={styles.modalActions}>
              <Btn
                label="取消"
                variant="ghost"
                onPress={() => setShowNickname(false)}
              />
              <Btn label="保存" onPress={onSaveNickname} loading={saving} />
            </View>
          </Pressable>
        </Pressable>
      </Modal>
    </Screen>
  )
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  actions: {
    flexDirection: 'row',
    gap: Spacing.two,
    marginTop: Spacing.two,
  },
  actionBtn: {
    flex: 1,
    height: 36,
  },
  group: {
    // SectionTitle 自带 marginBottom:-4，放在无 gap 的容器里会与卡片贴在一起；
    // 补一个 gap，让「标题 → 卡片」的间距与其余页面（Screen 的 gap）保持一致
    gap: Spacing.three,
  },
  menuRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: Spacing.three,
  },
  divider: {
    height: StyleSheet.hairlineWidth,
  },
  logout: {
    marginTop: Spacing.three,
  },
  modalMask: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.45)',
    justifyContent: 'center',
    padding: Spacing.four,
  },
  modalCard: {
    borderRadius: Spacing.two,
    padding: Spacing.four,
    gap: Spacing.two,
  },
  modalActions: {
    flexDirection: 'row',
    gap: Spacing.two,
  },
})
