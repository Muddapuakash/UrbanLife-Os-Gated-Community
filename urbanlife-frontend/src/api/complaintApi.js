import axiosInstance from './axiosInstance';

export const complaintApi = {
  create: (data) => axiosInstance.post('/complaints', data),
  getById: (id) => axiosInstance.get(`/complaints/${id}`),
  getByResident: (residentId) => axiosInstance.get(`/complaints/resident/${residentId}`),
  // Backend endpoint: /complaints/assigned/{userId}
  getByAssignedStaff: (userId) => axiosInstance.get(`/complaints/assigned/${userId}`),
  getByCommunity: (communityId) => axiosInstance.get(`/complaints/community/${communityId}`),
  assign: (id, userId) => axiosInstance.patch(`/complaints/${id}/assign`, { userId }),
  // Backend endpoint: PATCH /complaints/{id}/status with body { status, resolutionNote }
  updateStatus: (id, status, resolutionNote) => axiosInstance.patch(`/complaints/${id}/status`, { status, resolutionNote }),
  delete: (id) => axiosInstance.delete(`/complaints/${id}`)
};
