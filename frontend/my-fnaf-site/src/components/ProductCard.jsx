// src/components/ProductCard.jsx
import React, { useState } from 'react';
import Modal from './Modal';
import { useCart } from '../CartContext';


export default function ProductCard({ product }) {
  const [showModal, setShowModal] = useState(false);
  const { addToCart } = useCart();

  return (
    <div className="card">
      <img
        src={product.image}
        alt={product.name}
        className="card-image"
      />
      <h3 className="card-title">{product.name}</h3>
      <p className="card-description">{product.description}</p>
      <div className="card-footer">
        <span class="card-price">{product.price} ₽</span>
        <button class="buy-button" onClick={() => setShowModal(true)}>Выбрать</button>
      </div>

      {/* Можно убрать кнопку выше, если добавляем через модалку */}
      {showModal && (
        <Modal
          product={product}
          onClose={() => setShowModal(false)}
          onAddToCart={() => addToCart(product)} // Передаем в модалку
        />
      )}
    </div>
  );
}