import axiosInstance from './axiosInstance';

export const emergencyApi = {
  create: (data) => axiosInstance.post('/emergencies', data),
  getById: (id) => axiosInstance.get(`/emergencies/${id}`),
  getByCommunity: (communityId) => axiosInstance.get(`/emergencies/community/${communityId}`),
  getActiveByCommunity: (communityId) => axiosInstance.get(`/emergencies/community/${communityId}/active`),
  acknowledge: (id) => axiosInstance.patch(`/emergencies/${id}/acknowledge`),
  assign: (id, userId) => axiosInstance.patch(`/emergencies/${id}/assign/${userId}`),
  start: (id) => axiosInstance.patch(`/emergencies/${id}/start`),
  resolve: (id, resolutionNotes) => axiosInstance.patch(`/emergencies/${id}/resolve`, { resolutionNotes }),
  cancel: (id) => axiosInstance.patch(`/emergencies/${id}/cancel`)
};
