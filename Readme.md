🇸🇳 Sénégal Santé : Carnet de Santé NumériqueSénégal Santé est une application desktop native (JavaFX) conçue pour la gestion sécurisée et décentralisée des données médicales. Pensée pour le contexte sénégalais, elle fonctionne 100 % hors ligne, permettant aux familles de centraliser leurs informations de santé sans dépendre d'une connexion internet.🚀 Fonctionnalités principalesAuthentification sécurisée : Inscription et connexion avec hachage des mots de passe (BCrypt).Gestion multi-profils : Un compte principal peut gérer plusieurs profils (conjoint, enfants, parents) avec groupe sanguin et allergies.Suivi des Ordonnances : Saisie numérique, historique et possibilité de joindre des photos.Gestion des Médicaments : Inventaire par profil et système de rappels de prise.Dossier Médical (HealthRecord) : Historique complet des consultations et constantes.Agenda Médical : Gestion et rappels des rendez-vous.Export PDF : Génération de documents officiels et résumés de santé via OpenPDF.Stockage Local : Utilisation d'une base de données SQLite intégrée (aucun serveur à installer).🛠 Technologies utiliséesComposantTechnologieLangageJava 17Interface GraphiqueJavaFX 21Framework BackendSpring Boot 3.2 (Context & Injection)Base de donnéesSQLite via Hibernate/JPASécuritéSpring Security (BCrypt)Génération PDFOpenPDF (LibrePDF)Build ToolMaven📥 PrérequisAvant de lancer le projet, assurez-vous d'avoir installé :JDK 17 ou version supérieure.Maven 3.6+.⚙️ Installation et Lancement1. Cloner le projetBashgit clone https://github.com/votre-utilisateur/senegal-sante.git
cd senegal-sante
2. Compiler et exécuterVous pouvez lancer l'application via le plugin JavaFX :Bashmvn clean compile javafx:run
Ou via le plugin Spring Boot pour initialiser correctement le contexte de données :Bashmvn spring-boot:run
Note : Au premier démarrage, l'application crée automatiquement le fichier senegal_sante.db à la racine du projet.📂 Structure du ProjetPlaintextsrc/main/java/com/senegalsante/
├── SenegalSanteApp.java      # Point d'entrée (Main)
├── config/                   # Configuration Hibernate & Sécurité
├── controller/javafx/        # Contrôleurs de l'interface (UI Logic)
├── model/                    # Entités JPA (User, Profile, Medication...)
├── repository/               # Interfaces Spring Data JPA
├── service/                  # Logique métier & Génération PDF
└── util/                     # Utilitaires (Gestion des dates, contexte)

src/main/resources/
├── fxml/                     # Fichiers de vue JavaFX
├── css/                      # Styles personnalisés
└── application.properties    # Configuration SQLite et JPA
🛡 Sécurité et ConfidentialitéL'application repose sur le principe de la souveraineté des données :Données locales : Rien n'est envoyé sur un serveur tiers.Chiffrement : Les mots de passe ne sont jamais stockés en clair.Portabilité : Le fichier .db peut être sauvegardé manuellement par l'utilisateur.📄 LicenceProjet à usage éducatif / démonstration.Développé pour moderniser l'accès aux soins au Sénégal.
