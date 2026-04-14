import { apiClient } from './client';

export const AuthService = {
  login: async (data) => await apiClient.post('/api/v1/auth/login', data),
  register: async (data) => await apiClient.post('/api/v1/auth/register', data),
};

export const UserService = {
  getCurrentUser: async () => await apiClient.get('/api/v1/users/me'),
  updateProfile: (data) => apiClient.put('/api/v1/users/me', data),
  changePassword: (data) => apiClient.patch('/api/v1/users/me/password', data),
  promoteToAdmin: () => apiClient.post('/api/v1/users/promote-me'),
  getUserById: async (id) => await apiClient.get(`/api/v1/admin/users/${id}`),
};

export const PollsService = {
  getAllPolls: async () => await apiClient.get('/api/polls'),
  getPollById: async (id) => await apiClient.get(`/api/polls/${id}`),
  getMyPolls: async () => await apiClient.get('/api/polls/my-polls'),
  createPoll: async (data) => await apiClient.post('/api/polls', data),
  updatePollStatus: async (id, status) => await apiClient.patch(`/api/polls/${id}/status`, { status }),
  deletePoll: async (id) => await apiClient.delete(`/api/polls/${id}`),
  vote: (pollId, candidateId) => apiClient.post(`/api/polls/${pollId}/vote`, { candidateId }),
};