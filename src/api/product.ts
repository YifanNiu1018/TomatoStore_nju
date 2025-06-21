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

/**
 * 搜索商品
 * @param keyword 搜索关键词
 * @param page 页码
 * @param pageSize 每页数量
 */
export const searchProducts = async (keyword: string, page: number = 1, pageSize: number = 12) => {
  try {
    // 如果后端有专门的搜索接口，使用这个
    // return axios.get('/api/products/search', {
    //   params: { keyword, page, pageSize }
    // });

    // 如果没有专门的搜索接口，我们获取所有商品然后前端过滤
    const response = await getAllProducts();
    const allProducts = response.data.data || [];

    // 前端搜索过滤
    const filteredProducts = allProducts.filter((product: ProductVO) => {
      const searchText = keyword.toLowerCase();
      return (
        product.title?.toLowerCase().includes(searchText) ||
        product.description?.toLowerCase().includes(searchText) ||
        product.specifications?.some(spec =>
          spec.item?.toLowerCase().includes(searchText) ||
          spec.value?.toLowerCase().includes(searchText)
        )
      );
    });

    // 分页处理
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedResults = filteredProducts.slice(startIndex, endIndex);

    return {
      data: {
        data: paginatedResults,
        total: filteredProducts.length,
        page,
        pageSize
      }
    };
  } catch (error) {
    console.error('搜索商品失败:', error);
    throw error;
  }
};