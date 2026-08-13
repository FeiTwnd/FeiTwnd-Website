import { useSyncExternalStore } from 'react';

export type SessionState = 'loading' | 'none' | 'ok';

let current: SessionState = 'loading';
const listeners = new Set<() => void>();

function emit() {
  listeners.forEach((l) => l());
}

export function setSession(next: SessionState) {
  current = next;
  emit();
}

export function getSession(): SessionState {
  return current;
}

export function useSession(): SessionState {
  return useSyncExternalStore(
    (cb) => {
      listeners.add(cb);
      return () => listeners.delete(cb);
    },
    getSession,
    () => 'loading'
  );
}