import React, { useEffect, useState } from 'react';
import API from '../api';
import '../productadmin.css';
import '../adminnavbar.css';
import '../admin.css';
import AccessDenied from './AccessDenied';
import { useNavigate } from 'react-router-dom';

const categoryNames = {
  PIZZA: 'Пицца',
  DRINK: 'Напитки',
  SNACK: 'Закуски',
  DESSERT: 'Десерты',
  SALAD: 'Салаты',
  MERCH: 'Мерч',
  ROLLS: 'Роллы'
};

export default function ProductManagement() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selected, setSelected] = useState(null);
  const [quantity, setQuantity] = useState(0);
  const [imageFile, setImageFile] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const [editProduct, setEditProduct] = useState(null);
  const [newProduct, setNewProduct] = useState({
    name: '', description: '', weight: '', quantity: '', price: '', ingredients: '', category: ''
  });
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState('');
  const [role, setRole] = useState('');
  const [allowed, setAllowed] = useState(null);
  const navigate = useNavigate();


   useEffect(() => {
   if (!localStorage.getItem('adminAccess')) {
       navigate('/admin-login');
       return;
   }
    const userRole = localStorage.getItem('adminRole');
    setRole(userRole);
    setAllowed(userRole === 'ADMIN');
    fetchProducts()
  }, []);

  if (allowed === false) return <AccessDenied />;

    const fetchProducts = () => {
      API.get('/admin/menu', { withCredentials: true })
        .then(res => {
          setProducts(res.data);
          const cats = Array.from(new Set(res.data.map(p => p.category)));
          setCategories(cats);
        })
        .catch(err => console.error('Ошибка загрузки меню:', err));
    };

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

  const validate = (product = newProduct, editing = false) => {
    const errs = {};
    const nameTrim = product.name.trim();

    if (!nameTrim || nameTrim.length > 25) {
      errs.name = 'Название не должно превышать 25 символов или быть пустым.';
    } else {
      const conflict = products.find(p => p.name.toLowerCase() === nameTrim.toLowerCase());
      if (conflict && (!editing || conflict.id !== product.id)) {
        errs.name = 'Продукт уже существует';
      }
    }

    if (product.description.length > 140) errs.description = 'Описание не должно превышать 140 символов.';
    const weight = parseInt(product.weight);
    if (isNaN(weight) || weight <= 0 || weight >= 9999) errs.weight = 'Вес должен быть числом от 1 до 9999';
    const quantity = parseInt(product.quantity);
    if (isNaN(quantity) || quantity < 0 || quantity > 9999) errs.quantity = 'Количество должно быть от 0 до 9999';
    const price = parseFloat(product.price);
    if (isNaN(price) || price <= 0 || price > 99999) errs.price = 'Цена должна быть числом от 1 до 99999';
    const ing = product.ingredients.trim();
    if (!ing) {errs.ingredients = 'Поле Состав не должны быть пустым';
    } else if (ing.length > 70) {
      errs.ingredients = 'Состав не должен превышать 70 символов';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

    const handleAddProduct = async (e) => {
      e.preventDefault();
      setFormError('');
      if (!validate()) return;

      const payload = {
        name: newProduct.name.trim(),
        description: newProduct.description.trim() || null,
        weight: parseInt(newProduct.weight),
        quantity: parseInt(newProduct.quantity),
        price: parseFloat(newProduct.price),
        ingredients: newProduct.ingredients.trim(),
        category: newProduct.category.trim()
      };

      try {
        const { data: created } = await API.post('/admin/menu', payload);

        setProducts(prev => [...prev, created]);
        setCategories(prev => (prev.includes(created.category) ? prev : [...prev, created.category]));

        setShowAddModal(false);
        openModal(created);

        setNewProduct({ name: '', description: '', weight: '', quantity: '', price: '', ingredients: '', category: '' });
        setErrors({});
      } catch {
        setFormError('Ошибка при добавлении продукта');
      }
    };

  const openEditModal = (product) => {
    setEditProduct({ ...product, weight: String(product.weight), quantity: String(product.quantity), price: String(product.price) });
    setErrors({});
    setShowEditModal(true);
  };

  const handleEditChange = (key, value) => {
    if (['weight', 'quantity', 'price'].includes(key) && !/^\d*$/.test(value)) return;
    setEditProduct(prev => ({ ...prev, [key]: value }));
  };

    const handleSaveEdit = async () => {
      setFormError('');
      if (!validate(editProduct, true)) return;

      try {
        const payload = {
          name: editProduct.name.trim(),
          description: editProduct.description.trim() || null,
          weight: parseInt(editProduct.weight),
          quantity: parseInt(editProduct.quantity),
          price: parseFloat(editProduct.price),
          ingredients: editProduct.ingredients.trim(),
          category: editProduct.category
        };

        await API.put(`/admin/menu/${editProduct.id}`, payload);

        setProducts(prev =>
          prev.map(p => p.id === editProduct.id ? { ...editProduct } : p)
        );

        setSelected(prev => ({ ...prev, ...editProduct }));

        setShowEditModal(false);

      } catch {
        setFormError('Ошибка при обновлении продукта');
      }
    };

    const handleQuantityChange = (e) => {
      const raw = e.target.value;

      if (raw === '') {
        setQuantity('');
        return;
      }

      const digitsOnly = raw.replace(/\D/g, '');

      const limited = digitsOnly.slice(0, 4);

      const cleaned = limited.replace(/^0+(?=\d)/, '');

      const num = Number(cleaned);

      if (!isNaN(num)) {
        setQuantity(num);
      }
    };

    const decQty = () => setQuantity(prev => Math.max(0, (prev || 0) - 1));
    const incQty = () => setQuantity(prev => {
      const next = (prev || 0) + 1;
      return next > 9999 ? 9999 : next;
    });

    const handleUpdateQuantityAndImage = async () => {
          setFormError('');

          if (quantity === '' || Number(quantity) > 9999) {
            setFormError('Введите число от 0 до 9999');
            return;
          }

          const safeQuantity = Number(quantity);

          try {
            await API.patch(`/admin/menu/${selected.id}/quantity`, { quantity: safeQuantity });

            if (imageFile) {
              const formData = new FormData();
              formData.append('image', imageFile);
              const res = await API.post(`/admin/menu/${selected.id}/image`, formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
              });
              setProducts(prev =>
                prev.map(p => p.id === selected.id ? { ...p, imagePath: res.data.imagePath, quantity: safeQuantity } : p)
              );
            } else {
              setProducts(prev =>
                prev.map(p => p.id === selected.id ? { ...p, quantity: safeQuantity } : p)
              );
            }

            setShowModal(false);
          } catch {
            setFormError('Ошибка при обновлении продукта');
          }
        };

  return (
    <div className="admin-container full-bg">
      <div className="staff-header">
        <h2>Продукты:</h2>
        <button className="add-button" onClick={() => setShowAddModal(true)}>Добавить продукт</button>
      </div>

      {categories.length === 0 ? (
        <p className="None">Нет доступных товаров</p>
      ) : categories.map(cat => (
        <div key={cat}>
          <h3 style={{ marginTop: '50px', fontSize: '30px', marginLeft: '90px', marginBottom: '20px' }}>{categoryNames[cat] || cat}</h3>
          <div className="product-grid">
            {products.filter(p => p.category === cat).map(product => (
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
        </div>
      ))}

      {showModal && selected && (
        <div className="modal-overlay">
          <div className="modal-box wide" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            <h3 className="modal-title">{selected.name}</h3>
            <div className="modal-content-product">
              <div className="product-details-left">
                <p><strong>Описание:</strong> {selected.description}</p>
                <p><strong>Вес:</strong> {selected.weight} г</p>
                <div className="quantity-control">
                    <p><strong>Количество:</strong></p>
                    <div className="quantity-controls">
                      <button onClick={decQty}>-</button>
                      <input
                        type="number"
                        value={quantity === '' ? '' : quantity}
                        onChange={handleQuantityChange}
                        className="form-input-quantity"
                        style={{ appearance: 'textfield' }}
                      />
                      <button onClick={incQty}>+</button>
                      <button onClick={handleUpdateQuantityAndImage}
                              title="Сохранить"
                              disabled={quantity === '' || Number(quantity) > 9999}>✔</button>
                    </div>
                    {formError && <p className="form-error">{formError}</p>}
                </div>
                <p><strong>Цена:</strong> {selected.price} ₽</p>
                <p><strong>Состав:</strong> {selected.ingredients}</p>
                <p><strong>Категория:</strong> {categoryNames[selected.category] || selected.category}</p>
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
                    accept=".png, .jpg, .jpeg"
                    style={{ display: 'none' }}
                    onChange={(e) => {
                      const file = e.target.files[0];
                      if (!file) return;

                      const validTypes = ['image/png', 'image/jpeg', 'image/jpg'];
                      if (!validTypes.includes(file.type)) {
                        setErrors(prev => ({ ...prev, image: 'Недопустимое разрешение' }));
                        return;
                      }

                      setImageFile(file);

                      const formData = new FormData();
                      formData.append('image', file);

                      API.post(`/admin/menu/${selected.id}/image`, formData, {
                        headers: { 'Content-Type': 'multipart/form-data' }
                      })
                        .then(res => {
                          setProducts(prev =>
                            prev.map(p => p.id === selected.id
                              ? { ...p, imagePath: res.data.imagePath }
                              : p
                            )
                          );
                          setSelected(prev => ({ ...prev, imagePath: res.data.imagePath }));

                          setErrors(prev => ({ ...prev, image: '' }));
                        })
                        .catch(() => {
                          setErrors(prev => ({ ...prev, image: 'Ошибка при загрузке изображения' }));
                        });
                    }}
                  />

                  {errors.image && <p className="form-error">{errors.image}</p>}
                </div>

            </div>
            <button className="edit-button" onClick={() => openEditModal(selected)}>Редактировать продукт</button>
          </div>
        </div>
      )}

      {showEditModal && editProduct && (
        <div className="modal-overlay">
          <div className="modal-box wide" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowEditModal(false)}>×</button>
            <h3 className="modal-title">Редактировать</h3>
            <form className="form-grid">
                {[
                  ['Название:', 'name', 23],
                  ['Описание:', 'description', 140],
                  ['Вес:', 'weight', 4],
                  ['Количество:', 'quantity', 4],
                  ['Цена:', 'price', 5],
                  ['Состав:', 'ingredients', 70]
                ].map(([label, key, maxLen]) => (
                  <div className="form-row" key={key}>
                    <label>{label}</label>
                    <input
                      type="text"
                      inputMode={['weight','quantity','price'].includes(key) ? 'numeric' : undefined}
                      maxLength={maxLen}
                      value={editProduct[key]}
                      onChange={e => handleEditChange(key, e.target.value)}
                      className="form-input wide"
                      required
                    />
                    {errors[key] && <p className="form-error">{errors[key]}</p>}
                  </div>
                ))}
              <div className="form-row">
                <label>Категория:</label>
                <select
                  value={editProduct.category}
                  onChange={e => setEditProduct(prev => ({ ...prev, category: e.target.value }))}
                  className="form-select wide"
                  required
                >
                  <option value="">Выберите категорию</option>
                    {Object.entries(categoryNames).map(([code, name]) => (
                    <option key={code} value={code}>{name}</option>
                    ))}
                </select>
              </div>
              <div style={{ display: 'flex', gap: '15px', marginTop: '20px' }}>
                {formError && <p className="form-error">{formError}</p>}
                <button type="button" className="edit-button" onClick={handleSaveEdit}>Сохранить</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showAddModal && (
        <div className="modal-overlay">
          <div className="modal-box wide" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowAddModal(false)}>×</button>
            <h3 className="modal-title">Добавление продукта</h3>
            <form onSubmit={handleAddProduct} className="form-grid">
                {[
                  ['Название:', 'name', 23],
                  ['Описание:', 'description', 140],
                  ['Вес:', 'weight', 4],
                  ['Количество:', 'quantity', 4],
                  ['Цена:', 'price', 5],
                  ['Состав:', 'ingredients', 70]
                ].map(([label, key, maxLen]) => (
                  <div className="form-row" key={key}>
                    <label>{label}</label>
                    <input
                      type="text"
                      inputMode={['weight','quantity','price'].includes(key) ? 'numeric' : undefined}
                      maxLength={maxLen}
                      value={newProduct[key]}
                      onChange={e => {
                        const v = e.target.value;
                        if (['weight','quantity'].includes(key) && !/^\d*$/.test(v)) return;
                        if (key === 'price' && !/^\d*\.?\d*$/.test(v)) return;
                        setNewProduct(prev => ({ ...prev, [key]: v }));
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
                  onChange={e => setNewProduct(prev => ({ ...prev, category: e.target.value }))}
                  className="form-select wide"
                  required
                >
                  <option value="">Выберите категорию</option>
                        {Object.entries(categoryNames).map(([code, name]) => (
                     <option key={code} value={code}>{name}</option>
                 ))}
                </select>
              </div>
              {formError && <p className="form-error">{formError}</p>}
              <button className="edit-button" type="submit">Добавить продукт</button>
            </form>
          </div>
        </div>
      )}

      {showDeleteModal && (
        <div className="modal-overlay" onClick={() => setShowDeleteModal(false)}>
          <div className="modal-box" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowDeleteModal(false)}>×</button>
            <h3 className="modal-title">Подтвердите удаление</h3>
            <p style={{ textAlign: 'center', marginBottom: '20px', fontSize: '20px' }}>Вы уверены, что хотите удалить этот продукт?</p>
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