function esc(s) {
  if (s == null) return "";
  const d = document.createElement("div");
  d.textContent = String(s);
  return d.innerHTML;
}

document.addEventListener("DOMContentLoaded", async () => {
  const root = document.getElementById("low-stock-root") || document.getElementById("products-grid");
  if (!root) return;
  try {
    const res = await fetch("/api/products/low-stock");
    const items = res.ok ? await res.json() : [];
    root.innerHTML = items.length
      ? items
          .map(
            (p) =>
              `<div class="card pad"><h4>${esc(p.name)}</h4><p>Stock: ${p.stockQuantity}</p><p><a href="/product-detail?id=${p.id}">View</a> · <a href="/edit-product?id=${p.id}">Edit</a></p></div>`
          )
          .join("")
      : "<p>No low-stock products found.</p>";
  } catch (_e) {
    root.innerHTML = "<p>Unable to load low-stock items.</p>";
  }
});
