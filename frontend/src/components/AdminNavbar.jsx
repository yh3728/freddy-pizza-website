import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../adminnavbar.css';

const AdminNavbar = () => {
  const navigate = useNavigate();
  const username = localStorage.getItem('adminUser');
  const role = localStorage.getItem('adminRole');

  const logout = async () => {
    try {
      await fetch('http://localhost:8080/admin/auth/logout', {
        method: 'POST',
        credentials: 'include',
      });
    } catch (e) {
      console.warn('Ошибка при выходе', e);
    }
    localStorage.removeItem('adminAccess');
    localStorage.removeItem('adminRefresh');
    localStorage.removeItem('adminRole');
    localStorage.removeItem('adminUser');
    navigate('/admin-login');
  };

  return (
    <div className="admin-navbar">
      <div className="admin-navbar-header">
        <h1>Админ-панель</h1>
        <div className="admin-navbar-user">
          <span>{username} / {role}</span>
          <button onClick={logout}>Выйти</button>
        </div>
      </div>

      <div className="admin-navbar-links">
        {role === "ADMIN" && (
          <>
            <Link to="/admin/staff">Работники</Link>
            <Link to="/admin/products">Продукты</Link>
          </>
        )}

        <Link to="/admin/orders">Заказы</Link>
      </div>
    </div>
  );
};

export default AdminNavbar;