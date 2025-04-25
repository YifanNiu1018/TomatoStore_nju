// 在product.ts中添加
import { axios } from '@/utils/request'
export interface AdvertisementVO {
    id: number
    title: string
    content: string
    imageUrl: string
    productId: string
}

let advertisementCache: AdvertisementVO[] | null = null;
let lastFetchTime = 0;
const CACHE_DURATION = 5 * 60 * 1000; // 缓存5分钟

export const getAdById = async (id: string): Promise<AdvertisementVO> => {
    const now = Date.now();
    if (!advertisementCache || now - lastFetchTime > CACHE_DURATION) {
        try {
            const res = await getAllAdvertisements();
            advertisementCache = res.data.data;
            lastFetchTime = now;
        } catch (error) {
            throw new Error('获取广告数据失败');
        }
    }

    const foundAd = advertisementCache.find(ad => ad.id == id);

    if (!foundAd) {
        throw new Error('未找到指定ID的广告');
    }

    return {
        ...foundAd,
        imageUrl: foundAd.imageUrl || foundAd.imageUrl
    } as AdvertisementVO;
};


export const createAd = (advertisement: AdvertisementVO) => {
    return axios.post<AdvertisementVO>('/api/advertisements', advertisement)
        .then(res => res.data)
}

export const getAllAdvertisements = () => {
    return axios.get('/api/advertisements')
    .then(res => {
        return res;
    })
}

export const deleteAd = (id: number) => {
    return axios.delete(`/api/advertisements/${id}`)
    .then(res => {
        return res;
    })
}

export const updateAd = (ad: AdvertisementVO) => {
    return axios.put(`/api/advertisements`, ad)
    .then(res => {
        return res;
    })
}