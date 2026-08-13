import { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  FlatList,
  Image,
  KeyboardAvoidingView,
  Modal,
  Platform,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Switch,
  Text,
  TextInput,
  View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { ImageField } from '@/components/image-field';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Btn, Card, DANGER, EmptyView, Input, Pill, Segmented, useColors } from '@/components/ui';
import { Spacing } from '@/constants/theme';
import { ApiError } from '@/lib/api-client';

function errMsg(e: unknown): string {
  return e instanceof ApiError ? e.message : '网络错误';
}

export interface FieldDef {
  key: string;
  label: string;
  type?: 'text' | 'textarea' | 'number' | 'image' | 'toggle' | 'select';
  required?: boolean;
  placeholder?: string;
  options?: { label: string; value: number }[];
}

export interface CrudConfig<T extends { id: number }> {
  title: string;
  fields: FieldDef[];
  fetch: () => Promise<T[]>;
  create: (data: Record<string, unknown>) => Promise<unknown>;
  update: (id: number, data: Record<string, unknown>) => Promise<unknown>;
  remove: (id: number) => Promise<unknown>;
  titleKey?: string;
  subtitleKey?: string;
  imageKey?: string;
  showVisible?: boolean;
  extraActions?: (item: T) => { label: string; onPress: () => void }[];
}

function initValues(fields: FieldDef[], item: Record<string, unknown> | null): Record<string, unknown> {
  const v: Record<string, unknown> = {};
  for (const f of fields) {
    if (item && item[f.key] !== undefined && item[f.key] !== null) v[f.key] = item[f.key];
    else if (f.type === 'toggle') v[f.key] = 1;
    else if (f.type === 'select') v[f.key] = f.options?.[0]?.value ?? 0;
    else v[f.key] = '';
  }
  return v;
}

function buildPayload(fields: FieldDef[], values: Record<string, unknown>): Record<string, unknown> {
  const p: Record<string, unknown> = {};
  for (const f of fields) {
    const v = values[f.key];
    if (f.type === 'toggle') p[f.key] = v ? 1 : 0;
    else if (f.type === 'number') {
      if (v !== '' && v !== null && v !== undefined) p[f.key] = Number(v);
    } else {
      p[f.key] = v;
    }
  }
  return p;
}

function FieldRenderer({ field, value, onChange }: {
  field: FieldDef;
  value: unknown;
  onChange: (v: unknown) => void;
}) {
  const theme = useColors();
  const str = value === undefined || value === null ? '' : String(value);

  switch (field.type) {
    case 'toggle':
      return (
        <View style={styles.toggleRow}>
          <ThemedText type="small">{field.label}</ThemedText>
          <Switch value={value === 1 || value === true} onValueChange={(v) => onChange(v ? 1 : 0)} />
        </View>
      );
    case 'select':
      return (
        <View style={styles.fieldCol}>
          <ThemedText type="small" themeColor="textSecondary">{field.label}</ThemedText>
          <Segmented
            options={(field.options ?? []).map((o) => ({ value: o.value, label: o.label }))}
            value={(value as number) ?? field.options?.[0]?.value ?? 0}
            onChange={onChange}
          />
        </View>
      );
    case 'image':
      return <ImageField label={field.label} value={str || undefined} onChange={onChange} />;
    case 'textarea':
      return (
        <View style={styles.fieldCol}>
          <ThemedText type="small" themeColor="textSecondary">{field.label}</ThemedText>
          <TextInput
            multiline
            placeholder={field.placeholder}
            placeholderTextColor={theme.textSecondary}
            value={str}
            onChangeText={onChange}
            style={[styles.textarea, { backgroundColor: theme.backgroundElement, color: theme.text }]}
          />
        </View>
      );
    case 'number':
      return <Input label={field.label} placeholder={field.placeholder} keyboardType="numeric" value={str} onChangeText={onChange} />;
    default:
      return <Input label={field.label} placeholder={field.placeholder} value={str} onChangeText={onChange} />;
  }
}

