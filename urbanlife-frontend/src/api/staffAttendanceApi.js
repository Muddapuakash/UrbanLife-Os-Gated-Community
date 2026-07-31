import axiosInstance from './axiosInstance';

export const staffAttendanceApi = {
  recordEntry: (staffId, recordedByUserId) => axiosInstance.post(`/staff-attendance/${staffId}/entry`, { recordedByUserId }),
  recordExit: (staffId, recordedByUserId) => axiosInstance.patch(`/staff-attendance/${staffId}/exit`, { recordedByUserId }),
  getStaffHistory: (staffId) => axiosInstance.get(`/staff-attendance/${staffId}/history`),
  getCurrentlyInside: (communityId) => axiosInstance.get(`/staff-attendance/community/${communityId}/inside`)
};
