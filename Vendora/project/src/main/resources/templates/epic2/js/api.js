
import { APP_CONFIG } from './config.js';

async function request(path, options = {}) {
  const response = await fetch(`${APP_CONFIG.API_BASE}${path}`, {
    ...options,
    headers: {
      ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
      ...(options.headers || {})
    }
  });
  const text = await response.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = text; }
  if (!response.ok) {
    const message = data?.message || data || `Request failed (${response.status})`;
    throw new Error(message);
  }
  return data;
}

export const productApi = {
  getAll: (params = {}) => {
    const sp = new URLSearchParams();
    Object.entries(params).forEach(([k,v]) => {
      if (v !== undefined && v !== null && v !== '') sp.set(k, v);
    });
    return request(`/products${sp.toString() ? `?${sp.toString()}` : ''}`);
  },
  getById: (id) => request(`/products/${id}`),
  create: (formData) => request('/products', { method:'POST', body: formData }),
  update: (id, formData) => request(`/products/${id}`, { method:'PUT', body: formData }),
  delete: (id) => request(`/products/${id}`, { method:'DELETE' }),
  updateStock: (id, payload) => request(`/products/${id}/stock`, { method:'PATCH', body: JSON.stringify(payload) }),
  getLowStock: () => request('/products/low-stock'),
  getDashboardStats: () => request('/products/stats/dashboard')
};
