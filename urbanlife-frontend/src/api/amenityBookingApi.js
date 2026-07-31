import axiosInstance from './axiosInstance';

export const amenityBookingApi = {
  create: (data) => axiosInstance.post('/amenity-bookings', data),
  getById: (id) => axiosInstance.get(`/amenity-bookings/${id}`),
  getByResident: (residentId) => axiosInstance.get(`/amenity-bookings/resident/${residentId}`),
  getByAmenity: (amenityId) => axiosInstance.get(`/amenity-bookings/amenity/${amenityId}`),
  cancel: (id) => axiosInstance.patch(`/amenity-bookings/${id}/cancel`),
  complete: (id) => axiosInstance.patch(`/amenity-bookings/${id}/complete`)
};
