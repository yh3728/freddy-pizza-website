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
  const [showModal, setShowModal] = useState(false);
  const [itemModal, setItemModal] = useState('');
  const [orders, setOrders] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const role = localStorage.getItem('adminRole'); 
  const filterOptions = ['All', ...staff_status_get[role]];

  const adminOptions = [
      "NEW", 
      "IN_PROGRESS", 
      "OUT_FOR_DELIVERY", 
      "READY_FOR_DELIVERY", 
      "DELIVERED",
      "CANCELLED",
  ];

  // Проверка авторизации
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

    // Устанавливаем интервал для периодического обновления
    const intervalId = setInterval(fetchData, 5000); // Обновление каждые 10 секунд

    // Очистка интервала при размонтировании компонента
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

  const getOrderById = async (id) => {
    try {
      const response = await API.get(`/admin/orders/${id}`);
      setShowModal(true);
      setItemModal(response.data);
      console.log(response.data);
    } catch (err) {
      setError('Ошибка при получении данных о заказе');
      console.error('Ошибка:', err);
    }
  };

  const adminUpdateStatus = async(e) => {
    e.stopPropagation();
    const status = e.target.value;
    updateStatus(itemModal.id, status);
    let new_item = itemModal;
    new_item.status = status;
    setItemModal(new_item);
  }

  if (error) return <div className="error-style">{error}</div>;
  if (loading) return <h2>Загрузка...</h2>;

  if (role === "COOK")
  return (
    <>
      {/* Выпадающий список для фильтрации */}
      <select 
        value={selectedCategory}
        onChange={(e) => setSelectedCategory(e.target.value)}
        style={{ padding: '8px', fontSize: '16px' }}
      >
      {filterOptions.map(option => (
        <option key={option} value={option}>{rus_status[option]}</option>
      ))}
      </select>

      {/* Отображение отфильтрованного списка */}
      <div className="order-managment-card-container">
      {orders.map(item => (
        <div class="order-managment-main-container">
          <div class="order-managment-header-container" style={{ backgroundColor: info[item.status].color }}>
            <div class="order-managment-info1">
              {item.trackingCode}
            </div>
              {dateFormat(new Date(item.createdAt))}
          </div>
          <div class="order-managment-middle-container">
            {item.items.map(product => (
              <p>{product.quantity}  {product.productName}</p>
            ))}
	        </div>
          <div class="order-managment-footer-container" style={{marginTop: "auto",}}>
            {item.comment}
	        </div>
          <div class="order-managment-divider"></div>
          <div class="order-managment-end-header-container">
            <p>Статус: {rus_status[item.status]}</p>
            <button 
              style={{
                width: "120px", 
                backgroundColor: info[item.status].color,
              }}
              onClick={() => updateStatus(item.id, info[item.status].next_status)}>
                {info[item.status].button_text}
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
      {/* Выпадающий список для фильтрации */}
      <select 
        value={selectedCategory}
        onChange={(e) => setSelectedCategory(e.target.value)}
        style={{ padding: '8px', fontSize: '16px' }}
      >
      {filterOptions.map(option => (
        <option key={option} value={option}>{rus_status[option]}</option>
      ))}
      </select>

      {/* Отображение отфильтрованного списка */}
      <div className="order-managment-card-container">
      {orders.map(item => (
        <div class="order-managment-main-container">
          <div class="order-managment-header-container" style={{ backgroundColor: info[item.status].color }}>
            <div class="order-managment-info1">
              {item.trackingCode}
            </div>
              {dateFormat(new Date(item.createdAt))}
          </div>
          <div class="order-managment-middle-container">
            <p>Имя: {item.customerName}</p>
            <p>Адрес: {item.address}</p>
            <p>Телефон: {item.phone}</p>
            <p>Тип оплаты: {rus_payment[item.payment]}</p>
            <p>Комментарий:</p>
	        </div>
          <div class="order-managment-footer-container">
            {item.comment}
	        </div>
          <div class="order-managment-middle-container">
            <p>Заказ:</p>
            {item.items.map(product => (
              <p>{product.quantity}  {product.productName}</p>
            ))}
          </div>
          <div class="order-managment-middle-container" style={{marginTop: "auto",}}>
            <p>Итого: {item.totalPrice}</p>
          </div>
          <div class="order-managment-divider"></div>
          <div class="order-managment-end-header-container">
            <p>Статус: {rus_status[item.status]}</p>
            <button 
              style={{
                width: "120px", 
                backgroundColor: info[item.status].color,
              }}
              onClick={() => updateStatus(item.id, info[item.status].next_status)}>
                {info[item.status].button_text}
            </button>
          </div>
        </div>
      ))}
      </div>
    </>
  );
  
  else
  return (
    <>
      {/* Выпадающий список для фильтрации */}
      <select 
        value={selectedCategory}
        onChange={(e) => setSelectedCategory(e.target.value)}
        style={{ padding: '8px', fontSize: '16px' }}
      >
      {filterOptions.map(option => (
        <option key={option} value={option}>{rus_status[option]}</option>
      ))}
      </select>

      {/* Отображение отфильтрованного списка */}
      <div className="order-managment-card-container">
      {orders.map(item => (
        <div class="order-managment-main-container">
          <div class="order-managment-header-container" style={{ backgroundColor: info[item.status].color }}>
            <div class="order-managment-info1">
              {item.trackingCode}
            </div>
              {dateFormat(new Date(item.createdAt))}
          </div>
          <div class="order-managment-footer-container" 
            style={{display: "flex",
              justifyContent: "center",
              alignItems: "center"}}>
            <p>{item.customerName}</p>
          </div>
          <div class="order-managment-middle-container">
            {item.items.map(product => (
              <p>{product.quantity}  {product.productName}</p>
            ))}
	        </div>
          <div class="order-managment-footer-container" style={{marginTop: "auto",}}>
            {item.comment}
	        </div>
          <div class="order-managment-divider"></div>
          <div class="order-managment-end-header-container">
            <p>Статус: {rus_status[item.status]}</p>
            <button 
              style={{
                width: "120px", 
                backgroundColor: info[item.status].color,
              }}
              onClick={() => getOrderById(item.id)}>
                Подробнее
            </button>
          </div>
        </div>
      ))}
      </div>
      
      {showModal && (
        <div className="order-managment-modal-overlay" onClick={() => {setShowModal(false); setItemModal('')}}>
          <div className="order-managment-modal-container" onClick={(e) => e.stopPropagation()}>
            <div className="order-managment-modal-right-content">
              <span>
                Статус: 
                <select 
                  value={itemModal.status}
                  onChange={(e) => adminUpdateStatus(e)}
                  style={{ padding: '8px', fontSize: '16px' }}
                >
                {adminOptions.map(option => (
                  <option key={option} value={option}>{rus_status[option]}</option>
                ))}
                </select> 
              </span>
              {itemModal.assignedDelivery && (
                <span>Доставщик: {itemModal.assignedDelivery.username} </span>
              )}
            </div>
            <div className="order-managment-modal-left-content">
              <span>{itemModal.trackingCode}</span>
            </div>
            <p>Имя: {itemModal.customerName}</p>
            <p>Адрес: {itemModal.address}</p>
            <p>Телефон: {itemModal.phone}</p>
            <p>Тип оплаты: {rus_payment[itemModal.payment]}</p>
            <p>Комментарий:</p>
            <div className="order-managment-footer-container">
              {itemModal.comment}
            </div>
            <p>Итого: {itemModal.totalPrice}₽</p>
            <div className="order-managment-modal-down-content">
              {dateFormat(new Date(itemModal.createdAt))}
            </div>
          </div>
        </div>
      )}
      
    </> 
  );
}