function CrudFormModal<T extends { id: number }>({ config, item, onClose, onSaved }: {
  config: CrudConfig<T>;
  item: T | null;
  onClose: () => void;
  onSaved: () => void;
}) {
  const theme = useColors();
  const insets = useSafeAreaInsets();
  const [values, setValues] = useState<Record<string, unknown>>(() =>
    initValues(config.fields, item as unknown as Record<string, unknown> | null)
  );
  const [saving, setSaving] = useState(false);

  const setField = (key: string, v: unknown) => setValues((prev) => ({ ...prev, [key]: v }));

  const submit = async () => {
    for (const f of config.fields) {
      const v = values[f.key];
      if (f.required && (v === '' || v === null || v === undefined)) {
        Alert.alert('提示', `请填写「${f.label}」`);
        return;
      }
    }
    const payload = buildPayload(config.fields, values);
    setSaving(true);
    try {
      if (item) await config.update(item.id, payload);
      else await config.create(payload);
      onSaved();
    } catch (e) {
      Alert.alert(item ? '修改失败' : '添加失败', errMsg(e));
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal visible transparent animationType="slide" onRequestClose={onClose}>
      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : undefined} style={styles.modalMask}>
        <Pressable style={StyleSheet.absoluteFill} onPress={onClose} />
        <View style={[styles.modalCard, { backgroundColor: theme.background, paddingBottom: Spacing.four + insets.bottom }]}>
          <ThemedText type="smallBold">{item ? `编辑${config.title}` : `新增${config.title}`}</ThemedText>
          <ScrollView contentContainerStyle={styles.formBody} keyboardShouldPersistTaps="handled">
            {config.fields.map((f) => (
              <FieldRenderer key={f.key} field={f} value={values[f.key]} onChange={(v) => setField(f.key, v)} />
            ))}
          </ScrollView>
          <View style={styles.modalActions}>
            <Btn label="取消" variant="ghost" onPress={onClose} />
            <Btn label="保存" onPress={submit} loading={saving} />
          </View>
        </View>
      </KeyboardAvoidingView>
    </Modal>
  );
}

function CrudCard<T extends { id: number }>({ item, config, onEdit, onDelete }: {
  item: T;
  config: CrudConfig<T>;
  onEdit: () => void;
  onDelete: () => void;
}) {
  const theme = useColors();
  const rec = item as unknown as Record<string, unknown>;
  const title = config.titleKey ? rec[config.titleKey] : undefined;
  const subtitle = config.subtitleKey ? rec[config.subtitleKey] : undefined;
  const image = config.imageKey ? rec[config.imageKey] : undefined;
  const visible = config.showVisible ? (rec.isVisible as number | undefined) : undefined;
  const extras = config.extraActions ? config.extraActions(item) : [];

  return (
    <Card style={styles.card}>
      <View style={styles.cardTop}>
        {image ? <Image source={{ uri: String(image) }} style={styles.thumb} /> : null}
        <View style={styles.cardMain}>
          {title !== undefined && title !== null && title !== '' ? (
            <ThemedText type="smallBold" numberOfLines={1}>{String(title)}</ThemedText>
          ) : null}
          {subtitle !== undefined && subtitle !== null && subtitle !== '' ? (
            <ThemedText type="small" themeColor="textSecondary" numberOfLines={1}>{String(subtitle)}</ThemedText>
          ) : null}
        </View>
        {config.showVisible ? (
          <Pill label={visible === 1 ? '可见' : '隐藏'} color={visible === 1 ? theme.textSecondary : DANGER} />
        ) : null}
      </View>
      <View style={styles.cardActions}>
        {extras.map((e) => (
          <Btn key={e.label} label={e.label} variant="ghost" onPress={e.onPress} style={styles.actionBtn} />
        ))}
        <Btn label="编辑" variant="ghost" onPress={onEdit} style={styles.actionBtn} />
        <Btn label="删除" variant="danger" onPress={onDelete} style={styles.actionBtn} />
      </View>
    </Card>
  );
}

