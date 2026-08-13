/**
 * Learn more about light and dark modes:
 * https://docs.expo.dev/guides/color-schemes/
 */

import { Colors } from '@/constants/theme';
import { useColorScheme } from '@/hooks/use-color-scheme';

export function useTheme() {
  const scheme = useColorScheme();
  // useColorScheme 可能返回 null/undefined，统一兜底为 light，避免 Colors[undefined] 崩溃
  return Colors[scheme === 'dark' ? 'dark' : 'light'];
}
