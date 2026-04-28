
import { productApi } from './api.js';

const NAV_ITEMS = [
  { href:'index.html', label:'Dashboard', key:'dashboard' },
  { href:'products.html', label:'Inventory', key:'inventory' },
  { href:'user-products.html', label:'Catalog', key:'catalog' },
  { href:'low-stock.html', label:'Low Stock', key:'low-stock', badge:true },
  { href:'create-product.html', label:'Add Product', key:'create' }
];

export async function renderNavbar(activeKey = '') {
  const mount = document.getElementById('navbar-root');
  if (!mount) return;
  mount.innerHTML = `
    <header class="sticky-header">
      <div class="container navbar">
        <a class="brand" href="index.html">
          <img src="assets/images/vendora-logo.jpeg" alt="Vendora logo" />
          <span>Vendora</span>
        </a>
        <nav class="nav-links">
          ${NAV_ITEMS.map(item => `
            <a class="nav-link ${activeKey === item.key ? 'active' : ''}" href="${item.href}">
              ${item.label}
              ${item.badge ? '<span class="badge-dot" id="low-stock-badge" style="display:none"></span>' : ''}
            </a>`).join('')}
        </nav>
      </div>
    </header>`;
  try {
    const products = await productApi.getLowStock();
    const badge = document.getElementById('low-stock-badge');
    if (badge && products.length > 0) {
      badge.style.display = 'inline-flex';
      badge.textContent = products.length > 99 ? '99+' : String(products.length);
    }
  } catch {}
}
