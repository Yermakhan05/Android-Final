
# Auyrma App

## Overview
**Auyrma** is an Android application written in **Kotlin** where users can:
- Search for **Doctors** and **Hospitals**.
- Filter lists by city and hospitals.
- Book, view, and delete **sessions**.
- Mark doctors and hospitals as **favorites** and manage the favorite list.

## Features
1. **Search and Filter:**
   - Search doctors and hospitals by name, specialty, or city.
2. **Session Management:**
   - Create, view, and delete sessions.
3. **Favorites:**
   - Add or remove favorite doctors and hospitals.
   - View the list of favorite doctors and hospitals.

## Technologies Used
- **Backend:** Django REST Framework (DRF)
- **Frontend:** Kotlin (Android Studio)
- **Database:** SQLite (or your preferred database)
- **API Pagination:** PageNumberPagination

---

## API Documentation

### Endpoints:

#### 1. Doctors (`/api/medics_list/`)
- **GET**: Retrieve a list of doctors.
  - **Parameters**:
    - `search`: Filter by doctor name or specialty.
    - `hospital`: Filter by hospital ID.
    - `city`: Filter by city.
- **POST**: Add a new doctor.
- **Pagination**: Default page size is 15.

#### 2. Hospitals (`/api/hospital_list/`)
- **GET**: Retrieve a list of hospitals.
  - **Parameters**:
    - `search`: Filter by hospital name.
    - `city`: Filter by city.

#### 3. Sessions (`/api/sessions_list/`)
- **GET**: Retrieve a list of sessions.
  - **Parameters**:
    - `user`: Filter by client ID.
    - `medic`: Filter by medic ID.
    - `date`: Filter by date.
- **POST**: Create a session.
  - **Fields**:
    - `client_id`: Client ID (Required).
    - `medics_id`: Medic ID (Required).
    - `appointment`: ISO 8601 format date.

#### 4. Favorites
- **Add Doctor to Favorites**: `POST /api/add_favorite_medic/`
  - Parameters: `client_id`, `medic_id`
- **List Favorite Doctors**: `GET /api/list_favorite_medics/{client_id}`
- **Remove Doctor from Favorites**: `POST /api/remove_favorite_medic/`

- **Add Hospital to Favorites**: `POST /api/add_favorite_hospital/`
  - Parameters: `client_id`, `hospital_id`
- **List Favorite Hospitals**: `GET /api/list_favorite_hospitals/{client_id}`
- **Remove Hospital from Favorites**: `POST /api/remove_favorite_hospital/`

---

## Key Implementations

### Backend
1. **Pagination**: `PageNumberPagination` for doctor list.
2. **Filtering**: Search doctors and hospitals using query parameters.
3. **Sessions Management**: Create and manage client sessions.
4. **Favorites System**: Add/remove doctors and hospitals to/from the favorites list.

### Models
- **Client**: Represents a user with `client_name`.
- **Medics**: Represents doctors with fields like `speciality`, `city`, and `hospital`.
- **Hospital**: Contains `name`, `city`, `bed_count`, and `favorites`.
- **Sessions**: Stores client-medic appointment sessions.
- **Pharmacy**: Represents pharmacies with address and contact details.

---

## Project Setup

### Prerequisites:
- **Python 3.x**
- **Django & Django REST Framework**
- **Android Studio**

### Installation:

1. **Backend Setup**:
   ```bash
   git clone <your-repo-link>
   cd backend
   pip install -r requirements.txt
   python manage.py migrate
   python manage.py runserver
   ```

2. **Android App Setup**:
   - Open the project in **Android Studio**.
   - Ensure Kotlin dependencies are configured.
   - Update the API Base URL in the project.

3. **Run the App**:
   - Run the backend server on `http://127.0.0.1:8000/`.
   - Launch the Android app on an emulator or device.

---

## Future Enhancements
1. Add user authentication.
2. Implement a rating and review system.
3. Integrate push notifications for session reminders.

---

## License
This project is licensed under the **MIT License**.
