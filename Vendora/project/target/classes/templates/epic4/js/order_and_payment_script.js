// --- 1. Global Variables ---
let shippingCost = 5.99;
let shippingMethod = "Standard";
const subtotal = 45.00;

// --- 2. Shipping Selection ---
function selectShipping(cost, method, el) {
    shippingCost = cost;
    shippingMethod = method;
    document.getElementById('display-shipping').innerText = `$${cost.toFixed(2)}`;
    const total = subtotal + cost;
    document.getElementById('display-total').innerText = `$${total.toFixed(2)}`;
    document.querySelectorAll('.shipping-option').forEach(opt => opt.classList.remove('active'));
    el.classList.add('active');
}

// --- 3. Step Navigation ---
function goToStep(step) {
    if (step === 2) {
        const fields = ['firstName', 'email', 'address'];
        for (let f of fields) {
            let element = document.getElementById(f);
            if (!element || !element.value.trim()) {
                alert("Please fill all required fields marked with (*).");
                return;
            }
        }
    }
    document.getElementById('step-1').style.display = step === 1 ? 'block' : 'none';
    document.getElementById('step-2').style.display = step === 2 ? 'block' : 'none';
}

// --- 4. Payment View Toggle ---
function togglePayView(type) {
    const cardFields = document.getElementById('card-fields');
    if (cardFields) {
        cardFields.style.display = type === 'Online' ? 'block' : 'none';
    }
}

// --- 5. Final Order Placement ---
async function handlePlaceOrder() {
    const payType = document.querySelector('input[name="payType"]:checked').value;
    const totalAmount = parseFloat(document.getElementById('display-total').innerText.replace('$', ''));

    // Card Validation for Online Payments
    if (payType === 'Online') {
        const cardNum = document.getElementById('cardNum').value.trim();
        if (cardNum.length !== 16 || isNaN(cardNum)) {
            alert("Invalid card number. Please enter a valid 16-digit card number.");
            return;
        }
    }

    const orderData = {
        firstName: document.getElementById('firstName').value.trim(),
        lastName: document.getElementById('lastName').value.trim(),
        email: document.getElementById('email').value.trim(),
        phone: document.getElementById('phone') ? document.getElementById('phone').value.trim() : "",
        address: document.getElementById('address').value.trim(),
        city: document.getElementById('city') ? document.getElementById('city').value.trim() : "",
        zipCode: document.getElementById('zipCode') ? document.getElementById('zipCode').value.trim() : "",
        amount: totalAmount,
        paymentMethod: payType,
        product: "Sunset Eyes Palette",
        status: "PENDING"
    };

    try {
        const res = await fetch('/api/orders/add', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            credentials: 'include',
            body: JSON.stringify(orderData)
        });

        if (res.ok) {
            const savedOrder = await res.json();

            // Payment process simulation (If you have a payment API)
            const paymentData = {
                orderId: savedOrder.id,
                amount: totalAmount,
                paymentMethod: payType,
                status: (payType === 'Online' ? 'SUCCESS' : 'PENDING')
            };

            await fetch('/api/payments/process', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                credentials: 'include',
                body: JSON.stringify(paymentData)
            });

            showSuccessPage(savedOrder);
        } else {
            alert("Order failed. Please try again.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("Network error. Check that the app is running and the URL is correct.");
    }
}

// --- 6. Success Page with Cancel Button (US_4.6 Implementation) ---
function showSuccessPage(order) {
    document.body.innerHTML = `
        <div id="invoice" style="max-width: 600px; margin: 50px auto; padding: 30px; background: white; border-radius: 15px; box-shadow: 0 0 20px rgba(0,0,0,0.1); font-family: 'Segoe UI', sans-serif;">
            <div style="text-align: center; color: #be185d;">
                <h1 style="margin-bottom: 5px;">🎉 Order Successful!</h1>
                <p style="color: #666;">Thank you for your purchase, ${order.firstName}!</p>
                <p><strong>Order ID: #${order.id}</strong></p>
            </div>
            <hr style="border: 0.5px solid #eee; margin: 20px 0;">
            <div style="line-height: 1.6;">
                <p><strong>Product:</strong> ${order.product}</p>
                <p><strong>Shipping Address:</strong> ${order.address}</p>
                <p><strong>Payment Method:</strong> ${order.paymentMethod}</p>
                <p><strong>Status:</strong> <span id="statusText" style="color: #be185d; font-weight: bold;">${order.status}</span></p>
                <h2 style="color: #be185d; margin-top: 15px;">Total Paid: $${order.amount.toFixed(2)}</h2>
            </div>
            
            <div id="action-buttons" style="margin-top: 30px; display: flex; flex-direction: column; gap: 10px;">
                <button onclick="downloadPDF()" style="background: #be185d; color: white; border: none; padding: 12px; border-radius: 8px; cursor: pointer; font-weight: bold;">Download Receipt (PDF)</button>
                
                <button id="cancelBtn" onclick="cancelOrder(${order.id})" style="background: #ef4444; color: white; border: none; padding: 12px; border-radius: 8px; cursor: pointer; font-weight: bold;">Cancel My Order</button>
                
                <button onclick="window.location.reload()" style="background: #f3f4f6; color: #374151; border: none; padding: 12px; border-radius: 8px; cursor: pointer; font-weight: bold;">Place New Order</button>
            </div>
        </div>
    `;
}

// --- 7. Customer Cancel Function (US_4.6) ---
async function cancelOrder(orderId) {
    if (confirm("Are you sure you want to cancel this order? This cannot be undone.")) {
        try {

            const res = await fetch(`/api/orders/${orderId}/cancel`, {
                method: 'PUT',
                credentials: 'include'
            });

            if (res.ok) {
                alert("Your order has been cancelled successfully!");
                document.getElementById('statusText').innerText = "Cancelled";
                document.getElementById('statusText').style.color = "red";
                document.getElementById('cancelBtn').style.display = "none";
            } else {
                alert("Could not cancel order. It might already be shipped.");
            }
        } catch (error) {
            console.error("Error:", error);
            alert("Error connecting to server!");
        }
    }
}

// --- 8. PDF Download ---
function downloadPDF() {
    const element = document.getElementById('invoice');
    const buttons = document.getElementById('action-buttons');
    buttons.style.display = 'none';

    const opt = {
        margin: 0.5,
        filename: 'BeautyStore_Receipt.pdf',
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2 },
        jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
    };

    html2pdf().set(opt).from(element).save().then(() => {
        buttons.style.display = 'flex';
    });
}
