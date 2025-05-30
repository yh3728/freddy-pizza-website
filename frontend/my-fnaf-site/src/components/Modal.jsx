// src/components/Modal.jsx
import React, { useState } from 'react';

export default function Modal({ product, onClose, onAddToCart }) {
  const [count, setCount] = useState(1);

  return (
    <div class="modal-overlay1" onClick={onClose}>
      <div class="product-container1 modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>×</button>
          <div class="product-content1">
              <div class="product-image1">
                  <img src={product.image} alt="Изображение товара"/>
              </div>
              <div class="product-info1">
                  <div class="product-title1">
                      <h2>{product.name}</h2>
                  </div>
                  <div class="product-description1">
                      <p>Ингредиенты: {product.ingredients?.join(', ')}</p>
                      <p>Вес: {product.weight || product.volume}</p>
                      <p>Цена: {product.price} ₽</p>
                  </div>
              </div>
          </div>
          <div class="product-actions1">
              <div class="quantity-control1">
                  <button class="quantity-btn1" onClick={() => setCount(Math.max(1, count - 1))}>-</button>
                  <span class="quantity1">{count}</span>
                  <button class="quantity-btn1" onClick={() => setCount(count + 1)}>+</button>
              </div>
            <button 
                class="add-to-cart1"
                onClick={() => {
                    for (let i = 0; i < count; i++) {
                    onAddToCart(); // Добавляем нужное количество товаров
                    }
                    onClose();
                }}
                >Добавить в корзину
                </button>
          </div>
      </div>
    </div>
  );
}