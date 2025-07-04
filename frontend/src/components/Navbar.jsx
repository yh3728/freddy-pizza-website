import React from 'react';
import '../navbar.css';
import { useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';

const SmartAnchorLink = ({ anchor, children, onClick }) => {
  const navigate = useNavigate();
  const { pathname, hash } = useLocation();

  const handleClick = (e) => {
    e.preventDefault();
    onClick?.();

    if (pathname === '/') {
      const element = document.getElementById(anchor);
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
      }
    } else {
      navigate(`/#${anchor}`);
    }
  };

  useEffect(() => {
    if (pathname === '/' && hash === `#${anchor}`) {
      const element = document.getElementById(anchor);
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
      }
    }
  }, [pathname, hash, anchor]);

  return (
    <a
      href={`/#${anchor}`}
      onClick={handleClick}
      className={"content-link"}
    >
      {children}
    </a>
  );
};


export default function Navbar() {
  return (
    
    <div className="nav-menu-wrapper">
      <div className="nav-menu">
      
        <div className="nav-image-container">
          <img src={require("../assets/uzor11.png")} className="nav-image-first" />

          <div className="nav-image-middle-tiled"></div>
          
          <a href="/" target="_blank" className="nav-image-scaled-link">
            <img src={require("../assets/telegram.png")} className="nav-image-scaled" />
          </a>
          <a href="http://109.73.194.68:5056/" target="_blank" className="nav-image-scaled-link">
            <img src={require("../assets/friendly.png")} className="nav-image-scaled" />
          </a>
          <a href="/" target="_blank" className="nav-image-scaled-link">
            <img src={require("../assets/vkontakte.png")} className="nav-image-scaled" />
          </a>

          <img src={require("../assets/uzor13.png")} className="nav-image-last" />
        </div>
      
        <div className="title-row">
          <Link to="/about" className="title-link">
            О нас
          </Link>
          <Link to="/holidays" className="title-link">
            Детские праздники
          </Link>
          <Link to="/order" className="title-link">
            Заказ
          </Link>
        </div>

        <div className="nav-links-container">

          <div className="nav-link-group">
            <SmartAnchorLink anchor="pizza">
              Пицца
            </SmartAnchorLink>
            <SmartAnchorLink anchor="snack">
              Закуски
            </SmartAnchorLink>
            <SmartAnchorLink anchor="salad">
              Салаты
            </SmartAnchorLink>
            <SmartAnchorLink anchor="rolls">
              Роллы
            </SmartAnchorLink>
          </div>

          <div className="nav-empty-space"></div>

          <div className="nav-link-group">
            <SmartAnchorLink anchor="dessert">
              Десерты
            </SmartAnchorLink>
            <SmartAnchorLink anchor="drink">
              Напитки
            </SmartAnchorLink>
            <SmartAnchorLink anchor="merch">
              Мерч
            </SmartAnchorLink>
            <Link to="/cart" className="logo-link">
              <img src={require("../assets/basket.PNG")} alt = "корзина" className = "logo"/>
            </Link>
          </div>
        </div>
      
      </div>
    </div>
  );
}