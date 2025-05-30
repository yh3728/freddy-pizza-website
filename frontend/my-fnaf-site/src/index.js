// src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { CartProvider } from './CartContext'; // ✅ Подключаем контекст
import App from './App';
import './style.css';

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
  <BrowserRouter>
    <CartProvider> {/* ✅ Контекст должен быть выше App */}
      <App />
    </CartProvider>
  </BrowserRouter>
);