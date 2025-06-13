// src/api.js
import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });

  failedQueue = [];
};

// Добавим accessToken к каждому запросу
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('adminAccess');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

API.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
        .then(token => {
          originalRequest.headers['Authorization'] = 'Bearer ' + token;
          return API(originalRequest);
        })
        .catch(err => Promise.reject(err));
      }

      isRefreshing = true;

      try {
        const res = await axios.post('http://localhost:8080/admin/auth/refresh', {}, { withCredentials: true });
        const newToken = res.data.accessToken;
        localStorage.setItem('adminAccess', newToken);
        API.defaults.headers.common['Authorization'] = 'Bearer ' + newToken;
        processQueue(null, newToken);
        return API(originalRequest);
      } catch (err) {
        processQueue(err, null);
        localStorage.removeItem('adminAccess');
        localStorage.removeItem('adminRefresh');
        localStorage.removeItem('adminUser');
        localStorage.removeItem('adminRole');
        window.location.href = '/admin-login';
        return Promise.reject(err);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

API.getBaseURL = () => API.defaults.baseURL;

API.getImageURL = (product) => `${API.getBaseURL()}${product.imagePath}`;

export default API;

