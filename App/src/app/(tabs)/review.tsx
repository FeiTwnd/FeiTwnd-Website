import { useCallback, useEffect, useState } from 'react';
import {
  Alert,
  FlatList,
  Modal,
  Pressable,
  RefreshControl,
  StyleSheet,
  TextInput,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import {
  approveComments,
  approveMessages,
  deleteComments,
  deleteMessages,
  getComments,
  getMessages,
  replyComment,
  replyMessage,
} from '@/api/review';
import { Btn, Card, DANGER, EmptyView, LoadingView, Pill, Segmented, SUCCESS, useColors } from '@/components/ui';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing } from '@/constants/theme';
import { ApiError } from '@/lib/api-client';
import type { CommentItem, MessageItem, PageResult } from '@/lib/types';

const PAGE_SIZE = 20;

interface ReviewItem {
  id: number;
  articleId?: number;
  rootId: number | null;
  parentId: number | null;
  parentNickname: string | null;
  content: string;
  articleTitle?: string;
  nickname: string;
  location?: string;
  isApproved: number;
  isAdminReply: number;
  createTime: string;
}

function toReviewItem(c: CommentItem | MessageItem): ReviewItem {
  return {
    id: c.id,
    articleId: 'articleId' in c ? c.articleId : undefined,
    rootId: c.rootId,
    parentId: c.parentId,
    parentNickname: c.parentNickname,
    content: c.content,
    articleTitle: 'articleTitle' in c ? c.articleTitle : undefined,
    nickname: c.nickname,
    location: c.location,
    isApproved: c.isApproved,
    isAdminReply: c.isAdminReply,
    createTime: c.createTime,
  };
}

