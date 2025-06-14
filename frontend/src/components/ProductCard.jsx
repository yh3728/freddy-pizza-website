import React, { useState } from 'react';
import Modal from './Modal';
import { useCart } from '../CartContext';
import API from '../api';

export default function ProductCard({ product }) {
  const [showModal, setShowModal]   = useState(false);
  const [detailedProduct, setDetailedProduct] = useState(null);
  const [loading, setLoading]       = useState(false);
  const [error, setError]           = useState(null);
  const { addToCart }               = useCart();

  const handleSelect = async () => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await API.get(`/menu/${product.id}`);
      setDetailedProduct(data);
      setShowModal(true);
    } catch (err) {
      console.error('Ошибка при загрузке данных:', err);
      setError('Не удалось загрузить данные продукта');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <img
        src={API.getImageURL(product)}
        alt={product.name}
        className="card-image"
        onError={(e) => {
          e.target.onerror = null;
          e.target.src = '/image_placeholder.png';
        }}
      />

      <h3 className="card-title">{product.name}</h3>
      <p className="card-description">{product.description}</p>

      <div className="card-footer">
        <span className="card-price">{product.price}₽</span>
        <button className="buy-button" onClick={handleSelect}>Выбрать</button>
      </div>

      {showModal && detailedProduct && (
        <Modal
          product={detailedProduct}
          onClose={() => setShowModal(false)}
          onAddToCart={() => addToCart(product)}
        />
      )}
    </div>
  );
}