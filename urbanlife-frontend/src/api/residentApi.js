import axiosInstance from './axiosInstance';

export const residentApi = {
  create: (data) => axiosInstance.post('/residents', data),
  getById: (id) => axiosInstance.get(`/residents/${id}`),
  getByUserId: (userId) => axiosInstance.get(`/residents/user/${userId}`),
  getByFlat: (flatId) => axiosInstance.get(`/residents/flat/${flatId}`),
  getByCommunity: (communityId) => axiosInstance.get(`/residents/community/${communityId}`),
  update: (id, data) => axiosInstance.put(`/residents/${id}`, data),
  moveOut: (id) => axiosInstance.patch(`/residents/${id}/move-out`)
};
