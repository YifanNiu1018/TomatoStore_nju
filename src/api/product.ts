import { axios } from '../utils/request'
import type {AdvertisementVO} from "@/api/advertise.ts";

export interface ProductVO {
  id?: number;
  title: string
  price: number
  rate?: number
  description: string
  cover: string
  detail: string
  specifications?: SpecificationVO[]
  stockpile?: StockpileVO
}

export interface SpecificationVO {
  id?: number
  item: string
  value: string
  productId?: number
}

export interface StockpileVO {
  id?: number
  productId?: number
  amount: number
  frozen: number
}

export const getAllProducts = () => {
  return axios.get('/api/products')
    .then(res => {
      return res;
    })
}

export const createProduct = (product: ProductVO) => {
  return axios.post('/api/products', product)
    .then(res => {
      return res;
    })
}

export const getProductById = (id: number) => {
  return axios.get(`/api/products/${id}`)
    .then(res => {
      return res;
    })
}

export const getStockpile = (productId: number) => {
  return axios.get(`/api/products/stockpile/${productId}`)
    .then(res => {
      return res;
    })
}

export const deleteProduct = async (id: number) => {
  try {
    const adsRes = await axios.get<{ data: AdvertisementVO[] }>('/api/advertisements');
    const allAds = adsRes.data.data;

    const relatedAds = allAds.filter(ad => ad.productId === id);
    await Promise.all(
        relatedAds.map(ad =>
            axios.delete(`/api/advertisements/${ad.id}`)
                .catch(error => {
                  console.error(`DELETE ${ad.id} failed:`, error);
                  throw new Error(`Part DELETE (Ad ID: ${ad.id})`);
                })
        )
    );

    const productRes = await axios.delete(`/api/products/${id}`);
    return productRes;

  } catch (error) {
    console.error('DELETE error:', error);
    throw new Error(`DELETE error: ${error.message}`);
  }
};

export const updateProduct = (product: ProductVO) => {
  return axios.put(`/api/products`, product)
  .then(res => {
    return res;
  })
}


export const updateStockpile = (productId: number, stockpile: StockpileVO) => {
  return axios.patch(`/api/products/stockpile/${productId}`, stockpile)
  .then(res => {
    return res;
  })
}

export const addToCart = (productId: number, quantity: number) => {
  return axios.post('/api/cart', {
    productid: productId,  // 改为全小写，匹配后端字段
    quantity: quantity
  });
};