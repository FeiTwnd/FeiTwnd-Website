import { Stack, useLocalSearchParams } from 'expo-router';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { StyleSheet, useColorScheme } from 'react-native';
import { WebView } from 'react-native-webview';

import { getArticle } from '@/api/article';
import { ThemedView } from '@/components/themed-view';
import { ErrorView, LoadingView, useColors } from '@/components/ui';
import { ApiError } from '@/lib/api-client';
import type { ArticleDetail } from '@/lib/types';

/** 转义插入 HTML 的标题/摘要等用户文本，避免破坏结构 */
function escapeHtml(s: string): string {
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

export default function ArticleDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const theme = useColors();
  const dark = useColorScheme() === 'dark';
  const [article, setArticle] = useState<ArticleDetail | null>(null);
  const [error, setError] = useState('');

  const load = useCallback(() => {
    setError('');
    return getArticle(Number(id)).then(setArticle);
  }, [id]);

  useEffect(() => {
    void Promise.resolve().then(() =>
      load().catch((e) => setError(e instanceof ApiError ? e.message : '网络错误'))
    );
  }, [load]);

  const html = useMemo(() => {
    if (!article) return '';
    // 正文：优先渲染 HTML，回退到 Markdown 原文（作为等宽文本展示）
    const body = article.contentHtml?.trim()
      ? article.contentHtml
      : `<pre>${escapeHtml(article.contentMarkdown ?? '')}</pre>`;
    const meta = [
      article.publishDate,
      article.wordCount ? `${article.wordCount} 字` : '',
      article.readingTime ? `约 ${article.readingTime} 分钟` : '',
      `浏览 ${article.viewCount}`,
      `评论 ${article.commentCount}`,
      `点赞 ${article.likeCount}`,
    ]
      .filter(Boolean)
      .join(' · ');
    const cover = article.coverImage?.trim()
      ? `<img class="cover" src="${escapeHtml(article.coverImage.trim())}" alt="" />`
      : '';
    const summary = article.summary?.trim()
      ? `<p class="summary">${escapeHtml(article.summary.trim())}</p>`
      : '';

    return `<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<style>
  :root { color-scheme: ${dark ? 'dark' : 'light'}; }
  * { box-sizing: border-box; }
  html, body { margin: 0; padding: 0; background: ${theme.background}; }
  body {
    padding: 20px 18px 48px;
    color: ${theme.text};
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'PingFang SC', 'Microsoft YaHei', sans-serif;
    font-size: 16px;
    line-height: 1.75;
    word-break: break-word;
  }
  h1.title { font-size: 24px; line-height: 1.35; margin: 0 0 12px; }
  .meta { color: ${theme.textSecondary}; font-size: 13px; margin: 0 0 16px; }
  .cover { width: 100%; border-radius: 12px; margin: 0 0 16px; }
  .summary {
    color: ${theme.textSecondary}; font-size: 15px; line-height: 1.7;
    padding: 12px 14px; background: ${theme.backgroundElement};
    border-radius: 8px; margin: 0 0 20px;
  }
  /* 正文内容样式覆盖（适配主题） */
  img { max-width: 100%; height: auto; border-radius: 8px; }
  pre { background: ${theme.backgroundSelected}; padding: 14px; border-radius: 8px; overflow-x: auto; }
  code { font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace; font-size: 0.9em; }
  blockquote { margin: 0; padding: 8px 14px; border-left: 3px solid ${theme.backgroundSelected}; color: ${theme.textSecondary}; }
  a { color: ${theme.text}; }
  table { border-collapse: collapse; max-width: 100%; }
  th, td { border: 1px solid ${theme.backgroundSelected}; padding: 6px 10px; }
  h1, h2, h3, h4 { line-height: 1.4; }
</style>
</head>
<body>
  <h1 class="title">${escapeHtml(article.title)}</h1>
  ${meta ? `<p class="meta">${meta}</p>` : ''}
  ${cover}
  ${summary}
  ${body}
</body>
</html>`;
  }, [article, theme, dark]);

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: '文章详情' }} />
      <ThemedView style={styles.container}>
        {error ? (
          <ErrorView message={error} onRetry={load} />
        ) : !article ? (
          <LoadingView />
        ) : (
          <WebView
            originWhitelist={['*']}
            source={{ html }}
            style={[styles.webview, { backgroundColor: theme.background }]}
            setSupportMultipleWindows={false}
          />
        )}
      </ThemedView>
    </>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  webview: { flex: 1 },
});
