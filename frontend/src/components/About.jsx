import React, { useEffect, useRef, useState } from 'react';
import '../about.css';

export default function AboutPage() {
  const leftRef = useRef(null);
  const [leftHeight, setLeftHeight] = useState(0);
  const [showScreamer, setShowScreamer] = useState(false);

  useEffect(() => {
    if (leftRef.current) {
      setLeftHeight(leftRef.current.clientHeight);
    }
  }, []);

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') {
        setShowScreamer(false);
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  const handleScreamer = () => {
    const audio = new Audio(require('../assets/screamer.mp3'));
    audio.play();
    setShowScreamer(true);

    setTimeout(() => {
      setShowScreamer(false);
    }, 2500);
  };

 return (
     <div className="about-layout">
       <div className="about-left" ref={leftRef}>
         <div className="about-section">
           <h2>О нас — Freddy's Pizza: легенда с хрустящей корочкой!</h2>
           <p>
             Когда-то, давным-давно, где-то между 1983 и «вчера в 3 ночи», в одном тихом районе открылась необычная пиццерия.
             Там пахло расплавленным сыром, на сцене выступали аниматроники, а дети визжали от счастья
             (иногда и не только от счастья).
           </p>
           <p>
             Эта пиццерия исчезла из реальности... но осталась в памяти. И вот — мы её вернули. С соусом и легендой!
           </p>

           <h3> Немного (очень странной) истории</h3>
           <p>
             Freddy Fazbear's Pizza была не просто пиццерией. Это было шоу, семейное место и немного хоррор-квест в одном флаконе.
             Аниматроники пели песни, готовили сюрпризы, а охранники... ну, они старались.
           </p>

           <h3>Кто за этим стоит?</h3>
           <p>
             Мы — фанаты FNaF, которые в детстве не закрывали двери в охранной.<br />
             Мы решили создать реальную пиццерию по мотивам любимой игры, где каждое блюдо — это отсылка,
             а каждый интерьерный элемент — пасхалка.
           </p>

           <h3> Что на вкус?</h3>
           <ul className="fun-list">
             <li>Пиццы, собранные по рецептам с 1987 года (но с нормальным тестом, не как раньше)</li>
             <li>Напитки, которые светятся (ну почти)</li>
             <li>Десерты, которые выглядят подозрительно живыми</li>
             <li>Салаты, но только чтобы Фредди не злился</li>
           </ul>

           <h3> Будь начеку...</h3>
           <p>
             Иногда мигают лампы. Иногда играет музыка, которую ты не включал.
             Иногда курьер исчезает за секунду после доставки.<br />
             Но всё в порядке. Мы так и задумали.<br />
             Просто не забудь проверять вентиляции. И запеканку в духовке.
           </p>

           <div className="highlight-block">
             <h3>Почему ты это полюбишь:</h3>
             <ul>
               <li>Это FNaF в реальной жизни, но с настоящей пиццей</li>
               <li>Это весело, атмосферно, слегка пугающе и очень вкусно</li>
               <li>Это место, где ты чувствуешь себя как дома... у аниматроников</li>
             </ul>
           </div>

           <p className="signature">
             <strong>Freddy's Pizza</strong> — мы не просто кормим. Мы пробуждаем воспоминания.<br />
             И голод. Особенно к полуночи.
           </p>
         </div>

         <div className="about-section">
           <h2>Контакты</h2>
           <p><strong>Телефон:</strong> +7 (900) 123-45-67</p>
           <p><strong>Адрес:</strong> Союзная ул., 144, посёлок Тверицы, Ярославль</p>
           <p><strong>Часы работы:</strong> 10:00 - 23:59 (после полуночи — только для особых гостей)</p>
         </div>
       </div>

       <div className="about-right">
      <div className="freddy-nose" onClick={handleScreamer}></div>

             <img
               src={require('../assets/freddy-pizzeria.png')}
               alt="Интерьер Freddy's Pizza"
               className="about-image"
               style={{ height: `${leftHeight}px` }}
             />
           </div>

           {showScreamer && (
             <div className="modal-overlay2" onClick={() => setShowScreamer(false)}>
               <div className="modal-content1">
                 <img
                   src={require('../assets/screamer.gif')}
                   alt="Screamer"
                   className="screamer-image"
                 />
               </div>
             </div>
           )}
         </div>
       );
     }