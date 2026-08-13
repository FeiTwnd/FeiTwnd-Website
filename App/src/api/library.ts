import { api } from '@/lib/api-client';
import type { CityFootprint, CityImage, Music, PageResult } from '@/lib/types';

// 音乐与城市足迹走分页接口，但管理端列表量小，取一个足够大的单页即可。
const PAGE_SIZE = 500;

// —— 音乐 ——
export async function getMusic() {
  return api<PageResult<Music>>('/admin/music/page', { query: { page: 1, pageSize: PAGE_SIZE } });
}

export function createMusic(body: unknown) {
  return api('/admin/music', { method: 'POST', body });
}

export function updateMusic(body: unknown) {
  return api('/admin/music', { method: 'PUT', body });
}

export function deleteMusic(ids: number[]) {
  return api('/admin/music', { method: 'DELETE', query: { ids: ids.join(',') } });
}

// —— 城市足迹 ——
export async function getFootprints() {
  return api<PageResult<CityFootprint>>('/admin/footprint', { query: { page: 1, pageSize: PAGE_SIZE } });
}

export function createFootprint(body: unknown) {
  return api('/admin/footprint', { method: 'POST', body });
}

export function updateFootprint(body: unknown) {
  return api('/admin/footprint', { method: 'PUT', body });
}

export function deleteFootprint(ids: number[]) {
  return api('/admin/footprint', { method: 'DELETE', query: { ids: ids.join(',') } });
}

// —— 城市图片 ——
export function getCityImages(cityId: number) {
  return api<CityImage[]>('/admin/footprint/image', { query: { cityId } });
}

export function createCityImage(body: unknown) {
  return api('/admin/footprint/image', { method: 'POST', body });
}

export function updateCityImage(body: unknown) {
  return api('/admin/footprint/image', { method: 'PUT', body });
}

export function deleteCityImage(id: number) {
  return api('/admin/footprint/image', { method: 'DELETE', query: { id } });
}
