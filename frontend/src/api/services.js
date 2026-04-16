import { apiClient } from './client';

export const AuthService = {
  login: (data) => apiClient.post('/api/v1/auth/login', data),
  register: (data) => apiClient.post('/api/v1/auth/register', data),
  refresh: (refreshToken) => apiClient.post('/api/v1/auth/refresh', { refreshToken }),
};

export const UserService = {
  getCurrentUser: () => apiClient.get('/api/v1/users/me'),
  updateProfile: (data) => apiClient.put('/api/v1/users/me', data),
  changePassword: (data) => apiClient.patch('/api/v1/users/me/password', data),
  promoteToAdmin: () => apiClient.post('/api/v1/users/promote-me'),
  getUserById: (id) => apiClient.get(`/api/v1/users/${id}`),
};

export const PollsService = {
  getAllPolls: () => apiClient.get('/api/polls'),
  getPollById: (id) => apiClient.get(`/api/polls/${id}`),
  getMyPolls: () => apiClient.get('/api/polls/my-polls'),
  createPoll: (data) => apiClient.post('/api/polls', data),
  updatePoll: (id, data) => apiClient.put(`/api/polls/${id}`, data),
  updatePollStatus: (id, status) => apiClient.patch(`/api/polls/${id}/status`, { status }),
  deletePoll: (id) => apiClient.delete(`/api/polls/${id}`),
  
  // Options
  addOption: (pollId, data) => apiClient.post(`/api/polls/${pollId}/options`, data),
  deleteOption: (optionId) => apiClient.delete(`/api/options/${optionId}`),

  // Voting
  vote: (pollId, optionIds) => apiClient.post(`/api/polls/${pollId}/vote`, { optionIds }),
  cancelVote: (pollId) => apiClient.delete(`/api/polls/${pollId}/vote`),
  getMyVote: (pollId) => apiClient.get(`/api/polls/${pollId}/my-vote`),
  getResults: (pollId) => apiClient.get(`/api/polls/${pollId}/results`),
  
  // Admin Management
  removeParticipant: (pollId, userId) => apiClient.delete(`/api/polls/${pollId}/participants/${userId}`),
};