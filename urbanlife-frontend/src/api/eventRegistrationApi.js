import axiosInstance from './axiosInstance';

export const eventRegistrationApi = {
  register: (data) => axiosInstance.post('/event-registrations', data),
  cancel: (id) => axiosInstance.patch(`/event-registrations/${id}/cancel`),
  markAttendance: (id, attended) => axiosInstance.patch(`/event-registrations/${id}/attendance?attended=${attended}`),
  getByEvent: (eventId) => axiosInstance.get(`/event-registrations/event/${eventId}`),
  getByUser: (userId) => axiosInstance.get(`/event-registrations/user/${userId}`)
};
