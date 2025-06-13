import React, { useEffect, useState } from 'react';
import API from '../api';
import '../productadmin.css';
import '../adminnavbar.css';
import '../admin.css'; // Для стилей модального окна

export default function ProductManagement() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selected, setSelected] = useState(null);
  const [quantity, setQuantity] = useState(0);
  const [imageFile, setImageFile] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const [newProduct, setNewProduct] = useState({
    name: '',
    description: '',
    weight: '',
    quantity: '',
    price: '',
    ingredients: '',
    category: '',
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    API.get('/admin/menu')
      .then(res => {
        setProducts(res.data);
        const uniqueCategories = Array.from(new Set(res.data.map(p => p.category)));
        setCategories(uniqueCategories);
      })
      .catch(err => console.error('Ошибка загрузки продуктов:', err));
  }, []);

  const openModal = (product) => {
    setSelected(product);
    setQuantity(product.quantity);
    setImageFile(null);
    setShowModal(true);
  };

  const confirmDelete = (id) => {
    setDeleteId(id);
    setShowDeleteModal(true);
  };

  const handleDelete = () => {
    API.delete(`/admin/menu/${deleteId}`).then(() => {
      setProducts(prev => prev.filter(p => p.id !== deleteId));
      setShowDeleteModal(false);
    });
  };

  const handleUpdate = async () => {
    try {
      await API.patch(`/admin/menu/${selected.id}/quantity`, { quantity });

      if (imageFile) {
        const formData = new FormData();
        formData.append('image', imageFile);
        await API.post(`/admin/menu/${selected.id}/image`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });
      }

      setProducts(prev =>
        prev.map(p => p.id === selected.id ? { ...p, quantity, updatedAt: Date.now() } : p)
      );

      setShowModal(false);
    } catch (err) {
      alert('Ошибка при обновлении продукта');
    }
  };

  const validate = () => {
    const errs = {};
    const nameTrim = newProduct.name.trim();

    if (!nameTrim || nameTrim.length > 25) {
      errs.name = 'Название не должно превышать 25 символов.';
    } else if (products.some(p => p.name.toLowerCase() === nameTrim.toLowerCase())) {
      errs.name = 'Продукт уже существует';
    }

    if (newProduct.description.length > 140) {
      errs.description = 'Описание не должно превышать 140 символов.';
    }

    const weight = parseInt(newProduct.weight);
    if (isNaN(weight) || weight <= 0 || weight >= 9999) {
      errs.weight = 'Вес должен быть числом от 1 до 9999';
    }

    const quantity = parseInt(newProduct.quantity);
    if (isNaN(quantity) || quantity < 0 || quantity > 9999) {
      errs.quantity = 'Количество должно быть от 0 до 9999';
    }

    const price = parseFloat(newProduct.price);
    if (isNaN(price) || price <= 0 || price > 99999) {
      errs.price = 'Цена должна быть числом от 1 до 99999';
    }

    if (newProduct.ingredients.length > 70) {
      errs.ingredients = 'Состав не должен превышать 70 символов';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleAddProduct = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    const { name, description, weight, quantity, price, ingredients, category } = newProduct;
    const payload = {
      name: name.trim(),
      description: description.trim() || null,
      weight: weight ? parseInt(weight) : null,
      quantity: quantity ? parseInt(quantity) : 0,
      price: parseFloat(price),
      ingredients: ingredients.trim() || null,
      category: category.trim()
    };

    try {
      const res = await API.post('/admin/menu', payload);
      const addedProduct = res.data;
      setProducts(prev => [...prev, addedProduct]);

      setShowAddModal(false);
      setNewProduct({
        name: '', description: '', weight: '', quantity: '', price: '',
        ingredients: '', category: ''
      });
      setErrors({});
    } catch (err) {
      alert('Ошибка при добавлении продукта');
    }
  };

const handleQuantityChange = (e) => {
  const raw = e.target.value;

  if (raw === '') {
    setQuantity('');
    return;
  }

  const cleaned = raw.replace(/^0+(?=\d)/, '');

  const num = Math.max(0, Number(cleaned));
  if (!isNaN(num)) setQuantity(num);
};

const decQty = () => setQuantity(prev => Math.max(0, (prev || 0) - 1));
const incQty = () => setQuantity(prev => (prev || 0) + 1);

  return (
    <div className="admin-container full-bg">
      <div className="staff-header">
        <h2>Продукты:</h2>
        <button className="add-button" onClick={() => setShowAddModal(true)}>Добавить продукт</button>
      </div>

      <div className="product-grid">
        {products.map(product => (
          <div className="product-card" key={product.id}>
            <button className="product-close" onClick={() => confirmDelete(product.id)}>×</button>
            <img src={API.getImageURL(product)} alt={product.name} className="product-image" />
            <h4 className="product-name">{product.name}</h4>

            <div className="product-row">
                <p className="product-availability">В наличии: {product.quantity}</p>
                <button className="product-button" onClick={() => openModal(product)}>Подробнее</button>
            </div>
          </div>
        ))}
      </div>

      {/* Модальное окно редактирования */}
      {showModal && selected && (
        <div className="modal-overlay">
          <div className="modal-box wide" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            <h3 className="modal-title">{selected.name}</h3>
            <div className="modal-content-product">
              <div className="product-details-left">
                <p><strong>Название:</strong> {selected.name}</p>
                <p><strong>Описание:</strong> {selected.description}</p>
                <p><strong>Вес:</strong> {selected.weight} г</p>
                <div className="quantity-control">
                  <p><strong>Количество:</strong></p>
                  <button onClick={decQty}>-</button>
                  <input
                    type="number"
                    value={quantity === '' ? '' : quantity}
                    onChange={handleQuantityChange}
                    className="form-input-quantity"
                    style={{ appearance: 'textfield' }}
                  />
                  <button onClick={incQty}>+</button>
                </div>
                <p><strong>Цена:</strong> {selected.price} ₽</p>
                <p><strong>Состав:</strong> {selected.ingredients}</p>
                <p><strong>Категория:</strong> {selected.category}</p>
              </div>
               <div className="product-details-right">
                  <img src={API.getImageURL(selected)} alt="preview" className="modal-product-image" />

                  <label htmlFor="fileUpload" className="file-label">
                    Загрузить фото
                  </label>
                  <input
                    type="file"
                    id="fileUpload"
                    className="file-input"
                    onChange={e => setImageFile(e.target.files[0])}
                  />
               </div>
            </div>
            <button className="edit-button" onClick={handleUpdate}>Редактировать продукт</button>
          </div>
        </div>
      )}

      {/* Модальное окно добавления */}
      {showAddModal && (
        <div className="modal-overlay">
          <div className="modal-box wide" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowAddModal(false)}>×</button>
            <h3 className="modal-title">Добавление продукта</h3>
            <form onSubmit={handleAddProduct} className="form-grid">
              {[
                ['Название:', 'name'],
                ['Описание:', 'description'],
                ['Вес:', 'weight'],
                ['Количество:', 'quantity'],
                ['Цена:', 'price'],
                ['Состав:', 'ingredients']
              ].map(([label, key]) => (
                <div className="form-row" key={key}>
                  <label>{label}</label>
                  <input
                    type="text"
                    value={newProduct[key]}
                    onChange={e => {
                      if (key === 'description' && e.target.value.length > 250) return;
                      if (key === 'ingredients' && e.target.value.length > 100) return;
                      if (key === 'name' && e.target.value.length > 25) return;
                      setNewProduct({ ...newProduct, [key]: e.target.value });
                    }}
                    className="form-input wide"
                    required
                  />
                  {errors[key] && <p className="form-error">{errors[key]}</p>}
                </div>
              ))}
              <div className="form-row">
                <label>Категория:</label>
                <select
                  value={newProduct.category}
                  onChange={e => setNewProduct({ ...newProduct, category: e.target.value })}
                  className="form-select wide"
                  required
                >
                  <option value="">Выберите категорию</option>
                  {categories.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>
              <button className="edit-button" type="submit">Добавить продукт</button>
            </form>
          </div>
        </div>
      )}

      {/* Модальное окно подтверждения удаления */}
      {showDeleteModal && (
        <div className="modal-overlay" onClick={() => setShowDeleteModal(false)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowDeleteModal(false)}>×</button>
            <h3 className="modal-title">Подтвердите удаление</h3>
            <p style={{ textAlign: 'center', marginBottom: '20px' }}>Вы уверены, что хотите удалить этот продукт?</p>
            <div style={{ display: 'flex', justifyContent: 'center', gap: '15px' }}>
              <button className="delete-button" onClick={() => setShowDeleteModal(false)}>Отмена</button>
              <button className="delete-button" onClick={handleDelete}>Удалить</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}