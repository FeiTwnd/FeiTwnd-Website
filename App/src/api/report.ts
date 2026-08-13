import { api } from '@/lib/api-client';
import type { AdminOverviewVO, ArticleViewTop10VO } from '@/lib/types';

export function getOverview() {
  return api<AdminOverviewVO>('/admin/report/overview');
}

export function getArticleViewTop10() {
  return api<ArticleViewTop10VO>('/admin/report/articleViewTop10');
}

export interface ViewReportVO {
  dateList: string;
  viewCountList: string;
}

export interface VisitorReportVO {
  dateList: string;
  newVisitorCountList: string;
  totalVisitorCountList: string;
}

export interface ProvinceVisitorVO {
  provinceList: string;
  countList: string;
}

export function getViewStatistics(begin: string, end: string) {
  return api<ViewReportVO>('/admin/report/viewStatistics', { query: { begin, end } });
}

export function getVisitorStatistics(begin: string, end: string) {
  return api<VisitorReportVO>('/admin/report/visitorStatistics', { query: { begin, end } });
}

export function getProvinceDistribution() {
  return api<ProvinceVisitorVO>('/admin/report/provinceDistribution');
}