import React, { useEffect, useState } from 'react';
import './style.css';
import './productcard.css';
import './modal.css';
import backgroundImage from './assets/background.jpeg';
import contentBgImage from './assets/content.jpeg';
import ProductCard from './components/ProductCard';
import CartPage from './components/CartPage';
import About from './components/About';
import Holidays from './components/Holidays';
import Order from './components/Order';
import OrderSearch from './components/OrderSearch';
import AdminNavbar from './components/AdminNavbar';
import AdminLogin from './components/AdminLogin';
import StaffManagement from './components/StaffManagement';
import ProductManagement from './components/ProductManagement';
import OrderManagement from './components/OrderManagement';
import Navbar from './components/Navbar';
import PageNotFound from './components/PageNotFound';
import { Routes, Route, useLocation, Outlet } from 'react-router-dom';
import API from './api';

function ScrollToTop() {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [pathname]);

  return null;
}

const MainLayout = () => (
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
            <Outlet />
          </div>
        </div>
      </div>
    </>
);

const LoginLayout = () => (
  <Outlet />
)

const AdminLayout = () => (
  <div style={{ paddingTop: '120px', backgroundColor: '#fdf5c9' }}>
    <AdminNavbar />
    <Outlet />
  </div>
)

export default function App() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    API.get('/menu')
      .then((res) => {
        setProducts(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Ошибка API:', err);
        setLoading(false);
      });
  }, []);


  const categories = ["PIZZA","SNACK","SALAD","ROLLS","DESSERT","DRINK","MERCH"];
  const rus_category = {
  PIZZA: "Пицца",
  SNACK: "Закуски",
  SALAD: "Салаты",
  ROLLS: "Роллы",
  DESSERT: "Десерты",
  DRINK: "Напитки",
  MERCH: "Мерч",
};

  return (
    <Routes>
        <Route element={<MainLayout />}>
            <Route
            path="/"
            element={categories.map(category => (
                <React.Fragment key={category}>
                <div className="category-title" id={category.toLowerCase()}>
                    <h2>{rus_category[category]}</h2>
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
            />
            <Route path="/cart" element={<CartPage background={contentBgImage} />} />
            <Route path="/about" element={<About />} />
            <Route path="/holidays" element={<Holidays />} />
            <Route path="/order" element={<OrderSearch />} />
            <Route path="/order/:tracking_code" element={<Order />} />
            <Route path="*" element={<PageNotFound />} />

        </Route>
        <Route element={<LoginLayout />}>
          <Route path="/admin-login" element={<AdminLogin />} />
        </Route>
        <Route element={<AdminLayout />}>
            <Route path="/admin/staff" element={<StaffManagement />} />
            <Route path="/admin/products" element={<ProductManagement />} />
            <Route path="/admin/orders" element={<OrderManagement />} />
        </Route>
    </Routes>
  );
}