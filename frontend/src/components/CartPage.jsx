import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../cart.css';
import { useCart } from '../CartContext';
import API from '../api';

export default function CartPage() {
  const { cartItems, updateQuantity, removeFromCart, total } = useCart();
  const [setCartItems] = useState(() => () => {});
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [address, setAddress] = useState('');
  const [payment, setPayment] = useState('card');
  const [comment, setComment] = useState('');
  const [errors, setErrors] = useState({
    name: '',
    phone: '',
    address: '',
    comment: '',
    products: {}
  });

  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [addressConfirmed, setAddressConfirmed] = useState(false);

  const handleOrder = async (e) => {
    e.preventDefault();

    const newErrors = { name: '', phone: '', address: '', comment: '', products: {} };
    let hasError = false;

    if (name.trim().length < 2) {
      newErrors.name = 'Имя должно содержать минимум 2 символа.';
      hasError = true;
    }

    const phonePattern = /^(\+7|8)\d{10}$/;
    if (!phonePattern.test(phone.trim())) {
      newErrors.phone = 'Телефон должен быть в формате +7XXXXXXXXXX или 8XXXXXXXXXX.';
      hasError = true;
    }

    if (comment.length > 250) {
      newErrors.comment = 'Комментарий не должен превышать 250 символов.';
      hasError = true;
    }

    if (!addressConfirmed) {
      newErrors.address = 'Пожалуйста, введите допустимый адрес.';
      hasError = true;
    }

    if (hasError) {
      setErrors(newErrors);
      return;
    }

    try {
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

      const res = await API.post('/orders', payload);
      const code = res.data.trackingCode;
      localStorage.removeItem('cart');
      window.location.href = `/order/${code}`;
    } catch (err) {
      console.error('Ошибка при заказе:', err);
      const resp = err.response?.data;
      if (
        resp?.error === 'BAD_REQUEST' &&
        resp.message === 'Недостаточно товара в наличии' &&
        Array.isArray(resp.details)
      ) {
        const updatedProductErrors = {};
        resp.details.forEach(item => {
          updatedProductErrors[item.productId] = `Недостаточно товара в наличии (доступно: ${item.availableQuantity} шт.)`;
          const cartItem = cartItems.find(p => p.id === item.productId);
          if (cartItem && cartItem.quantity > item.availableQuantity) {
            updateQuantity(item.productId, item.availableQuantity - cartItem.quantity);
          }
        });
        setErrors(prev => ({ ...prev, products: updatedProductErrors }));
      } 
    }
  };

  const fetchSuggestions = async (query) => {
    if (query.length < 3) {
      setSuggestions([]);
      return;
    }

    try {
      const response = await axios.post(
        "https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/address",
        { query },
        {
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "Authorization": "Token 554acbc27ba1622e76adea8f5ffe027cecdd2cc7"
          }
        }
      );
      setSuggestions(response.data.suggestions || []);
      setShowSuggestions(true);
    } catch (error) {
      console.error("Ошибка при получении подсказок:", error);
    }
  };

  return (
    <div className="content-area">
      <h2>Корзина</h2>

      {cartItems.length > 0 ? (
        cartItems.map(item => (
          <div
            key={item.id}
            className="cart-item"
            style={errors.products[item.id] ? { border: '2px solid red' } : {}}
          >
            <img src={API.getImageURL(item)} alt={item.name} className="product-image" />
            <div className="product-info">
              <h3>{item.name}</h3>
              <p>{item.description}</p>
              {errors.products[item.id] && (
                <p className="form-error" style={{ color: 'red', marginTop: '5px' }}>
                  {errors.products[item.id]}
                </p>
              )}
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
        <p className="empty-cart">Корзина пуста</p>
      )}

      {cartItems.length >= 20 && (
        <p className="cart-limit-message">
          Вы достигли максимального количества разных товаров (20).
        </p>
      )}

      <h2>Доставка</h2>
      <form>
        <div className="form-group delivery-form">
          <div className="left-column">
            <label htmlFor="name">Ваше имя</label>
            <input type="text" id="name" value={name} maxLength={20} onChange={e => setName(e.target.value)} />
            {errors.name && <p className="form-error">{errors.name}</p>}

            <label htmlFor="phone">Телефон</label>
            <input type="tel" id="phone" value={phone} onChange={e => setPhone(e.target.value)} />
            {errors.phone && <p className="form-error">{errors.phone}</p>}
          </div>
          <div className="right-column" style={{ position: 'relative' }}>
            <label htmlFor="address">Адрес</label>
            <input
              type="text"
              id="address"
              value={address}
              onChange={(e) => {
                setAddress(e.target.value);
                fetchSuggestions(e.target.value);
                setAddressConfirmed(false);
              }}
              onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
              onFocus={() => fetchSuggestions(address)}
            />
            {showSuggestions && suggestions.length > 0 && (
              <ul className="suggestions-list">
                {suggestions.map((s, i) => (
                  <li
                    key={i}
                    onClick={() => {
                      setAddress(s.value);
                      setAddressConfirmed(true);
                      setShowSuggestions(false);
                    }}
                  >
                    {s.value}
                  </li>
                ))}
              </ul>
            )}
            {errors.address && <p className="form-error">{errors.address}</p>}

            <label htmlFor="payment">Способ оплаты</label>
            <select id="payment" value={payment} onChange={e => setPayment(e.target.value)}>
              <option value="card">Оплата картой</option>
              <option value="cash">Наличными</option>
            </select>
          </div>
        </div>

        <div className="form-group">
        <label
          htmlFor="comments"
          style={{ height: '100px' }} >
          Комментарии
        </label>
          <textarea
            id="comments"
            value={comment}
            onChange={e => setComment(e.target.value)}
            rows="4"
            maxLength="250"
          ></textarea>
          {errors.comment && <p className="form-error">{errors.comment}</p>}
        </div>

        <div className="checkout">
          <p>К оплате: {total} ₽</p>
          <button
            className="order-btn"
            onClick={handleOrder}
            disabled={cartItems.length === 0}
          >
            Заказать
          </button>
        </div>
      </form>
    </div>
  );
}