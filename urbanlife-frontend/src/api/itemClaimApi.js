import axiosInstance from './axiosInstance';

export const itemClaimApi = {
  claim: (data) => axiosInstance.post('/item-claims', data),
  claimItem: (itemId, userId, data) => axiosInstance.post(`/item-claims/item/${itemId}/user/${userId}`, data),
  getById: (id) => axiosInstance.get(`/item-claims/${id}`),
  getByItem: (itemId) => axiosInstance.get(`/item-claims/item/${itemId}`),
  getByUser: (userId) => axiosInstance.get(`/item-claims/user/${userId}`),
  approve: (id) => axiosInstance.patch(`/item-claims/${id}/approve`),
  reject: (id) => axiosInstance.patch(`/item-claims/${id}/reject`),
  cancel: (claimId, userId) => axiosInstance.patch(`/item-claims/${claimId}/cancel/user/${userId}`),
  markReturned: (id) => axiosInstance.patch(`/item-claims/${id}/returned`)
};
