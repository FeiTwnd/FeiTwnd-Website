import * as SecureStore from 'expo-secure-store'
import { Platform } from 'react-native'

const TOKEN_KEY = 'feitwnd_admin_token'
const ADMIN_ID_KEY = 'feitwnd_admin_id'

// SecureStore 不支持 Web，Web 上用 localStorage 兜底
const webStorage =
  Platform.OS === 'web' && typeof globalThis.localStorage !== 'undefined'
    ? globalThis.localStorage
    : null

function getItem(key: string): Promise<string | null> {
  if (webStorage) return Promise.resolve(webStorage.getItem(key))
  return SecureStore.getItemAsync(key)
}

function setItem(key: string, value: string): Promise<void> {
  if (webStorage) {
    webStorage.setItem(key, value)
    return Promise.resolve()
  }
  return SecureStore.setItemAsync(key, value)
}

function deleteItem(key: string): Promise<void> {
  if (webStorage) {
    webStorage.removeItem(key)
    return Promise.resolve()
  }
  return SecureStore.deleteItemAsync(key)
}

export async function getToken(): Promise<string | null> {
  return getItem(TOKEN_KEY)
}

export async function setToken(token: string): Promise<void> {
  await setItem(TOKEN_KEY, token)
}

export async function getAdminId(): Promise<string | null> {
  return getItem(ADMIN_ID_KEY)
}

export async function setAdminId(id: number): Promise<void> {
  await setItem(ADMIN_ID_KEY, String(id))
}

export async function clearSession(): Promise<void> {
  await deleteItem(TOKEN_KEY)
  await deleteItem(ADMIN_ID_KEY)
}
