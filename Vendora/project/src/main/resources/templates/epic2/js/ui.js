
const toastWrapId = 'toast-wrap';
export function ensureToastContainer() {
  let el = document.getElementById(toastWrapId);
  if (!el) {
    el = document.createElement('div');
    el.id = toastWrapId;
    el.className = 'toast-wrap';
    document.body.appendChild(el);
  }
  return el;
}
export function toast(message, type = 'success', timeout = 3200) {
  const wrap = ensureToastContainer();
  const item = document.createElement('div');
  item.className = `toast ${type}`;
  item.textContent = message;
  wrap.appendChild(item);
  setTimeout(() => item.remove(), timeout);
}
export function setLoading(el, html = 'Loading...') {
  if (el) el.innerHTML = `<div class="loading-state">${html}</div>`;
}
export function setError(el, message = 'Something went wrong') {
  if (el) el.innerHTML = `<div class="error-state">${message}</div>`;
}
export function setEmpty(el, message = 'No data found') {
  if (el) el.innerHTML = `<div class="empty-state">${message}</div>`;
}
export function openModal(id) { document.getElementById(id)?.classList.add('open'); }
export function closeModal(id) { document.getElementById(id)?.classList.remove('open'); }
export function bindModalClose(id) {
  const backdrop = document.getElementById(id);
  if (!backdrop) return;
  backdrop.onclick = (e) => {
    if (e.target === backdrop || e.target.matches('[data-close-modal]')) closeModal(id);
  };
}
