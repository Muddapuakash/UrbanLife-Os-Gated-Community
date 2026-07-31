import axiosInstance from './axiosInstance';

export const amenityApi = {
  create: (data) => axiosInstance.post('/amenities', data),
  getById: (id) => axiosInstance.get(`/amenities/${id}`),
  getByCommunity: (communityId) => axiosInstance.get(`/amenities/community/${communityId}`),
  updateStatus: (id, isActive) => axiosInstance.patch(`/amenities/${id}/status?isActive=${isActive}`)
};
