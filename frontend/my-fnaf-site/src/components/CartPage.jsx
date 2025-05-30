// src/components/CartPage.jsx
import React from 'react';
import '../cart.css'; // твои стили для корзины
import { useCart } from '../CartContext';

export default function CartPage({ background }) {
  const { cartItems, updateQuantity, removeFromCart, total } = useCart();

  return (
    <div className="content-area">
      <h2>Корзина</h2>

      {/* Список товаров */}
      {cartItems.length > 0 ? (
        cartItems.map(item => (
          <div key={item.id} className="cart-item">
            <img src={item.image} alt={item.name} className="product-image" />
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
            <button className="remove-btn" onClick={() => removeFromCart(item.id)}>
              ×
            </button>
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
            <input type="text" id="name" placeholder="Введите ваше имя" />

            <label htmlFor="phone">Телефон</label>
            <input type="tel" id="phone" placeholder="+7 (___) ___-__-__" />
          </div>
          <div className="right-column">
            <label htmlFor="address">Адрес</label>
            <input type="text" id="address" placeholder="Введите ваш адрес" />

            <label htmlFor="payment">Способ оплаты</label>
            <select id="payment">
              <option value="card">Оплата картой</option>
              <option value="cash">Наличными</option>
            </select>
          </div>
        </div>

        {/* Комментарии */}
        <div className="form-group">
          <label htmlFor="comments">Комментарии</label>
          <textarea id="comments" placeholder="Добавьте комментарий к заказу" rows="4"></textarea>
        </div>

        {/* Итоговая сумма и кнопка заказа */}
        <div className="checkout">
          <p>К оплате: {total} ₽</p>
          <button className="order-btn">Заказать</button>
        </div>
      </form>
    </div>
  );
}