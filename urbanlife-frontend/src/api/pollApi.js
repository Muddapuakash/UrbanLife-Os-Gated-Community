import axiosInstance from './axiosInstance';

export const pollApi = {
  create: (data) => axiosInstance.post('/polls', data),
  getById: (id) => axiosInstance.get(`/polls/${id}`),
  getByCommunity: (communityId) => axiosInstance.get(`/polls/community/${communityId}`),
  getActiveByCommunity: (communityId) => axiosInstance.get(`/polls/community/${communityId}/active`),
  activate: (id) => axiosInstance.patch(`/polls/${id}/activate`),
  close: (id) => axiosInstance.patch(`/polls/${id}/close`),
  delete: (id) => axiosInstance.delete(`/polls/${id}`)
};
