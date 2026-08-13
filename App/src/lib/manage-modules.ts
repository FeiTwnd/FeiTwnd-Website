import { router } from 'expo-router';

import * as article from '@/api/article';
import * as library from '@/api/library';
import * as site from '@/api/site';
import type { CrudConfig } from '@/components/crud';

/**
 * 高频内容模块的注册表：字段配置驱动通用 CRUD 页面。
 * 键即路由 /manage/<key>，profile 菜单据此跳转。
 */
export const MANAGE_MODULES: Record<string, CrudConfig<any>> = {
  'social-media': {
    title: '社交媒体',
    titleKey: 'name',
    subtitleKey: 'link',
    showVisible: true,
    fields: [
      { key: 'name', label: '名称', required: true },
      { key: 'icon', label: '图标类名' },
      { key: 'link', label: '链接' },
      { key: 'sort', label: '排序', type: 'number' },
      { key: 'isVisible', label: '是否可见', type: 'toggle' },
    ],
    fetch: () => site.getSocialMedia(),
    create: (d) => site.createSocialMedia(d),
    update: (id, d) => site.updateSocialMedia({ ...d, id }),
    remove: (id) => site.deleteSocialMedia([id]),
  },

  experience: {
    title: '经历',
    titleKey: 'title',
    subtitleKey: 'subtitle',
    showVisible: true,
    fields: [
      {
        key: 'type',
        label: '类型',
        type: 'select',
        required: true,
        options: [
          { label: '教育', value: 0 },
          { label: '工作', value: 1 },
          { label: '项目', value: 2 },
        ],
      },
      { key: 'title', label: '标题', required: true },
      { key: 'subtitle', label: '副标题' },
      { key: 'logoUrl', label: 'Logo', type: 'image' },
      { key: 'content', label: '内容', type: 'textarea', required: true },
      { key: 'startDate', label: '开始时间', required: true, placeholder: 'yyyy-MM-dd' },
      { key: 'endDate', label: '结束时间', placeholder: 'yyyy-MM-dd' },
      { key: 'isVisible', label: '是否可见', type: 'toggle' },
    ],
    fetch: () => site.getExperience(),
    create: (d) => site.createExperience(d),
    update: (id, d) => site.updateExperience({ ...d, id }),
    remove: (id) => site.deleteExperience([id]),
  },

  skill: {
    title: '技能',
    titleKey: 'name',
    subtitleKey: 'description',
    showVisible: true,
    fields: [
      { key: 'name', label: '名称', required: true },
      { key: 'description', label: '描述', type: 'textarea' },
      { key: 'icon', label: '图标', type: 'image' },
      { key: 'sort', label: '排序', type: 'number' },
      { key: 'isVisible', label: '是否可见', type: 'toggle' },
    ],
    fetch: () => site.getSkills(),
    create: (d) => site.createSkill(d),
    update: (id, d) => site.updateSkill({ ...d, id }),
    remove: (id) => site.deleteSkill([id]),
  },

  'friend-link': {
    title: '友链',
    titleKey: 'name',
    subtitleKey: 'url',
    showVisible: true,
    fields: [
      { key: 'name', label: '网站名称', required: true },
      { key: 'url', label: '网站地址', required: true },
      { key: 'avatarUrl', label: '头像', type: 'image' },
      { key: 'description', label: '描述', type: 'textarea' },
      { key: 'sort', label: '排序', type: 'number' },
      { key: 'isVisible', label: '是否可见', type: 'toggle' },
    ],
    fetch: () => site.getFriendLinks(),
    create: (d) => site.createFriendLink(d),
    update: (id, d) => site.updateFriendLink({ ...d, id }),
    remove: (id) => site.deleteFriendLink([id]),
  },

  music: {
    title: '音乐',
    titleKey: 'title',
    subtitleKey: 'artist',
    showVisible: true,
    fields: [
      { key: 'title', label: '标题', required: true },
      { key: 'artist', label: '作者' },
      { key: 'duration', label: '时长(秒)', type: 'number' },
      { key: 'coverImage', label: '封面', type: 'image' },
      { key: 'musicUrl', label: '音频 URL', required: true },
      { key: 'lyricUrl', label: '歌词 URL' },
      { key: 'hasLyric', label: '是否有歌词', type: 'toggle' },
      { key: 'lyricType', label: '歌词类型', placeholder: 'lrc / json / txt' },
      { key: 'sort', label: '排序', type: 'number' },
      { key: 'isVisible', label: '是否可见', type: 'toggle' },
    ],
    fetch: async () => (await library.getMusic()).records,
    create: (d) => library.createMusic(d),
    update: (id, d) => library.updateMusic({ ...d, id }),
    remove: (id) => library.deleteMusic([id]),
  },

  category: {
    title: '文章分类',
    titleKey: 'name',
    subtitleKey: 'slug',
    fields: [
      { key: 'name', label: '名称', required: true },
      { key: 'slug', label: 'URL 标识', required: true },
      { key: 'description', label: '描述', type: 'textarea' },
      { key: 'sort', label: '排序', type: 'number' },
    ],
    fetch: () => article.getCategories(),
    create: (d) => article.createCategory(d),
    update: (id, d) => article.updateCategory({ ...d, id }),
    remove: (id) => article.deleteCategory([id]),
  },

  tag: {
    title: '文章标签',
    titleKey: 'name',
    subtitleKey: 'slug',
    fields: [
      { key: 'name', label: '名称', required: true },
      { key: 'slug', label: 'URL 标识', required: true },
    ],
    fetch: () => article.getTags(),
    create: (d) => article.createTag(d),
    update: (id, d) => article.updateTag({ ...d, id }),
    remove: (id) => article.deleteTag([id]),
  },

  footprint: {
    title: '城市足迹',
    titleKey: 'cityName',
    subtitleKey: 'visitTime',
    showVisible: true,
    fields: [
      { key: 'cityCode', label: '城市编码', required: true },
      { key: 'cityName', label: '城市名称', required: true },
      { key: 'visitTime', label: '访问时间', placeholder: 'yyyy-MM-dd' },
      { key: 'isVisible', label: '是否可见', type: 'toggle' },
    ],
    fetch: async () => (await library.getFootprints()).records,
    create: (d) => library.createFootprint(d),
    update: (id, d) => library.updateFootprint({ ...d, id }),
    remove: (id) => library.deleteFootprint([id]),
    extraActions: (item) => [
      {
        label: '图片',
        onPress: () =>
          router.push({
            pathname: '/manage/footprint-images',
            params: { cityId: String(item.id), cityName: item.cityName },
          }),
      },
    ],
  },
};
