import axiosInstance from './axiosInstance';

export const emergencyUpdateApi = {
  create: (emergencyId, data) => axiosInstance.post(`/emergency-updates/emergency/${emergencyId}`, data),
  getByEmergency: (emergencyId) => axiosInstance.get(`/emergency-updates/emergency/${emergencyId}`)
};
