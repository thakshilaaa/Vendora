function esc(s) {
  if (s == null) return "";
  const d = document.createElement("div");
  d.textContent = String(s);
  return d.innerHTML;
}

async function loadProducts() {
  const grid = document.getElementById("products-grid");
  const tableBody = document.getElementById("products-table-body");
  const summary = document.getElementById("products-summary");
  if (!grid || !tableBody || !summary) return;

  try {
    const res = await fetch("/api/products");
    const products = res.ok ? await res.json() : [];
    summary.textContent = `${products.length} products found`;
    grid.innerHTML = products.map((p) => `<div class="card pad"><h4>${esc(p.name)}</h4><p>Rs. ${p.price ?? 0}</p><a href="/product-detail?id=${p.id}">View</a></div>`).join("");
    tableBody.innerHTML = products.map((p) => `<tr><td>${esc(p.name)}</td><td>${esc(p.category)}</td><td>Rs. ${p.price ?? 0}</td><td>${p.stockQuantity ?? 0}</td><td>${p.lowStock ? "Low" : "OK"}</td><td>${esc(p.status)}</td><td><a href="/product-detail?id=${p.id}">View</a> <a href="/edit-product?id=${p.id}">Edit</a></td></tr>`).join("");
  } catch (_e) {
    summary.textContent = "Unable to load products.";
  }
}

document.addEventListener("DOMContentLoaded", loadProducts);
