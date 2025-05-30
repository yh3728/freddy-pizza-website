// src/App.jsx
import React, { useEffect } from 'react';
import './style.css';
import './productcard.css';
import './modal.css';
import backgroundImage from './assets/background.jpeg';
import contentBgImage from './assets/content.jpeg';
import { products } from './data';
import ProductCard from './components/ProductCard';
import CartPage from './components/CartPage';
import Navbar from './components/Navbar';
import { Routes, Route, useLocation } from 'react-router-dom';

function ScrollToTop() {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo(0, 0); // Прокрутка к верху при смене роута
  }, [pathname]);

  return null;
}

export default function App() {
  const categories = [...new Set(products.map(p => p.category))];

  return (
    <>
      <ScrollToTop />
      <Navbar />
          <img 
            src={require("./assets/freddy.png")}
            className="divider-image"
          />
      <div className="app" style={{ backgroundImage: `url(${backgroundImage})` }}>
        <div className="content-box" style={{ backgroundImage: `url(${contentBgImage})` }}>
          <div className="content-area">
            <Routes>
              
              <Route
                path="/"
                element={
                  <>
                    {categories.map(category => (
                      <React.Fragment key={category}>
                        <div className="category-title" id={category}>
                          <h2>{category.toUpperCase()}</h2>
                        </div>
                        <div className="cards-container">
                        {products
                          .filter(p => p.category === category)
                          .map(product => (
                            <ProductCard key={product.id} product={product} />
                          ))}
                        </div>
                      </React.Fragment>
                    ))}
                  </>
                }
              />
              <Route path="/cart" element={<CartPage background={contentBgImage} />} />
            </Routes>
          </div>
        </div>
      </div>
    </>
  );
}