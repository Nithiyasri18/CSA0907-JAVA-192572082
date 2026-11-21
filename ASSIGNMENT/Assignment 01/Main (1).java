import java.time.*;
import java.util.*;

/** CSA0907 Assignment 1: unified parking management demonstration (Tasks 1-12). */
public class Main {
    interface Chargeable {
        double chargingFee(int kWh);
        default double chargingFee(int kWh, double discountPercent) { return chargingFee(kWh) * (1 - discountPercent / 100); }
        static boolean isValidUnits(int kWh) { return kWh >= 0; }
    }
    interface Pass { boolean covers(LocalDate date); }
    static class MonthlyPass implements Pass {
        private final YearMonth month;
        MonthlyPass(YearMonth month) { this.month = Objects.requireNonNull(month); }
        public boolean covers(LocalDate date) { return YearMonth.from(date).equals(month); }
    }
    static class OneDayPass implements Pass {
        static final double PRICE = 100;
        private final LocalDate day;
        OneDayPass(LocalDate day) { this.day = Objects.requireNonNull(day); }
        public boolean covers(LocalDate date) { return day.equals(date); }
    }
    static abstract class Vehicle {
        private final String regNo;
        Vehicle(String regNo) {
            if (regNo == null || regNo.trim().isEmpty()) throw new IllegalArgumentException("Registration required");
            this.regNo = regNo.trim().toUpperCase(Locale.ROOT);
        }
        public String getRegNo() { return regNo; }
        protected abstract double ratePerHour();
        public final int billableHours(int minutes) {
            if (minutes < 0) throw new IllegalArgumentException("Duration cannot be negative");
            return Math.min(8, (int)((minutes + 59L) / 60));
        }
        public double calculateFee(int minutes) { return billableHours(minutes) * ratePerHour(); }
        public double calculateFee(LocalTime entry, int minutes) { return calculateFee(minutes); }
        public double calculateFee(LocalTime in, LocalTime out) {
            long minutes = Duration.between(in, out).toMinutes();
            if (minutes < 0) minutes += 24 * 60;
            return calculateFee(in, Math.toIntExact(minutes));
        }
        @Override public boolean equals(Object o) { return o instanceof Vehicle && regNo.equals(((Vehicle)o).regNo); }
        @Override public int hashCode() { return regNo.hashCode(); }
        @Override public String toString() { return getClass().getSimpleName() + "[" + regNo + "]"; }
    }
    static class TwoWheeler extends Vehicle {
        TwoWheeler(String reg) { super(reg); }
        protected double ratePerHour() { return 10; }
    }
    static class Car extends Vehicle {
        Car(String reg) { super(reg); }
        protected double ratePerHour() { return 30; }
    }
    static class ElectricCar extends Car implements Chargeable {
        ElectricCar(String reg) { super(reg); }
        public double chargingFee(int kWh) {
            if (!Chargeable.isValidUnits(kWh)) throw new IllegalArgumentException("Negative charging units");
            return 12.0 * kWh;
        }
    }
    static class EBike extends Vehicle {
        static final double FLAT_FEE = 20;
        EBike(String reg) { super(reg); }
        protected double ratePerHour() { return 0; }
        @Override public double calculateFee(int minutes) { billableHours(minutes); return FLAT_FEE; }
    }
    static class DeliveryVan extends Car {
        DeliveryVan(String reg) { super(reg); }
        @Override public double calculateFee(LocalTime entry, int minutes) {
            Objects.requireNonNull(entry);
            boolean peak = !entry.isBefore(LocalTime.of(9,0)) && entry.isBefore(LocalTime.of(17,0));
            return calculateFee(minutes) * (peak ? 2 : 1);
        }
    }
    static class VehicleFactory {
        static Vehicle create(String type, String reg) {
            if (type == null) throw new IllegalArgumentException("Vehicle type required");
            switch (type.trim().toLowerCase(Locale.ROOT)) {
                case "bike": case "two-wheeler": return new TwoWheeler(reg);
                case "car": return new Car(reg);
                case "ev": return new ElectricCar(reg);
                case "ebike": return new EBike(reg);
                case "van": return new DeliveryVan(reg);
                default: throw new IllegalArgumentException("Unknown vehicle type: " + type);
            }
        }
    }
    static class ParkingFacility {
        private final String[][] slots;
        ParkingFacility() { this(new int[]{50,50,50}); }
        ParkingFacility(int[] sizes) {
            if (sizes == null || sizes.length == 0) throw new IllegalArgumentException("Floors required");
            slots = new String[sizes.length][];
            for (int f=0; f<sizes.length; f++) {
                if (sizes[f] < 0) throw new IllegalArgumentException("Negative slots");
                slots[f] = new String[sizes[f]];
            }
        }
        int[] allocate(String reg) {
            for (int f=0; f<slots.length; f++) for (int s=0; s<slots[f].length; s++)
                if (slots[f][s] == null) { slots[f][s] = reg; return new int[]{f,s}; }
            return null;
        }
        boolean release(int f, int s) {
            if (f<0 || f>=slots.length || s<0 || s>=slots[f].length || slots[f][s]==null) return false;
            slots[f][s] = null;
            return true;
        }
        int totalSlots() { int n=0; for (String[] floor:slots) n+=floor.length; return n; }
    }
    static class ParkingTicket {
        private static int counter;
        private final int ticketNo, floor, slot;
        private final Vehicle vehicle;
        private final LocalTime entry;
        ParkingTicket(Vehicle v, int floor, int slot, LocalTime entry) {
            this.vehicle = Objects.requireNonNull(v); this.entry = Objects.requireNonNull(entry);
            this.floor = floor; this.slot = slot; this.ticketNo = ++counter;
        }
        ParkingTicket(Vehicle v, int floor, int slot) { this(v,floor,slot,LocalTime.now()); }
        ParkingTicket(ParkingTicket t) {
            ticketNo=t.ticketNo; floor=t.floor; slot=t.slot; vehicle=t.vehicle; entry=t.entry;
        }
        int getTicketNo() { return ticketNo; }
        Vehicle getVehicle() { return vehicle; }
        int getFloor() { return floor; }
        int getSlot() { return slot; }
        LocalTime getEntry() { return entry; }
        @Override public String toString() { return "T"+ticketNo+"["+floor+","+slot+"]"; }
    }
    static class ParkingService {
        private final ParkingFacility facility;
        private final Map<String,ParkingTicket> active = new HashMap<>();
        private double totalCollection;
        ParkingService(ParkingFacility facility) { this.facility=facility; }
        ParkingTicket enter(String type, String reg, LocalTime time) {
            Vehicle v=VehicleFactory.create(type,reg);
            if (active.containsKey(v.getRegNo())) throw new IllegalStateException("Already parked: "+v.getRegNo());
            int[] pos=facility.allocate(v.getRegNo());
            if (pos==null) throw new IllegalStateException("Facility full");
            ParkingTicket ticket=new ParkingTicket(v,pos[0],pos[1],time);
            active.put(v.getRegNo(),ticket);
            return ticket;
        }
        double exit(String reg, int minutes, int kWh, Pass pass, LocalDate today) {
            String key=reg==null?"":reg.trim().toUpperCase(Locale.ROOT);
            ParkingTicket ticket=active.get(key);
            if (ticket==null) throw new IllegalStateException("No active ticket");
            Vehicle v=ticket.getVehicle();
            v.billableHours(minutes);
            if (kWh<0) throw new IllegalArgumentException("Negative charging units");
            double fee=(pass!=null && pass.covers(today))?0:v.calculateFee(ticket.getEntry(),minutes);
            if (v instanceof Chargeable) fee+=((Chargeable)v).chargingFee(kWh);
            active.remove(key);
            if (!facility.release(ticket.getFloor(),ticket.getSlot())) throw new IllegalStateException("Slot release failed");
            totalCollection+=fee;
            return fee;
        }
        double getTotalCollection() { return totalCollection; }
        int activeCount() { return active.size(); }
    }
    private static int tests=0, passed=0;
    static void check(String name, Object expected, Object actual) {
        tests++; boolean ok=Objects.equals(expected,actual); if(ok) passed++;
        System.out.printf("%02d %-38s expected=%-16s actual=%-16s %s%n",tests,name,expected,actual,ok?"PASS":"FAIL");
    }
    static void checkFee(String name,double expected,double actual) { check(name,expected,actual); }
    static void expectError(String name, Runnable action) {
        boolean threw=false; try { action.run(); } catch (IllegalArgumentException | IllegalStateException ex) { threw=true; }
        check(name,true,threw);
    }
    public static void main(String[] args) {
        System.out.println("CSA0907 - ASSIGNMENT 1 - SMART PARKING MANAGEMENT");
        System.out.println("Tasks 1-12: OOP, arrays, inheritance, polymorphism, interfaces, testing");
        LocalDate today=LocalDate.of(2026,11,21);
        ParkingFacility facility=new ParkingFacility(new int[]{2,3,1,2,1});
        ParkingService service=new ParkingService(facility);
        ParkingTicket bike=service.enter("bike","TN01A1",LocalTime.of(8,0));
        ParkingTicket car=service.enter("car","TN02B2",LocalTime.of(8,0));
        ParkingTicket ev=service.enter("ev","TN03C3",LocalTime.of(8,0));
        ParkingTicket passCar=service.enter("car","TN04D4",LocalTime.of(8,0));
        check("First free slot bike","T1[0,0]",bike.toString());
        check("First free slot car","T2[0,1]",car.toString());
        check("First free slot EV","T3[1,0]",ev.toString());
        check("First free slot fourth car","T4[1,1]",passCar.toString());
        checkFee("Bike 130 minutes",30,service.exit("TN01A1",130,0,null,today));
        checkFee("Car 130 minutes",90,service.exit("TN02B2",130,0,null,today));
        checkFee("EV 130 minutes plus 10 kWh",210,service.exit("TN03C3",130,10,null,today));
        checkFee("Monthly pass free parking",0,service.exit("TN04D4",130,0,new MonthlyPass(YearMonth.from(today)),today));
        checkFee("Daily collection",330,service.getTotalCollection());
        ParkingTicket reused=service.enter("car","TN05E5",LocalTime.NOON);
        check("Released slot reused","[0, 0]",Arrays.toString(new int[]{reused.getFloor(),reused.getSlot()}));
        checkFee("Zero minute fee",0,new Car("X1").calculateFee(0));
        checkFee("One minute rounds to one hour",30,new Car("X2").calculateFee(1));
        checkFee("60 minutes",30,new Car("X3").calculateFee(60));
        checkFee("61 minutes",60,new Car("X4").calculateFee(61));
        checkFee("Daily eight hour cap",240,new Car("X5").calculateFee(1000));
        expectError("Negative duration rejected",()->new Car("X6").calculateFee(-1));
        expectError("Duplicate entry rejected",()->service.enter("car","TN05E5",LocalTime.NOON));
        expectError("Exit without ticket rejected",()->service.exit("UNKNOWN",10,0,null,today));
        check("Different classes same reg equal",true,new Car("TN02B2").equals(new ElectricCar("TN02B2")));
        check("Same reg hash codes",true,new Car("TN02B2").hashCode()==new ElectricCar("TN02B2").hashCode());
        check("Ticket copy keeps number",reused.getTicketNo(),new ParkingTicket(reused).getTicketNo());
        check("Facility 5 floor capacity",9,facility.totalSlots());
        System.out.println("--- Task 12: extension and regression ---");
        checkFee("E-bike flat fee",20,new EBike("E1").calculateFee(300));
        checkFee("Van 10 AM peak",120,new DeliveryVan("V1").calculateFee(LocalTime.of(10,0),90));
        checkFee("Van 6 PM off peak",60,new DeliveryVan("V2").calculateFee(LocalTime.of(18,0),90));
        checkFee("Van 9 AM boundary",120,new DeliveryVan("V3").calculateFee(LocalTime.of(9,0),90));
        checkFee("Van 5 PM boundary",60,new DeliveryVan("V4").calculateFee(LocalTime.of(17,0),90));
        check("One day pass price",100.0,OneDayPass.PRICE);
        check("One day pass covers today",true,new OneDayPass(today).covers(today));
        check("Expired day pass not valid",false,new OneDayPass(today.minusDays(1)).covers(today));
        checkFee("Expired day pass parking fee",240,new Car("C1").calculateFee(600));
        expectError("E-bike negative minutes",()->new EBike("E2").calculateFee(-1));
        ParkingFacility small=new ParkingFacility(new int[]{2,3,1,2,1});
        for(int i=0;i<9;i++) check("Slot "+(i+1)+" allocated",true,small.allocate("N"+i)!=null);
        check("10th vehicle refused",null,small.allocate("FULL"));
        check("Expanded facility 170 slots",170,new ParkingFacility(new int[]{20,35,50,40,25}).totalSlots());
        System.out.printf("RESULT: %d/%d tests passed%n",passed,tests);
        if (passed!=tests) System.exit(1);
    }
}
