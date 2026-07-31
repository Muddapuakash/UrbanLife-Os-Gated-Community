import axiosInstance from './axiosInstance';

export const voteApi = {
  // POST /votes/poll/{pollId}/user/{userId} with body { optionId }
  castVote: (pollId, userId, optionId) =>
    axiosInstance.post(`/votes/poll/${pollId}/user/${userId}`, { optionId }),

  // GET /votes/poll/{pollId}/user/{userId}
  getUserVote: (pollId, userId) =>
    axiosInstance.get(`/votes/poll/${pollId}/user/${userId}`),

  // GET /votes/user/{userId}
  getUserVotes: (userId) =>
    axiosInstance.get(`/votes/user/${userId}`)
};
