package apap.ti._5.tour_package_2306240111_be.repository;

import apap.ti._5.tour_package_2306240111_be.model.Package;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PackageRepositoryTest {

    @Autowired
    private PackageRepository packageRepository;

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
    void testSavePackage() {
        Package saved = packageRepository.save(package_);

        assertNotNull(saved);
        assertEquals("PACK-USER123-001", saved.getId());
        assertEquals("Bali Holiday Package", saved.getPackageName());
    }

    @Test
    void testFindPackageById() {
        packageRepository.save(package_);
        Optional<Package> found = packageRepository.findById("PACK-USER123-001");

        assertTrue(found.isPresent());
        assertEquals("Bali Holiday Package", found.get().getPackageName());
    }

    @Test
    void testFindPackageByIdNotFound() {
        Optional<Package> found = packageRepository.findById("NON-EXISTENT");

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdatePackage() {
        packageRepository.save(package_);
        package_.setPackageName("Updated Package");
        package_.setStatus("Processed");
        Package updated = packageRepository.save(package_);

        assertEquals("Updated Package", updated.getPackageName());
        assertEquals("Processed", updated.getStatus());
    }

    @Test
    void testDeletePackage() {
        packageRepository.save(package_);
        packageRepository.delete(package_);

        Optional<Package> found = packageRepository.findById("PACK-USER123-001");
        assertFalse(found.isPresent());
    }

    @Test
    void testCountByUserId() {
        Package pkg1 = package_.toBuilder().id("PACK-USER123-001").build();
        Package pkg2 = package_.toBuilder().id("PACK-USER123-002").build();
        Package pkg3 = Package.builder()
                .id("PACK-USER456-001")
                .userId("USER456")
                .packageName("Another Package")
                .quota(5)
                .price(30000000L)
                .status("Pending")
                .startDate(startDate)
                .endDate(endDate)
                .isDeleted(false)
                .build();

        packageRepository.save(pkg1);
        packageRepository.save(pkg2);
        packageRepository.save(pkg3);

        long count = packageRepository.countByUserId("USER123");
        assertEquals(2, count);
    }

    @Test
    void testFindAllPagination() {
        Package pkg1 = package_.toBuilder().id("PACK-USER123-001").build();
        Package pkg2 = package_.toBuilder().id("PACK-USER123-002").build();
        Package pkg3 = package_.toBuilder().id("PACK-USER123-003").build();

        packageRepository.save(pkg1);
        packageRepository.save(pkg2);
        packageRepository.save(pkg3);

        Pageable pageable = PageRequest.of(0, 2);
        Page<Package> page = packageRepository.findAll(pageable);

        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getContent().size());
    }

    @Test
    void testPackageIsDeletedFilter() {
        Package activePackage = package_.toBuilder().id("PACK-001").isDeleted(false).build();
        Package deletedPackage = package_.toBuilder().id("PACK-002").isDeleted(true).build();

        packageRepository.save(activePackage);
        packageRepository.save(deletedPackage);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Package> page = packageRepository.findAll(pageable);

        // Only non-deleted packages should be returned
        assertEquals(1, page.getContent().size());
    }

    @Test
    void testPackageWithDifferentStatuses() {
        Package pendingPackage = package_.toBuilder().id("PACK-001").status("Pending").build();
        Package processedPackage = package_.toBuilder().id("PACK-002").status("Processed").build();

        packageRepository.save(pendingPackage);
        packageRepository.save(processedPackage);

        assertEquals(2, packageRepository.count());
    }

    @Test
    void testMultipleUserPackages() {
        Package userAPackage1 = package_.toBuilder().id("PACK-USER-A-001").userId("USER-A").build();
        Package userAPackage2 = package_.toBuilder().id("PACK-USER-A-002").userId("USER-A").build();
        Package userBPackage1 = package_.toBuilder().id("PACK-USER-B-001").userId("USER-B").build();

        packageRepository.save(userAPackage1);
        packageRepository.save(userAPackage2);
        packageRepository.save(userBPackage1);

        long countUserA = packageRepository.countByUserId("USER-A");
        long countUserB = packageRepository.countByUserId("USER-B");

        assertEquals(2, countUserA);
        assertEquals(1, countUserB);
    }
}
