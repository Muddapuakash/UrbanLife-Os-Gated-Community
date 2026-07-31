import axiosInstance from './axiosInstance';

export const flatApi = {
  create: (data) => axiosInstance.post('/flats', data),
  getById: (id) => axiosInstance.get(`/flats/${id}`),
  getByBlock: (blockId) => axiosInstance.get(`/flats/block/${blockId}`),
  getByCommunity: (communityId) => axiosInstance.get(`/flats/community/${communityId}`),
  update: (id, data) => axiosInstance.put(`/flats/${id}`, data),
  delete: (id) => axiosInstance.delete(`/flats/${id}`)
};