export default function ReviewScreen() {
  const theme = useColors();
  const [tab, setTab] = useState<'comment' | 'message'>('comment');
  const [isApproved, setIsApproved] = useState<0 | 1>(0);
  const [items, setItems] = useState<ReviewItem[] | null>(null);
  const [refreshing, setRefreshing] = useState(false);
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);
  const [replyTarget, setReplyTarget] = useState<ReviewItem | null>(null);
  const [replyText, setReplyText] = useState('');
  const [replying, setReplying] = useState(false);

  const load = useCallback(
    async (p = 1, merge = false) => {
      if (tab === 'comment') {
        const res: PageResult<CommentItem> = await getComments({
          page: p,
          pageSize: PAGE_SIZE,
          isApproved,
        });
        const list = res.records.map(toReviewItem);
        setItems(merge ? (prev) => [...(prev ?? []), ...list] : list);
        setTotal(res.total);
      } else {
        const res: PageResult<MessageItem> = await getMessages({
          page: p,
          pageSize: PAGE_SIZE,
          isApproved,
        });
        const list = res.records.map(toReviewItem);
        setItems(merge ? (prev) => [...(prev ?? []), ...list] : list);
        setTotal(res.total);
      }
      setPage(p);
    },
    [tab, isApproved]
  );

  useEffect(() => {
    void Promise.resolve().then(() =>
      load(1, false).catch((e) => Alert.alert('加载失败', e instanceof ApiError ? e.message : '网络错误'))
    );
  }, [load]);

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    try {
      await load(1, false);
    } catch (e) {
      Alert.alert('刷新失败', e instanceof ApiError ? e.message : '网络错误');
    } finally {
      setRefreshing(false);
    }
  }, [load]);

  const onApprove = async (item: ReviewItem) => {
    try {
      if (tab === 'comment') await approveComments([item.id]);
      else await approveMessages([item.id]);
      setItems((prev) => (prev ?? []).filter((i) => i.id !== item.id));
      setTotal((t) => t - 1);
    } catch (e) {
      Alert.alert('操作失败', e instanceof ApiError ? e.message : '网络错误');
    }
  };

  const onDelete = (item: ReviewItem) => {
    Alert.alert('确认删除', '删除后不可恢复，确定删除这条记录吗？', [
      { text: '取消', style: 'cancel' },
      {
        text: '删除',
        style: 'destructive',
        onPress: async () => {
          try {
            if (tab === 'comment') await deleteComments([item.id]);
            else await deleteMessages([item.id]);
            setItems((prev) => (prev ?? []).filter((i) => i.id !== item.id));
            setTotal((t) => t - 1);
          } catch (e) {
            Alert.alert('操作失败', e instanceof ApiError ? e.message : '网络错误');
          }
        },
      },
    ]);
  };

  const onReplySend = async () => {
    if (!replyText.trim()) return;
    setReplying(true);
    try {
      if (tab === 'comment') {
        await replyComment({
          articleId: replyTarget!.articleId!,
          parentId: replyTarget!.id,
          // 回复根级记录时 rootId 用记录自身 id，保证树形挂接正确（与 Web 管理端一致）
          rootId: replyTarget!.rootId ?? replyTarget!.id,
          parentNickname: replyTarget!.nickname,
          content: replyText.trim(),
          isMarkdown: 0,
        });
      } else {
        await replyMessage({
          parentId: replyTarget!.id,
          // 回复根级记录时 rootId 用记录自身 id，保证树形挂接正确（与 Web 管理端一致）
          rootId: replyTarget!.rootId ?? replyTarget!.id,
          parentNickname: replyTarget!.nickname,
          content: replyText.trim(),
          isMarkdown: 0,
        });
      }
      setReplyTarget(null);
      setReplyText('');
      Alert.alert('回复成功', '回复已发送');
    } catch (e) {
      Alert.alert('回复失败', e instanceof ApiError ? e.message : '网络错误');
    } finally {
      setReplying(false);
    }
  };

  const onLoadMore = () => {
    if ((items ?? []).length >= total) return;
    load(page + 1, true).catch(() => {});
  };

  return (
    <SafeAreaView edges={['top']} style={{ flex: 1 }}>
      <ThemedView style={styles.header}>
        <Segmented
          options={[
            { value: 'comment', label: '评论' },
            { value: 'message', label: '留言' },
          ]}
          value={tab}
          onChange={setTab}
        />
        <Segmented
          options={[
            { value: 0 as const, label: '待审核' },
            { value: 1 as const, label: '已通过' },
          ]}
          value={isApproved}
          onChange={setIsApproved}
        />
      </ThemedView>

      {items === null ? (
        <LoadingView />
      ) : (
        <FlatList
          data={items}
          keyExtractor={(item) => String(item.id)}
          contentContainerStyle={styles.list}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
          onEndReached={onLoadMore}
          onEndReachedThreshold={0.3}
          ListEmptyComponent={
            <EmptyView text={isApproved === 0 ? '没有待审核的内容' : '暂无已通过的内容'} />
          }
          renderItem={({ item }) => (
            <Card key={item.id}>
              <View style={styles.row}>
                <ThemedText type="smallBold" style={styles.nickname}>{item.nickname}</ThemedText>
                {item.isAdminReply === 1 && <Pill label="管理员回复" color={SUCCESS} />}
                {item.isApproved === 0 && <Pill label="待审核" color={DANGER} />}
                <ThemedText type="small" themeColor="textSecondary" style={styles.time}>
                  {item.createTime}
                </ThemedText>
              </View>
              {item.articleTitle ? (
                <ThemedText type="small" themeColor="textSecondary">
                  文章：{item.articleTitle}
                </ThemedText>
              ) : null}
              {item.parentNickname ? (
                <ThemedText type="small" themeColor="textSecondary">
                  回复 @{item.parentNickname}
                </ThemedText>
              ) : null}
              <ThemedText type="small" style={styles.content}>{item.content}</ThemedText>
              {item.location ? (
                <ThemedText type="small" themeColor="textSecondary">{item.location}</ThemedText>
              ) : null}
              <View style={styles.actions}>
                {item.isApproved === 0 && (
                  <Btn label="通过" onPress={() => onApprove(item)} style={styles.actionBtn} />
                )}
                <Btn label="回复" variant="ghost" onPress={() => setReplyTarget(item)} style={styles.actionBtn} />
                <Btn label="删除" variant="danger" onPress={() => onDelete(item)} style={styles.actionBtn} />
              </View>
            </Card>
          )}
        />
      )}

      <Modal visible={replyTarget !== null} transparent animationType="fade">
        <Pressable style={styles.modalMask} onPress={() => setReplyTarget(null)}>
          <Pressable style={[styles.modalCard, { backgroundColor: theme.background }]} onPress={(e) => e.stopPropagation()}>
            <ThemedText type="smallBold">回复 @{replyTarget?.nickname}</ThemedText>
            <TextInput
              multiline
              placeholder="输入回复内容…"
              placeholderTextColor={theme.textSecondary}
              value={replyText}
              onChangeText={setReplyText}
              style={[
                styles.replyInput,
                { backgroundColor: theme.backgroundElement, color: theme.text },
              ]}
            />
            <View style={styles.modalActions}>
              <Btn label="取消" variant="ghost" onPress={() => setReplyTarget(null)} />
              <Btn label="发送" onPress={onReplySend} loading={replying} />
            </View>
          </Pressable>
        </Pressable>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  header: {
    padding: Spacing.three,
    gap: Spacing.two,
  },
  list: {
    paddingHorizontal: Spacing.three,
    paddingBottom: Spacing.five,
    gap: Spacing.two,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    flexWrap: 'wrap',
  },
  nickname: {
    fontSize: 14,
  },
  time: {
    marginLeft: 'auto',
    fontSize: 12,
  },
  content: {
    fontSize: 14,
  },
  actions: {
    flexDirection: 'row',
    gap: Spacing.two,
  },
  actionBtn: {
    flex: 1,
    height: 36,
  },
  modalMask: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.45)',
    justifyContent: 'center',
    padding: Spacing.four,
  },
  modalCard: {
    borderRadius: Spacing.three,
    padding: Spacing.four,
    gap: Spacing.three,
  },
  replyInput: {
    minHeight: 100,
    borderRadius: Spacing.two,
    padding: Spacing.three,
    fontSize: 15,
    textAlignVertical: 'top',
  },
  modalActions: {
    flexDirection: 'row',
    gap: Spacing.two,
  },
});