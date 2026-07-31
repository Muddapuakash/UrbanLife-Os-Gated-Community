import axiosInstance from './axiosInstance';

export const blockApi = {
  create: (data) => axiosInstance.post('/blocks', data),
  getAll: () => axiosInstance.get('/blocks'),
  getById: (id) => axiosInstance.get(`/blocks/${id}`),
  getByCommunity: (communityId) => axiosInstance.get(`/blocks/community/${communityId}`),
  update: (id, data) => axiosInstance.put(`/blocks/${id}`, data),
  delete: (id) => axiosInstance.delete(`/blocks/${id}`)
};
