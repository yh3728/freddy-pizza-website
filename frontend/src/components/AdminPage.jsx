// src/components/AdminPage.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';
import '../admin.css';

export default function AdminPage() {
  const [orders, setOrders] = useState([]);
  const navigate = useNavigate();

  // Проверка авторизации
  useEffect(() => {
    if (!localStorage.getItem('adminAccess')) {
  navigate('/admin-login');
  }
  }, [navigate]);

  useEffect(() => {
    API.get('/orders')
      .then(res => setOrders(res.data))
      .catch(err => console.error('Ошибка при загрузке заказов:', err));
  }, []);

  const rusStatus = {
    NEW: "Новый",
    IN_PROGRESS: "В процессе",
    READY_FOR_DELIVERY: "Готово к доставке",
    OUT_FOR_DELIVERY: "На доставке",
    DELIVERED: "Доставлено",
    CANCELLED: "Отменён"
  };

  const updateStatus = (id, status) => {
    API.patch(`/orders/${id}`, { status })
      .then(() => {
        setOrders(prev => prev.map(order => order.id === id ? { ...order, status } : order));
      })
      .catch(err => alert('Ошибка при обновлении статуса'));
  };

  const deleteOrder = (id) => {
    if (window.confirm("Удалить заказ?")) {
      API.delete(`/orders/${id}`)
        .then(() => setOrders(prev => prev.filter(order => order.id !== id)))
        .catch(err => alert('Ошибка при удалении'));
    }
  };

  const logout = async () => {
  try {
    await API.post('/admin/auth/logout', {}, { withCredentials: true });
  } catch (e) {
    console.warn("Ошибка при выходе", e);
  }
  localStorage.removeItem('adminAccess');
  localStorage.removeItem('adminRefresh');
  localStorage.removeItem('adminRole');
  navigate('/admin-login');
};


  return (
    <div className="admin-container full-bg">
      <h2>Панель администратора</h2>

      {orders.map(order => (
        <div key={order.id} className="admin-order-card">
          <p><strong>Трек-код:</strong> {order.trackingCode}</p>
          <p><strong>Дата:</strong> {new Date(order.createdAt).toLocaleString()}</p>
          <p><strong>Статус:</strong> {rusStatus[order.status]}</p>
          <p><strong>Сумма:</strong> {order.totalPrice} ₽</p>

          <select
            value={order.status}
            onChange={(e) => updateStatus(order.id, e.target.value)}
          >
            {Object.keys(rusStatus).map(status => (
              <option key={status} value={status}>{rusStatus[status]}</option>
            ))}
          </select>

          <button onClick={() => deleteOrder(order.id)}>Удалить</button>
        </div>
      ))}
    </div>
  );
}