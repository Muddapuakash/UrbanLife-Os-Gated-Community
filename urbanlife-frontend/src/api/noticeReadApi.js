import axiosInstance from './axiosInstance';

export const noticeReadApi = {
  markRead: (noticeId, userId) => axiosInstance.post(`/notice-reads/notice/${noticeId}/user/${userId}`),
  getUserHistory: (userId) => axiosInstance.get(`/notice-reads/user/${userId}`),
  getNoticeReaders: (noticeId) => axiosInstance.get(`/notice-reads/notice/${noticeId}`),
  getReadCount: (noticeId) => axiosInstance.get(`/notice-reads/notice/${noticeId}/count`)
};
