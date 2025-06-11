import React, { useEffect, useState } from 'react';
import API from '../api';
import '../admin.css';
import '../adminnavbar.css';
import { useNavigate } from 'react-router-dom';

export default function StaffManagement() {
  const navigate = useNavigate();
  const [staffList, setStaffList] = useState([]);
  const [newStaff, setNewStaff] = useState({ username: '', password: '', role: 'ADMIN' });
  const [showAddModal, setShowAddModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');

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

  const handleDelete = () => {
    API.delete(`/admin/staff/${deleteId}`)
      .then(() => {
        fetchStaff();
        setShowDeleteModal(false);
      })
      .catch(() => alert('Ошибка при удалении сотрудника'));
  };

  const handleCreate = (e) => {
  e.preventDefault();
  setErrorMessage('');

  API.post('/admin/staff', newStaff, { withCredentials: true })
    .then(() => {
      fetchStaff();
      setShowAddModal(false);
      setNewStaff({ username: '', password: '', role: 'ADMIN' });
    })
    .catch(err => {
      if (err.response?.status === 409) {
        // 409 Conflict → логин уже существует
        setErrorMessage('Логин уже используется');
      } else {
        console.error('Неизвестная ошибка:', err);
        setErrorMessage('Ошибка при добавлении сотрудника');
      }
    });
};

  return (
    <div className="admin-container full-bg">
      <div className="staff-header">
        <h2>Работники:</h2>
        <button className="add-button" onClick={() => setShowAddModal(true)}>Добавить работника</button>
      </div>

      <div className="staff-grid">
        {staffList.map(user => (
          <div key={user.id} className="staff-card">
            <div className="staff-info">
              <p><strong>Логин:</strong> {user.username || user.login}</p>
              <p><strong>Роль:</strong> {user.role}</p>
            </div>
            {user.role !== 'ADMIN' && (
              <button onClick={() => { setDeleteId(user.id); setShowDeleteModal(true); }} className="delete-button">
                Удалить пользователя
              </button>
            )}
          </div>
        ))}
      </div>

      {showAddModal && (
        <div className="modal-overlay">
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close" onClick={() => { setShowAddModal(false); setErrorMessage(''); }}>×</button>
            <h3 className="modal-title">Добавление работника:</h3>
            <form onSubmit={handleCreate} className="admin-login-form">
              <label>Логин:</label>
              <input
                type="text"
                value={newStaff.username}
                onChange={(e) => setNewStaff({ ...newStaff, username: e.target.value })}
                className="form-input"
                required
                maxLength={10}
              />
              {errorMessage && <p className="form-error">{errorMessage}</p>}
              <label>Пароль:</label>
              <input
                type="password"
                value={newStaff.password}
                onChange={(e) => setNewStaff({ ...newStaff, password: e.target.value })}
                className="form-input"
                required
              />
              <label>Роль:</label>
              <select
                value={newStaff.role}
                onChange={(e) => setNewStaff({ ...newStaff, role: e.target.value })}
                className="form-select"
              >
                <option value="ADMIN">Admin</option>
                <option value="COOK">Cook</option>
                <option value="DELIVERY">Delivery</option>
              </select>
              <button type="submit" className="add-button-stuff">Добавить</button>
            </form>
          </div>
        </div>
      )}

      {showDeleteModal && (
        <div className="modal-overlay" onClick={() => setShowDeleteModal(false)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowDeleteModal(false)}>×</button>
            <h3 className="modal-title">Подтвердите удаление</h3>
            <p style={{ textAlign: 'center', marginBottom: '20px' }}>Вы уверены, что хотите удалить этого сотрудника?</p>
            <div style={{ display: 'flex', justifyContent: 'center', gap: '15px' }}>
              <button className="delete-button" onClick={() => setShowDeleteModal(false)}>Отмена</button>
              <button className="delete-button" onClick={handleDelete}>Удалить</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}