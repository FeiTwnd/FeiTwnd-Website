export interface PageResult<T> {
  total: number;
  records: T[];
}

export interface AdminVO {
  id: number;
  nickname: string;
  email: string;
  role: number;
}

export interface AdminOverviewVO {
  totalViewCount: number;
  totalVisitorCount: number;
  todayViewCount: number;
  todayNewVisitorCount: number;
  totalArticleCount: number;
  totalCommentCount: number;
  totalMessageCount: number;
  pendingCommentCount: number;
  pendingMessageCount: number;
}

export interface ArticleViewTop10VO {
  titleList: string[];
  viewCountList: number[];
}

export interface CommentItem {
  id: number;
  articleId: number;
  rootId: number | null;
  parentId: number | null;
  parentNickname: string | null;
  content: string;
  articleTitle?: string;
  nickname: string;
  emailOrQq?: string;
  location?: string;
  isApproved: number;
  isAdminReply: number;
  createTime: string;
}

export interface MessageItem {
  id: number;
  rootId: number | null;
  parentId: number | null;
  parentNickname: string | null;
  content: string;
  nickname: string;
  emailOrQq?: string;
  location?: string;
  isApproved: number;
  isAdminReply: number;
  createTime: string;
}

export interface ArticleItem {
  id: number;
  title: string;
  slug: string;
  summary?: string;
  coverImage?: string;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  isPublished: number;
  isTop: number;
  publishTime?: string;
  updateTime: string;
}

export interface ArticleDetail extends ArticleItem {
  contentMarkdown?: string;
  contentHtml?: string;
  categoryId?: number;
  wordCount?: number;
  readingTime?: number;
  publishDate?: string;
  tagIds?: number[];
}

export interface VisitorItem {
  id: number;
  fingerprint?: string;
  ip?: string;
  country?: string;
  province?: string;
  city?: string;
  totalViews: number;
  isBlocked: number;
  lastVisitTime: string;
}

export interface PersonalInfo {
  id: number;
  nickname: string;
  tag: string;
  description?: string;
  avatar?: string;
  website?: string;
  email?: string;
  github?: string;
  location?: string;
  updateTime?: string;
}

export interface SocialMedia {
  id: number;
  name: string;
  icon?: string;
  link?: string;
  sort?: number;
  isVisible?: number;
}

export interface Experience {
  id: number;
  type: number;
  title: string;
  subtitle?: string;
  logoUrl?: string;
  content: string;
  startDate: string;
  endDate?: string;
  isVisible?: number;
}

export interface Skill {
  id: number;
  name: string;
  description?: string;
  icon?: string;
  sort?: number;
  isVisible?: number;
}

export interface FriendLink {
  id: number;
  name: string;
  url: string;
  avatarUrl?: string;
  description?: string;
  sort?: number;
  isVisible?: number;
}

export interface Music {
  id: number;
  title: string;
  artist?: string;
  duration?: number;
  coverImage?: string;
  musicUrl: string;
  lyricUrl?: string;
  hasLyric?: number;
  lyricType?: string;
  sort?: number;
  isVisible?: number;
}

export interface ArticleCategory {
  id: number;
  name: string;
  slug: string;
  description?: string;
  sort?: number;
  articleCount?: number;
}

export interface ArticleTag {
  id: number;
  name: string;
  slug: string;
  articleCount?: number;
}

export interface CityFootprint {
  id: number;
  cityCode: string;
  cityName: string;
  visitTime?: string;
  isVisible?: number;
}

export interface CityImage {
  id: number;
  cityId: number;
  imageUrl: string;
  sort?: number;
  isVisible?: number;
}