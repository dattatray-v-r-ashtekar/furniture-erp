import React, { useState } from 'react';
import { ShoppingCart, CreditCard, CheckCircle } from 'lucide-react';

const products = [
  { id: 'BED-KING', name: 'Luxury King Size Bed', price: 45000.00, img: '🛏️', desc: 'Solid teak wood frame with memory foam mattress.' },
  { id: 'TABLE-OAK', name: 'Custom Oak Dining Table', price: 75000.50, img: '🪑', desc: 'Solid wood dining table, built to order.' },
  { id: 'CHAIR-OFFICE', name: 'Ergo Office Chair', price: 12500.00, img: '💺', desc: 'Breathable mesh back with lumbar support.' }
];

export default function Ecommerce() {
  const [cart, setCart] = useState([]);
  const [checkoutStatus, setCheckoutStatus] = useState(null);
  const [responseLog, setResponseLog] = useState(null);

  const addToCart = (product) => {
    setCart([...cart, product]);
  };

  const getCartTotal = () => {
    return cart.reduce((total, item) => total + item.price, 0).toFixed(2);
  };

  const handleCheckout = async () => {
    if (cart.length === 0) return;
    setCheckoutStatus('processing');
    
    try {
      const items = cart.map(item => ({
        sku: item.id,
        name: item.name,
        quantity: 1,
        price: item.price
      }));
      const totalAmount = parseFloat(getCartTotal());
      const orderRef = `ORD-B2C-${Date.now().toString().slice(-6)}`;

      const payload = {
        referenceCode: orderRef,
        totalAmount: totalAmount,
        items: items
      };
      
      const response = await fetch('http://localhost:8081/api/v1/ecommerce/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      
      const data = await response.json();
      setResponseLog(data);
      setCheckoutStatus('success');
      setCart([]); // Empty cart on success
    } catch (error) {
      console.error(error);
      setCheckoutStatus('error');
      setResponseLog({ error: 'Failed to reach http://localhost:8081. Ensure backend is running.' });
    }
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px', height: '100%' }}>
      {/* Product Catalog */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <h2 style={{ fontSize: '1.2rem', marginBottom: '8px' }}>Featured Products</h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '16px' }}>
          {products.map(p => (
            <div key={p.id} className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div style={{ fontSize: '4rem', textAlign: 'center' }}>{p.img}</div>
              <h3 style={{ fontSize: '1.1rem' }}>{p.name}</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{p.desc}</p>
              <div style={{ marginTop: 'auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '16px' }}>
                <span style={{ fontWeight: 'bold', fontSize: '1.2rem', color: 'var(--accent-primary)' }}>₹{p.price.toFixed(2)}</span>
                <button className="btn btn-primary" style={{ padding: '6px 12px', fontSize: '0.85rem' }} onClick={() => addToCart(p)}>
                  Add to Cart
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Shopping Cart */}
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', height: 'fit-content', position: 'sticky', top: '16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '24px' }}>
          <ShoppingCart size={24} color="var(--accent-primary)" />
          <h2 style={{ fontSize: '1.2rem' }}>Your Cart</h2>
        </div>

        {cart.length === 0 ? (
          <div style={{ padding: '32px 0', textAlign: 'center', color: 'var(--text-secondary)' }}>
            Your cart is empty.
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {cart.map((item, idx) => (
              <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '12px', borderBottom: '1px solid var(--border-glass)' }}>
                <span>{item.name}</span>
                <span>₹{item.price.toFixed(2)}</span>
              </div>
            ))}
            
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '16px', fontWeight: 'bold', fontSize: '1.2rem' }}>
              <span>Total:</span>
              <span className="gradient-text">₹{getCartTotal()}</span>
            </div>

            <button 
              className="btn btn-primary" 
              style={{ marginTop: '24px', width: '100%', padding: '12px' }}
              onClick={handleCheckout}
              disabled={checkoutStatus === 'processing'}
            >
              {checkoutStatus === 'processing' ? 'Processing...' : <><CreditCard size={18} /> Checkout Now</>}
            </button>
          </div>
        )}

        {/* Success / Error Log */}
        {checkoutStatus === 'success' && (
          <div className="animate-fade-in" style={{ marginTop: '24px', padding: '16px', background: 'rgba(16, 185, 129, 0.1)', border: '1px solid var(--status-success)', borderRadius: 'var(--radius-sm)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--status-success)', marginBottom: '8px', fontWeight: 'bold' }}>
              <CheckCircle size={18} /> Order Placed Successfully!
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
              Order Reference: <strong>{responseLog?.referenceCode}</strong>
            </p>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
              Total Amount: <strong>₹{Number(responseLog?.totalAmount || 0).toFixed(2)}</strong>
            </p>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '8px' }}>
              Order UUID: <code style={{ fontSize: '0.75rem' }}>{responseLog?.id}</code>
            </p>
            <div style={{ marginTop: '12px', fontSize: '0.8rem', color: 'var(--accent-primary)', lineHeight: '1.4' }}>
              💡 Events published! Check <strong>Sales Orders</strong>, <strong>Finance Ledger</strong>, and <strong>Manufacturing</strong> tabs to see real-time updates.
            </div>
          </div>
        )}

        {checkoutStatus === 'error' && (
           <div className="animate-fade-in" style={{ marginTop: '24px', padding: '16px', background: 'rgba(239, 68, 68, 0.1)', border: '1px solid var(--status-error)', borderRadius: 'var(--radius-sm)' }}>
             <p style={{ color: 'var(--status-error)', fontSize: '0.85rem' }}>{responseLog?.error}</p>
           </div>
        )}
      </div>
    </div>
  );
}
