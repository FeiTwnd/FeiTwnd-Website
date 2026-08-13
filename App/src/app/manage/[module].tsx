import { Stack, useLocalSearchParams } from 'expo-router';
import { StyleSheet, View } from 'react-native';

import { CrudScreen } from '@/components/crud';
import { ThemedText } from '@/components/themed-text';
import { MANAGE_MODULES } from '@/lib/manage-modules';

export default function ManageModuleScreen() {
  const { module } = useLocalSearchParams<{ module: string }>();
  const config = MANAGE_MODULES[module ?? ''];

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: config?.title ?? '管理' }} />
      {config ? (
        <CrudScreen config={config} />
      ) : (
        <View style={styles.missing}>
          <ThemedText type="small" themeColor="textSecondary">模块不存在</ThemedText>
        </View>
      )}
    </>
  );
}

const styles = StyleSheet.create({
  missing: { flex: 1, alignItems: 'center', justifyContent: 'center' },
});
