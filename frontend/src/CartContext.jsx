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
      return prev.map(item =>
        item.id === product.id ? { ...item, quantity: newQuantity } : item
      );
    } else {
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