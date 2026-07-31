import axiosInstance from './axiosInstance';

export const staffRatingApi = {
  addRating: (staffId, data) => axiosInstance.post(`/staff-ratings/${staffId}`, data),
  getByStaff: (staffId) => axiosInstance.get(`/staff-ratings/${staffId}`),
  getAverageRating: (staffId) => axiosInstance.get(`/staff-ratings/${staffId}/average`)
};
