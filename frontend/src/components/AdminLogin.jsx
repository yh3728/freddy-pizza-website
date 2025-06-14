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
        const res = await API.post(
          '/admin/auth',
          { username: login, password },
          { withCredentials: true }
        );

        const { accessToken, refreshToken } = res.data;

        const res2 = await API.get('/admin/auth/me', { withCredentials: true });
        const { role, id, username } = res2.data;

      localStorage.setItem('adminAccess', accessToken)
      localStorage.setItem('adminRefresh', refreshToken);
      localStorage.setItem('adminRole', role);
      localStorage.setItem('adminUser', username);

      navigate('/admin/orders');
    } catch (err) {
      console.error('Ошибка входа:', err);
      setError('Неверный логин или пароль');
    }
  };

  return (
    <div className="admin-login-container">
      <h2>Вход в админ-панель</h2>
      <form onSubmit={handleLogin} className="admin-login-form">
        <label className="admin-login-row">
          <span className="admin-login-label">Логин:</span>
          <input
            type="text"
            value={login}
            onChange={(e) => setLogin(e.target.value)}
            required
            className="admin-login-input"
          />
        </label>

        <label className="admin-login-row">
          <span className="admin-login-label">Пароль:</span>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className="admin-login-input"
          />
        </label>

        {error && <p className="form-error">{error}</p>}

        <button type="submit" className="login-button">Войти</button>
      </form>
    </div>
  );
}