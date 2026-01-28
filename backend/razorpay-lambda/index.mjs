import Razorpay from 'razorpay';

export const handler = async (event) => {
    const razorpay = new Razorpay({
        key_id: process.env.KEY_ID,         // Set this in AWS
        key_secret: process.env.KEY_SECRET, // Set this in AWS
    });

    // 1. Get amount from app (default to 50000 paise = ₹500 if missing)
    let body = {};
    try { body = JSON.parse(event.body); } catch (e) {}
    const amount = body.amount || 50000;

    const options = {
        amount: amount,
        currency: "INR",
        receipt: "receipt_" + Date.now(),
    };

    try {
        // 2. Ask Razorpay for an Order ID
        const order = await razorpay.orders.create(options);

        return {
            statusCode: 200,
            body: JSON.stringify(order),
        };
    } catch (error) {
        return { statusCode: 500, body: JSON.stringify({ error: error.message }) };
    }
};