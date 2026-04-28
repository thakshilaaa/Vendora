/* ==========================================================
   Vendora cart page interactions:
   - +/- AJAX quantity update
   - Select All toggle, search filter
   - Buy now / Buy selected modal
   ========================================================== */

(function () {
    const fmt = n => n.toLocaleString('en-LK', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

    // ---------- helpers ----------
    function selectedItems() {
        return Array.from(document.querySelectorAll('.item-check:checked'))
            .map(cb => {
                const card = cb.closest('.cart-card');
                return {
                    id: card.dataset.id,
                    name: card.querySelector('h4').textContent.trim(),
                    qty: parseInt(card.querySelector('.qty-input').value, 10),
                    price: parseFloat(card.dataset.price),
                };
            });
    }

    function recalcSummary() {
        let subtotal = 0;
        document.querySelectorAll('.cart-card').forEach(card => {
            const qty = parseInt(card.querySelector('.qty-input').value, 10) || 0;
            const price = parseFloat(card.dataset.price) || 0;
            subtotal += qty * price;
        });
        document.getElementById('subtotal').textContent = fmt(subtotal);
        const delivery = parseFloat(document.getElementById('deliveryFee').textContent.replace(/,/g, '')) || 500;
        document.getElementById('total').textContent = fmt(subtotal + delivery);
    }

    // ---------- AJAX +/- buttons ----------
    function bindQty() {
        document.querySelectorAll('.cart-card').forEach(card => {
            const id = card.dataset.id;
            const input = card.querySelector('.qty-input');
            const inc = card.querySelector('.qty-btn.inc');
            const dec = card.querySelector('.qty-btn.dec');

            const send = newQty => {
                const max = parseInt(input.max, 10) || newQty;
                newQty = Math.max(1, Math.min(newQty, max));
                input.value = newQty;
                fetch('/cart/update', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'itemId=' + id + '&quantity=' + newQty,
                })
                    .then(r => r.json())
                    .then(() => recalcSummary())
                    .catch(() => {});
            };

            inc.addEventListener('click', () => send(parseInt(input.value, 10) + 1));
            dec.addEventListener('click', () => send(parseInt(input.value, 10) - 1));
            input.addEventListener('change', () => send(parseInt(input.value, 10) || 1));
        });
    }

    // ---------- Select All ----------
    function bindSelectAll() {
        const all = document.getElementById('selectAll');
        if (!all) return;
        all.addEventListener('change', () => {
            document.querySelectorAll('.item-check:not(:disabled)')
                .forEach(cb => { cb.checked = all.checked; });
        });
    }

    // ---------- Search filter ----------
    function bindSearch() {
        const search = document.getElementById('cartSearch');
        if (!search) return;
        search.addEventListener('input', () => {
            const q = search.value.toLowerCase();
            document.querySelectorAll('.cart-card').forEach(card => {
                const name = card.querySelector('h4').textContent.toLowerCase();
                card.style.display = name.includes(q) ? '' : 'none';
            });
        });
    }

    // ---------- Modal ----------
    const modal       = document.getElementById('confirmModal');
    const modalLines  = document.getElementById('modalLines');
    const modalSub    = document.getElementById('modalSubtotal');
    const modalTotal  = document.getElementById('modalTotal');
    const modalCancel = document.getElementById('modalCancel');
    const modalGo     = document.getElementById('modalConfirm');
    const DELIVERY_DEFAULT = 500;

    function openModal(items) {
        if (!items.length) {
            alert('Please select at least one item.');
            return;
        }
        const sub = items.reduce((a, i) => a + i.qty * i.price, 0);
        modalLines.innerHTML = items.map(i =>
            '<div class="row"><span>' + i.name + ' × ' + i.qty + '</span>' +
            '<strong>Rs. ' + fmt(i.qty * i.price) + '</strong></div>'
        ).join('');
        modalSub.textContent   = fmt(sub);
        modalTotal.textContent = fmt(sub + DELIVERY_DEFAULT);
        modalGo.href = '/checkout?' + items.map(i => 'itemIds=' + i.id).join('&');
        modal.hidden = false;
    }

    function bindBuyNow() {
        document.querySelectorAll('.buy-now-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const card = btn.closest('.cart-card');
                openModal([{
                    id: card.dataset.id,
                    name: card.querySelector('h4').textContent.trim(),
                    qty: parseInt(card.querySelector('.qty-input').value, 10),
                    price: parseFloat(card.dataset.price),
                }]);
            });
        });

        const buySelected = document.getElementById('buySelectedBtn');
        if (buySelected) buySelected.addEventListener('click', () => openModal(selectedItems()));

        modalCancel.addEventListener('click', () => { modal.hidden = true; });
        modal.addEventListener('click', e => { if (e.target === modal) modal.hidden = true; });
    }

    document.addEventListener('DOMContentLoaded', () => {
        bindQty();
        bindSelectAll();
        bindSearch();
        bindBuyNow();
        recalcSummary();
    });
})();
