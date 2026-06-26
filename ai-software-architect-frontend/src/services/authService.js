import api from './api';

/**
 * Service handling login and registration API communication.
 */
export const authService = {
  /**
   * Sends user credentials and resolves AuthResponse containing JWT.
   */
  login: async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  },

  /**
     * Registers a new user.
     */
  register: async (username, email, password) => {
    const response = await api.post('/auth/register', { username, email, password });
    return response.data;
  }
};
