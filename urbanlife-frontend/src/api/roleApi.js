import axiosInstance from './axiosInstance';

export const roleApi = {
  create: (data) => axiosInstance.post('/roles', data),
  getAll: () => axiosInstance.get('/roles'),
  getById: (id) => axiosInstance.get(`/roles/${id}`),
  update: (id, data) => axiosInstance.put(`/roles/${id}`, data),
  delete: (id) => axiosInstance.delete(`/roles/${id}`)
};
