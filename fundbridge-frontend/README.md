# FundBridge Frontend

A React frontend for the FundBridge Spring Boot backend (crowdfunding
campaigns + peer-to-peer lending, with Razorpay payments).

## What's included

- **Auth**: register / login, JWT stored in `localStorage`, attached to every
  request automatically.
- **Campaigns**: browse active campaigns (with search + pagination), view a
  campaign, donate (via Razorpay Checkout), create/edit/delete your own
  campaigns.
- **Loans**: request a loan, view your requests and any offers on them
  (accept/reject), browse pending requests as a lender and submit offers,
  view/pay a repayment schedule.
- **Profile**: view and update your profile.
- **Admin**: view all users (deactivate), review campaigns and change status.

## Requirements

- Node.js 18+ and npm
- The FundBridge Spring Boot backend running locally on `http://localhost:8080`
  with context path `/api` (this matches `server.servlet.context-path=/api`
  in the backend's `application.properties`).

## Setup

```bash
npm install
npm start
```

The app runs on `http://localhost:3000` by default, which matches the CORS
origin already allowed by the backend's `SecurityConfig`.

The API base URL is configured in `.env`:

```
REACT_APP_API_URL=http://localhost:8080/api
```

Change this if your backend runs elsewhere, then restart `npm start`.

## Payments

Donations and loan repayments use the Razorpay Checkout script
(`checkout.razorpay.com/v1/checkout.js`), loaded on demand. The backend's
`PaymentController` currently returns a **stub** order (see the comment in
that file) — to accept real payments, add the Razorpay Java SDK to the
backend as described there, and set real `RAZORPAY_KEY_ID` /
`RAZORPAY_KEY_SECRET` values.

## Building for production

```bash
npm run build
```

This outputs a static bundle in `build/` that you can serve with any static
file host (nginx, Netlify, S3, etc.) — just make sure `REACT_APP_API_URL`
points at your deployed backend before building.
