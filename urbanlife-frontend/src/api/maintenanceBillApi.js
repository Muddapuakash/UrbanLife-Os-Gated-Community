import axiosInstance from './axiosInstance';

export const maintenanceBillApi = {
  create: (data) => axiosInstance.post('/maintenance-bills', data),
  generate: (data) => axiosInstance.post('/maintenance-bills', data),
  getById: (id) => axiosInstance.get(`/maintenance-bills/${id}`),
  getByBillNumber: (num) => axiosInstance.get(`/maintenance-bills/number/${num}`),
  getByFlat: (flatId) => axiosInstance.get(`/maintenance-bills/flat/${flatId}`),
  getByCommunity: (communityId) => axiosInstance.get(`/maintenance-bills/community/${communityId}`),
  cancel: (id) => axiosInstance.patch(`/maintenance-bills/${id}/cancel`),
  markOverdue: (id) => axiosInstance.patch(`/maintenance-bills/${id}/mark-overdue`)
};
