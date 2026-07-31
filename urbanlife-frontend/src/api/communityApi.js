import axiosInstance from './axiosInstance';

export const communityApi = {
  create: (data) => axiosInstance.post('/communities', data),
  getAll: () => axiosInstance.get('/communities'),
  getById: (id) => axiosInstance.get(`/communities/${id}`),
  update: (id, data) => axiosInstance.put(`/communities/${id}`, data),
  delete: (id) => axiosInstance.delete(`/communities/${id}`)
};
