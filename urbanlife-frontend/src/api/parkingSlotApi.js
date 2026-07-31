import axiosInstance from './axiosInstance';

export const parkingSlotApi = {
  // POST /parking-slots
  create: (data) => axiosInstance.post('/parking-slots', data),

  // GET /parking-slots
  getAll: () => axiosInstance.get('/parking-slots'),

  // GET /parking-slots/{id}
  getById: (id) => axiosInstance.get(`/parking-slots/${id}`),

  // GET /parking-slots/community/{communityId}
  getByCommunity: (communityId) => axiosInstance.get(`/parking-slots/community/${communityId}`),

  // GET /parking-slots/community/{communityId}/status?status=AVAILABLE
  getAvailableByCommunity: (communityId) =>
    axiosInstance.get(`/parking-slots/community/${communityId}/status`, { params: { status: 'AVAILABLE' } })
};
