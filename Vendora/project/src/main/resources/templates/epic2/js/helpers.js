
export const CATEGORIES = ['COSMETICS','SKINCARE','HAIRCARE','BODYCARE','FRAGRANCE','BEAUTY_TOOLS'];
export const CATEGORY_LABELS = {
  COSMETICS:'💄 Cosmetics', SKINCARE:'🧴 Skincare', HAIRCARE:'💆 Haircare', BODYCARE:'🛁 Body Care',
  FRAGRANCE:'🌸 Fragrance', BEAUTY_TOOLS:'🛠️ Beauty tools'
};
export const STATUS_LABELS = { ACTIVE:'Active', INACTIVE:'Inactive', DISCONTINUED:'Discontinued' };

export function qs(name) { return new URL(location.href).searchParams.get(name); }
export function buildImageSrc(product) {
  if (!product?.imageBase64) return '';
  return `data:${product.imageContentType || 'image/jpeg'};base64,${product.imageBase64}`;
}
export function formatCurrency(val) {
  if (val == null || val === '') return '—';
  return 'Rs. ' + Number(val).toLocaleString('en-LK');
}
export function formatDate(dateStr) {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return isNaN(d) ? dateStr : d.toLocaleDateString('en-LK', { year:'numeric', month:'short', day:'numeric' });
}
export function getStockLabel(product) {
  if (product.stockQuantity === 0) return 'Out of Stock';
  if (product.lowStock) return 'Low Stock';
  return 'In Stock';
}
export function getStockClass(product) {
  if (product.stockQuantity === 0) return 'badge badge-out-stock';
  if (product.lowStock) return 'badge badge-low-stock';
  return 'badge badge-ok';
}
export function getStatusClass(status) {
  return status === 'ACTIVE' ? 'badge badge-active' : status === 'INACTIVE' ? 'badge badge-inactive' : 'badge badge-discontinued';
}
export function escapeHtml(str = '') {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}
export function toFormData(data, imageFile, removeImage = false) {
  const fd = new FormData();
  Object.entries(data).forEach(([k,v]) => {
    if (v !== null && v !== undefined && v !== '') fd.append(k, v);
  });
  if (imageFile) fd.append('image', imageFile);
  if (removeImage) fd.append('removeImage', 'true');
  return fd;
}
export function serializeForm(form) {
  const fd = new FormData(form);
  const out = {};
  for (const [k,v] of fd.entries()) out[k] = v;
  ['price','costPrice','stockQuantity','lowStockThreshold'].forEach(k => {
    if (out[k] !== undefined && out[k] !== '') out[k] = Number(out[k]);
  });
  return out;
}
export function renderOptions(items, selected = '') {
  return items.map(([value,label]) => `<option value="${value}" ${selected === value ? 'selected' : ''}>${label}</option>`).join('');
}
