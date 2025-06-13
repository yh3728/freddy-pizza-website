import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';
import '../admin.css';

export default function AdminLogin() {
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    try {
      // 1. Вход по логину/паролю
      await API.post('/admin/auth', { username: login, password }, { withCredentials: true });

      // 2. Получение информации о текущем пользователе
      const res = await API.get('/admin/auth/me', { withCredentials: true });
      const { role, id, username } = res.data;

      localStorage.setItem('adminAccess', 'true'); // маркер авторизации
      localStorage.setItem('adminRole', role); // теперь точно ADMIN/COOK/DELIVERY
      localStorage.setItem('adminUser', username);

      navigate('/admin/orders');
    } catch (err) {
      console.error('Ошибка входа:', err);
      setError('Неверный логин или пароль');
    }
  };

  return (
    <div className="content-area">
      <h2>Вход в админ-панель</h2>
      <form onSubmit={handleLogin} className="admin-login-form">
        <label>
          Логин:
          <input type="text" value={login} onChange={(e) => setLogin(e.target.value)} required />
        </label>
        <label>
          Пароль:
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </label>
        {error && <p className="form-error">{error}</p>}
        <button type="submit" className="order-btn">Войти</button>
      </form>
    </div>
  );
}