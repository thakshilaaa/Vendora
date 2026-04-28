async function loadDashboard() {
  const statsGrid = document.getElementById("stats-grid");
  const lowStockEl = document.getElementById("dashboard-low-stock");
  if (!statsGrid || !lowStockEl) return;

  try {
    const [statsRes, lowStockRes] = await Promise.all([
      fetch("/api/products/stats/dashboard"),
      fetch("/api/products/low-stock")
    ]);
    const stats = statsRes.ok ? await statsRes.json() : {};
    const lowStock = lowStockRes.ok ? await lowStockRes.json() : [];

    statsGrid.innerHTML = `
      <div class="card pad"><h3>Total Products</h3><p>${stats.totalProducts ?? 0}</p></div>
      <div class="card pad"><h3>Low Stock</h3><p>${stats.lowStockCount ?? stats.lowStockProducts ?? 0}</p></div>
      <div class="card pad"><h3>Out of Stock</h3><p>${stats.outOfStockCount ?? stats.outOfStockProducts ?? 0}</p></div>
    `;
    lowStockEl.innerHTML = lowStock.length
      ? lowStock.map((p) => `<p>${p.name} (${p.stockQuantity})</p>`).join("")
      : "<p>No low stock products.</p>";
  } catch (_e) {
    statsGrid.innerHTML = "<p>Unable to load dashboard data.</p>";
    lowStockEl.innerHTML = "<p>Unable to load stock alerts.</p>";
  }
}

document.addEventListener("DOMContentLoaded", loadDashboard);
