# Fast Delivery — Multi-Vendor E-commerce Marketplace

**Fast Delivery** (দ্রুত ডেলিভারি) is a production-oriented, mobile-first multi-vendor e-commerce platform and Android marketplace crafted primarily for users in Bangladesh.

---

## 1. System Architecture & Features Summary

| Component | Technology | Implementation Details |
| :--- | :--- | :--- |
| **Mobile & App Framework** | Jetpack Compose (M3) & Capacitor Ready | Kotlin, MVVM, reactive StateFlows, strict edge-to-edge system bar insets, adaptive launcher icon. |
| **Language & Localization** | Bengali-First & English (`BN` / `EN`) | Instant bilingual switch (🇧🇩 বাংলা / 🌐 English), Bengali numerals (`১,২৫০`), and formatted BDT currency (`৳`). |
| **Catalog & Marketplace** | Multi-Vendor Hub | Groceries & Daily Bazar, Electronics, Fashion, Sweets & Snacks, Health & Wellness, Home & Living. |
| **Multi-Vendor Isolation** | Vendor Management | Seller registration, shop profiles, product submission queue (`isApproved = false`), seller order fulfillment. |
| **Private Admin Panel** | Protected `/admin` Console | Super Admin gate, pending product approval queue, order status lifecycle, vendor verification, administrative audit trail. |
| **AI Order Assistant** | Google Gemini (`gemini-3.5-flash`) | FastBot: Grounded on real product stock, server-side delivery fee calculation, 1-tap Cash on Delivery order card. |
| **Payments & Logistics** | 100% Cash on Delivery (COD) | Inside Dhaka: ৳60 (24–48 hrs) • Outside Dhaka: ৳120 (3–5 days via Steadfast / Pathao). |
| **Messaging APIs** | Meta Messenger & WhatsApp Cloud API | Official webhook simulator, verify token, test ping, 24-hour service window compliance, HSM templates. |
| **Android & Play Store** | Gradle, Capacitor & AAB | Release keystore instructions, `bundleRelease` commands, Target SDK 36, and Google Play Console checklist. |

---

## 2. Customer Shopping Interface

* **Bengali-First Navigation**: Seamless 1-tap language toggle in the top bar and profile settings.
* **Bangladeshi Currency & Numbers**: Prices formatted as `৳ ১,২৫০` (Bengali) or `৳ 1,250` (English).
* **Delivery Information Highlights**: Transparent shipping costs (Inside Dhaka ৳60, Outside Dhaka ৳120) and 100% Cash on Delivery protection.
* **Real-time Search & Filter**: Search by product name, category, or vendor name with instant results.
* **Shopping Cart & Checkout**:
  * Cart quantity increment/decrement, coupon applicator (`FAST50` for ৳50 discount).
  * Bangladesh address selector: 8 Divisions (Dhaka, Chittagong, Rajshahi, Khulna, Barishal, Sylhet, Rangpur, Mymensingh), District, Thana/Upazila, Street Address, and Phone.
  * Payment method: Cash on Delivery with parcel inspection guarantee.
* **Live Order Tracking**:
  * Real-time order progress timeline: `Pending` -> `Confirmed` -> `Processing` -> `Shipped` -> `Delivered`.
  * Courier details (Steadfast Courier / Pathao) with tracking IDs.
  * 7-day hassle-free return request and order cancellation support.

---

## 3. Multi-Vendor Marketplace Workflow

1. **Vendor Registration**: Sellers register shop name, owner contact (+880...), pickup address, and trade license or NID.
2. **Product Submission**: Sellers add new products. Products are created with `isApproved = false` (Pending Admin Review).
3. **Super Admin Review**: Only when a Super Admin verifies and clicks "Approve" does the item become visible on the public marketplace.
4. **Data Isolation**: Sellers can only view and manage their own inventory, sales metrics, and assigned customer orders.

---

## 4. Private Admin Panel (`/admin`)

* Protected access gate preventing unauthorized public access.
* **Product Approval Queue**: Review pending vendor products with 1-tap Approve/Reject buttons.
* **Order Management**: Transition orders through official delivery lifecycle states.
* **Seller Management**: Toggle vendor verification badges and monitor store GMV.
* **Administrative Audit Logs**: Immutable chronological record of system events, timestamps, and admin email.
* **Messaging Integrations**:
  * Meta Facebook Messenger: Page ID, Webhook callback URL, Server Verify Token, test ping.
  * WhatsApp Business Cloud API: WABA ID, Phone Number ID, Webhook verification, test ping.
* **Gemini AI Settings**: Temperature adjustments, high-value order human telephone review threshold (৳10,000 GMV).

---

## 5. AI Order Assistant (FastBot)

* Built with Google's modern `gemini-3.5-flash` model.
* Strictly grounded on Fast Delivery's verified catalog, real prices, and shipping rules (never hallucinates inventory or pricing).
* Asks clarifying questions if customer information (district, phone) is missing.
* Directly generates a server-calculated **1-Tap Order Confirmation Card** inside the chat so customers can place Cash on Delivery orders without leaving the conversation.

---

## 6. Android & Google Play Packaging Guide

### A. Capacitor Frontend Wrapping
```bash
npm install @capacitor/core @capacitor/cli @capacitor/android
npx cap init "Fast Delivery" "com.aistudio.fastdelivery.bdmkpt"
npm run build
npx cap add android
npx cap sync
```

### B. Production Release Keystore Generation
```bash
keytool -genkey -v -keystore fastdelivery-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias fastdelivery -storetype JKS
```

### C. Signed Android App Bundle (.aab) Build
```bash
gradle :app:bundleRelease
```
Output path: `app/build/outputs/bundle/release/app-release.aab`
