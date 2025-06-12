const [orders, setOrders] = useState([]);
  const navigate = useNavigate();

  const role = localStorage.getItem('adminRole');
  const filterOptions = ['All', ...staff_status_get[role]];

  // Проверка авторизации
  useEffect(() => {
    if (!localStorage.getItem('adminAccess')) {
  navigate('/admin-login');
  }
  }, [navigate]);

  useEffect(() => {
    API.get('/admin/orders')
      .then(res => setOrders(res.data.filter(obj => staff_status_get[role].includes(obj.status))))
      .catch(err => console.error('Ошибка при загрузке заказов:', err));
  }, []);

  const [selectedCategory, setSelectedCategory] = useState('All');
  const [filteredItems, setFilteredItems] = useState(orders);

   if (role !== "COOK"){
    return ("");
  }

  const handleFilterChange = (e) => {
    const status = e.target.value;
    setSelectedCategory(status);

    // Фильтрация списка
    if (status === 'All') {
      setFilteredItems(orders);
    } else {
      const filtered = orders.filter(item => item.status === status);
      setFilteredItems(filtered);
    }
  };



  const updateStatus = (id, status) => {
    API.patch(`/orders/${id}`, { status })
      .then(() => {
        setOrders(prev => prev.map(order => order.id === id ? { ...order, status } : order));
      })
      .catch(err => alert('Ошибка при обновлении статуса'));
  };

  const deleteOrder = (id) => {
    if (window.confirm("Удалить заказ?")) {
      API.delete(`/orders/${id}`)
        .then(() => setOrders(prev => prev.filter(order => order.id !== id)))
        .catch(err => alert('Ошибка при удалении'));
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial' }}>
      <h1>Filterable List</h1>

      {/* Выпадающий список для фильтрации */}
      <select
        value={selectedCategory}
        onChange={handleFilterChange}
        style={{ padding: '8px', fontSize: '16px' }}
      >
        {filterOptions.map(option => (
          <option key={option} value={option}>{rus_status[option]}</option>
        ))}
      </select>

      {/* Отображение отфильтрованного списка */}
      <div style={{ listStyle: 'none', padding: 0 }}>
        {filteredItems.map(item => (
          <div className="item-container-order">
            <p>id: {item.id}</p>
            <p>Статус: {rus_status[item.status]}</p>
            <p>Создано: {new Date(item.createdAt).toLocaleString()}</p>
            {("payment" in item)
              ? <p>Способ оплаты: {rus_payment[item.payment]}</p>
              : ""}
            {("comment" in item)
              ? <p>Комментарий: {item.comment}</p>
              : ""}
            {("customerName" in item)
              ? <p>Имя получателя: {item.customerName}</p>
              : ""}
            {("phone" in item)
              ? <p>Номер телефона: {item.phone}</p>
              : ""}
            {("address" in item)
              ? <p>Адрес: {item.address}</p>
              : ""}
          </div>
        ))}
      </div>
    </div>
  );