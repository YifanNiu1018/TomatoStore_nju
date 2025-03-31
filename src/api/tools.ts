import {axios} from "@/utils/request.ts";

export const uploadImage = (file: File) => {
  const formData = new FormData();
  formData.append('multipartFile', file);
  return axios.post('/api/images', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(res => {
    return res;
  });
};
