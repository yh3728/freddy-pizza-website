<div className="modal-overlay" onClick={onClose}>
    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
    <button className="modal-close" onClick={onClose}>×</button>
    <img src={product.image} alt={product.name} className="modal-image" />
    <h2>{product.name}</h2>
    <p><strong>Ингредиенты:</strong> {product.ingredients?.join(', ')}</p>
    <p><strong>Вес:</strong> {product.weight || product.volume}</p>
    <p><strong>Цена:</strong> {product.price} ₽</p>

    <div className="modal-quantity">
        <span>Количество:</span>
        <div className="quantity-controls">
        <button onClick={() => setCount(Math.max(0, count - 1))}>−</button>
        <span>{count}</span>
        <button onClick={() => setCount(count + 1)}>+</button>
        </div>
        <p><strong>Сумма:</strong> {count * product.price} ₽</p>
    </div>

    {count > 0 && (
        <button
        className="add-to-cart-btn"
        onClick={() => {
            for (let i = 0; i < count; i++) {
            onAddToCart(); // Добавляем нужное количество товаров
            }
            onClose();
        }}
        >
        Добавить в корзину
        </button>
    )}
    </div>
</div>