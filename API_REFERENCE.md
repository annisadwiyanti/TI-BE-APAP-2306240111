# Quick API Reference

## Base URL
```
http://localhost:8080/api/package
```

## Endpoints

### 1. GET All Packages (Fitur 2)
```http
GET /api/package?page=0&size=10
```

**Response Example:**
```json
{
  "status": 200,
  "message": "Success retrieve all packages",
  "timestamp": "2025-11-05T10:30:00",
  "data": {
    "packages": [
      {
        "id": "PACK-user001-001",
        "packageName": "Jakarta - Bali Adventure Package",
        "userId": "user001",
        "quota": 25,
        "price": 67500000,
        "status": "Pending",
        "startDate": "01/11/2025 08:00",
        "endDate": "07/11/2025 08:00"
      }
    ],
    "currentPage": 0,
    "totalItems": 5,
    "totalPages": 1
  }
}
```

---

### 2. GET Package Detail (Fitur 3)
```http
GET /api/package/{packageId}
```

**Example:**
```http
GET /api/package/PACK-user001-001
```

**Response Example:**
```json
{
  "status": 200,
  "message": "Success retrieve package detail",
  "timestamp": "2025-11-05T10:30:00",
  "data": {
    "id": "PACK-user001-001",
    "packageName": "Jakarta - Bali Adventure Package",
    "userId": "user001",
    "quota": 25,
    "price": 67500000,
    "status": "Pending",
    "startDate": "1 November 2025",
    "endDate": "7 November 2025",
    "plans": [
      {
        "id": "uuid-here",
        "packageId": "PACK-user001-001",
        "planName": "Jakarta-Bali Flight Plan",
        "price": 37500000,
        "activityType": "Flight",
        "status": "Fulfilled",
        "startDate": "08:00, 1 November 2025",
        "endDate": "10:30, 1 November 2025",
        "startLocation": "DKI Jakarta (Provinsi)",
        "endLocation": "Bali (Provinsi)"
      }
    ]
  }
}
```

---

### 3. GET Create Form (Fitur 4)
```http
GET /api/package/create
```

**Response Example:**
```json
{
  "status": 200,
  "message": "Create package form structure",
  "timestamp": "2025-11-05T10:30:00",
  "data": {
    "packageName": "text input",
    "userId": "text input",
    "quota": "numeric input",
    "startDate": "date input",
    "endDate": "date input"
  }
}
```

---

### 4. POST Create Package (Fitur 4)
```http
POST /api/package/create
Content-Type: application/json
```

**Request Body:**
```json
{
  "packageName": "Jakarta - Bali Adventure Package",
  "userId": "user001",
  "quota": 25,
  "startDate": "2025-11-01T08:00",
  "endDate": "2025-11-07T08:00"
}
```

**Response Example (Success):**
```json
{
  "status": 201,
  "message": "Package created successfully",
  "timestamp": "2025-11-05T10:30:00",
  "data": {
    "id": "PACK-user001-003",
    "packageName": "Jakarta - Bali Adventure Package",
    "userId": "user001",
    "quota": 25,
    "price": 0,
    "status": "Pending",
    "startDate": "01/11/2025 08:00",
    "endDate": "07/11/2025 08:00"
  }
}
```

**Response Example (Error):**
```json
{
  "status": 400,
  "message": "Error: End date must be after start date",
  "timestamp": "2025-11-05T10:30:00",
  "data": null
}
```

---

## Testing dengan Postman/Thunder Client

### Get All Packages
1. Method: GET
2. URL: `http://localhost:8080/api/package`
3. Query Params (optional):
   - page: 0
   - size: 10

### Get Package Detail
1. Method: GET
2. URL: `http://localhost:8080/api/package/PACK-user001-001`

### Create Package
1. Method: POST
2. URL: `http://localhost:8080/api/package/create`
3. Headers:
   - Content-Type: application/json
4. Body (raw JSON):
```json
{
  "packageName": "Test Package",
  "userId": "user001",
  "quota": 20,
  "startDate": "2025-12-01T08:00",
  "endDate": "2025-12-05T08:00"
}
```

---

## Testing dengan curl

```bash
# Get all packages
curl http://localhost:8080/api/package

# Get all packages with pagination
curl "http://localhost:8080/api/package?page=0&size=5"

# Get package detail
curl http://localhost:8080/api/package/PACK-user001-001

# Get create form structure
curl http://localhost:8080/api/package/create

# Create new package
curl -X POST http://localhost:8080/api/package/create \
  -H "Content-Type: application/json" \
  -d '{
    "packageName": "Yogyakarta Cultural Heritage Tour",
    "userId": "user002",
    "quota": 30,
    "startDate": "2025-11-15T08:00",
    "endDate": "2025-11-18T08:00"
  }'
```

---

## Format Tanggal

### Request (Input)
- Pattern: `yyyy-MM-dd'T'HH:mm`
- Contoh: `2025-11-01T08:00`
- Timezone: Asia/Jakarta

### Response (Output)

**Untuk List Package:**
- Pattern: `dd/MM/yyyy HH:mm`
- Contoh: `01/11/2025 08:00`

**Untuk Detail Package:**
- Pattern: `d MMMM yyyy`
- Contoh: `1 November 2025`

**Untuk Plan:**
- Pattern: `HH:mm, d MMMM yyyy`
- Contoh: `08:00, 1 November 2025`

---

## Error Codes

- **200 OK**: Success retrieve data
- **201 Created**: Success create package
- **400 Bad Request**: Validation error (e.g., endDate before startDate)
- **404 Not Found**: Package not found
- **500 Internal Server Error**: Server error
