import axiosInstance from './axiosInstance';

export const parkingAllocationApi = {
  // POST /parking-allocations
  allocate: (data) => axiosInstance.post('/parking-allocations', data),

  // GET /parking-allocations/{allocationId}
  getById: (id) => axiosInstance.get(`/parking-allocations/${id}`),

  // GET /parking-allocations/active
  getActive: () => axiosInstance.get('/parking-allocations/active'),

  // GET /parking-allocations/community/{communityId}
  getByCommunity: (communityId) => axiosInstance.get(`/parking-allocations/community/${communityId}`),

  // GET /parking-allocations/resident/{residentId}
  getByResident: (residentId) => axiosInstance.get(`/parking-allocations/resident/${residentId}`),

  // PATCH /parking-allocations/{allocationId}/release
  release: (id) => axiosInstance.patch(`/parking-allocations/${id}/release`)
};
