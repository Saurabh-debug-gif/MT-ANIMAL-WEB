# 🐔 MT Animal Care – Poultry Shop Web Application

MT Animal Care is a full-stack web application built for showcasing and selling poultry & animal healthcare products.  
It includes product listings, cart functionality, Google OAuth login, admin management, enquiry system, and deployment on Render with PostgreSQL.

🔗 **Live Demo:** https://mt-animal-care.shop

---

## 🚀 Features

### 👤 User Features
- Browse products with images, description & stock
- Live product search (name + description)
- Add to cart & manage quantities
- Google OAuth2 Login
- View orders
- Enquiry form (WhatsApp integration)
- AI Chatbot for product queries
- Responsive mobile-friendly UI

### 🛠 Admin Features
- Admin login via Google OAuth (ROLE_ADMIN)
- Manage products (Add / Update / Delete)
- View orders
- Secure admin routes (`/admin/**`)

---

## 🧑‍💻 Tech Stack

**Backend**
- Java 17  
- Spring Boot 3  
- Spring Security (OAuth2 – Google Login)  
- Spring Data JPA (Hibernate)

**Frontend**
- Thymeleaf  
- HTML, CSS, JavaScript  
- Mobile responsive UI

**Database**
- PostgreSQL (Render Managed DB)  
- MySQL (Local development)

**DevOps / Deployment**
- Docker  
- Render (Web Service + PostgreSQL)  
- GitHub for version control  

**Payments**
- Cashfree Payment Gateway (Integration ready)

---

## ⚙️ Setup & Run Locally

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/Saurabh-debug-gif/MT-ANIMAL-WEB.git
cd MT-ANIMAL-WEB
