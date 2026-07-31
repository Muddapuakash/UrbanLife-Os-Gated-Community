import axiosInstance from './axiosInstance';

export const maintenancePaymentApi = {
  // Backend endpoint: POST /maintenance-payments/bill/{billId}
  pay: (billId, data) => axiosInstance.post(`/maintenance-payments/bill/${billId}`, data),
  getById: (id) => axiosInstance.get(`/maintenance-payments/${id}`),
  getByBill: (billId) => axiosInstance.get(`/maintenance-payments/bill/${billId}`),
  getByFlat: (flatId) => axiosInstance.get(`/maintenance-payments/flat/${flatId}`),
  getByCommunity: (communityId) => axiosInstance.get(`/maintenance-payments/community/${communityId}`)
};
