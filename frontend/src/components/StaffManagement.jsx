import React, { useEffect, useState } from 'react';
import API from '../api';
import '../admin.css';
import '../adminnavbar.css';
import { useNavigate } from 'react-router-dom';

export default function StaffManagement() {
  const navigate = useNavigate();
  const [staffList, setStaffList] = useState([]);
  const [newStaff, setNewStaff] = useState({ username: '', password: '', role: 'ADMIN' });
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    const role = localStorage.getItem('adminRole');
    if (role !== 'ADMIN') {
      alert('Доступ запрещён. Только для администраторов.');
      navigate('/admin');
    } else {
      fetchStaff();
    }
  }, []);

  const fetchStaff = () => {
    API.get('/admin/staff')
      .then(res => setStaffList(res.data))
      .catch(err => console.error('Ошибка загрузки сотрудников:', err));
  };

  const handleDelete = (id) => {
    if (window.confirm('Удалить этого сотрудника?')) {
      API.delete(`/admin/staff/${id}`)
        .then(() => fetchStaff())
        .catch(err => alert('Ошибка при удалении сотрудника'));
    }
  };

  const handleCreate = (e) => {
    e.preventDefault();
    API.post('/admin/staff', newStaff, { withCredentials: true })
      .then(() => {
        fetchStaff();
        setShowModal(false);
        setNewStaff({ username: '', password: '', role: 'ADMIN' });
      })
      .catch(err => alert('Ошибка при добавлении сотрудника'));
  };

  return (
    <div className="admin-container full-bg">
      <div className="staff-header">
        <h2>Работники:</h2>
        <button className="add-button" onClick={() => setShowModal(true)}>Добавить работника</button>
      </div>

      <div className="staff-grid">
        {staffList.map(user => (
          <div key={user.id} className="staff-card">
            <div className="staff-info">
            <p><strong>Логин:</strong> {user.username || user.login}</p>
            <p><strong>Роль:</strong> {user.role}</p>
            </div>
            {user.role !== 'ADMIN' && (
              <button onClick={() => handleDelete(user.id)} className="delete-button">
                Удалить пользователя
              </button>
            )}
          </div>
        ))}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            <h3 className="modal-title">Добавление работника:</h3>
            <form onSubmit={handleCreate} className="admin-login-form">
              <label>Логин:
                <input type="text" value={newStaff.username} onChange={(e) => setNewStaff({ ...newStaff, username: e.target.value })} required />
              </label>
              <label>Пароль:
                <input type="password" value={newStaff.password} onChange={(e) => setNewStaff({ ...newStaff, password: e.target.value })} required />
              </label>
              <label>Роль:
                <select value={newStaff.role} onChange={(e) => setNewStaff({ ...newStaff, role: e.target.value })}>
                  <option value="ADMIN">Admin</option>
                  <option value="COOK">Cook</option>
                  <option value="DELIVERY">Delivery</option>
                </select>
              </label>
              <button type="submit" className="order-btn">Добавить</button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}