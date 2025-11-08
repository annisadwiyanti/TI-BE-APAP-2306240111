package apap.ti._5.tour_package_2306240111_be;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.repository.ActivityRepository;
import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SpringBootApplication
public class TourPackage2306240111BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourPackage2306240111BeApplication.class, args);
    }

    @Bean
    @Order(1)
    public CommandLineRunner createDummyActivities(ActivityRepository activityRepository) {
        return args -> {
            System.out.println("=== CLEARING EXISTING ACTIVITIES ===");
            activityRepository.deleteAll();
            activityRepository.flush();
            
            System.out.println("=== GENERATING CONTROLLED DUMMY ACTIVITIES ===");
            
            Faker faker = new Faker(new Locale("id", "ID"));
            Random random = new Random(42);
            
            List<Activity> activities = new ArrayList<>();

            activities.addAll(generateFlightsControlled(faker, random));
            activities.addAll(generateAccommodationsControlled(faker, random));
            activities.addAll(generateVehicleRentalsControlled(faker, random));

            activityRepository.saveAll(activities);
            System.out.println("=== GENERATED " + activities.size() + " ACTIVITIES ===");
        };
    }

    private String[] getPopularProvinces() {
        return new String[] {
            "DKI Jakarta", "Bali", "Jawa Barat", "Jawa Timur", 
            "Daerah Istimewa Yogyakarta", "Sumatera Utara", "Sulawesi Selatan"
        };
    }

    private List<Activity> generateFlightsControlled(Faker faker, Random random) {
        List<Activity> flights = new ArrayList<>();
        
        String[] flightClasses = {"Economy", "Business", "First Class"};
        String[] popularProvinces = getPopularProvinces();
        
        int[][] monthYears = {{11, 2025}, {12, 2025}, {1, 2026}};
        
        int[] allHours = {6, 7, 8, 9, 10, 11, 12, 17, 18, 19, 20, 21};
        int[] allMinutes = {0, 30};
        int[] flightDurationMinutes = {120, 150, 180}; 
        
        int flightCounter = 0;
        
        System.out.println("Generating flights with optimized coverage...");
        
        for (String origin : popularProvinces) {
            for (String destination : popularProvinces) {
                if (origin.equals(destination)) continue;
                
                for (int[] monthYear : monthYears) {
                    int month = monthYear[0];
                    int year = monthYear[1];
                    int maxDay = getDaysInMonth(month);
                    
                    int[] flightDates = {1, 7, 20, 27};
                    
                    for (int day : flightDates) {
                        if (day > maxDay) continue;
                        
                        for (int hour : allHours) {
                            for (int minute : allMinutes) {
                                for (int durationMinutes : flightDurationMinutes) {
                                    String airline = faker.company().name() + " Airlines";
                                    String flightClass = flightClasses[flightCounter % flightClasses.length];
                                    
                                    LocalDateTime startDate = LocalDateTime.of(year, month, day, hour, minute);
                                    LocalDateTime endDate = startDate.plusMinutes(durationMinutes);
                                    
                                    if (endDate.getMonthValue() != month) {
                                        continue;
                                    }
                                    
                                    long basePrice = 700_000L + (random.nextInt(15) * 100_000L);
                                    if (flightClass.equals("Business")) basePrice *= 1.5;
                                    if (flightClass.equals("First Class")) basePrice *= 2;
                                    
                                    flights.add(Activity.builder()
                                            .id("ACT-FL-" + String.format("%05d", flightCounter + 1))
                                            .activityName(airline + " - " + origin + " to " + destination)
                                            .activityItem(airline + " " + flightClass)
                                            .capacity(50 + random.nextInt(100))
                                            .price(basePrice)
                                            .activityType("Flight")
                                            .startDate(startDate)
                                            .endDate(endDate)
                                            .startLocation(origin)
                                            .endLocation(destination)
                                            .build());
                                    
                                    flightCounter++;
                                    
                                    if (flightCounter >= 60000) {
                                        System.out.println("Reached safety limit for flights: " + flightCounter);
                                        System.out.println("Total flights generated: " + flightCounter);
                                        return flights;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        System.out.println("Total flights generated: " + flightCounter);
        return flights;
    }

    private List<Activity> generateAccommodationsControlled(Faker faker, Random random) {
        List<Activity> accommodations = new ArrayList<>();
        
        String[] roomTypes = {"Deluxe Room", "Suite"};
        String[] popularProvinces = getPopularProvinces();
        
        int[][] monthYears = {{11, 2025}, {12, 2025}, {1, 2026}};
        int[] checkInHours = {14, 15};
        int[] checkInMinutes = {0, 30}; // Include :00 and :30
        int[] stayDurations = {2, 3};
        
        int accCounter = 0;
        
        System.out.println("Generating accommodations with optimized coverage...");
        
        for (String province : popularProvinces) {
            for (int[] monthYear : monthYears) {
                int month = monthYear[0];
                int year = monthYear[1];
                int maxDay = getDaysInMonth(month);
                
                int[][] dateRanges = {{1, 7}, {20, 27}};
                
                for (int[] range : dateRanges) {
                    int rangeStart = range[0];
                    int rangeEnd = Math.min(range[1], maxDay);
                    
                    for (int checkInDay = rangeStart; checkInDay <= rangeEnd; checkInDay++) {
                        for (int checkInHour : checkInHours) {
                            for (int checkInMinute : checkInMinutes) {
                                for (int stayDuration : stayDurations) {
                                    for (String roomType : roomTypes) {
                                        String hotel = faker.company().name() + " Hotel";
                                        
                                        LocalDateTime startDate = LocalDateTime.of(year, month, checkInDay, checkInHour, checkInMinute);
                                        LocalDateTime endDate = startDate.plusDays(stayDuration);
                                        
                                        if (endDate.getMonthValue() != month) {
                                            continue;
                                        }
                                        
                                        long basePrice = 500_000L + (random.nextInt(30) * 100_000L);
                                        if (roomType.contains("Suite")) basePrice *= 1.5;
                                        
                                        accommodations.add(Activity.builder()
                                                .id("ACT-ACC-" + String.format("%05d", accCounter + 1))
                                                .activityName(hotel + " " + province)
                                                .activityItem(hotel + " - " + roomType)
                                                .capacity(10 + random.nextInt(30))
                                                .price(basePrice)
                                                .activityType("Accommodation")
                                                .startDate(startDate)
                                                .endDate(endDate)
                                                .startLocation(province)
                                                .endLocation(province)
                                                .build());
                                        
                                        accCounter++;
                                        
                                        if (accCounter >= 15000) {
                                            System.out.println("Reached safety limit for accommodations: " + accCounter);
                                            System.out.println("Total accommodations generated: " + accCounter);
                                            return accommodations;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        System.out.println("Total accommodations generated: " + accCounter);
        return accommodations;
    }

    private List<Activity> generateVehicleRentalsControlled(Faker faker, Random random) {
        List<Activity> rentals = new ArrayList<>();
        
        String[] vehicles = {"Toyota Avanza", "Honda CR-V"};
        String[] popularProvinces = getPopularProvinces();
        
        int[][] monthYears = {{11, 2025}, {12, 2025}, {1, 2026}};
        int[] pickUpHours = {8, 9};
        int[] pickUpMinutes = {0, 30}; // Include :00 and :30
        int[] rentalDurations = {2, 3};
        
        int rentalCounter = 0;
        
        System.out.println("Generating vehicle rentals with optimized coverage...");
        
        for (String province : popularProvinces) {
            for (int[] monthYear : monthYears) {
                int month = monthYear[0];
                int year = monthYear[1];
                int maxDay = getDaysInMonth(month);
                
                int[][] dateRanges = {{1, 7}, {20, 27}};
                
                for (int[] range : dateRanges) {
                    int rangeStart = range[0];
                    int rangeEnd = Math.min(range[1], maxDay);
                    
                    for (int pickUpDay = rangeStart; pickUpDay <= rangeEnd; pickUpDay++) {
                        for (int pickUpHour : pickUpHours) {
                            for (int pickUpMinute : pickUpMinutes) {
                                for (int rentalDuration : rentalDurations) {
                                    for (String vehicle : vehicles) {
                                        String rentalCompany = faker.company().name() + " Rent";
                                        
                                        int capacity = vehicle.contains("Avanza") ? 7 : 5;
                                        
                                        LocalDateTime startDate = LocalDateTime.of(year, month, pickUpDay, pickUpHour, pickUpMinute);
                                        LocalDateTime endDate = startDate.plusDays(rentalDuration);
                                        
                                        if (endDate.getMonthValue() != month) {
                                            continue;
                                        }
                                        
                                        long basePrice = 300_000L + (random.nextInt(15) * 50_000L);
                                        if (capacity >= 7) basePrice *= 1.3;
                                        
                                        rentals.add(Activity.builder()
                                                .id("ACT-VH-" + String.format("%05d", rentalCounter + 1))
                                                .activityName("Rental " + vehicle + " - " + province + " by " + rentalCompany)
                                                .activityItem(vehicle + " (" + capacity + " Seater)")
                                                .capacity(capacity)
                                                .price(basePrice)
                                                .activityType("Vehicle Rental")
                                                .startDate(startDate)
                                                .endDate(endDate)
                                                .startLocation(province)
                                                .endLocation(province)
                                                .build());
                                        
                                        rentalCounter++;
                                        
                                        if (rentalCounter >= 15000) {
                                            System.out.println("Reached safety limit for vehicle rentals: " + rentalCounter);
                                            System.out.println("Total vehicle rentals generated: " + rentalCounter);
                                            return rentals;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        System.out.println("Total vehicle rentals generated: " + rentalCounter);
        return rentals;
    }

    private int getDaysInMonth(int month) {
        switch (month) {
            case 2: return 28;
            case 4: case 6: case 9: case 11: return 30;
            default: return 31;
        }
    }
}