import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';
import '../admin.css';
import '../ordermanagment.css';

const rus_payment = {
    "CASH":"наличные",
    "CARD":"карта"
}

const rus_status = {
    "All": "Все",
    "NEW": "Новый",
    "IN_PROGRESS":"В процессе",
    "OUT_FOR_DELIVERY":"На доставке",
    "READY_FOR_DELIVERY":"Готово к доставке",
    "DELIVERED":"Доставлено",
    "CANCELLED":"Отменён"
}

const staff_status_get = {
    "ADMIN": [
        "NEW", 
        "IN_PROGRESS", 
        "OUT_FOR_DELIVERY", 
        "READY_FOR_DELIVERY", 
        "DELIVERED",
        "CANCELLED",
    ],
    "COOK" : [
        "NEW", 
        "IN_PROGRESS",
    ],
    "DELIVERY": [
        "OUT_FOR_DELIVERY", 
        "READY_FOR_DELIVERY",
    ]
}

const staff_status_post = {
    "ADMIN": [
        "NEW", 
        "IN_PROGRESS", 
        "OUT_FOR_DELIVERY", 
        "READY_FOR_DELIVERY", 
        "DELIVERED",
        "CANCELLED",
    ],
    "COOK" : [
        "IN_PROGRESS",
        "READY_FOR_DELIVERY",
    ],
    "DELIVERY": [
        "OUT_FOR_DELIVERY",
        "DELIVERED",
    ]
}

const info = {
  "NEW" : {next_status : "IN_PROGRESS", button_text : "Готовить", color : "#899fff"},
  "IN_PROGRESS" : {next_status : "READY_FOR_DELIVERY", button_text : "Готов", color : "#a2f476"},
  "READY_FOR_DELIVERY" : {next_status : "OUT_FOR_DELIVERY", button_text : "На доставке", color : "#9ed8ff"},
  "OUT_FOR_DELIVERY" : {next_status: "DELIVERED", button_text : "Доставлено", color : "#d18ffb"},
  "DELIVERED" : {color : "#ff8bb6"},
  "CANCELLED" : {color : "#ff6d6d"},
}

function dateFormat(date){
  const timeString = date.toLocaleTimeString('ru-RU', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
  });

  const dateString = date.toLocaleDateString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
  }).replace(/\//g, '.');

  return timeString + " " + dateString;
}

export default function OrderManagement() {
  const [orders, setOrders] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const role = localStorage.getItem('adminRole');
  const filterOptions = ['All', ...staff_status_get[role]];

  useEffect(() => {
      if (!localStorage.getItem('adminAccess')) {
    navigate('/admin-login');
    }
  }, [navigate]);

  const fetchData = async () => {
    try {
      let data_url = `/admin/orders`;
      if (selectedCategory !== "All"){
        data_url += `?status=${selectedCategory}`;
      }
      const response = await API.get(data_url);
      setOrders(response.data);
      setError(null);
    } catch (err) {
        setError(err.message);
    } finally {
        setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();

    const intervalId = setInterval(fetchData, 5000);

    return () => clearInterval(intervalId);
  }, [selectedCategory]);

  const updateStatus = async (id, status) => {
    try {
      API.patch(`/admin/orders/${id}/status`, { status }, { withCredentials: true });
      fetchData();
    } catch (err) {
      setError('Ошибка при изменении группы');
      console.error('Ошибка:', err);
    }
  };

    if (error) return <div className="error-style">{error}</div>;
    if (loading) return <h2 className="loading-text">Загрузка...</h2>;

    if (role === "COOK")
    return (
      <>
        <select
          className="button-select"
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
          style={{ padding: '8px', fontSize: '16px' }}
        >
          {filterOptions.map(option => (
            <option className="option-select" key={option} value={option}>
              {rus_status[option]}
            </option>
          ))}
        </select>

        <div className="order-managment-card-container">
          {orders.map(item => (
            <div className="order-managment-main-container">
              <div
                className="order-managment-header-container"
                style={{ backgroundColor: info[item.status].color }}
              >
                <div className="order-managment-info1">{item.trackingCode}</div>
                <div className="order-managment-date">{dateFormat(new Date(item.createdAt))}</div>
              </div>

              <div className="order-managment-middle-container">
                {item.items.map((product, index) => (
                  <p className="order-managment-product" key={index}>
                    {product.quantity} {product.productName}
                  </p>
                ))}
              </div>

              <div className="order-managment-footer-container" style={{ marginTop: "auto" }}>
                <p className="order-managment-comment">{item.comment}</p>
              </div>

              <div className="order-managment-divider"></div>

              <div className="order-managment-end-header-container">
                <p className="order-managment-status"><strong>Статус:</strong> {rus_status[item.status]}</p>
                <button
                  className="order-managment-button"
                  style={{
                    width: "120px",
                    backgroundColor: info[item.status].color,
                  }}
                  onClick={() => updateStatus(item.id, info[item.status].next_status)}
                >
                  <strong>{info[item.status].button_text}</strong>
                </button>
              </div>
            </div>
          ))}
        </div>
      </>
    );

    else if (role === "DELIVERY")
    return (
      <>
        <select
          className="button-select"
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
          style={{ padding: '8px', fontSize: '16px' }}
        >
          {filterOptions.map(option => (
            <option className="option-select" key={option} value={option}>
              {rus_status[option]}
            </option>
          ))}
        </select>

        <div className="order-managment-card-container">
          {orders.map(item => (
            <div className="order-managment-main-container">
              <div
                className="order-managment-header-container"
                style={{ backgroundColor: info[item.status].color }}
              >
                <div className="order-managment-info1">{item.trackingCode}</div>
                <div className="order-managment-date">{dateFormat(new Date(item.createdAt))}</div>
              </div>

              <div className="order-managment-middle-container">
                <p className="order-managment-customer-name"><strong>Имя:</strong> {item.customerName}</p>
                <p className="order-managment-address"><strong>Адрес:</strong> {item.address}</p>
                <p className="order-managment-phone"><strong>Телефон:</strong> {item.phone}</p>
                <p className="order-managment-payment"><strong>Тип оплаты:</strong> {rus_payment[item.payment]}</p>
                <p className="order-managment-comment-label"><strong>Комментарий:</strong></p>
              </div>

              <div className="order-managment-footer-container">
                <p className="order-managment-comment">{item.comment}</p>
              </div>

              <div className="order-managment-middle-container">
                <p className="order-managment-products-label"><strong>Заказ:</strong></p>
                {item.items.map((product, index) => (
                  <p className="order-managment-product" key={index}>
                    {product.quantity} {product.productName}
                  </p>
                ))}
              </div>

              <div className="order-managment-middle-container" style={{ marginTop: "auto" }}>
                <p className="order-managment-total"><strong>Итого:</strong> {item.totalPrice} ₽</p>
              </div>

              <div className="order-managment-divider"></div>

              <div className="order-managment-end-header-container">
                <p className="order-managment-status"><strong>Статус:</strong> {rus_status[item.status]}</p>
                <button
                  className="order-managment-button"
                  style={{
                    width: "120px",
                    backgroundColor: info[item.status].color,
                  }}
                  onClick={() => updateStatus(item.id, info[item.status].next_status)}
                >
                  {info[item.status].button_text}
                </button>
              </div>
            </div>
          ))}
        </div>
      </>
    );

    return ("");
}