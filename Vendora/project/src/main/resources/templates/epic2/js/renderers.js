
import { buildImageSrc, formatCurrency, CATEGORY_LABELS, getStockClass, getStockLabel, getStatusClass, escapeHtml } from './helpers.js';

export function renderProductCard(product, { hrefBase = 'product-detail.html', user = false } = {}) {
  const img = buildImageSrc(product);
  return `
  <article class="card product-card">
    <a href="${hrefBase}?id=${product.id}">
      <div class="product-image">${img ? `<img src="${img}" alt="${escapeHtml(product.name)}" />` : '<div class="muted">No image</div>'}</div>
      <div class="product-content">
        <div class="product-brand">${escapeHtml(product.brand || '—')}</div>
        <h3 class="product-name">${escapeHtml(product.name || 'Untitled')}</h3>
        <div class="flex items-center gap-8" style="flex-wrap:wrap; margin-bottom:10px;">
          <span class="badge cat-badge">${CATEGORY_LABELS[product.category] || product.category || 'Category'}</span>
          <span class="${getStockClass(product)}">${getStockLabel(product)}</span>
          ${user ? '' : `<span class="${getStatusClass(product.status)}">${escapeHtml(product.status || '')}</span>`}
        </div>
        <p class="muted" style="margin:0; min-height:38px;">${escapeHtml(product.description || '')}</p>
        <p class="product-price">${formatCurrency(product.price)}</p>
        <div class="product-meta">
          <span>${escapeHtml(product.sku || 'No SKU')}</span>
          <span>${product.stockQuantity ?? 0} ${escapeHtml(product.unit || 'units')}</span>
        </div>
      </div>
    </a>
  </article>`;
}

export function renderProductRow(product) {
  const img = buildImageSrc(product);
  return `
  <tr>
    <td>
      <div class="flex items-center gap-12">
        ${img ? `<img class="small-thumb" src="${img}" alt="${escapeHtml(product.name)}" />` : '<div class="small-thumb"></div>'}
        <div>
          <div style="font-weight:800">${escapeHtml(product.name)}</div>
          <div class="muted" style="font-size:13px;">${escapeHtml(product.brand || '')}</div>
        </div>
      </div>
    </td>
    <td><span class="badge cat-badge">${CATEGORY_LABELS[product.category] || product.category || '—'}</span></td>
    <td>${formatCurrency(product.price)}</td>
    <td>${product.stockQuantity ?? 0} ${escapeHtml(product.unit || 'units')}</td>
    <td><span class="${getStockClass(product)}">${getStockLabel(product)}</span></td>
    <td><span class="${getStatusClass(product.status)}">${escapeHtml(product.status || '')}</span></td>
    <td><a class="btn btn-secondary" href="product-detail.html?id=${product.id}">View</a></td>
  </tr>`;
}

export function infoRow(label, value) {
  return `<div class="info-row"><div class="k">${escapeHtml(label)}</div><div class="v">${value == null || value === '' ? '—' : escapeHtml(String(value))}</div></div>`;
}
