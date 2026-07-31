import axiosInstance from './axiosInstance';

export const vehicleApi = {
  create: (data) => axiosInstance.post('/vehicles', data),
  getById: (id) => axiosInstance.get(`/vehicles/${id}`),
  getByNumber: (num) => axiosInstance.get(`/vehicles/number/${num}`),
  getByResident: (residentId) => axiosInstance.get(`/vehicles/resident/${residentId}`),
  getByCommunity: (communityId) => axiosInstance.get(`/vehicles/community/${communityId}`),
  deactivate: (id) => axiosInstance.patch(`/vehicles/${id}/deactivate`)
};
