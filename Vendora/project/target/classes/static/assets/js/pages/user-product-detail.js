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
    const img = p.imageBase64 && p.imageContentType
      ? `<p><img src="data:${p.imageContentType};base64,${p.imageBase64}" alt="" style="max-width:100%;max-height:240px;object-fit:contain;" /></p>`
      : "";
    root.innerHTML = `
      <section class="card pad">
        ${img}
        <h2>${esc(p.name || "Product")}</h2>
        <p>Price: Rs. ${p.price ?? 0}</p>
        <p>${esc(p.description)}</p>
        <p class="flex-gap-sm" style="display:flex;flex-wrap:wrap;gap:8px;align-items:center;">
          <button type="button" class="btn btn-secondary" id="add-to-cart-btn">Add to cart</button>
          <a href="/order-payment" class="btn btn-primary">Buy now</a>
          <a href="/cart" class="btn btn-outline">View cart</a>
        </p>
        <p class="muted" style="font-size:0.9rem">Sign in is required. After login, your session cookie allows checkout.</p>
      </section>`;
    const atc = document.getElementById("add-to-cart-btn");
    if (atc) {
      atc.addEventListener("click", () => {
        const f = document.createElement("form");
        f.method = "POST";
        f.action = "/cart/add";
        const pid = document.createElement("input");
        pid.type = "hidden";
        pid.name = "productId";
        pid.value = id;
        const q = document.createElement("input");
        q.type = "hidden";
        q.name = "quantity";
        q.value = "1";
        f.appendChild(pid);
        f.appendChild(q);
        document.body.appendChild(f);
        f.submit();
      });
    }
  } catch (_e) {
    root.innerHTML = "<p>Unable to load product.</p>";
  }
});
