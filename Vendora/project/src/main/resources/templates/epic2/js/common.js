
export async function initPage(activeKey, fn) {
  const { renderNavbar } = await import('./navbar.js');
  await renderNavbar(activeKey);
  if (typeof fn === 'function') await fn();
}
