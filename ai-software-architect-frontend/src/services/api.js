import axios from 'axios';

/**
 * Pre-configured Axios instance.
 *
 * Automatically intercepts every outgoing HTTP request and attaches
 * the JSON Web Token (JWT) from localStorage inside the Authorization header.
 *
 * This matches backend expectation for stateless authentication guards.
 */
const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;
