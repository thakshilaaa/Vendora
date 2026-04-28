/** Shared helpers for create-product and edit-product (epic2). */

// Must match com.vendora.epic2.model.Product.Category and products.category in Vendora - Database.sql
export const PRODUCT_CATEGORIES = [
  "COSMETICS", "SKINCARE", "HAIRCARE", "BODYCARE", "FRAGRANCE", "BEAUTY_TOOLS"
];

export const PRODUCT_STATUSES = ["ACTIVE", "INACTIVE", "DISCONTINUED"];

/**
 * Fills #form-category and #form-status if they exist and are empty.
 */
export function populateProductSelects() {
  const cat = document.getElementById("form-category");
  const st = document.getElementById("form-status");
  if (cat && cat.options.length === 0) {
    PRODUCT_CATEGORIES.forEach((c) => {
      const o = document.createElement("option");
      o.value = c;
      o.textContent = c.replace(/_/g, " ");
      cat.appendChild(o);
    });
  }
  if (st && st.options.length === 0) {
    PRODUCT_STATUSES.forEach((s) => {
      const o = document.createElement("option");
      o.value = s;
      o.textContent = s;
      st.appendChild(o);
    });
  }
}

/**
 * Wires image file input, preview, remove button. Returns { getRemoveImageFlag, resetRemove }
 */
export function wireImageUI() {
  const input = document.getElementById("image-input");
  const preview = document.getElementById("image-preview");
  const trigger = document.getElementById("upload-trigger");
  const removeBtn = document.getElementById("remove-image-btn");
  let userRemovedImage = false;

  if (!input || !preview) {
    return {
      wasRemoved: () => userRemovedImage,
      reset: () => { userRemovedImage = false; }
    };
  }

  const showPreview = (file) => {
    if (!file || !file.type.startsWith("image/")) {
      preview.innerHTML = "<span class=\"muted\">No image</span>";
      return;
    }
    const url = URL.createObjectURL(file);
    preview.innerHTML = `<img src="${url}" alt="Preview" style="max-width:100%;max-height:200px;object-fit:contain;" />`;
  };

  if (trigger) {
    trigger.addEventListener("click", () => input.click());
  }
  input.addEventListener("change", () => {
    userRemovedImage = false;
    const f = input.files && input.files[0];
    showPreview(f);
    if (removeBtn) {
      removeBtn.classList.toggle("hidden", !f);
    }
  });
  if (removeBtn) {
    removeBtn.addEventListener("click", () => {
      userRemovedImage = true;
      input.value = "";
      preview.innerHTML = "<span class=\"muted\">No image</span>";
      removeBtn.classList.add("hidden");
    });
  }

  return {
    wasRemoved: () => userRemovedImage,
    reset: () => { userRemovedImage = false; }
  };
}

/**
 * @param {Record<string, unknown>} p - product from GET /api/products/:id
 */
export function setFormFromProduct(p) {
  const form = document.getElementById("product-form");
  if (!form || !p) return;
  const set = (name, v) => {
    if (v === undefined || v === null) return;
    const el = form.querySelector(`[name="${name}"]`);
    if (!el) return;
    if (el.type === "date" && typeof v === "string") {
      el.value = v.length >= 10 ? v.slice(0, 10) : "";
    } else {
      el.value = v;
    }
  };
  set("name", p.name);
  set("supplierId", p.supplierId);
  set("brand", p.brand);
  set("sku", p.sku);
  set("barcode", p.barcode);
  const cat = form.querySelector('[name="category"]');
  if (cat && p.category) {
    cat.value = String(p.category);
  }
  const st = form.querySelector('[name="status"]');
  if (st && p.status) {
    st.value = String(p.status);
  }
  set("description", p.description);
  set("tags", p.tags);
  if (p.price != null) set("price", p.price);
  if (p.costPrice != null) set("costPrice", p.costPrice);
  if (p.stockQuantity != null) set("stockQuantity", p.stockQuantity);
  if (p.lowStockThreshold != null) set("lowStockThreshold", p.lowStockThreshold);
  set("unit", p.unit);
  set("volume", p.volume);
  set("shade", p.shade);
  set("skinType", p.skinType);
  set("countryOfOrigin", p.countryOfOrigin);
  set("ingredients", p.ingredients);
  set("usageInstructions", p.usageInstructions);
  set("supplierName", p.supplierName);
  set("supplierContact", p.supplierContact);
  set("supplierEmail", p.supplierEmail);
  set("supplierAddress", p.supplierAddress);
  if (p.manufactureDate) {
    set("manufactureDate", p.manufactureDate);
  }
  if (p.expiryDate) {
    set("expiryDate", p.expiryDate);
  }

  const preview = document.getElementById("image-preview");
  const removeBtn = document.getElementById("remove-image-btn");
  if (preview && p.imageBase64) {
    const type = p.imageContentType || "image/jpeg";
    preview.innerHTML = `<img src="data:${type};base64,${p.imageBase64}" alt="Product" style="max-width:100%;max-height:200px;object-fit:contain;" />`;
    if (removeBtn) removeBtn.classList.remove("hidden");
  }
}
