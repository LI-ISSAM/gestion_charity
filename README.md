#  CharityApp
lien vers le site : gestioncharity-production.up.railway.app

> **Plateforme de gestion des actions de charité au Maroc**  
> Built with Spring Boot 3.2 · Java 21 · PostgreSQL · Stripe · Thymeleaf

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=flat-square&logo=docker)
![Stripe](https://img.shields.io/badge/Stripe-Payments-purple?style=flat-square&logo=stripe)
![Railway](https://img.shields.io/badge/Deployed%20on-Railway-black?style=flat-square&logo=railway)

---

##  Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Stack technique](#-stack-technique)
- [Architecture](#-architecture)
- [Installation locale](#-installation-locale)
- [Variables d'environnement](#-variables-denvironnement)
- [Déploiement Docker](#-déploiement-docker)
- [Déploiement Railway](#-déploiement-railway)
- [Auteur](#-auteur)

---

##  À propos

**CharityApp** est une application web complète permettant aux organisations caritatives de créer et gérer leurs initiatives, aux utilisateurs de faire des dons sécurisés et de participer aux actions, et aux administrateurs de superviser l'ensemble de la plateforme.

L'application supporte le **Français** et l'**Arabe (RTL)** et intègre un système de paiement via **Stripe**.

---

##  Fonctionnalités

###  Utilisateurs
- ✅ Inscription avec photo de profil
- ✅ Connexion / Déconnexion sécurisée
- ✅ Profil modifiable (infos + photo)
- ✅ Historique des dons et participations

###  Organisations
- ✅ Création et soumission d'organisation
- ✅ Validation par l'administrateur
- ✅ CRUD complet des actions de charité
- ✅ Cycle de vie : `DRAFT → ACTIVE → ARCHIVED`
- ✅ Tableau de bord avec statistiques

###  Actions de charité
- ✅ Création avec upload d'image
- ✅ Suivi de progression (montant collecté / objectif)
- ✅ Compteur de vues intelligent (unique par utilisateur)
- ✅ Recherche et filtres avancés (mot-clé, catégorie, ville)

###  Dons
- ✅ Paiement sécurisé via **Stripe Checkout**
- ✅ Historique des transactions
- ✅ Mise à jour automatique des montants collectés

###  Participation
- ✅ Inscription/désinscription aux actions
- ✅ Contrainte d'unicité par utilisateur/action
- ✅ Statuts : `REGISTERED → CONFIRMED → ATTENDED`

###  Administration
- ✅ Dashboard avec statistiques globales
- ✅ Validation/rejet/suspension des organisations
- ✅ Gestion des utilisateurs (rôles, suppression)
- ✅ Supervision de toutes les actions

###  Notifications
- ✅ Email de bienvenue à l'inscription
- ✅ Confirmation de participation
- ✅ Notification de nouvelle action publiée

###  Multilingue
- ✅ Interface en **Français** et **Arabe**
- ✅ Support **RTL** pour l'arabe

---

## 🛠 Stack technique

| Couche | Technologie | Version |
|---|---|---|
| Backend | Spring Boot | 3.2 |
| Langage | Java | 21 LTS |
| Frontend | Thymeleaf + Bootstrap | 3.1 / 5.3 |
| Base de données | PostgreSQL | 16 |
| ORM | Spring Data JPA | — |
| Paiement | Stripe | 24.3 |
| Stockage images | Cloudinary | 1.37 |
| Email | Brevo (SMTP) | — |
| Build | Maven | 3.9 |
| Containerisation | Docker + Compose | — |
| Déploiement | Railway | — |

---

##  Architecture

```
com.jee.app/
├── config/          # StripeConfig, CloudinaryConfig, LocaleConfig...
├── controllers/     # AuthController, DonationController...
├── dto/             # RegisterDTO, LoginDTO, OrganisationDTO...
├── enums/           # Role, ActionStatus, PaymentStatus...
├── model/           # Users, Organisation, CharityAction...
├── repositories/    # Spring Data JPA Repositories
├── services/        # Business Logic (UserService, StripeService...)
└── AppApplication.java
```

---

##  Installation locale

### Prérequis
- Java 21+
- Maven 3.9+
- PostgreSQL 16+
- Docker (optionnel)

### 1. Clone le projet

```bash
git clone https://github.com/LI-ISSAM/gestion_charity.git
cd gestion_charity
```

### 2. Configure la base de données

```sql
CREATE DATABASE charity_db;
```

### 3. Configure les variables d'environnement

Crée un fichier `.env` à la racine :

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/charity_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=1234
STRIPE_API_KEY=sk_test_xxx
STRIPE_PUBLIC_KEY=pk_test_xxx
SPRING_MAIL_USERNAME=your-brevo-login@smtp-brevo.com
SPRING_MAIL_PASSWORD=your-brevo-smtp-key
APP_BASE_URL=http://localhost:8080
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
```

### 4. Lance l'application

```bash
mvn spring-boot:run
```

Accède à : **http://localhost:8080**

### 5. Compte admin par défaut

```
Email    : litimi.dev@gmail.com
Password : issam1212
```

---

##  Variables d'environnement

| Variable | Description | Requis |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL PostgreSQL | ✅ |
| `SPRING_DATASOURCE_USERNAME` | User PostgreSQL | ✅ |
| `SPRING_DATASOURCE_PASSWORD` | Password PostgreSQL | ✅ |
| `STRIPE_API_KEY` | Clé secrète Stripe | ✅ |
| `STRIPE_PUBLIC_KEY` | Clé publique Stripe | ✅ |
| `APP_BASE_URL` | URL de base de l'app | ✅ |
| `SPRING_MAIL_HOST` | Serveur SMTP | ✅ |
| `SPRING_MAIL_PORT` | Port SMTP | ✅ |
| `SPRING_MAIL_USERNAME` | Login SMTP | ✅ |
| `SPRING_MAIL_PASSWORD` | Mot de passe SMTP | ✅ |
| `CLOUDINARY_CLOUD_NAME` | Nom cloud Cloudinary | ✅ |
| `CLOUDINARY_API_KEY` | API Key Cloudinary | ✅ |
| `CLOUDINARY_API_SECRET` | API Secret Cloudinary | ✅ |
| `PORT` | Port serveur (Railway) | Auto |

---

##  Déploiement Docker

```bash
# Lance l'application + PostgreSQL
docker-compose up --build -d

# Voir les logs
docker-compose logs -f app

# Arrêter
docker-compose down
```

---

##  Déploiement Railway

1. Push le code sur **GitHub**
2. Crée un projet sur **[railway.app](https://railway.app)**
3. Connecte ton repo GitHub
4. Ajoute un service **PostgreSQL**
5. Configure les variables d'environnement
6. Génère un domaine dans **Settings → Networking**
7. Deploy ! 

---

##  Test Stripe (Sandbox)

| Scénario | Numéro de carte |
|---|---|
|  Paiement réussi | `4242 4242 4242 4242` |
|  Paiement refusé | `4000 0000 0000 0002` |
|  3D Secure | `4000 0025 0000 3155` |

> Date : n'importe quelle date future · CVC : n'importe quels 3 chiffres

---

##  Structure du projet

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/jee/app/
│   │   └── resources/
│   │       ├── templates/        # Thymeleaf HTML
│   │       ├── static/           # CSS, JS
│   │       ├── messages.properties      # Français
│   │       └── messages_ar.properties   # Arabe
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

##  Auteur

**Issam Litimi**

[![GitHub](https://img.shields.io/badge/GitHub-LI--ISSAM-black?style=flat-square&logo=github)](https://github.com/LI-ISSAM)
[![Email](https://img.shields.io/badge/Email-litimi.dev%40gmail.com-red?style=flat-square&logo=gmail)](mailto:litimi.dev@gmail.com)

---

##  Licence

Ce projet est développé dans le cadre d'un projet académique.

---


</div>
