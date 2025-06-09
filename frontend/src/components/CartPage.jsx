// src/components/CartPage.jsx
import React, { useState } from 'react';
import '../cart.css';
import { useCart } from '../CartContext';
import API from '../api';

export default function CartPage({ background }) {
  const { cartItems, updateQuantity, removeFromCart, total } = useCart();

  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [address, setAddress] = useState('');
  const [payment, setPayment] = useState('card');
  const [comment, setComment] = useState('');

  const handleOrder = (e) => {
  e.preventDefault();

  // Валидация
  if (name.trim().length < 2) {
    alert('Пожалуйста, введите имя (минимум 2 символа).');
    return;
  }

  const phonePattern = /^(\+7|8)\d{10}$/;
if (!phonePattern.test(phone.trim())) {
  alert('Введите телефон в формате: +7XXXXXXXXXX или 8XXXXXXXXXX (всего 11 цифр).');
  return;
}


  if (address.trim().length < 5) {
    alert('Пожалуйста, введите адрес (минимум 5 символов).');
    return;
  }

  if (comment.length > 200) {
    alert('Комментарий не должен превышать 200 символов.');
    return;
  }

  // Формируем тело запроса
  const payload = {
    customerName: name,
    phone: phone,
    address: address,
    comment: comment,
    payment: payment.toUpperCase(),
    items: cartItems.map(item => ({
      productId: item.id,
      quantity: item.quantity
    }))
  };

  API.post('/orders', payload)
  .then(res => {
    const code = res.data.trackingCode;
    localStorage.setItem('lastTrackingCode', code); // ✅ сохраняем
    alert(`Заказ оформлен! Код отслеживания: ${code}`);
    localStorage.removeItem('cart');
    window.location.href = '/order'; // ✅ переход на страницу заказа
  })
  .catch(err => {
  console.error('Ошибка при заказе:', err);

  // Если сервер прислал ответ
  if (err.response && err.response.status === 400) {
    const serverMessage = err.response.data?.message;

    if (serverMessage?.includes("недостаточное количество")) {
      alert(`Некоторые товары превышают допустимое количество на складе.\n\n${serverMessage}`);
    } else {
      alert(`Ошибка: ${serverMessage}`);
    }

  } else {
    alert('Не удалось оформить заказ. Попробуйте позже.');
  }
  });
};


  return (
    <div className="content-area">
      <h2>Корзина</h2>

      {/* Список товаров */}
      {cartItems.length > 0 ? (
        cartItems.map(item => (
          <div key={item.id} className="cart-item">
            <img src={API.getImageURL(item)} alt={item.name} className="product-image" />
            <div className="product-info">
              <h3>{item.name}</h3>
              <p>{item.description}</p>
            </div>
            <div className="quantity-group">
              <button onClick={() => updateQuantity(item.id, -1)}>-</button>
              <span className="quantity">{item.quantity}</span>
              <button onClick={() => updateQuantity(item.id, +1)}>+</button>
            </div>
            <div className="price">{item.price * item.quantity} ₽</div>
            <button className="remove-btn" onClick={() => removeFromCart(item.id)}>×</button>
          </div>
        ))
      ) : (
        <p>Корзина пуста</p>
      )}

      {/* Доставка */}
      <h2>Доставка</h2>
      <form>
        <div className="form-group delivery-form">
          <div className="left-column">
            <label htmlFor="name">Ваше имя</label>
            <input type="text" id="name" value={name} onChange={e => setName(e.target.value)} />

            <label htmlFor="phone">Телефон</label>
            <input type="tel" id="phone" value={phone} onChange={e => setPhone(e.target.value)} />
          </div>
          <div className="right-column">
            <label htmlFor="address">Адрес</label>
            <input type="text" id="address" value={address} onChange={e => setAddress(e.target.value)} />

            <label htmlFor="payment">Способ оплаты</label>
            <select id="payment" value={payment} onChange={e => setPayment(e.target.value)}>
              <option value="card">Оплата картой</option>
              <option value="cash">Наличными</option>
            </select>
          </div>
        </div>

        {/* Комментарии */}
        <div className="form-group">
          <label htmlFor="comments">Комментарии</label>
          <textarea id="comments" value={comment} onChange={e => setComment(e.target.value)} rows="4" maxLength="200"></textarea>
        </div>

        {/* Итоговая сумма и кнопка заказа */}
        <div className="checkout">
          <p>К оплате: {total} ₽</p>
          <button className="order-btn" onClick={handleOrder}>Заказать</button>
        </div>
      </form>
    </div>
  );
}