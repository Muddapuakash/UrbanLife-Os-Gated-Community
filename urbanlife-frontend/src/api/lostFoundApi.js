import axiosInstance from './axiosInstance';

export const lostFoundApi = {
  // POST /lost-found
  reportItem: (data) => axiosInstance.post('/lost-found', data),
  create:     (data) => axiosInstance.post('/lost-found', data),

  // GET /lost-found/{itemId}
  getById: (id) => axiosInstance.get(`/lost-found/${id}`),

  // GET /lost-found/community/{communityId}
  getByCommunity: (communityId) => axiosInstance.get(`/lost-found/community/${communityId}`),

  // GET /lost-found/community/{communityId}/open?type=LOST|FOUND
  getOpenItems: (communityId, type) => axiosInstance.get(`/lost-found/community/${communityId}/open`, { params: { type } }),

  // GET /lost-found/community/{communityId}/category?category=KEYS
  getByCategory: (communityId, category) => axiosInstance.get(`/lost-found/community/${communityId}/category`, { params: { category } }),

  // GET /lost-found/user/{userId}
  getByUser: (userId) => axiosInstance.get(`/lost-found/user/${userId}`),

  // PUT /lost-found/{itemId}/user/{userId}
  update: (itemId, userId, data) => axiosInstance.put(`/lost-found/${itemId}/user/${userId}`, data),

  // PATCH /lost-found/{itemId}/close
  close: (id) => axiosInstance.patch(`/lost-found/${id}/close`),

  // DELETE /lost-found/{itemId}/user/{userId}
  delete: (itemId, userId) => axiosInstance.delete(`/lost-found/${itemId}/user/${userId}`)
};