export function CrudScreen<T extends { id: number }>({ config }: { config: CrudConfig<T> }) {
  const theme = useColors();
  const [items, setItems] = useState<T[] | null>(null);
  const [error, setError] = useState('');
  const [refreshing, setRefreshing] = useState(false);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<T | null>(null);

  const load = useCallback(async () => {
    setError('');
    setItems(await config.fetch());
  }, [config]);

  useEffect(() => {
    void Promise.resolve().then(() => load().catch((e) => setError(errMsg(e))));
  }, [load]);

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    try {
      await load();
    } catch (e) {
      setError(errMsg(e));
    } finally {
      setRefreshing(false);
    }
  }, [load]);

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const openEdit = (item: T) => {
    setEditing(item);
    setFormOpen(true);
  };

  const onDelete = (item: T) => {
    Alert.alert('确认删除', '删除后不可恢复，确定删除吗？', [
      { text: '取消', style: 'cancel' },
      {
        text: '删除',
        style: 'destructive',
        onPress: async () => {
          try {
            await config.remove(item.id);
            setItems((prev) => (prev ?? []).filter((i) => i.id !== item.id));
          } catch (e) {
            Alert.alert('删除失败', errMsg(e));
          }
        },
      },
    ]);
  };

  const onSaved = async () => {
    setFormOpen(false);
    setEditing(null);
    try {
      await load();
    } catch (e) {
      Alert.alert('刷新失败', errMsg(e));
    }
  };

  return (
    <ThemedView style={styles.container}>
      {items === null && !error ? (
        <View style={styles.loading}>
          <ActivityIndicator color={theme.textSecondary} />
        </View>
      ) : (
        <FlatList
          data={items ?? []}
          keyExtractor={(i) => String(i.id)}
          contentContainerStyle={styles.list}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
          ListEmptyComponent={<EmptyView text={error || '暂无数据，点右下角 ＋ 新增'} />}
          renderItem={({ item }) => (
            <CrudCard item={item} config={config} onEdit={() => openEdit(item)} onDelete={() => onDelete(item)} />
          )}
        />
      )}

      <Pressable
        onPress={openCreate}
        style={({ pressed }) => [styles.fab, { backgroundColor: theme.text, opacity: pressed ? 0.8 : 1 }]}>
        <Text style={[styles.fabIcon, { color: theme.background }]}>＋</Text>
      </Pressable>

      {formOpen ? (
        <CrudFormModal config={config} item={editing} onClose={() => setFormOpen(false)} onSaved={onSaved} />
      ) : null}
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  loading: { flex: 1, alignItems: 'center', justifyContent: 'center' },
  list: { padding: Spacing.three, gap: Spacing.two },
  fab: {
    position: 'absolute',
    right: Spacing.four,
    bottom: Spacing.five,
    width: 52,
    height: 52,
    borderRadius: 26,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 4,
    shadowColor: '#000',
    shadowOpacity: 0.2,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 3 },
  },
  fabIcon: { fontSize: 26, fontWeight: '600', lineHeight: 30 },
  card: { gap: Spacing.two },
  cardTop: { flexDirection: 'row', alignItems: 'center', gap: Spacing.two },
  thumb: { width: 44, height: 44, borderRadius: Spacing.two, backgroundColor: '#00000010' },
  cardMain: { flex: 1, gap: Spacing.half },
  cardActions: { flexDirection: 'row', gap: Spacing.two },
  actionBtn: { flex: 1, height: 36 },
  toggleRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', paddingVertical: Spacing.one },
  fieldCol: { gap: Spacing.one },
  textarea: {
    minHeight: 88,
    borderRadius: Spacing.two,
    padding: Spacing.three,
    fontSize: 15,
    textAlignVertical: 'top',
  },
  modalMask: { flex: 1, backgroundColor: 'rgba(0,0,0,0.45)', justifyContent: 'flex-end' },
  modalCard: {
    borderTopLeftRadius: Spacing.four,
    borderTopRightRadius: Spacing.four,
    padding: Spacing.four,
    gap: Spacing.three,
    maxHeight: '90%',
  },
  formBody: { gap: Spacing.three },
  modalActions: { flexDirection: 'row', gap: Spacing.two },
});
