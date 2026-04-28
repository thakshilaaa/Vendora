import { populateProductSelects, wireImageUI, setFormFromProduct } from "./product-form-shared.js";

function getId() {
  return new URLSearchParams(window.location.search).get("id");
}

async function readErrorMessage(res) {
  try {
    const j = await res.json();
    return (j && j.message) || "Update failed";
  } catch {
    return "Update failed";
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  const id = getId();
  const form = document.getElementById("product-form");
  if (!id || !form) {
    if (form && !id) {
      alert("Missing product id. Open this page with ?id=…");
    }
    return;
  }

  populateProductSelects();
  const imageUi = wireImageUI();

  try {
    const res = await fetch(`/api/products/${id}`);
    if (!res.ok) {
      if (res.status === 404) {
        document.querySelector("main")?.insertAdjacentHTML(
          "afterbegin",
          "<p class=\"text-red\" style=\"padding:12px;\">Product not found.</p>"
        );
      }
      return;
    }
    const p = await res.json();
    setFormFromProduct(p);
  } catch {
    // leave form empty; user can still try back link
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const fd = new FormData(form);
    const imageInput = document.getElementById("image-input");
    if (imageInput && imageInput.files[0]) {
      fd.set("image", imageInput.files[0]);
    } else if (imageUi.wasRemoved()) {
      fd.set("removeImage", "true");
    }

    try {
      const res = await fetch(`/api/products/${id}`, { method: "PUT", body: fd });
      if (!res.ok) {
        const msg = await readErrorMessage(res);
        throw new Error(msg);
      }
      window.location.href = "/products";
    } catch (err) {
      alert(err.message || "Unable to update product.");
    }
  });
});
