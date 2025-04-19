
# 🌦️ MyMeteoMockup

**MyMeteoMockup** est une application Android dynamique qui affiche la météo actuelle, les prévisions de la semaine, et fournit un **conseil vestimentaire personnalisé** grâce à une intégration de l’IA via l’API Mistral.

---

## ✨ Fonctionnalités

- 📍 Météo géolocalisée (OpenWeather API)
- 🌡️ Données : température, vent, humidité, visibilité, icône météo
- 📅 Prévisions des 7 prochains jours (avec scrolling horizontal)
- 🧥 Conseil vestimentaire IA en fonction des conditions météo
- 🗓️ Affichage dynamique de la date
- 💅 UI responsive et clean (Material Design)

---

## ⚙️ Technologies utilisées

- **Langage** : Kotlin
- **API Météo** : [OpenWeather](https://openweathermap.org/api)
- **IA** : [Mistral API](https://console.mistral.ai) (alternative ChatGPT)
- **HTTP** : OkHttp3
- **Localisation** : Google Play Services (FusedLocationProvider)

---

## 🧪 Pré-requis

- Android Studio Arctic Fox ou +
- SDK Android 33 ou +

---

## 🚀 Lancer l’application

1. Clone le repo :
```bash
git clone https://github.com/tonpseudo/MyMeteoMockup.git
```

2. Ouvre dans Android Studio
3. Lance l'émulateur ou branche un appareil
4. Clique sur ▶️ "Run"

---

## 📸 Aperçu

| Météo actuelle | Prévision 7 jours | Conseil IA |
|----------------|-------------------|------------|
| ![temp](https://i.imgur.com/T7k1E4T.png) | ![forecast](https://i.imgur.com/tIbWGBU.png) | ![ai](https://i.imgur.com/DHkYdc0.png) |

---

## 🤖 Exemple de réponse Mistral :

> _"Le ciel est couvert et il fait 14°C. Prends une veste légère et évite les vêtements trop fins. Le vent est modéré, pense à une écharpe fine si tu es frileux."_

---

## 📝 Licence

Ce projet est open source sous licence [MIT](LICENSE).

---