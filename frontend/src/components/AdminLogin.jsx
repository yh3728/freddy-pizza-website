// src/components/AdminLogin.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../admin.css'; // Можно переиспользовать

export default function AdminLogin() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();

    // Простой логин (можно заменить на реальный запрос к backend)
    if (username === 'admin' && password === 'pizza123') {
      localStorage.setItem('adminAuth', 'true');
      navigate('/admin');
    } else {
      alert('Неверный логин или пароль');
    }
  };

  return (
    <div className="content-area">
      <h2>Вход в админ-панель</h2>
      <form onSubmit={handleLogin} className="admin-login-form">
        <label>
          Логин:
          <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
        </label>
        <label>
          Пароль:
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </label>
        <button type="submit" className="order-btn">Войти</button>
      </form>
    </div>
  );
}