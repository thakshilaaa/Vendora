import { populateProductSelects, wireImageUI } from "./product-form-shared.js";

async function readErrorMessage(res, fallback) {
  try {
    const j = await res.json();
    return (j && j.message) || fallback;
  } catch {
    return fallback;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  populateProductSelects();
  wireImageUI();

  const form = document.getElementById("product-form");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const fd = new FormData(form);
    const imageInput = document.getElementById("image-input");
    if (imageInput && imageInput.files[0]) {
      fd.set("image", imageInput.files[0]);
    }

    try {
      const res = await fetch("/api/products", { method: "POST", body: fd });
      if (!res.ok) {
        const msg = await readErrorMessage(res, "Create failed");
        throw new Error(msg);
      }
      window.location.href = "/products";
    } catch (err) {
      alert(err.message || "Unable to create product.");
    }
  });
});
