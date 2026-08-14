import { useCallback, useEffect, useState } from 'react';
import { RefreshControl, StyleSheet, View } from 'react-native';

import {
  getArticleViewTop10,
  getOverview,
  getProvinceDistribution,
  getViewStatistics,
  getVisitorStatistics,
} from '@/api/report';
import { Card, DANGER, EmptyView, ErrorView, LoadingView, Screen, SectionTitle, Segmented, useColors } from '@/components/ui';
import { ThemedText } from '@/components/themed-text';
import { Spacing } from '@/constants/theme';
import { ApiError } from '@/lib/api-client';
import type { AdminOverviewVO, ArticleViewTop10VO } from '@/lib/types';

function fmtDate(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

function BarChart({ values }: { values: number[] }) {
  const theme = useColors();
  const max = Math.max(...values, 1);
  return (
    <View style={styles.bars}>
      {values.map((v, i) => (
        <View key={i} style={styles.barCol}>
          <View
            style={[
              styles.bar,
              { height: Math.max(2, Math.round((v / max) * 80)), backgroundColor: theme.text },
            ]}
          />
        </View>
      ))}
    </View>
  );
}

function HBarRow({ label, value, max }: { label: string; value: number; max: number }) {
  const theme = useColors();
  return (
    <View style={styles.hRow}>
      <ThemedText type="small" style={styles.hLabel} numberOfLines={1}>{label}</ThemedText>
      <View style={styles.hTrack}>
        <View style={[styles.hFill, { width: `${(value / max) * 100}%`, backgroundColor: theme.text }]} />
      </View>
      <ThemedText type="small" themeColor="textSecondary" style={styles.hVal}>{value}</ThemedText>
    </View>
  );
}

export default function DashboardScreen() {
  const theme = useColors();
  const [overview, setOverview] = useState<AdminOverviewVO | null>(null);
  const [top10, setTop10] = useState<ArticleViewTop10VO | null>(null);
  const [range, setRange] = useState(7);
  const [trend, setTrend] = useState<{ views: number[]; visitors: number[] } | null>(null);
  const [province, setProvince] = useState<{ names: string[]; counts: number[] } | null>(null);
  const [error, setError] = useState('');
  const [refreshing, setRefreshing] = useState(false);

  const loadOverview = useCallback(async () => {
    const [ov, top] = await Promise.all([getOverview(), getArticleViewTop10()]);
    setOverview(ov);
    setTop10(top);
  }, []);

  const loadStats = useCallback(async () => {
    const end = new Date();
    const begin = new Date(end.getTime() - (range - 1) * 86400000);
    const [view, visitor, prov] = await Promise.all([
      getViewStatistics(fmtDate(begin), fmtDate(end)),
      getVisitorStatistics(fmtDate(begin), fmtDate(end)),
      getProvinceDistribution(),
    ]);
    setTrend({
      views: view.viewCountList.split(',').map(Number),
      visitors: visitor.newVisitorCountList.split(',').map(Number),
    });
    setProvince({
      names: prov.provinceList.split(',').filter(Boolean),
      counts: prov.countList.split(',').map(Number),
    });
  }, [range]);

  useEffect(() => {
    void Promise.resolve().then(() =>
      loadOverview().catch((e) => setError(e instanceof ApiError ? e.message : '加载失败'))
    );
  }, [loadOverview]);

  useEffect(() => {
    void Promise.resolve().then(() => loadStats().catch(() => {}));
  }, [loadStats]);

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await Promise.allSettled([loadOverview(), loadStats()]);
    setRefreshing(false);
  }, [loadOverview, loadStats]);

  if (error && !overview) {
    return (
      <Screen refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}>
        <ErrorView message={error} onRetry={loadOverview} />
      </Screen>
    );
  }

  if (!overview) {
    return (
      <Screen refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}>
        <LoadingView />
      </Screen>
    );
  }

  const stats: { label: string; value: number; accent: boolean }[] = [
    { label: '今日浏览', value: overview.todayViewCount, accent: true },
    { label: '今日访客', value: overview.todayNewVisitorCount, accent: true },
    { label: '待审评论', value: overview.pendingCommentCount, accent: overview.pendingCommentCount > 0 },
    { label: '待审留言', value: overview.pendingMessageCount, accent: overview.pendingMessageCount > 0 },
    { label: '总浏览', value: overview.totalViewCount, accent: false },
    { label: '总访客', value: overview.totalVisitorCount, accent: false },
    { label: '文章数', value: overview.totalArticleCount, accent: false },
    { label: '总评论', value: overview.totalCommentCount, accent: false },
    { label: '总留言', value: overview.totalMessageCount, accent: false },
  ];

  const maxView = top10 && top10.viewCountList.length > 0 ? Math.max(...top10.viewCountList) : 1;
  const maxProvince = province && province.counts.length > 0 ? Math.max(...province.counts) : 1;

  return (
    <Screen refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}>
      <View style={styles.grid}>
        {stats.map((s) => (
          <Card key={s.label} style={styles.statCard}>
            <ThemedText
              type="title"
              style={[styles.statValue, { color: s.accent ? DANGER : theme.text }]}>
              {s.value}
            </ThemedText>
            <ThemedText type="small" themeColor="textSecondary">{s.label}</ThemedText>
          </Card>
        ))}
      </View>

      <SectionTitle>数据趋势</SectionTitle>
      <Card>
        <Segmented
          options={[
            { value: 7, label: '近7天' },
            { value: 30, label: '近30天' },
            { value: 90, label: '近90天' },
          ]}
          value={range}
          onChange={setRange}
        />
        <ThemedText type="small" themeColor="textSecondary">浏览量</ThemedText>
        <BarChart values={trend?.views ?? []} />
        <ThemedText type="small" themeColor="textSecondary">新增访客</ThemedText>
        <BarChart values={trend?.visitors ?? []} />
      </Card>

      <SectionTitle>省份分布</SectionTitle>
      <Card>
        {province && province.names.length > 0 ? (
          province.names.map((name, i) => (
            <HBarRow key={`${name}-${i}`} label={name} value={province.counts[i] ?? 0} max={maxProvince} />
          ))
        ) : (
          <EmptyView text="暂无数据" />
        )}
      </Card>

      <SectionTitle>热门文章 Top 10</SectionTitle>
      <Card>
        {top10 && top10.titleList.length > 0 ? (
          top10.titleList.map((title, i) => (
            <View key={i} style={styles.topRow}>
              <ThemedText type="small" style={styles.topRank}>{i + 1}</ThemedText>
              <View style={styles.topBody}>
                <ThemedText type="small" numberOfLines={1} style={styles.topTitle}>{title}</ThemedText>
                <View style={[styles.topBar, { backgroundColor: theme.backgroundSelected }]}>
                  <View
                    style={[
                      styles.topBarFill,
                      {
                        width: `${(top10.viewCountList[i] / maxView) * 100}%`,
                        backgroundColor: theme.text,
                      },
                    ]}
                  />
                </View>
              </View>
              <ThemedText type="small" themeColor="textSecondary">{top10.viewCountList[i]}</ThemedText>
            </View>
          ))
        ) : (
          <EmptyView text="暂无数据" />
        )}
      </Card>
    </Screen>
  );
}

const styles = StyleSheet.create({
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Spacing.two,
  },
  statCard: {
    flexBasis: '30%',
    flexGrow: 1,
    minWidth: 90,
    alignItems: 'center',
    gap: Spacing.one,
    paddingVertical: Spacing.three,
  },
  statValue: {
    fontSize: 24,
    lineHeight: 30,
  },
  bars: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    gap: 2,
    height: 80,
  },
  barCol: {
    flex: 1,
    justifyContent: 'flex-end',
  },
  bar: {
    borderRadius: 2,
  },
  hRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  hLabel: {
    width: 52,
    fontSize: 12,
  },
  hTrack: {
    flex: 1,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#00000010',
    overflow: 'hidden',
  },
  hFill: {
    height: 8,
    borderRadius: 4,
  },
  hVal: {
    width: 36,
    fontSize: 12,
    textAlign: 'right',
  },
  topRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  topRank: {
    width: 20,
    fontWeight: '700',
  },
  topBody: {
    flex: 1,
    gap: Spacing.one,
  },
  topTitle: {
    fontSize: 13,
  },
  topBar: {
    height: 4,
    borderRadius: 2,
    overflow: 'hidden',
  },
  topBarFill: {
    height: 4,
    borderRadius: 2,
  },
});
