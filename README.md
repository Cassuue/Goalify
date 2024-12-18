# Goalify

Goalify est une application mobile conçue pour aider les utilisateurs à atteindre leurs objectifs en leur proposant des tâches quotidiennes et en suivant leur progression. L'application utilise Firebase pour la gestion des utilisateurs et des tâches, et intègre l'intelligence artificielle pour générer des suggestions de tâches personnalisées.

Attention : Ce projet est un prototype et n'est pas destiné à être utilisé en production (notamment en raison de l'utilisation de clés d'API sensibles qui pourraientt être extraites à partir du code compilé présent sur les appareils des utilisateurs).

## Table des matières

- [Introduction](#introduction)
- [Fonctionnalités](#fonctionnalités)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [Contribution](#contribution)
- [Contributeurs](#contributeurs)

## Introduction

Goalify est une application mobile développée en Kotlin, utilisant Firebase pour la gestion des utilisateurs et des tâches. L'application permet aux utilisateurs de définir des objectifs personnels et de recevoir des suggestions de tâches quotidiennes pour les aider à atteindre ces objectifs. L'application intègre également l'intelligence artificielle pour générer des suggestions de tâches personnalisées.

## Fonctionnalités

- **Gestion des utilisateurs** : Les utilisateurs peuvent créer un compte, se connecter et se déconnecter.
- **Gestion des tâches** : Les utilisateurs peuvent ajouter, modifier et supprimer des tâches.
- **Suggestions de tâches** : L'application utilise l'intelligence artificielle pour générer des suggestions de tâches personnalisées en fonction de l'historique des tâches de l'utilisateur.
- **Notifications** : Les utilisateurs reçoivent des notifications lorsque des tâches sont débloquées ou lorsque des objectifs sont atteints.

## Installation

Pour installer et configurer l'application, suivez ces étapes :

1. **Cloner le dépôt** :
   ```sh
   git clone https://github.com/Cassuue/Goalify.git
   cd Goalify
   ```

2. **Configurer Firebase** :
    - Créez un projet Firebase et ajoutez l'application Android à votre projet Firebase.
    - Téléchargez le fichier `google-services.json` et placez-le dans le répertoire `app` de votre projet.

3. **Configurer l'IA** :
    - Créez un compte sur [groq.com](https://groq.com) pour obtenir une clé d'API.
    - Dans le fichier local.properties, ajoutez la clé d'API comme suit :
      ```properties
      groqApiKey=gsk_**********************
      ```

4. **Construire et exécuter l'application** :
    - Utilisez Android Studio pour construire et exécuter l'application sur un appareil ou un émulateur.

## Utilisation

Pour utiliser l'application, suivez ces étapes :

1. **Créer un compte** : Inscrivez-vous ou connectez-vous à l'application en utilisant votre adresse e-mail et votre mot de passe.
2. **Définir des objectifs** : Définissez des objectifs personnels que vous souhaitez atteindre.
3. **Ajouter des tâches** : Ajoutez des tâches quotidiennes qui vous aideront à atteindre vos objectifs.
4. **Recevoir des notifications** : Recevez des notifications lorsque des tâches sont débloquées ou lorsque des objectifs sont atteints.

## Contribution

Les contributions sont les bienvenues ! Si vous souhaitez contribuer à ce projet, veuillez suivre ces étapes :

1. **Forker le dépôt** : Forker le dépôt sur GitHub.
2. **Créer une branche** : Créez une nouvelle branche pour votre contribution.
3. **Faire des modifications** : Apportez vos modifications ou ajouts à l'application.
4. **Créer une Pull Request** : Créez une Pull Request avec vos modifications.

## Contributeurs

- [Cassie](https://github.com/Cassuue)
- [Léo](https://github.com/Esscraye)
- [Erwan](https://github.com/Tuurtleee)
- [Antoine](https://github.com/Antirion779)

### Utilisation de l'IA pour aider au développement, merci à :
- [ChatGPT](https://chatgpt.com/)
- [Mistral](https://chat.mistral.ai/chat)
- [Github Copilot](https://copilot.github.com/)