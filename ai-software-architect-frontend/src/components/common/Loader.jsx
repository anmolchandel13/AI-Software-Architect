import React from 'react';

/**
 * Premium loading spinner component with customizable text.
 */
const Loader = ({ message = "Processing..." }) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '20px' }}>
      <div style={{
        width: '48px',
        height: '48px',
        border: '4px solid rgba(139, 92, 246, 0.1)',
        borderTop: '4px solid #8b5cf6',
        borderRadius: '50%',
        animation: 'spin 1s linear infinite'
      }} />
      <span style={{ color: 'var(--text-secondary)', fontWeight: 500, fontSize: '0.95rem' }}>{message}</span>
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};

export default Loader;
