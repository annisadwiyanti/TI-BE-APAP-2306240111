# Panduan Testing Backend Tour Package API

## Prerequisites
1. Database MySQL sudah running
2. Database `tour_package_db` sudah dibuat
3. Backend sudah di-run (port 8080)

## Database Setup
```sql
CREATE DATABASE IF NOT EXISTS tour_package_db;
USE tour_package_db;
```

## Running the Backend

### Option 1: Using Gradle
```bash
cd /Users/annisadwiyanti/Desktop/SEM\ 5/APAP/TI\ APAP/tour-package/tour-package-be
./gradlew bootRun
```

### Option 2: Using IDE
- Open project di IntelliJ/VS Code
- Run TourPackage2306240111BeApplication.java

Backend akan run di: http://localhost:8080

---

## Testing Endpoints

### 1. Home Endpoint
**GET** `http://localhost:8080/api/package/`

```bash
curl -X GET http://localhost:8080/api/package/
```

Expected Response:
```json
{
  "status": 200,
  "message": "Welcome to Tour Package Management System",
  "timestamp": "2025-11-05T10:00:00Z",
  "data": "Tour Package Management API is running"
}
```

---

### 2. Read All Packages (with Pagination)
**GET** `http://localhost:8080/api/package?page=0&size=10`

```bash
curl -X GET "http://localhost:8080/api/package?page=0&size=10"
```

Expected Response:
```json
{
  "status": 200,
  "message": "Success retrieving all packages",
  "timestamp": "2025-11-05T10:00:00Z",
  "data": {
    "content": [
      {
        "id": "PACK-user001-001",
        "packageName": "Bandung Highland Experience",
        "userId": "user001",
        "quota": 20,
        "price": 650000,
        "status": "Pending",
        "startDate": "01/12/2025 08:00",
        "endDate": "04/12/2025 18:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

---

### 3. Create Package
**POST** `http://localhost:8080/api/package/create`

```bash
curl -X POST http://localhost:8080/api/package/create \
  -H "Content-Type: application/json" \
  -d '{
    "packageName": "Bandung Highland Experience",
    "userId": "user001",
    "quota": 20,
    "price": 650000,
    "startDate": "01/12/2025 08:00",
    "endDate": "04/12/2025 18:00"
  }'
```

Expected Response:
```json
{
  "status": 201,
  "message": "Package created successfully",
  "timestamp": "2025-11-05T10:00:00Z",
  "data": {
    "id": "PACK-user001-001",
    "packageName": "Bandung Highland Experience",
    "userId": "user001",
    "quota": 20,
    "price": 650000,
    "status": "Pending",
    "startDate": "01/12/2025 08:00",
    "endDate": "04/12/2025 18:00"
  }
}
```

---

### 4. Get Package Detail (with Plans)
**GET** `http://localhost:8080/api/package/PACK-user001-001`

```bash
curl -X GET http://localhost:8080/api/package/PACK-user001-001
```

Expected Response:
```json
{
  "status": 200,
  "message": "Success retrieving package detail",
  "timestamp": "2025-11-05T10:00:00Z",
  "data": {
    "packageInfo": {
      "id": "PACK-user001-001",
      "packageName": "Bandung Highland Experience",
      "userId": "user001",
      "quota": 20,
      "price": 650000,
      "status": "Pending",
      "startDate": "01/12/2025 08:00",
      "endDate": "04/12/2025 18:00"
    },
    "plans": []
  }
}
```

---

### 5. Edit Package
**PUT** `http://localhost:8080/api/package/PACK-user001-001/edit`

```bash
curl -X PUT http://localhost:8080/api/package/PACK-user001-001/edit \
  -H "Content-Type: application/json" \
  -d '{
    "packageName": "Bandung Highland Experience Updated",
    "quota": 25,
    "price": 700000,
    "startDate": "01/12/2025 08:00",
    "endDate": "04/12/2025 18:00"
  }'
```

Expected Response:
```json
{
  "status": 200,
  "message": "Package updated successfully",
  "timestamp": "2025-11-05T10:00:00Z",
  "data": {
    "id": "PACK-user001-001",
    "packageName": "Bandung Highland Experience Updated",
    "userId": "user001",
    "quota": 25,
    "price": 700000,
    "status": "Pending",
    "startDate": "01/12/2025 08:00",
    "endDate": "04/12/2025 18:00"
  }
}
```

---

### 6. Delete Package (Soft Delete)
**DELETE** `http://localhost:8080/api/package/PACK-user001-001/delete`

```bash
curl -X DELETE http://localhost:8080/api/package/PACK-user001-001/delete
```

Expected Response:
```json
{
  "status": 200,
  "message": "Package deleted successfully",
  "timestamp": "2025-11-05T10:00:00Z",
  "data": "Package PACK-user001-001 has been deleted"
}
```

---

## Testing dengan Postman

1. Download Postman: https://www.postman.com/downloads/
2. Create New Collection: "Tour Package API"
3. Add requests sesuai endpoint di atas
4. Test satu per satu

---

## Common Errors & Solutions

### Error: "database connection refused"
- Check MySQL service running: `brew services list`
- Start MySQL: `brew services start mysql`
- Create database: `mysql -u root < setup.sql`

### Error: "Package 'package' not found"
- Delete folder `/restdto/request/package/` 
- Keep file di `/restdto/request/CreatePackageRequestDTO.java`

### Error: "Hibernate validation error"
- Check spring.jpa.hibernate.ddl-auto=update
- Database schema akan auto-created

---

## What to Check

✅ Compile Successfully (no red squiggles in IDE)
✅ Application starts on port 8080
✅ GET / returns welcome message
✅ POST /create membuat package baru di database
✅ PUT /edit update package dengan benar
✅ DELETE /delete soft delete (set is_deleted=true)
✅ Validation works (misalnya endDate < startDate)

