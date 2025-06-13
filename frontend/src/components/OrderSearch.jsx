import React, { useState} from 'react';
import {useNavigate} from "react-router-dom";

export default function OrderSearch() {
    const navigate = useNavigate();
    const [inputValue, setInputValue] = useState('');

    const handleInputChange = (e) => {
        const value = e.target.value.toUpperCase();
        // Фильтруем ввод, оставляя только разрешенные символы
        const filteredValue = value.replace(/[^0-9A-Z]/gi, '');
        // Обрезаем до 6 символов
        const truncatedValue = filteredValue.slice(0, 6);
        setInputValue(truncatedValue);
    };

    const handleSave = () => {
        navigate(`/order/${inputValue}`);
    };
  
    return (
        <div className="order-search">
            <h2>Введите номер заказа:</h2>
            <input
                type="text"
                value={inputValue}
                onChange={handleInputChange}
                maxLength={6}
                style={{
                padding: '8px',
                fontSize: '22px',
                marginRight: '10px',
                border: '2px solid #ed1308'
                }}
            />
            <button
                onClick={handleSave}
                style={{
                padding: '8px 16px',
                fontSize: '22px',
                backgroundColor: '#ed1308',
                color: 'black',
                fontWeight: 700,
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
                }}
            >
                Поиск
            </button>
        </div>
    );
    
}