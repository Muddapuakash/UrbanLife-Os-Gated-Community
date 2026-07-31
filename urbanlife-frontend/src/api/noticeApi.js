import axiosInstance from './axiosInstance';

export const noticeApi = {
  create: (data) => axiosInstance.post('/notices', data),
  getById: (id) => axiosInstance.get(`/notices/${id}`),
  getByCommunity: (communityId) => axiosInstance.get(`/notices/community/${communityId}`),
  getPublishedByCommunity: (communityId) => axiosInstance.get(`/notices/community/${communityId}/published`),
  publish: (id) => axiosInstance.patch(`/notices/${id}/publish`),
  cancel: (id) => axiosInstance.patch(`/notices/${id}/cancel`),
  delete: (id) => axiosInstance.delete(`/notices/${id}`)
};
