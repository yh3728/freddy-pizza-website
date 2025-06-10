// src/api.js
import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

// Добавим accessToken к каждому запросу
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('adminAccess');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

API.getBaseURL = () => API.defaults.baseURL;

API.getImageURL = (product) => `${API.getBaseURL()}${product.imagePath}`;

export default API;

