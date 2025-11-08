package apap.ti._5.tour_package_2306240111_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PackageTest {

    private Package package_;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2025, 11, 8, 10, 0);
        endDate = LocalDateTime.of(2025, 11, 15, 12, 0);
        
        package_ = Package.builder()
                .id("PACK-USER123-001")
                .userId("USER123")
                .packageName("Bali Holiday Package")
                .quota(10)
                .price(50000000L)
                .status("Pending")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();
    }

    @Test
    void testPackageBuilder() {
        assertNotNull(package_);
        assertEquals("PACK-USER123-001", package_.getId());
        assertEquals("USER123", package_.getUserId());
        assertEquals("Bali Holiday Package", package_.getPackageName());
        assertEquals(10, package_.getQuota());
        assertEquals(50000000L, package_.getPrice());
        assertEquals("Pending", package_.getStatus());
        assertEquals(startDate, package_.getStartDate());
        assertEquals(endDate, package_.getEndDate());
        assertFalse(package_.getIsDeleted());
    }

    @Test
    void testPackageSetters() {
        package_.setPackageName("Updated Package");
        package_.setQuota(20);
        package_.setPrice(100000000L);
        package_.setStatus("Processed");
        package_.setIsDeleted(true);

        assertEquals("Updated Package", package_.getPackageName());
        assertEquals(20, package_.getQuota());
        assertEquals(100000000L, package_.getPrice());
        assertEquals("Processed", package_.getStatus());
        assertTrue(package_.getIsDeleted());
    }

    @Test
    void testPackageNoArgsConstructor() {
        Package emptyPackage = new Package();
        assertNull(emptyPackage.getId());
        assertNull(emptyPackage.getUserId());
    }

    @Test
    void testPackageAllArgsConstructor() {
        Package fullPackage = new Package(
                "PACK-USER456-002",
                "USER456",
                "Jakarta Package",
                5,
                30000000L,
                "Pending",
                startDate,
                endDate,
                false
        );

        assertEquals("PACK-USER456-002", fullPackage.getId());
        assertEquals("USER456", fullPackage.getUserId());
        assertEquals(5, fullPackage.getQuota());
    }

    @Test
    void testPackageToBuilder() {
        Package modifiedPackage = package_.toBuilder()
                .status("Processed")
                .quota(15)
                .build();

        assertEquals("Processed", modifiedPackage.getStatus());
        assertEquals(15, modifiedPackage.getQuota());
        assertEquals("Bali Holiday Package", modifiedPackage.getPackageName());
    }

    @Test
    void testPackageIsDeletedDefault() {
        Package newPackage = Package.builder()
                .id("PACK-USER789-003")
                .userId("USER789")
                .packageName("Default Package")
                .quota(1)
                .price(1000000L)
                .status("Pending")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertFalse(newPackage.getIsDeleted());
    }

    @Test
    void testPackageEquality() {
        Package package2 = Package.builder()
                .id("PACK-USER123-001")
                .userId("USER123")
                .packageName("Bali Holiday Package")
                .quota(10)
                .price(50000000L)
                .status("Pending")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();

        assertEquals(package_, package2);
    }

    @Test
    void testPackageHashCode() {
        Package package2 = Package.builder()
                .id("PACK-USER123-001")
                .userId("USER123")
                .packageName("Bali Holiday Package")
                .quota(10)
                .price(50000000L)
                .status("Pending")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();

        assertEquals(package_.hashCode(), package2.hashCode());
    }

    @Test
    void testPackageToString() {
        String packageString = package_.toString();
        assertNotNull(packageString);
        assertTrue(packageString.contains("PACK-USER123-001"));
        assertTrue(packageString.contains("Bali Holiday Package"));
    }

    @Test
    void testPackageDifferentStatuses() {
        Package pendingPackage = package_.toBuilder().status("Pending").build();
        Package processedPackage = package_.toBuilder().status("Processed").build();

        assertEquals("Pending", pendingPackage.getStatus());
        assertEquals("Processed", processedPackage.getStatus());
    }

    @Test
    void testPackageWithDifferentQuotas() {
        Package smallPackage = package_.toBuilder().quota(1).build();
        Package largePackage = package_.toBuilder().quota(100).build();

        assertEquals(1, smallPackage.getQuota());
        assertEquals(100, largePackage.getQuota());
    }

    @Test
    void testPackageWithNullableFields() {
        Package packageWithNulls = Package.builder()
                .id("PACK-NULL-001")
                .userId("NULLUSER")
                .packageName("Test")
                .quota(5)
                .price(5000000L)
                .status("Pending")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();

        assertEquals("PACK-NULL-001", packageWithNulls.getId());
    }
}
