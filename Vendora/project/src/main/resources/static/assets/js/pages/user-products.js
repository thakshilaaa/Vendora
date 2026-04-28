function esc(s) {
  if (s == null) return "";
  const d = document.createElement("div");
  d.textContent = String(s);
  return d.innerHTML;
}

document.addEventListener("DOMContentLoaded", async () => {
  const root = document.getElementById("products-grid") || document.getElementById("catalog-grid");
  if (!root) return;
  try {
    const res = await fetch("/api/products");
    const products = res.ok ? await res.json() : [];
    root.innerHTML = products
      .map(
        (p) =>
          `<div class="card pad"><h4>${esc(p.name)}</h4><p>Rs. ${p.price ?? 0}</p><a href="/product-view?id=${p.id}" class="btn btn-primary">View</a></div>`
      )
      .join("");
  } catch (_e) {
    root.innerHTML = "<p>Unable to load catalog.</p>";
  }
});
