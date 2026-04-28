function getId() {
  return new URLSearchParams(window.location.search).get("id");
}

function esc(s) {
  if (s == null) return "";
  const d = document.createElement("div");
  d.textContent = String(s);
  return d.innerHTML;
}

document.addEventListener("DOMContentLoaded", async () => {
  const root = document.getElementById("detail-root");
  const id = getId();
  if (!root || !id) return;
  try {
    const res = await fetch(`/api/products/${id}`);
    if (!res.ok) throw new Error("Not found");
    const p = await res.json();
    root.innerHTML = `
      <section class="card pad">
        <h2>${esc(p.name || "Product")}</h2>
        <p>Category: ${esc(p.category) || "-"}</p>
        <p>Price: Rs. ${p.price ?? 0}</p>
        <p>Stock: ${p.stockQuantity ?? 0}</p>
        <p>${esc(p.description)}</p>
        <p><a class="btn btn-secondary" href="/edit-product?id=${id}">Edit product</a></p>
      </section>`;
  } catch (_e) {
    root.innerHTML = "<p>Unable to load product details.</p>";
  }
});
