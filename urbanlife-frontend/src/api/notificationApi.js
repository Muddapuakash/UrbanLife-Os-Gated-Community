import axiosInstance from './axiosInstance';

export const notificationApi = {
  create: (data) => axiosInstance.post('/notifications', data),
  getById: (id) => axiosInstance.get(`/notifications/${id}`),
  getByUser: (userId) => axiosInstance.get(`/notifications/user/${userId}`),
  getUnreadCount: (userId) => axiosInstance.get(`/notifications/user/${userId}/unread-count`),
  markAsRead: (id) => axiosInstance.patch(`/notifications/${id}/read`),
  markAllAsRead: (userId) => axiosInstance.patch(`/notifications/user/${userId}/read-all`),
  delete: (id) => axiosInstance.delete(`/notifications/${id}`),
  broadcastCommunity: (communityId, data) => axiosInstance.post(`/notifications/broadcast/community/${communityId}`, data)
};
