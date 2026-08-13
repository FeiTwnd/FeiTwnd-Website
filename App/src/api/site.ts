import { api } from '@/lib/api-client';
import type { Experience, FriendLink, PersonalInfo, Skill, SocialMedia } from '@/lib/types';

// —— 个人信息 ——
export function getPersonalInfo() {
  return api<PersonalInfo>('/admin/personalInfo');
}

export function updatePersonalInfo(body: unknown) {
  return api('/admin/personalInfo', { method: 'PUT', body });
}

// —— 社交媒体 ——
export function getSocialMedia() {
  return api<SocialMedia[]>('/admin/socialMedia');
}

export function createSocialMedia(body: unknown) {
  return api('/admin/socialMedia', { method: 'POST', body });
}

export function updateSocialMedia(body: unknown) {
  return api('/admin/socialMedia', { method: 'PUT', body });
}

export function deleteSocialMedia(ids: number[]) {
  return api('/admin/socialMedia', { method: 'DELETE', query: { ids: ids.join(',') } });
}

// —— 经历 ——
export function getExperience(type?: number) {
  return api<Experience[]>('/admin/experience', { query: { type } });
}

export function createExperience(body: unknown) {
  return api('/admin/experience', { method: 'POST', body });
}

export function updateExperience(body: unknown) {
  return api('/admin/experience', { method: 'PUT', body });
}

export function deleteExperience(ids: number[]) {
  return api('/admin/experience', { method: 'DELETE', query: { ids: ids.join(',') } });
}

// —— 技能 ——
export function getSkills() {
  return api<Skill[]>('/admin/skill');
}

export function createSkill(body: unknown) {
  return api('/admin/skill', { method: 'POST', body });
}

export function updateSkill(body: unknown) {
  return api('/admin/skill', { method: 'PUT', body });
}

export function deleteSkill(ids: number[]) {
  return api('/admin/skill', { method: 'DELETE', query: { ids: ids.join(',') } });
}

// —— 友链 ——
export function getFriendLinks() {
  return api<FriendLink[]>('/admin/friendLink');
}

export function createFriendLink(body: unknown) {
  return api('/admin/friendLink', { method: 'POST', body });
}

export function updateFriendLink(body: unknown) {
  return api('/admin/friendLink', { method: 'PUT', body });
}

export function deleteFriendLink(ids: number[]) {
  return api('/admin/friendLink', { method: 'DELETE', query: { ids: ids.join(',') } });
}
