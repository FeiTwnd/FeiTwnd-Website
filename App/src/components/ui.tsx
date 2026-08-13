import { forwardRef } from 'react';
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  ScrollViewProps,
  StyleProp,
  StyleSheet,
  Text,
  TextInput,
  TextInputProps,
  View,
  ViewProps,
  ViewStyle,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing, ThemeColor } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export const DANGER = '#f56c6c';
export const SUCCESS = '#67c23a';

export function useColors() {
  return useTheme();
}

export function Screen({ children, ...props }: ScrollViewProps) {
  const insets = useSafeAreaInsets();
  return (
    <ScrollView
      keyboardShouldPersistTaps="handled"
      style={{ flex: 1 }}
      // 顶部安全区：刘海 / 灵动岛 / 状态栏区域不贴内容（Android edge-to-edge 需要显式 padding）
      contentContainerStyle={[styles.screenContent, { paddingTop: insets.top }]}
      {...props}>
      {children}
    </ScrollView>
  );
}

export function Card({ style, type = 'backgroundElement', children, ...props }: ViewProps & { type?: ThemeColor }) {
  return (
    <ThemedView type={type} style={[styles.card, style]} {...props}>
      {children}
    </ThemedView>
  );
}

export function Btn({ label, onPress, disabled, variant = 'primary', loading, style }: {
  label: string;
  onPress: () => void;
  disabled?: boolean;
  variant?: 'primary' | 'danger' | 'ghost';
  loading?: boolean;
  style?: StyleProp<ViewStyle>;
}) {
  const theme = useTheme();
  const bg =
    variant === 'primary' ? theme.text : variant === 'danger' ? DANGER : theme.backgroundSelected;
  const color = variant === 'ghost' ? theme.text : theme.background;

  return (
    <Pressable
      disabled={disabled || loading}
      onPress={onPress}
      style={({ pressed }) => [
        styles.btn,
        { backgroundColor: bg, opacity: disabled || loading ? 0.5 : pressed ? 0.8 : 1 },
        style,
      ]}>
      {loading ? (
        <ActivityIndicator size="small" color={color} />
      ) : (
        <Text style={[styles.btnLabel, { color }]}>{label}</Text>
      )}
    </Pressable>
  );
}

export function Pill({ label, color }: { label: string; color: string }) {
  return (
    <ThemedView
      style={[styles.pill, { backgroundColor: `${color}22` }]}>
      <Text style={[styles.pillLabel, { color }]}>{label}</Text>
    </ThemedView>
  );
}

export function EmptyView({ text }: { text: string }) {
  const theme = useTheme();
  return <ThemedText type="small" style={{ color: theme.textSecondary, textAlign: 'center', padding: Spacing.five }}>{text}</ThemedText>;
}

export function ErrorView({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <View style={styles.errorView}>
      <ThemedText type="small" style={{ color: DANGER, textAlign: 'center' }}>{message}</ThemedText>
      {onRetry && (
        <Btn variant="ghost" label="重试" onPress={onRetry} style={{ marginTop: Spacing.three, alignSelf: 'center' }} />
      )}
    </View>
  );
}

export function LoadingView({ text = '加载中…' }: { text?: string }) {
  const theme = useTheme();
  return (
    <View style={styles.loadingView}>
      <ActivityIndicator color={theme.textSecondary} />
      <ThemedText type="small" style={{ color: theme.textSecondary, marginTop: Spacing.two }}>{text}</ThemedText>
    </View>
  );
}

export function Segmented<T extends string | number>({ options, value, onChange }: {
  options: { value: T; label: string }[];
  value: T;
  onChange: (v: T) => void;
}) {
  const theme = useTheme();
  return (
    <View style={[styles.segmented, { backgroundColor: theme.backgroundSelected }]}>
      {options.map((opt) => {
        const selected = opt.value === value;
        return (
          <Pressable
            key={opt.value}
            onPress={() => onChange(opt.value)}
            style={[
              styles.segment,
              { backgroundColor: selected ? theme.background : 'transparent' },
            ]}>
            <Text style={[styles.segmentLabel, { color: selected ? theme.text : theme.textSecondary }]}>
              {opt.label}
            </Text>
          </Pressable>
        );
      })}
    </View>
  );
}

export const Input = forwardRef<TextInput, TextInputProps & { label?: string }>(function Input(
  { label, style, ...props },
  ref
) {
  const theme = useTheme();
  return (
    <View style={styles.inputWrap}>
      {label && <ThemedText type="small" themeColor="textSecondary">{label}</ThemedText>}
      <TextInput
        ref={ref}
        placeholderTextColor={theme.textSecondary}
        style={[
          styles.input,
          { backgroundColor: theme.backgroundElement, color: theme.text },
          style,
        ]}
        {...props}
      />
    </View>
  );
});

export function SectionTitle({ children }: { children: string }) {
  return (
    <ThemedText type="smallBold" themeColor="textSecondary" style={styles.sectionTitle}>
      {children}
    </ThemedText>
  );
}

const styles = StyleSheet.create({
  screenContent: {
    padding: Spacing.three,
    gap: Spacing.three,
  },
  card: {
    padding: Spacing.three,
    borderRadius: Spacing.three,
    gap: Spacing.two,
  },
  btn: {
    height: 44,
    borderRadius: Spacing.two,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: Spacing.four,
  },
  btnLabel: {
    fontSize: 15,
    fontWeight: '600',
  },
  pill: {
    borderRadius: Spacing.two,
    paddingHorizontal: Spacing.two,
    paddingVertical: Spacing.half,
    alignSelf: 'flex-start',
  },
  pillLabel: {
    fontSize: 12,
    fontWeight: '600',
  },
  errorView: {
    padding: Spacing.four,
    gap: Spacing.two,
  },
  loadingView: {
    padding: Spacing.five,
    alignItems: 'center',
  },
  segmented: {
    flexDirection: 'row',
    borderRadius: Spacing.two,
    padding: Spacing.half,
    gap: Spacing.half,
  },
  segment: {
    flex: 1,
    paddingVertical: Spacing.two,
    borderRadius: Spacing.one + 2,
    alignItems: 'center',
  },
  segmentLabel: {
    fontSize: 14,
    fontWeight: '600',
  },
  inputWrap: {
    gap: Spacing.one,
  },
  input: {
    height: 44,
    borderRadius: Spacing.two,
    paddingHorizontal: Spacing.three,
    fontSize: 15,
  },
  sectionTitle: {
    marginTop: Spacing.two,
    marginBottom: -Spacing.one,
  },
});