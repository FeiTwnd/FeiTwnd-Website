import { Stack, useLocalSearchParams } from 'expo-router';
import { useMemo } from 'react';

import { createCityImage, deleteCityImage, getCityImages, updateCityImage } from '@/api/library';
import { CrudScreen, type CrudConfig } from '@/components/crud';
import type { CityImage } from '@/lib/types';

export default function FootprintImagesScreen() {
  const { cityId } = useLocalSearchParams<{ cityId: string }>();
  const cid = Number(cityId);

  const config = useMemo<CrudConfig<CityImage>>(
    () => ({
      title: '城市图片',
      imageKey: 'imageUrl',
      fields: [
        { key: 'imageUrl', label: '图片', type: 'image', required: true },
        { key: 'sort', label: '排序', type: 'number' },
        { key: 'isVisible', label: '是否可见', type: 'toggle' },
      ],
      fetch: () => getCityImages(cid),
      create: (d) => createCityImage({ ...d, cityId: cid }),
      update: (id, d) => updateCityImage({ ...d, id }),
      remove: (id) => deleteCityImage(id),
    }),
    [cid]
  );

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: '城市图片' }} />
      <CrudScreen config={config} />
    </>
  );
}
