import axiosInstance from './axiosInstance';

export const domesticStaffApi = {
  create: (data) => axiosInstance.post('/staff', data),
  getById: (id) => axiosInstance.get(`/staff/${id}`),
  getByCommunity: (communityId) => axiosInstance.get(`/staff/community/${communityId}`),
  getByType: (communityId, type) => axiosInstance.get(`/staff/community/${communityId}/type`, { params: { type } }),
  getByStatus: (communityId, status) => axiosInstance.get(`/staff/community/${communityId}/status`, { params: { status } }),
  getByVerification: (communityId, status) => axiosInstance.get(`/staff/community/${communityId}/verification`, { params: { status } }),
  verify: (id, data) => axiosInstance.patch(`/staff/${id}/verify`, data),
  block: (id, data) => axiosInstance.patch(`/staff/${id}/block`, data),
  activate: (id) => axiosInstance.patch(`/staff/${id}/activate`),
  assign: (staffId, data) => axiosInstance.post(`/staff/${staffId}/assign`, data),
  getAssignments: (staffId) => axiosInstance.get(`/staff/${staffId}/assignments`),
  removeAssignment: (assignmentId) => axiosInstance.patch(`/staff/assignments/${assignmentId}/remove`),
  deleteStaff: (staffId) => axiosInstance.delete(`/staff/${staffId}`)
};
