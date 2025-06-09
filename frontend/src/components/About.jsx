import React, { useEffect, useRef, useState } from 'react';
import '../about.css';

export default function About() {
  const leftRef = useRef(null);
  const [leftHeight, setLeftHeight] = useState(0);

  // при загрузке компонента запоминаем высоту левой части
  useEffect(() => {
    const updateHeight = () => {
      if (leftRef.current) {
        setLeftHeight(leftRef.current.offsetHeight);
      }
    };

    updateHeight(); // первичный замер

    window.addEventListener('resize', updateHeight);
    return () => window.removeEventListener('resize', updateHeight);
  }, []);

  return (
    <div className="about-layout">
      <div className="about-left" ref={leftRef}>
        <div className="about-section">
          <h2>О нас</h2>
          <p>
            Добро пожаловать в пиццерию <strong>Мишки Фредди</strong> — место, где вкусная пицца и весёлые приключения встречаются каждый день!<br /><br />
            Наша история началась с мечты: создать место, куда семьи могут приходить отдыхать, праздновать дни рождения и просто весело проводить время вместе.
            Мы вдохновлялись классическими семейными пиццериями, где тепло, уютно и всегда пахнет свежей выпечкой.
          </p>
          <ul>
            <li> аппетитная пицца на любой вкус;</li>
            <li> уютная атмосфера и интерьер;</li>
            <li> развлекательная зона с аниматрониками;</li>
            <li> душевная команда, которая любит своё дело.</li>
          </ul>
          <p>
            <strong>Приходите к нам — в гости к Мишке Фредди! </strong>
          </p>
        </div>

        <div className="about-section">
          <h2>Контакты</h2>
          <p><strong>Телефон:</strong> +7 (900) 123-45-67</p>
          <p><strong>Адрес:</strong> Союзная ул., 144, посёлок Тверицы, Ярославль</p>
        </div>
      </div>

      <div className="about-right">
        <img
          src={require('../assets/freddy-pizzeria.png')}
          alt="фото"
          className="about-image"
          style={{ height: `${leftHeight}px` }}
        />
      </div>
    </div>
  );
}