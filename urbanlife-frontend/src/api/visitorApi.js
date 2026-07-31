import axiosInstance from './axiosInstance';

export const visitorApi = {
  create: (data) => axiosInstance.post('/visitors', data),
  getById: (id) => axiosInstance.get(`/visitors/${id}`),
  // Correct endpoint: /pass/{passCode}
  getByPassCode: (code) => axiosInstance.get(`/visitors/pass/${code}`),
  getByFlat: (flatId) => axiosInstance.get(`/visitors/flat/${flatId}`),
  getByResident: (residentId) => axiosInstance.get(`/visitors/resident/${residentId}`),
  getByCommunity: (communityId) => axiosInstance.get(`/visitors/community/${communityId}`),
  // Check-in: POST body with { passCode, securityUserId }
  checkIn: (passCode, securityUserId) => axiosInstance.patch('/visitors/check-in', { passCode, securityUserId }),
  // Check-out: PATCH /{visitorId}/check-out with body { securityUserId }
  checkOut: (visitorId, securityUserId) => axiosInstance.patch(`/visitors/${visitorId}/check-out`, { securityUserId }),
  approve: (id, data) => axiosInstance.patch(`/visitors/${id}/approval`, data),
  cancel: (id) => axiosInstance.patch(`/visitors/${id}/cancel`)
};
