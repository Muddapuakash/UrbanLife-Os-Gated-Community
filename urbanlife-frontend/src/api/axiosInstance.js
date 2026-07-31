import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Attach Basic Auth header on every outgoing request
axiosInstance.interceptors.request.use(
  (config) => {
    const email = localStorage.getItem('email');
    const password = localStorage.getItem('password');

    if (email && password) {
      const authHeader = 'Basic ' + btoa(`${email}:${password}`);
      config.headers['Authorization'] = authHeader;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Intercept 401 Unauthorized responses globally
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.clear();
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
