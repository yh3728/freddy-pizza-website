import React from 'react';

export default function AccessDenied() {
  return (
    <div className="admin-container full-bg">
        <div className="error-style">
            Доступ запрещён. Только для администратора.
        </div>
    </div>
  );
}