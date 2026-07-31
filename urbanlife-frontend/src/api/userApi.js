import axiosInstance from './axiosInstance';

export const userApi = {
  register: (data) => axiosInstance.post('/users/register', data),
  create: (data) => axiosInstance.post('/users', data),
  getAll: () => axiosInstance.get('/users'),
  getById: (id) => axiosInstance.get(`/users/${id}`),
  getByEmail: (email) => axiosInstance.get(`/users/email/${email}`),
  update: (id, data) => axiosInstance.put(`/users/${id}`, data),
  delete: (id) => axiosInstance.delete(`/users/${id}`)
};
