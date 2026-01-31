# 🌱 KrishiSetu - Bridging Farmers & Buyers

**KrishiSetu** is a comprehensive digital marketplace designed to empower farmers by connecting them directly with buyers, eliminating intermediaries, and ensuring fair pricing. Built with a modern **Android (Kotlin + Jetpack Compose)** frontend and a robust **AWS Amplify** backend, it integrates **AI assistance (Gemini)** and **secure payments (Razorpay)** to create a seamless agricultural ecosystem.

---

## 🚀 Features

### 🌾 For Farmers
* **Direct Marketplace:** List crops with details, quantity, and expected price.
* **Order Management:** Accept or reject purchase requests from buyers.
* **Real-time Negotiation:** Chat directly with buyers to finalize deals.
* **AI Assistant:** Consult "Krishi Rakshak" (powered by Google Gemini) for farming advice and market trends.

### 🛒 For Buyers
* **Crop Discovery:** Browse and search for fresh produce directly from the source.
* **Secure Transactions:** Integrated **Razorpay** payment gateway for safe and fast payments.
* **Order Tracking:** Monitor the status of orders from "Accepted" to "Delivered".

### 🚚 Delivery & Logistics
* **Dedicated Role:** Specialized dashboard for delivery agents.
* **Status Updates:** Update delivery milestones (Shipped, Out for Delivery, Delivered) in real-time.

---

## 🛠 Tech Stack

### 📱 Android Frontend
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Dependency Injection:** Dagger Hilt
* **Asynchronous Processing:** Coroutines & Flow

### ☁️ Backend & Cloud (AWS Amplify)
* **Authentication:** Amazon Cognito (User Pools with Custom Groups: Farmer, Buyer, Delivery).
* **API:** AWS AppSync (GraphQL) for real-time data synchronization.
* **Database:** Amazon DynamoDB (NoSQL).
* **Serverless Functions:** AWS Lambda (Node.js) for payment processing and triggers.
* **Storage:** Amazon S3 (for profile pictures and crop images).

### 🤖 AI & Integrations
* **Artificial Intelligence:** Google Gemini 2.0 Flash (via Google AI Client SDK).
* **Payments:** Razorpay (Integrated via AWS Lambda & API Gateway).

---

## 📂 Project Structure
```bash
KrishiSetu/

├── app/
│   ├── src/main/java/com/cyberlabs/krishisetu/
│   │   ├── ai/                # Gemini AI Chat implementation
│   │   ├── authentication/    # Cognito Auth Repos & ViewModels
│   │   ├── crops/             # Crop listing and management
│   │   ├── shopping/          # Cart, Orders, and Checkout logic
│   │   └── ui/                # Jetpack Compose Screens
│   └── build.gradle.kts       # App-level dependencies
├── amplify/                   # AWS Amplify Backend configuration
│   ├── backend/
│   │   ├── api/               # GraphQL Schema (AppSync)
│   │   ├── auth/              # Cognito setup
│   │   └── function/          # Lambda functions (PostConfirmation, etc.)
└── backend/
    └── razorpay-lambda/       # Node.js logic for Razorpay Order creation
```

---

## ⚙️ Setup & Installation

### Prerequisites
* Android Studio (Ladybug or newer recommended)
* Java JDK 17+
* Node.js & NPM (for Amplify CLI)
* AWS Account (Free Tier sufficient for testing)
* Razorpay Test Account
* Google AI Studio API Key

### 1. Clone the Repository
```bash
git clone [https://github.com/your-username/KrishiSetu.git](https://github.com/your-username/KrishiSetu.git)
cd KrishiSetu
```
### 2. Configure Environment Keys
Create a local.properties file in the root directory if it doesn't exist, and add your Google Gemini API key:
```bash
sdk.dir=/path/to/your/android/sdk
GEMINI_API_KEY="your_google_gemini_api_key_here"
```
### 3. Setup AWS Backend
  1. Install Amplify CLI:
```bash
npm install -g @aws-amplify/cli
```
  2. Initialize the project (if setting up fresh) or pull existing config:
```bash
amplify init
# OR if you have the amplify push ready
amplify pull
```
  3. Deploy the backend
```bash
amplify push
```
---
### 4. Setup Payment Lambda (Razorpay)
* Navigate to the AWS Lambda console.
* Locate the function responsible for checkout (e.g., `RazorpayAPI`).
* Add the following **Environment Variables**:
    * `KEY_ID`: Your Razorpay Key ID
    * `KEY_SECRET`: Your Razorpay Key Secret

### 5. Run the App
* Open the project in Android Studio.
* Sync Gradle files.
* Select an emulator or physical device.
* Click **Run**.

---

## 🔒 Security & Roles
The app uses a robust role-based security model defined in `schema.graphql` and enforced by a Post-Confirmation Lambda trigger:
* **@auth rules:** Ensure users can only edit their own data.
* **Cognito Groups:** Users are automatically sorted into `FarmerGroup`, `BuyerGroup`, or `DeliveryGroup` upon sign-up based on their selected role.

---

## 🤝 Contributing
Contributions are welcome! Please fork the repository and submit a pull request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📜 License
This project is licensed under the MIT License.
