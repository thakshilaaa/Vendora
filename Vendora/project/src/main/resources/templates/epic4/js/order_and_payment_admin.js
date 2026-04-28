const BASE = '/api/orders';
const ADMIN = '/api/admin';
const fetchOpts = { credentials: 'include' };

document.addEventListener('DOMContentLoaded', () => {
    loadOrders();
    loadPayments();
    updateDashboardStats();
});

async function updateDashboardStats() {
    try {
        const response = await fetch(`${ADMIN}/stats`, fetchOpts);
        if (!response.ok) throw new Error('Failed to fetch stats');
        const data = await response.json();
        const revenueEl = document.getElementById('total-revenue');
        const ordersEl = document.getElementById('total-orders');
        const pendingEl = document.getElementById('pending-orders');
        const avgEl = document.getElementById('avg-value');
        if (revenueEl) revenueEl.innerText = `Rs. ${data.totalRevenue}`;
        if (ordersEl) ordersEl.innerText = data.totalOrders;
        if (pendingEl) pendingEl.innerText = data.pendingOrders;
        if (avgEl) avgEl.innerText = `Rs. ${data.avgOrderValue}`;
    } catch (error) {
        console.error("Dashboard Stats Error:", error);
    }
}

async function loadOrders() {
    try {
        const response = await fetch(BASE, fetchOpts);
        if (!response.ok) throw new Error("Orders fetch failed");
        const orders = await response.json();
        const tableBody = document.getElementById('orderTableBody');
        if (!tableBody) return;
        tableBody.innerHTML = "";
        orders.forEach(order => {
            const status = order.status || 'PENDING';
            const name = `${order.firstName || ''} ${order.lastName || ''}`;
            tableBody.innerHTML += `
                <tr>
                    <td>#${order.id}</td>
                    <td>${name}</td>
                    <td>${order.product}</td>
                    <td><span class="status-badge ${(status || '').toLowerCase()}">${status}</span></td>
                    <td>
                        <button class="btn-status" style="background:#be185d; color:white; border:none; padding:5px 10px; border-radius:4px; cursor:pointer;" onclick="updateStatus(${order.id})">Update Status</button>
                        <button class="btn-del" style="background:#ef4444; color:white; border:none; padding:5px 10px; border-radius:4px; cursor:pointer;" onclick="deleteOrder(${order.id})">Delete Order</button>
                    </td>
                </tr>
            `;
        });
    } catch (error) { console.error("Error loading orders:", error); }
}

async function loadPayments() {
    try {
        const response = await fetch(BASE, fetchOpts);
        const orders = await response.json();
        const payBody = document.getElementById('paymentTableBody');
        if (!payBody) return;
        payBody.innerHTML = "";
        orders.forEach(order => {
            const pStatus = order.paymentStatus || 'UNPAID';
            const needsVerify = pStatus === 'PENDING' || pStatus === 'UNPAID';
            payBody.innerHTML += `
                <tr>
                    <td>#${order.id}</td>
                    <td>Rs. ${order.amount ? order.amount.toFixed(2) : '0.00'}</td>
                    <td><strong>${order.paymentMethod || 'N/A'}</strong></td>
                    <td><span class="status-badge ${(pStatus || '').toLowerCase()}">${pStatus}</span></td>
                    <td>
                        ${needsVerify
                ? `<button class="btn-pay" style="background:#059669; color:white; border:none; padding:5px 10px; border-radius:4px; cursor:pointer;" onclick="confirmPayment(${order.id})">Confirm Receipt</button>`
                : '<span style="color: #059669; font-weight: bold;">✅ Verified</span>'}
                    </td>
                </tr>
            `;
        });
    } catch (error) { console.error("Error loading payments:", error); }
}

async function updateStatus(id) {
    const s = prompt("New Status (Shipped / Delivered / PENDING):");
    if (s) {
        const res = await fetch(`${BASE}/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(s)
        });
        if (res.ok) {
            alert("Status Updated!");
            loadOrders();
            updateDashboardStats();
        }
    }
}

async function confirmPayment(id) {
    if (confirm("Verify payment for Order #" + id + "?")) {
        try {
            const res = await fetch(`${ADMIN}/${id}/pay-confirm`, { method: 'PUT', ...fetchOpts });
            if (res.ok) {
                alert("Payment Verified!");
                if (typeof refreshData === "function") refreshData();
            } else {
                alert("Error: Could not verify payment.");
            }
        } catch (error) {
            console.error("Error confirming payment:", error);
        }
    }
}

async function deleteOrder(id) {
    if (confirm("Delete this order?")) {
        const res = await fetch(`${BASE}/${id}`, { method: 'DELETE', ...fetchOpts });
        if (res.ok) {
            loadOrders();
            loadPayments();
            updateDashboardStats();
        }
    }
}

function downloadReceipt(orderId) {
    window.location.href = `${ADMIN}/download-receipt/${orderId}`;
}

window.confirmPayment = confirmPayment;
window.updateStatus = updateStatus;
window.deleteOrder = deleteOrder;
window.downloadReceipt = downloadReceipt;
