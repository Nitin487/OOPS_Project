package oops.project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

// Enum for Vehicle Type
enum VehicleType {
    CAR, BIKE, TRUCK
}

// Interface Billable
interface Billable {
    double generateBill();
}

// Abstract User Class
class User {
    protected String name;
    protected String contact;

    public User(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }
}

// Admin Class
class Admin extends User {
    public Admin(String name, String contact) {
        super(name, contact);
    }

    public void addParkingLot(ParkingLot lot) {
        System.out.println("Parking lot added: " + lot.getLocation());
    }

    public void removeParkingLot(ParkingLot lot) {
        System.out.println("Parking lot removed: " + lot.getLocation());
    }
}

// Parking Lot Class
class ParkingLot implements Billable {
    private int id;
    private String location;
    private int capacity;
    private int availableSlots;

    public ParkingLot(int id, String location, int capacity) {
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.availableSlots = capacity;
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void occupySlot() {
        if (availableSlots > 0) {
            availableSlots--;
        } else {
            System.out.println("No available slots.");
        }
    }

    public void releaseSlot() {
        if (availableSlots < capacity) {
            availableSlots++;
        }
    }

    @Override
    public double generateBill() {
        return capacity * 10; // Dummy implementation
    }
}

// Vehicle Class
class Vehicle {
    protected String vehicleNumber;
    protected VehicleType vehicleType;
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;

    public Vehicle(String vehicleNumber, VehicleType vehicleType, LocalDateTime entryTime) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.entryTime = entryTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public long calculateDuration() {
        if (exitTime == null) {
            return 0;
        }
        return java.time.Duration.between(entryTime, exitTime).toMinutes();
    }

    public double generateBill() {
        long duration = calculateDuration();
        double ratePerHour = switch (vehicleType) {
            case CAR -> 10;
            case BIKE -> 5;
            case TRUCK -> 20;
        };
        return duration * (ratePerHour / 60); // Bill per minute
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
}

// Booking Class
class Booking {
    private String bookingId;
    private Vehicle vehicle;
    private int slotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Booking(String bookingId, Vehicle vehicle, int slotId, LocalDateTime startTime) {
        this.bookingId = bookingId;
        this.vehicle = vehicle;
        this.slotId = slotId;
        this.startTime = startTime;
    }

    public void releaseSlot(LocalDateTime endTime) {
        this.endTime = endTime;
        vehicle.setExitTime(endTime);
    }

    public double calculateBill() {
        return vehicle.generateBill();
    }

    public String getVehicleNumber() {
        return vehicle.getVehicleNumber();
    }
}

// Main Application Class
public class MainApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Booking> bookings = new ArrayList<>();
        ParkingLot lot = new ParkingLot(1, "Downtown", 10);

        System.out.println("Welcome to the Parking Management System");
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add a Vehicle and Reserve a Slot");
            System.out.println("2. Release a Slot and Calculate Bill");
            System.out.println("3. Check Available Slots");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    if (lot.getAvailableSlots() == 0) {
                        System.out.println("No available slots!");
                        break;
                    }

                    System.out.print("Enter vehicle number: ");
                    String vehicleNumber = scanner.nextLine();

                    // Check if vehicle number already exists
                    boolean vehicleExists = bookings.stream()
                            .anyMatch(booking -> booking.getVehicleNumber().equals(vehicleNumber));

                    if (vehicleExists) {
                        System.out.println("A vehicle with the same number already exists.");
                    } else {
                        System.out.print("Enter vehicle type (CAR/BIKE/TRUCK): ");
                        String type = scanner.nextLine().toUpperCase();

                        VehicleType vehicleType = VehicleType.valueOf(type);
                        Vehicle vehicle = new Vehicle(vehicleNumber, vehicleType, LocalDateTime.now());
                        int slotId = lot.getAvailableSlots(); // Assign slot based on availability
                        Booking booking = new Booking("B" + (bookings.size() + 1), vehicle, slotId, LocalDateTime.now());

                        bookings.add(booking);
                        lot.occupySlot();

                        System.out.println("Vehicle added and slot reserved.");
                    }
                }
                case 2 -> {
                    System.out.print("Enter vehicle number to release slot: ");
                    String vehicleNumber = scanner.nextLine();

                    Booking bookingToRelease = null;
                    for (Booking booking : bookings) {
                        if (booking.getVehicleNumber().equals(vehicleNumber)) {
                            bookingToRelease = booking;
                            break;
                        }
                    }

                    if (bookingToRelease == null) {
                        System.out.println("No booking found for the given vehicle number.");
                    } else {
                        bookingToRelease.releaseSlot(LocalDateTime.now());
                        lot.releaseSlot();

                        double bill = bookingToRelease.calculateBill();
                        System.out.println("Slot released. Bill amount: $" + bill);

                        bookings.remove(bookingToRelease); // Remove the booking from active list
                    }
                }
                case 3 -> {
                    System.out.println("Available slots: " + lot.getAvailableSlots());
                }
                case 4 -> {
                    System.out.println("Exiting the system. Thank you!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }
    }
}