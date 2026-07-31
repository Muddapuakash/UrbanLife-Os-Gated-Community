import axiosInstance from './axiosInstance';

export const eventApi = {
  create: (data) => axiosInstance.post('/events', data),
  getById: (id) => axiosInstance.get(`/events/${id}`),
  getByCommunity: (communityId) => axiosInstance.get(`/events/community/${communityId}`),
  update: (id, data) => axiosInstance.put(`/events/${id}`, data),
  cancel: (id) => axiosInstance.patch(`/events/${id}/cancel`),
  delete: (id) => axiosInstance.delete(`/events/${id}`)
};
