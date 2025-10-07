🎓 QuietCorner — MVP Project

Goal:
Help students and freelancers find quiet and comfortable places to study and work in the city.

🧩 Core Features (MVP)
Interactive map (OpenStreetMap via OSMDroid).
Four pre-defined quiet places shown as markers.
Popup window with place name and description on click.
Fully offline, no server required.
Built entirely in Java for Android.

🏗️ Technology Stack
Language: Java
Framework: Android SDK
Map: OpenStreetMap (OSMDroid 6.1.16)
IDE: Android Studio
Build System: Gradle Kotlin DSL

🧠 Architecture
MainActivity — loads map and places
Place class — data model (name, latitude, longitude, description)
Static list of places → displayed as markers on the map

🚀 Future Improvements
Dynamic loading of places from JSON or database
Filter by Wi-Fi / power / noise
User reviews and ratings
Web version with Leaflet.js
