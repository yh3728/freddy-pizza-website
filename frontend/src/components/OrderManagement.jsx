import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';
import '../admin.css';

const rus_payment = {
    "CASH":"наличные",
    "CARD":"карта"
}

const rus_status = {
    "All": "Все",
    "NEW": "Новый",
    "IN_PROGRESS":"В процессе",
    "OUT_FOR_DELIVERY":"На доставке",
    "READY_FOR_DELIVERY":"Готово к доставке",
    "DELIVERED":"Доставлено",
    "CANCELLED":"Отменён"
}

const staff_status_get = {
    "ADMIN": [
        "NEW", 
        "IN_PROGRESS", 
        "OUT_FOR_DELIVERY", 
        "READY_FOR_DELIVERY", 
        "DELIVERED",
        "CANCELLED",
    ],
    "COOK" : [
        "NEW", 
        "IN_PROGRESS",
    ],
    "DELIVERY": [
        "OUT_FOR_DELIVERY", 
        "READY_FOR_DELIVERY",
    ]
}

const staff_status_post = {
    "ADMIN": [
        "NEW", 
        "IN_PROGRESS", 
        "OUT_FOR_DELIVERY", 
        "READY_FOR_DELIVERY", 
        "DELIVERED",
        "CANCELLED",
    ],
    "COOK" : [
        "IN_PROGRESS",
        "READY_FOR_DELIVERY",
    ],
    "DELIVERY": [
        "OUT_FOR_DELIVERY",
        "DELIVERED",
    ]
}

const info = {
  "NEW" : {next_status : "IN_PROGRESS", button_text : "Готовить", color : "#899fff"},
  "IN_PROGRESS" : {next_status : "READY_FOR_DELIVERY", button_text : "Готов", color : "#a2f476"},
}

export default function OrderManagement() {
  return ("");
}