import React, { useState, useEffect } from 'react';
import { useParams } from "react-router-dom";
import '../order.css';
import API from '../api';

const rus_payment = {
    "CASH":"наличные",
    "CARD":"карта"
}
const rus_status = {
    "NEW": "Новый",
    "IN_PROGRESS":"В процессе",
    "OUT_FOR_DELIVERY":"На доставке",
    "READY_FOR_DELIVERY":"Готово к доставке",
    "DELIVERED":"Доставлено",
    "CANCELLED":"Отменён"
}

export default function Order() {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const { tracking_code } = useParams();

    useEffect(() => {
        if (!tracking_code)
            return;
        const fetchData = async () => {
        try {
            const response = await API.get(`/orders/${tracking_code}`);
            setData(response.data);
            setError(null);
        } catch (err) {
            if (err.response.status === 404)
                setError("Заказ не найден.")
            else
                setError(err.message);
        } finally {
            setLoading(false);
        }
        };

        fetchData();

        const intervalId = setInterval(fetchData, 10000);

        return () => clearInterval(intervalId);
    }, [tracking_code]);

    if (error) return <div className="error-style">{error}</div>;
    if (loading) return <h2>Загрузка...</h2>;

    const order = data;

    const date = new Date(order.createdAt);

    const timeString = date.toLocaleTimeString('ru-RU', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    });

    const dateString = date.toLocaleDateString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    }).replace(/\//g, '.');

    return (
        <div className="orders-container">
            <div className="orders-info-container">
                <div className="orders-block block1">
                    Трек-номер:
                </div>
                <div className="orders-block block2">
                    {tracking_code}
                </div>
                <div className="orders-block block3">
                    {rus_status[order.status]}
                </div>
                <div className="orders-extra-block">Для отслеживания статуса заказа введите номер в разделе "Заказ"</div>
            </div>
            <div className="order-info1">
                <p>Заказ:</p>
                <span className="order-time">
                    {timeString}
                </span>
                <span className="order-date">
                    {dateString}
                </span>
            </div>
            {order.items.map((item, idx) => (
              <div
                key={item.id ?? item.productId ?? `${item.productName}-${idx}`}
                className="order-cart-item"
              >
                <div className="order-product-info">
                  <p>{item.productName}</p>
                </div>
                <div className="order-quantity-group">
                  <span className="order-quantity">{item.quantity}</span>
                </div>
                <div className="order-price">{item.quantity * item.price} ₽</div>
              </div>
            ))}
            
            <div className="order-info2">
                Тип оплаты: {rus_payment[order.payment]}
            </div>
            {order.comment && order.comment.trim() !== '' && (
              <>
                <div className="order-info3">
                  Комментарий:
                </div>
                <div className="order-comment">
                  {order.comment}
                </div>
              </>
            )}
            <div className="order-info4">
                Итого: {order.totalPrice} ₽
            </div>
        </div>
    )
}