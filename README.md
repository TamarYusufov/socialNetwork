# Social Network For Pet Owners

### A social networking app for pet owners and professionals.

This app connects pet owners with professionals who provide various pet-related services. Users can register as either pet owners or professionals, manage profiles, add pets, chat, and share insights about pet care.

https://github.com/TamarYusufov/socialNetwork/issues/1#issue-2655226205
---

## Features

### User Types
- **Pet Owner**: Can create an account, add their pets, find professionals, and chat with them.
- **Professional**: Can create an account, specify their specialty, and connect with pet owners.

### Pet Profiles
- **Add and Manage Pets**: Pet owners can add details about their pets, including name, breed, age, and other relevant information.
- **Browse Professionals**: Find professionals in various fields, view profiles, and start a conversation.

### Specializations
- **Professionals Choose Specialties**: Professionals can select their area of expertise (e.g., Veterinarian, Trainer, Groomer, etc.) to appear in relevant searches.
- **Filter and Discover**: Pet owners can filter professionals based on their pets' needs.

### Messaging
- **Chat**: Real-time messaging between pet owners and professionals.

---
## Firebase Integration

This app uses **Firebase** as the backend to handle user authentication, database storage, and real-time data management. Firebase simplifies managing and synchronizing user data and enables real-time interaction between pet owners and professionals.

---

### Firebase Services Used

1. **Firebase Authentication**:
   - Manages user registration and login securely.
   - Supports authentication methods (e.g., Email/Password).
  
2. **Firebase Realtime Database**:
   - Stores all user-related data, including pet profiles, user profiles, and chat messages.
   - Provides real-time synchronization, so data updates instantly across devices.
   - Organizes data in a structured JSON format to handle and retrieve data efficiently.

3. **Firebase Storage**:
   - Used to store images for users or pet profiles. 

---

## Getting Started

To get a local copy up and running, follow these steps.

### Prerequisites
- **Java 11** or higher
- **Android Studio** with the latest stable version
- **Firebase Account** (for database and authentication)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/socialNetwork.git
   cd socialNetwork

 ###  Open the Project in Android Studio:

2. Open Android Studio and select "Open an existing Android Studio project."
Navigate to the cloned project folder and open it.
Setup Firebase:

3. Go to Firebase Console and create a new project.
Register your app on Firebase and add the google-services.json file to the app directory.
Enable Authentication (Email/Password) and Firebase Realtime Database.
Build and Run the App:

4. Sync the project with Gradle files.
Run the app on an emulator or a physical device.
