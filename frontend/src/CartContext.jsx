// src/CartContext.jsx
import React, { createContext, useState, useEffect } from 'react';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  // Читаем из localStorage или создаём пустой массив
  const [cartItems, setCartItems] = useState(() => {
    const saved = localStorage.getItem('cart');
    return saved ? JSON.parse(saved) : [];
  });

  // Сохраняем в localStorage при каждом изменении корзины
  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cartItems));
  }, [cartItems]);

  const addToCart = (product) => {
  setCartItems(prev => {
    const existing = prev.find(item => item.id === product.id);
    const max = product.quantity;

    if (existing) {
      const newQuantity = existing.quantity + 1;
      if (newQuantity > max) {
        alert(`Недостаточное количество товара на складе (доступно: ${max} шт.)`);
        return prev;
      }
      return prev.map(item =>
        item.id === product.id ? { ...item, quantity: newQuantity } : item
      );
    } else {
      if (max < 1) {
        alert(`Товар недоступен (на складе 0)`);
        return prev;
      }
      return [...prev, { ...product, quantity: 1 }];
    }
    });
  };

  const removeFromCart = (id) => {
    setCartItems(prev => prev.filter(item => item.id !== id));
  };

  const updateQuantity = (id, delta) => {
  setCartItems(prev => {
    return prev.map(item => {
      if (item.id === id) {
        const newQuantity = item.quantity + delta;
        if (newQuantity > item.quantity && newQuantity > item.quantityLimit) {
          alert(`Достигнут лимит (на складе только ${item.quantityLimit} шт.)`);
          return item;
        }
        return { ...item, quantity: Math.max(1, newQuantity) };
      }
      return item;
    });
  });
};

  const total = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <CartContext.Provider value={{ cartItems, addToCart, removeFromCart, updateQuantity, total }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => React.useContext(CartContext);