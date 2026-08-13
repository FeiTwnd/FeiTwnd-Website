import { router } from 'expo-router';
import { useCallback, useEffect, useState } from 'react';
import { Alert, FlatList, Pressable, RefreshControl, StyleSheet, Switch, TextInput, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { deleteArticles, getArticles, setPublished, setTop } from '@/api/article';
import { Card, DANGER, EmptyView, LoadingView, Pill, SUCCESS, useColors } from '@/components/ui';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing } from '@/constants/theme';
import { ApiError } from '@/lib/api-client';
import type { ArticleItem } from '@/lib/types';

const PAGE_SIZE = 20;

export default function ArticlesScreen() {
  const theme = useColors();
  const [items, setItems] = useState<ArticleItem[] | null>(null);
  const [keyword, setKeyword] = useState('');
  const [refreshing, setRefreshing] = useState(false);
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);

  const load = useCallback(
    async (p = 1, merge = false) => {
      const res = await getArticles({
        page: p,
        pageSize: PAGE_SIZE,
        title: keyword.trim() || undefined,
      });
      setItems(merge ? (prev) => [...(prev ?? []), ...res.records] : res.records);
      setTotal(res.total);
      setPage(p);
    },
    [keyword]
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

  const onTogglePublished = async (item: ArticleItem, next: boolean) => {
    try {
      await setPublished(item.id, next ? 1 : 0);
      setItems((prev) =>
        (prev ?? []).map((i) => (i.id === item.id ? { ...i, isPublished: next ? 1 : 0 } : i))
      );
    } catch (e) {
      Alert.alert('操作失败', e instanceof ApiError ? e.message : '网络错误');
    }
  };

  const onToggleTop = async (item: ArticleItem, next: boolean) => {
    try {
      await setTop(item.id, next ? 1 : 0);
      setItems((prev) => (prev ?? []).map((i) => (i.id === item.id ? { ...i, isTop: next ? 1 : 0 } : i)));
    } catch (e) {
      Alert.alert('操作失败', e instanceof ApiError ? e.message : '网络错误');
    }
  };

  const onDelete = (item: ArticleItem) => {
    Alert.alert('确认删除', `确定删除文章「${item.title}」吗？`, [
      { text: '取消', style: 'cancel' },
      {
        text: '删除',
        style: 'destructive',
        onPress: async () => {
          try {
            await deleteArticles([item.id]);
            setItems((prev) => (prev ?? []).filter((i) => i.id !== item.id));
            setTotal((t) => t - 1);
          } catch (e) {
            Alert.alert('操作失败', e instanceof ApiError ? e.message : '网络错误');
          }
        },
      },
    ]);
  };

  const onLoadMore = () => {
    if ((items ?? []).length >= total) return;
    load(page + 1, true).catch(() => {});
  };

  return (
    <SafeAreaView edges={['top']} style={{ flex: 1 }}>
      <ThemedView style={styles.header}>
        <TextInput
          placeholder="搜索文章标题…"
          placeholderTextColor={theme.textSecondary}
          value={keyword}
          onChangeText={setKeyword}
          style={[styles.search, { backgroundColor: theme.backgroundElement, color: theme.text }]}
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
          ListEmptyComponent={<EmptyView text="没有找到文章" />}
          renderItem={({ item }) => (
            <Card key={item.id}>
              <Pressable
                onPress={() =>
                  router.push({ pathname: '/manage/article/[id]', params: { id: String(item.id) } })
                }
                style={styles.row}
              >
                <ThemedText type="smallBold" style={styles.title} numberOfLines={1}>
                  {item.title}
                </ThemedText>
                <ThemedText type="small" themeColor="textSecondary" style={styles.meta}>
                  {item.updateTime?.slice(0, 10)}
                </ThemedText>
              </Pressable>
              <View style={styles.pills}>
                {item.isPublished === 1 ? (
                  <Pill label="已发布" color={SUCCESS} />
                ) : (
                  <Pill label="草稿" color={DANGER} />
                )}
                {item.isTop === 1 && <Pill label="置顶" color="#303133" />}
                <ThemedText type="small" themeColor="textSecondary">
                  浏览 {item.viewCount} · 评论 {item.commentCount} · 点赞 {item.likeCount}
                </ThemedText>
              </View>
              <View style={styles.switches}>
                <View style={styles.switchRow}>
                  <ThemedText type="small">发布</ThemedText>
                  <Switch
                    value={item.isPublished === 1}
                    onValueChange={(v) => onTogglePublished(item, v)}
                  />
                </View>
                <View style={styles.switchRow}>
                  <ThemedText type="small">置顶</ThemedText>
                  <Switch value={item.isTop === 1} onValueChange={(v) => onToggleTop(item, v)} />
                </View>
              </View>
              <View style={styles.actions}>
                <Pressable
                  onPress={() =>
                    router.push({ pathname: '/manage/article/[id]', params: { id: String(item.id) } })
                  }
                  hitSlop={8}
                >
                  <ThemedText type="small">查看 ›</ThemedText>
                </Pressable>
                <Pressable onPress={() => onDelete(item)} hitSlop={8}>
                  <ThemedText type="small" style={{ color: DANGER }}>删除</ThemedText>
                </Pressable>
              </View>
            </Card>
          )}
        />
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  header: {
    padding: Spacing.three,
  },
  search: {
    height: 40,
    borderRadius: Spacing.two,
    paddingHorizontal: Spacing.three,
    fontSize: 14,
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
  },
  title: {
    flex: 1,
    fontSize: 15,
  },
  meta: {
    fontSize: 12,
  },
  pills: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    flexWrap: 'wrap',
  },
  switches: {
    flexDirection: 'row',
    gap: Spacing.four,
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: Spacing.three,
  },
  switchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.one,
  },
});