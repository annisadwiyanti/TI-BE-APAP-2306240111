# Backend Implementation - Fitur 2, 3, 4

## Summary

Implementasi backend untuk Tour Package Management System telah selesai untuk fitur 2, 3, dan 4.

## File yang Dibuat/Dimodifikasi

### 1. DTO (Data Transfer Objects)

#### Request DTOs
- **CreatePackageRequestDTO.java** (`restdto/request/packagereq/`)
  - Untuk create package
  - Fields: packageName, userId, quota, startDate, endDate

#### Response DTOs
- **PackageResponseDTO.java** (`restdto/response/pkg/`) - Sudah ada sebelumnya
  - Untuk list packages
  - Fields: id, packageName, userId, quota, price, status, startDate, endDate

- **PackageDetailResponseDTO.java** (`restdto/response/pkg/`) - BARU
  - Untuk detail package dengan plans
  - Fields: semua field package + list of plans

- **PlanResponseDTO.java** (`restdto/response/plan/`) - Sudah ada sebelumnya
  - Untuk detail plan dalam package

### 2. Service Layer

- **PackageRestService.java** (`restservice/`)
  - Interface untuk package service
  - Methods: getAllPackages(), getPackageById(), createPackage(), generatePackageId()

- **PackageRestServiceImpl.java** (`restservice/`)
  - Implementation dari PackageRestService
  - Business logic untuk fitur 2, 3, 4
  - Generate Package ID dengan format: PACK-{userId}-{3-digit-sequence}
  - Validasi: endDate harus setelah startDate

### 3. Repository

- **PackageRepository.java** (`repository/`) - UPDATED
  - Ditambahkan method: `countByUserId(String userId)`
  - Untuk menghitung jumlah package per user (untuk generate ID)

### 4. Controller

- **PackageRestController.java** (`restcontroller/`)
  - REST API endpoints untuk fitur 2, 3, 4
  - Base URL: `/api/package`

## API Endpoints

### Fitur 2: Read All Packages
```
GET /api/package
Query Parameters:
  - page: int (default: 0)
  - size: int (default: 10)

Response:
{
  "status": 200,
  "message": "Success retrieve all packages",
  "timestamp": "2025-11-05T...",
  "data": {
    "packages": [...],
    "currentPage": 0,
    "totalItems": 5,
    "totalPages": 1
  }
}
```

### Fitur 3: Detail Package
```
GET /api/package/{id}

Response:
{
  "status": 200,
  "message": "Success retrieve package detail",
  "timestamp": "2025-11-05T...",
  "data": {
    "id": "PACK-user001-001",
    "packageName": "Jakarta - Bali Adventure Package",
    "userId": "user001",
    "quota": 25,
    "price": 67500000,
    "status": "Pending",
    "startDate": "1 November 2025",
    "endDate": "7 November 2025",
    "plans": [...]
  }
}
```

### Fitur 4: Create Package

**Get Form Structure:**
```
GET /api/package/create

Response:
{
  "status": 200,
  "message": "Create package form structure",
  "timestamp": "2025-11-05T...",
  "data": {
    "packageName": "text input",
    "userId": "text input",
    "quota": "numeric input",
    "startDate": "date input",
    "endDate": "date input"
  }
}
```

**Submit Form:**
```
POST /api/package/create
Content-Type: application/json

Request Body:
{
  "packageName": "Jakarta - Bali Adventure Package",
  "userId": "user001",
  "quota": 25,
  "startDate": "2025-11-01T08:00",
  "endDate": "2025-11-07T08:00"
}

Response:
{
  "status": 201,
  "message": "Package created successfully",
  "timestamp": "2025-11-05T...",
  "data": {
    "id": "PACK-user001-001",
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

## Business Rules Implemented

1. **Package ID Generation**: Format `PACK-{userId}-{3-digit-sequence}`
   - Contoh: user001 membuat package pertama → PACK-user001-001
   - Package kedua → PACK-user001-002

2. **Initial Values**:
   - Status: "Pending"
   - Price: 0 (akan dihitung dari total price plans)
   - isDeleted: false

3. **Validation**:
   - EndDate tidak boleh lebih dahulu atau sama dengan startDate

## Testing

Untuk test API endpoints, gunakan tools seperti:
- Postman
- Thunder Client (VS Code extension)
- curl

Contoh test dengan curl:
```bash
# Get all packages
curl http://localhost:8080/api/package

# Get package detail
curl http://localhost:8080/api/package/PACK-user001-001

# Create package
curl -X POST http://localhost:8080/api/package/create \
  -H "Content-Type: application/json" \
  -d '{
    "packageName": "Test Package",
    "userId": "user001",
    "quota": 20,
    "startDate": "2025-12-01T08:00",
    "endDate": "2025-12-05T08:00"
  }'
```

## Next Steps

Untuk melengkapi aplikasi, berikut yang perlu dilakukan:
1. Update Package (Edit Package) - untuk mengubah data package
2. Process Package - untuk mengubah status dari Pending ke Processed
3. CRUD untuk Plan (Create, View, Update, Delete Plan dalam Package)
4. CRUD untuk Activity
5. Integration dengan frontend Vue.js
6. Error handling yang lebih detail
7. Validation dengan Spring Validation
8. Unit testing

## Catatan

- CORS sudah diaktifkan dengan `@CrossOrigin(origins = "*")` untuk development
- Untuk production, sebaiknya specify origin yang spesifik
- Format tanggal di response menggunakan pattern sesuai mockup
- Semua endpoint menggunakan BaseResponseDTO untuk consistency
