import axiosInstance from './axiosInstance';

export const dashboardApi = {
  // GET /dashboard/community/{communityId}
  getCommunityDashboard: (communityId) => axiosInstance.get(`/dashboard/community/${communityId}`)
};
