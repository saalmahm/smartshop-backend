# 🛍️ SmartShop – Backend REST API

> Une API REST complète pour la gestion de boutique en ligne avec système de fidélité, gestion des stocks et paiements sécurisés.

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-green?style=flat&logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue?style=flat&logo=postgresql)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 📋 Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Stack technique](#-stack-technique)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [API Endpoints](#-api-endpoints)
- [Authentification](#-authentification)
- [Tests](#-tests)
- [Guide de démarrage](#-guide-de-démarrage)

---

## 🎯 À propos

**SmartShop** est une application backend robuste conçue pour gérer tous les aspects d'une boutique en ligne moderne. Elle offre :

- ✅ Gestion complète des produits et du stock
- ✅ Système de fidélité multi-niveaux
- ✅ Codes promotionnels à usage unique
- ✅ Gestion avancée des commandes et paiements
- ✅ API REST sécurisée et documentée
- ✅ Architecture propre et maintenable

---

## ✨ Fonctionnalités

### 👥 Gestion des Clients

- **CRUD complet** des clients (réservé ADMIN)
- **Système de fidélité** à 4 niveaux : `BASIC` → `SILVER` → `GOLD` → `PLATINUM`
- **Historique des commandes** accessible côté admin et client
- **Profil détaillé** avec statistiques :
    - Nombre total de commandes
    - Montant total dépensé
    - Niveau de fidélité actuel
    - Dates de première et dernière commande

### 📦 Gestion des Produits

- CRUD complet des produits
- Gestion automatique du stock
- Prix unitaire, description, catégories
- Validation de disponibilité lors des commandes

### 🛒 Gestion des Commandes

**Création de commande intelligente :**
```json
{
  "clientId": 1,
  "promoCode": "PROMO-AB12",
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 1 }
  ]
}
```

**Statuts de commande :**
- `PENDING` ⏳ En attente de confirmation
- `CONFIRMED` ✅ Confirmée et en cours
- `CANCELLED` ❌ Annulée
- `REJECTED` 🚫 Rejetée (stock insuffisant)

**Calculs automatiques :**
1. Sous-total HT
2. Remise fidélité (selon tier)
3. Remise code promo
4. Total HT après remises
5. TVA
6. **Total TTC**

### 🎁 Codes Promotionnels

- Format validé : `PROMO-XXXX`
- **Usage unique** : un code ne peut être utilisé qu'une seule fois
- Validation automatique lors de la création de commande

### 💳 Gestion des Paiements

**Types de paiement :**
- 💵 **ESPECES** : encaissement immédiat
- 💳 **Carte bancaire** : statut `EN_ATTENTE`
- 📄 **Chèque** : à encaisser manuellement
- 🏦 **Virement** : à confirmer

**Statuts de paiement :**
- `EN_ATTENTE` ⏳ En attente de traitement
- `ENCAISSE` ✅ Encaissé avec succès
- `REJETE` ❌ Rejeté

**Règles de gestion :**
- Montant > 0 et ≤ limite légale
- Montant ≤ solde restant de la commande
- Paiements autorisés uniquement sur commandes `PENDING`
- Recalcul automatique du `remainingAmount`

---

## 🛠️ Stack Technique

| Technologie | Version | Usage |
|------------|---------|-------|
| ☕ **Java** | 17 | Langage principal |
| 🍃 **Spring Boot** | 4 | Framework backend |
| 🗄️ **PostgreSQL** | Latest | Base de données |
| 📊 **Spring Data JPA** | - | Couche de persistence |
| 🔒 **BCrypt** | - | Hachage des mots de passe |
| 🔄 **MapStruct** | - | Mapping DTO ↔ Entités |
| ✅ **Bean Validation** | Jakarta | Validation des données |
| 🧪 **JUnit 5** | - | Tests unitaires |
| 🎭 **Mockito** | - | Mocking pour tests |
| 📈 **JaCoCo** | - | Couverture de code |

---

## 🏗️ Architecture

```
src/main/java/com/smartshop/
│
├── 🎮 controller/          # Endpoints REST
│   ├── AuthController
│   ├── ClientController
│   ├── ProductController
│   ├── OrderController
│   ├── PaymentController
│   └── MeController
│
├── ⚙️ service/             # Logique métier
│   ├── ClientService
│   ├── ProductService
│   ├── OrderService
│   ├── OrderCalculationService
│   ├── LoyaltyService
│   └── PaymentService
│
├── 💾 repository/          # Accès données (Spring Data JPA)
│   ├── ClientRepository
│   ├── ProductRepository
│   ├── OrderRepository
│   └── PaymentRepository
│
├── 📦 entity/              # Entités JPA
│   ├── Client
│   ├── Product
│   ├── Order
│   ├── OrderItem
│   └── Payment
│
├── 📄 dto/                 # Data Transfer Objects
│   ├── request/
│   └── response/
│
├── 🔄 mapper/              # MapStruct mappers
│
└── 🚨 exception/           # Gestion des erreurs
    ├── ResourceNotFoundException
    ├── BusinessValidationException
    └── GlobalExceptionHandler
```

---

## 🚀 Installation

### Prérequis

- ☕ Java 17 ou supérieur
- 📦 Maven 3.9+
- 🐘 PostgreSQL

### Étapes d'installation

1️⃣ **Cloner le repository**
```bash
git clone https://github.com/votre-repo/smartshop.git
cd smartshop
```

2️⃣ **Configurer la base de données**

Créer une base PostgreSQL :
```sql
CREATE DATABASE smartshop_db;
```

3️⃣ **Configurer `application.properties`**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smartshop_db
spring.datasource.username=votre_user
spring.datasource.password=votre_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

4️⃣ **Installer les dépendances et lancer**
```bash
mvn clean install
mvn spring-boot:run
```

5️⃣ **Accéder à l'API**
```
🌐 http://localhost:8080
```

---

## 📡 API Endpoints

### 🔐 Authentification

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/auth/login` | Connexion utilisateur |

### 👤 Clients (ADMIN)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/admin/clients` | Liste tous les clients |
| `POST` | `/admin/clients` | Créer un client |
| `GET` | `/admin/clients/{id}` | Détails d'un client |
| `PUT` | `/admin/clients/{id}` | Modifier un client |
| `DELETE` | `/admin/clients/{id}` | Supprimer un client |
| `GET` | `/admin/clients/{id}/orders` | Historique commandes |
| `POST` | `/admin/clients/{id}/user` | Associer un compte user |

### 📦 Produits (ADMIN)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/admin/products` | Liste tous les produits |
| `POST` | `/admin/products` | Créer un produit |
| `GET` | `/admin/products/{id}` | Détails d'un produit |
| `PUT` | `/admin/products/{id}` | Modifier un produit |
| `DELETE` | `/admin/products/{id}` | Supprimer un produit |

### 🛒 Commandes (ADMIN)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/admin/orders` | Créer une commande |
| `GET` | `/admin/orders/{id}` | Détails d'une commande |
| `PUT` | `/admin/orders/{id}/confirm` | Confirmer une commande |

### 💳 Paiements (ADMIN)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/admin/payments` | Créer un paiement |
| `PUT` | `/admin/payments/{id}/encash` | Encaisser un paiement |
| `PUT` | `/admin/payments/{id}/reject` | Rejeter un paiement |

### 👤 Espace Client

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/me/profile` | Mon profil et statistiques |
| `GET` | `/me/orders` | Mes commandes |

---

## 🔒 Authentification

### Login

**Endpoint :** `POST /auth/login`

**Payload :**
```json
{
  "username": "john.doe",
  "password": "motdepasse123"
}
```

**Réponse :**
```json
{
  "id": 1,
  "username": "john.doe",
  "role": "CLIENT",
  "message": "Login successful"
}
```

### Sécurité

- 🔐 Mots de passe hashés avec **BCrypt**
- 🍪 Session HTTP pour l'authentification
- 🛡️ Contrôle d'accès basé sur les rôles (`ADMIN` / `CLIENT`)
- 🚫 Endpoints `/admin/**` réservés aux administrateurs
- 👤 Endpoints `/me/**` réservés au client connecté

---

## ✅ Validation des données

Toutes les requêtes sont validées avec **Jakarta Bean Validation** :

| Annotation | Usage |
|------------|-------|
| `@NotBlank` | Champs texte obligatoires |
| `@NotNull` | Champs obligatoires |
| `@Email` | Format email valide |
| `@Min(value)` | Valeur minimale |
| `@DecimalMin(value)` | Montant minimal |
| `@Size(min, max)` | Longueur de chaîne |
| `@Pattern(regexp)` | Format personnalisé |

**Exemple de réponse d'erreur :**
```json
{
  "timestamp": "2025-12-03T10:30:00",
  "status": 400,
  "error": "Validation error",
  "message": "Invalid input data",
  "path": "/admin/products",
  "errors": {
    "unitPrice": "must be greater than or equal to 0.01",
    "name": "must not be blank"
  }
}
```

---

## 🧪 Tests

### Lancer les tests

```bash
mvn clean test
```

### Couverture de code avec JaCoCo

Générer le rapport :
```bash
mvn clean test jacoco:report
```

Ouvrir le rapport :
```bash
open target/site/jacoco/index.html
```

### Tests implémentés

- ✅ `LoyaltyServiceTest` - Calcul des niveaux de fidélité
- ✅ `OrderCalculationServiceTest` - Calculs de commandes
- ✅ `PaymentServiceTest` - Logique de paiement
- ✅ `SmartshopApplicationTests` - Tests d'intégration Spring

---

## 🎮 Guide de démarrage rapide

### Scénario complet avec Postman

1️⃣ **Créer un administrateur** (via SQL ou endpoint)

2️⃣ **Se connecter en tant qu'ADMIN**
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

3️⃣ **Créer des produits**
```http
POST /admin/products
Content-Type: application/json

{
  "name": "Laptop Dell XPS 15",
  "description": "Ordinateur portable haute performance",
  "unitPrice": 1299.99,
  "stockQuantity": 10
}
```

4️⃣ **Créer un client**
```http
POST /admin/clients
Content-Type: application/json

{
  "name": "Jean Dupont",
  "email": "jean.dupont@email.com",
  "phone": "+33612345678",
  "address": "123 Rue de Paris, 75001 Paris"
}
```

5️⃣ **Créer un compte utilisateur pour le client**
```http
POST /admin/clients/1/user
Content-Type: application/json

{
  "username": "jean.dupont",
  "password": "Password123!"
}
```

6️⃣ **Créer une commande avec code promo**
```http
POST /admin/orders
Content-Type: application/json

{
  "clientId": 1,
  "promoCode": "PROMO-NEW2024",
  "items": [
    { "productId": 1, "quantity": 1 }
  ]
}
```

7️⃣ **Créer un paiement**
```http
POST /admin/payments
Content-Type: application/json

{
  "orderId": 1,
  "amount": 500.00,
  "type": "ESPECES",
  "paymentDate": "2025-12-03"
}
```

8️⃣ **Se connecter en tant que CLIENT**
```http
POST /auth/login
Content-Type: application/json

{
  "username": "jean.dupont",
  "password": "Password123!"
}
```

9️⃣ **Voir mon profil**
```http
GET /me/profile
```

🔟 **Voir mes commandes**
```http
GET /me/orders
```

---

## 🚨 Gestion des erreurs

Le système gère automatiquement les erreurs avec des réponses JSON standardisées :

| Code | Type d'erreur | Description |
|------|---------------|-------------|
| `400` | Bad Request | Erreur de validation |
| `404` | Not Found | Ressource introuvable |
| `422` | Unprocessable Entity | Erreur métier |
| `500` | Internal Server Error | Erreur serveur |

**Exemple de réponse d'erreur :**
```json
{
  "timestamp": "2025-12-03T10:30:00",
  "status": 422,
  "error": "Unprocessable entity",
  "message": "Promo code already used",
  "path": "/admin/orders"
}
```

## 👥 Auteur

- **Développée par** : Salma Hamdi