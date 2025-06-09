// src/api.js
import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

API.getBaseURL = () => API.defaults.baseURL;

API.getImageURL = (product) => `${API.getBaseURL()}${product.imagePath}`;

export default API;

