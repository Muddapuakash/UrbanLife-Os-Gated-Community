import axiosInstance from './axiosInstance';

export const parcelApi = {
  // Create parcel (Security gate)
  create: (data) => axiosInstance.post('/parcels', data),

  // Get by ID
  getById: (id) => axiosInstance.get(`/parcels/${id}`),

  // ✅ GET parcels for a specific resident (used in MyParcelsPage)
  getByResident: (residentId) => axiosInstance.get(`/parcels/resident/${residentId}`),

  // GET parcels for a flat
  getByFlat: (flatId) => axiosInstance.get(`/parcels/flat/${flatId}`),

  // GET all parcels for community (Security/Admin)
  getByCommunity: (communityId) => axiosInstance.get(`/parcels/community/${communityId}`),

  // Notify resident their parcel arrived
  notify: (id) => axiosInstance.patch(`/parcels/${id}/notify`),

  // ✅ Collect parcel — backend expects @RequestBody { collectedByName }
  collect: (id, collectedByName) =>
    axiosInstance.patch(`/parcels/${id}/collect`, { collectedByName }),

  // Return parcel to sender
  return: (id, data) => axiosInstance.patch(`/parcels/${id}/return`, data || {}),

  // Pending count
  getPendingCount: (communityId) =>
    axiosInstance.get(`/parcels/community/${communityId}/pending/count`),
};
