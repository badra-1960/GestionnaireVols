✈️ Air Mali — Gestionnaire de Réservations de Vols

Flight Reservation Management System



Projet universitaire — Sous-groupe 13
University Project — Sub-group 13



MembreRôleAli Badara CamaraDéveloppeurAminata Belly SeckDéveloppeur


 Présentation du projet

Air Mali est une application de bureau développée en Java Swing permettant de gérer les réservations de vols d'une compagnie aérienne fictive. L'interface graphique permet à un agent ou à un passager de :


➕ Ajouter une réservation (Nom, Destination, N° de vol)
🗑️ Supprimer une réservation existante
🔍 Rechercher une réservation par nom, destination ou numéro de vol
📋 Consulter la liste complète des réservations enregistrées
🪑 Choisir un siège sur une carte visuelle de l'avion
🎫 Imprimer un ticket de boarding pass


Les données sont persistées automatiquement dans un fichier reservations.dat à chaque modification.


🇬🇧 Project Overview

Air Mali is a desktop application built with Java Swing for managing flight reservations for a fictional airline. The graphical interface allows an agent or passenger to:


➕ Add a reservation (Name, Destination, Flight number)
🗑️ Delete an existing reservation
🔍 Search a reservation by name, destination, or flight number
📋 View the full list of registered reservations
🪑 Select a seat on a visual aircraft map
🎫 Print a boarding pass ticket


Data is automatically saved to a reservations.dat file on every change.


🖼️ Aperçu de l'interface / Interface Preview


Remplace ce bloc par une capture d'écran de ton application une fois lancée.
Replace this block with a screenshot of your running application.



[ Screenshot à ajouter ici / Screenshot to be added here ]


⚙️ Prérequis / Prerequisites

OutilVersion minimaleJava JDK8+Apache NetBeans12+Apache AntInclus dans NetBeans


🚀 Installation & Lancement / Setup & Run

 Étapes


Cloner ou télécharger ce dépôt sur votre machine
Ouvrir NetBeans
Aller dans File → Open Project
Sélectionner le dossier GestionnaireVols
Cliquer sur Open Project
Clic droit sur le projet → Run (ou F6)



⚠️ Si un fichier reservations.dat est absent au premier lancement, il sera créé automatiquement à la première réservation.



🇬🇧 Steps


Clone or download this repository to your machine
Open NetBeans
Go to File → Open Project
Select the GestionnaireVols folder
Click Open Project
Right-click the project → Run (or press F6)



⚠️ If the reservations.dat file is missing on first launch, it will be created automatically upon the first reservation.




📁 Structure du projet / Project Structure

GestionnaireVols/
│
├── src/
│   └── gestionnairevols/
│       ├── GestionnaireVols.java   # Point d'entrée / Entry point
│       ├── Reservation.java        # Modèle de données / Data model
│       └── ReservationManager.java # Interface graphique + logique / UI + Logic
│
├── reservations.dat                # Données persistées (généré auto)
└── README.md


🧑‍💻 Technologies utilisées / Technologies Used


Java 8 — Langage principal / Main language
Java Swing — Interface graphique / GUI framework
Java Serialization (ObjectOutputStream) — Persistance des données / Data persistence
Apache Ant — Build system (NetBeans natif / native)



⚠️ Problèmes connus / Known Issues

#Description FRDescription EN1Le fichier SeedReservations.java est un outil de test interne — ne pas exécuter en productionSeedReservations.java is an internal dev tool — do not run in production2L'espace admin utilise des identifiants codés en dur (admin / admin123)Admin panel uses hardcoded credentials (admin / admin123)


📄 Licence / License

Projet académique — usage éducatif uniquement.
Academic project — educational use only.


Air Mali © 2026 — Ali Badara Camara & Aminata Belly Seck
