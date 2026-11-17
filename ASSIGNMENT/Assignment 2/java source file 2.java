import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        while (true) {
            System.out.println("\nCSA09 - Java Assignment 2 (CO2)");
        System.out.println("1. Q1/SharedLogDemo.java");
        System.out.println("2. Q1/TicketAlternatives.java");
        System.out.println("3. Q1/TicketBenchmark.java");
        System.out.println("4. Q2/ArrayPlaylist.java");
        System.out.println("5. Q2/PlaylistBenchmark.java");
        System.out.println("6. Q2/UpNextDemo.java");
        System.out.println("7. Q3/DedupAlternatives.java");
        System.out.println("8. Q3/HashContractTests.java");
        System.out.println("9. Q3/after_TODO/Attendance.java");
        System.out.println("10. Q3/before_TODO/Attendance.java");
        System.out.println("11. Q4/PharmacyBenchmark.java");
        System.out.println("12. Q4/PharmacyDemo.java");
        System.out.println("13. Q5/ComparatorTests.java");
        System.out.println("14. Q5/GenericsIntro.java");
        System.out.println("15. Q5/LeaderboardDemo.java");
        System.out.println("16. Q5/RankingExtras.java");
        System.out.println("17. Q6/AgingTriage.java");
        System.out.println("18. Q6/Triage.java");
        System.out.println("19. Q6/TriageBenchmark.java");
        System.out.println("20. Q6/TriageEight.java");
        System.out.println("21. Q7/AgingBenchmark.java");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            if (!scanner.hasNextLine()) return;
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                case 0: return;
                case 1: Unit1.SharedLogDemo.main(new String[0]); break;
                case 2: Unit2.TicketAlternatives.main(new String[0]); break;
                case 3: Unit3.TicketBenchmark.main(new String[0]); break;
                case 4: Unit4.ArrayPlaylist.main(new String[0]); break;
                case 5: Unit5.PlaylistBenchmark.main(new String[0]); break;
                case 6: Unit6.UpNextDemo.main(new String[0]); break;
                case 7: Unit7.DedupAlternatives.main(new String[0]); break;
                case 8: Unit8.HashContractTests.main(new String[0]); break;
                case 9: Unit9.Attendance.main(new String[0]); break;
                case 10: Unit10.Attendance.main(new String[0]); break;
                case 11: Unit11.PharmacyBenchmark.main(new String[0]); break;
                case 12: Unit12.PharmacyDemo.main(new String[0]); break;
                case 13: Unit13.ComparatorTests.main(new String[0]); break;
                case 14: Unit14.GenericsIntro.main(new String[0]); break;
                case 15: Unit15.LeaderboardDemo.main(new String[0]); break;
                case 16: Unit16.RankingExtras.main(new String[0]); break;
                case 17: Unit17.AgingTriage.main(new String[0]); break;
                case 18: Unit18.Triage.main(new String[0]); break;
                case 19: Unit19.TriageBenchmark.main(new String[0]); break;
                case 20: Unit20.TriageEight.main(new String[0]); break;
                case 21: Unit21.AgingBenchmark.main(new String[0]); break;
                default: System.out.println("Invalid choice.");
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    // Q1/SharedLogDemo.java
    static class Unit1 {
        public static class SharedLogDemo {
            static final int THREADS = 4, APPENDS = 200_000;
        
            static int test(Consumer<String> add,
                    IntSupplier length) throws InterruptedException {
                Thread[] ts = new Thread[THREADS];
                for (int i = 0; i < THREADS; i++) {
                    ts[i] = new Thread(() -> {
                        try {
                            for (int k = 0; k < APPENDS; k++) add.accept("x");
                        } catch (RuntimeException e) {
                            System.out.println("  a thread failed: " + e);
                        }
                    });
                    ts[i].start();
                }
                for (Thread t : ts) t.join();
                return length.getAsInt();
            }
        
            public static void main(String[] args) throws Exception {
                StringBuilder sb = new StringBuilder();
                StringBuffer sf = new StringBuffer();
                System.out.println("Expected length = " + THREADS * APPENDS);
                System.out.println("StringBuilder length = "
                        + test(sb::append, sb::length));
                System.out.println("StringBuffer  length = "
                        + test(sf::append, sf::length));
            }
        }
    }

    // Q1/TicketAlternatives.java
    static class Unit2 {
        public static class TicketAlternatives {
            public static void main(String[] args) {
                String[] f = {"PNR=2345678901", "NAME=Arun", "COACH=S4", "BERTH=32",
                        "FARE=745.50"};
        
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < f.length; i++) {
                    if (i > 0) b.append('|');
                    b.append(f[i]);
                }
                String viaBuilder = b.toString();
        
                String viaJoin = String.join("|", f);
        
                StringJoiner j = new StringJoiner("|");
                for (String x : f) j.add(x);
                String viaJoiner = j.toString();
        
                String viaFormat = String.format("%s|%s|%s|%s|%s", (Object[]) f);
        
                System.out.println(viaBuilder);
                System.out.println("join   equals builder: "
                        + viaJoin.equals(viaBuilder));
                System.out.println("joiner equals builder: "
                        + viaJoiner.equals(viaBuilder));
                System.out.println("format equals builder: "
                        + viaFormat.equals(viaBuilder));
            }
        }
    }

    // Q1/TicketBenchmark.java
    static class Unit3 {
        public static class TicketBenchmark {
        
            // Four ways to build the same ticket (every field already ends with '|')
            static String viaString(String[] f) {
                String s = "";
                for (String x : f) s = s + x;
                return s;
            }
        
            static String viaBuilder(String[] f) {
                StringBuilder b = new StringBuilder();
                for (String x : f) b.append(x);
                return b.toString();
            }
        
            static String viaBuffer(String[] f) {
                StringBuffer b = new StringBuffer();
                for (String x : f) b.append(x);
                return b.toString();
            }
        
            static String viaJoin(String[] f) {
                return String.join("", f);
            }
        
            static final String[] METHODS =
                {"String", "StringBuilder", "StringBuffer", "String.join"};
        
            static String build(int m, String[] f) {
                switch (m) {
                    case 0:  return viaString(f);
                    case 1:  return viaBuilder(f);
                    case 2:  return viaBuffer(f);
                    default: return viaJoin(f);
                }
            }
        
            // Distinct ticket data, generated once from a fixed seed
            static String[][] makeTickets(int tickets, int fields, int minLen,
                                          int maxLen, long seed) {
                Random r = new Random(seed);
                String[][] t = new String[tickets][fields];
                for (int i = 0; i < tickets; i++) {
                    for (int j = 0; j < fields; j++) {
                        int len = minLen + r.nextInt(maxLen - minLen + 1);
                        StringBuilder v = new StringBuilder("F" + j + "=");
                        for (int k = 0; k < len; k++) {
                            v.append((char) ('A' + r.nextInt(26)));
                        }
                        t[i][j] = v.append('|').toString();
                    }
                }
                return t;
            }
        
            // One pass = build every ticket once; the returned length is consumed
            static long pass(int m, String[][] t) {
                long chars = 0;
                for (String[] f : t) chars += build(m, f).length();
                return chars;
            }
        
            static long allocatedBytes() {
                return ((com.sun.management.ThreadMXBean)
                        ManagementFactory.getThreadMXBean())
                                .getCurrentThreadAllocatedBytes();
            }
        
            static long heapUsed() {
                Runtime rt = Runtime.getRuntime();
                return rt.totalMemory() - rt.freeMemory();
            }
        
            static final int WARMUP = 3, TRIALS = 5;
        
            public static void main(String[] args) throws Exception {
                String[] names = {"A", "B", "C"};
                // tickets, fields, minLen, maxLen, passes per trial
                int[][] cfg = {{1, 6, 4, 10, 20000},
                               {10000, 50, 5, 12, 1},
                               {1000, 200, 5, 12, 1}};
                long checksum = 0;
                try (PrintWriter csv = new PrintWriter("q1_raw.csv")) {
                    csv.println("workload,method,trial,tickets,fields,passes,appends,"
                        + "total_ns,ns_per_ticket,alloc_bytes_per_ticket");
                    for (int w = 0; w < cfg.length; w++) {
                        int tk = cfg[w][0], fl = cfg[w][1], passes = cfg[w][4];
                        String[][] t = makeTickets(tk, fl, cfg[w][2], cfg[w][3], 42L);
        
                        // correctness: all methods must give identical tickets
                        boolean same = true;
                        Set<String> distinct = new HashSet<>();
                        for (String[] f : t) {
                            String ref = viaBuilder(f);
                            distinct.add(ref);
                            for (int m = 0; m < 4; m++) same &= ref.equals(build(m, f));
                        }
                        System.out.println("Workload " + names[w] + ": tickets=" + tk
                            + ", fields=" + fl + ", distinct tickets=" + distinct.size()
                            + ", outputs identical=" + same);
        
                        for (int m = 0; m < 4; m++) {           // warm-up
                            for (int i = 0; i < WARMUP * passes; i++) {
                                checksum += pass(m, t);
                            }
                        }
        
                        System.gc();
                        long heapBefore = heapUsed();
                        double[][] ns = new double[4][TRIALS];
                        double[][] bytes = new double[4][TRIALS];
                        for (int trial = 0; trial < TRIALS; trial++) {
                            for (int k = 0; k < 4; k++) {
                                int m = (trial + k) % 4;       // rotate the order
                                long a0 = allocatedBytes(), t0 = System.nanoTime();
                                for (int p = 0; p < passes; p++) checksum += pass(m, t);
                                long total = System.nanoTime() - t0;
                                long alloc = allocatedBytes() - a0;
                                double per = (double) tk * passes;
                                ns[m][trial] = total / per;
                                bytes[m][trial] = alloc / per;
                                String row = names[w] + "," + METHODS[m] + ","
                                        + (trial + 1);
                                row += "," + tk + "," + fl + "," + passes;
                                row += "," + (long) tk * fl * passes + "," + total;
                                row += "," + ns[m][trial] + "," + bytes[m][trial];
                                csv.println(row);
                            }
                        }
                        long heapAfter = heapUsed();
                        System.out.println("  heap used before=" + heapBefore / 1024
                            + " KB, after=" + heapAfter / 1024 + " KB (approximate)");
                        for (int m = 0; m < 4; m++) {
                            double sn = 0, sb = 0;
                            for (int i = 0; i < TRIALS; i++) {
                                sn += ns[m][i];
                                sb += bytes[m][i];
                            }
                            System.out.printf("  %-14s avg ns/ticket = %12.1f"
                                + "   avg bytes/ticket = %10.0f%n",
                                METHODS[m], sn / TRIALS, sb / TRIALS);
                        }
                    }
                }
                System.out.println("checksum (ignore) = " + checksum);
            }
        }
    }

    // Q2/ArrayPlaylist.java
    static class Unit4 {
        public static class ArrayPlaylist {
            private String[] songs = new String[4];
            private int size = 0;
        
            void add(String s) {                    // O(1) amortised: copy only when full
                if (size == songs.length) songs = Arrays.copyOf(songs, size * 2);
                songs[size++] = s;
            }
        
            void insert(int index, String s) {      // O(n - index): shift the tail right
                if (size == songs.length) songs = Arrays.copyOf(songs, size * 2);
                for (int i = size; i > index; i--) songs[i] = songs[i - 1];
                songs[index] = s;
                size++;
            }
        
            String remove(int index) {              // O(n - index): shift the tail left
                String removed = songs[index];
                for (int i = index; i < size - 1; i++) songs[i] = songs[i + 1];
                songs[--size] = null;
                return removed;
            }
        
            String get(int k) { return songs[k]; }  // O(1)
        
            public static void main(String[] args) {
                ArrayPlaylist p = new ArrayPlaylist();
                p.add("A"); p.add("B"); p.add("C");
                p.insert(0, "Opening");
                p.insert(2, "Middle");
                System.out.println(Arrays.toString(Arrays.copyOf(p.songs, p.size)));
                System.out.println("removed " + p.remove(0) + ", get(2) = " + p.get(2));
                System.out.println(Arrays.toString(Arrays.copyOf(p.songs, p.size)));
            }
        }
    }

    // Q2/PlaylistBenchmark.java
    static class Unit5 {
        public static class PlaylistBenchmark {
            static final int OPS = 500, TRIALS = 5, WARMUP = 2;
            static final String[] STRUCTS = {"ArrayList", "LinkedList", "ArrayDeque"};
            static final String[] OPNAMES = {"addEnd", "addFront", "addMiddle",
                    "removeFront",
                "removeMiddle", "getKth", "upNext", "iterate"};
            // consumed so work is not optimised away
            static long sink;
        
            static Collection<String> make(int s, String[] songs) {
                List<String> base = Arrays.asList(songs);
                if (s == 0) return new ArrayList<>(base);
                if (s == 1) return new LinkedList<>(base);
                return new ArrayDeque<>(base);
            }
        
            // ArrayDeque has no index, so it only takes part in end/front/iterate tests
            static boolean supported(String op, int s) {
                if (s != 2) return true;
                return !(op.equals("addMiddle") || op.equals("removeMiddle")
                        || op.equals("getKth"));
            }
        
            // returns the number of operations performed
            static int run(String op, Collection<String> c, int[] idx, int passes) {
                List<String> list = (c instanceof List) ? (List<String>) c : null;
                Deque<String> dq = (c instanceof List) ? null : (Deque<String>) c;
                switch (op) {
                    case "addEnd":
                        for (int i = 0; i < OPS; i++) {
                            if (list != null) list.add("NEW"); else dq.addLast("NEW");
                        }
                        return OPS;
                    case "addFront":
                        for (int i = 0; i < OPS; i++) {
                            if (list != null) list.add(0, "NEW"); else dq.addFirst("NEW");
                        }
                        return OPS;
                    case "addMiddle":
                        for (int i = 0; i < OPS; i++) list.add(list.size() / 2, "NEW");
                        return OPS;
                    case "removeFront":
                        for (int i = 0; i < OPS; i++) {
                            String first = (list != null)
                                    ? list.remove(0) : dq.removeFirst();
                            sink += first.length();
                        }
                        return OPS;
                    case "removeMiddle":
                        for (int i = 0; i < OPS; i++) {
                            sink += list.remove(list.size() / 2).length();
                        }
                        return OPS;
                    case "getKth":
                        for (int i = 0; i < OPS; i++) sink += list.get(idx[i]).length();
                        return OPS;
                    // add at end, add at front, play (remove) front
                    case "upNext":
                        for (int i = 0; i < OPS; i++) {
                            if (list != null) {
                                list.add("NEW"); list.add(0, "URGENT");
                                sink += list.remove(0).length();
                            } else {
                                dq.addLast("NEW"); dq.addFirst("URGENT");
                                sink += dq.removeFirst().length();
                            }
                        }
                        return OPS;
                    // iterate: one operation = one full pass
                    default:
                        for (int p = 0; p < passes; p++)
                            for (String s : c) sink += s.length();
                        return passes;
                }
            }
        
            public static void main(String[] args) throws Exception {
                int[] sizes = {1_000, 10_000, 100_000};
                try (PrintWriter csv = new PrintWriter("q2_raw.csv")) {
                    csv.println("size,structure,operation,trial,ops,total_ns");
                    for (int size : sizes) {
                        String[] songs = new String[size];
                        for (int i = 0; i < size; i++) songs[i] = "Song" + i;
                        Random r = new Random(7);
                        int[] idx = new int[OPS];
                        for (int i = 0; i < OPS; i++) idx[i] = r.nextInt(size);
                        int passes = Math.max(1, 1_000_000 / size);
        
                        for (String op : OPNAMES) {
                            long[][] total = new long[3][TRIALS];
                            int ops = 0;
                            for (int s = 0; s < 3; s++)                       // warm-up
                                if (supported(op, s))
                                    for (int w = 0; w < WARMUP; w++)
                                        run(op, make(s, songs), idx, passes);
                            for (int t = 0; t < TRIALS; t++) {
                                for (int s = 0; s < 3; s++) {
                                    if (!supported(op, s)) continue;
                                    // fresh, untimed
                                    Collection<String> c = make(s, songs);
                                    long t0 = System.nanoTime();
                                    ops = run(op, c, idx, passes);
                                    total[s][t] = System.nanoTime() - t0;
                                    csv.println(size + "," + STRUCTS[s] + "," + op + ","
                                        + (t + 1) + "," + ops + "," + total[s][t]);
                                }
                            }
                            StringBuilder line =
                                new StringBuilder(String.format("%7d %-12s", size, op));
                            for (int s = 0; s < 3; s++) {
                                if (!supported(op, s)) {
                                    line.append(String.format(" %14s", "n/a"));
                                    continue;
                                }
                                double sum = 0;
                                for (long v : total[s]) sum += v;
                                line.append(String.format(" %14.1f", sum / TRIALS / ops));
                            }
                            System.out.println(line);
                        }
                    }
                }
                System.out.println("columns: ArrayList  LinkedList  ArrayDeque"
                        + "  (average ns per operation)");
                System.out.println("sink (ignore) = " + sink);
            }
        }
    }

    // Q2/UpNextDemo.java
    static class Unit6 {
        public static class UpNextDemo {
            public static void main(String[] args) {
                Deque<String> upNext = new ArrayDeque<>();
                upNext.addLast("Track B");     // added at the back of the queue
                upNext.addLast("Track C");
                upNext.addFirst("Urgent A");   // "play next" goes to the front
                System.out.println("Queue: " + upNext);
                String playing = upNext.removeFirst();
                System.out.println("Now playing: " + playing);
                System.out.println("Queue: " + upNext);
            }
        }
    }

    // Q3/DedupAlternatives.java
    static class Unit7 {
        public static class DedupAlternatives {
            // no equals()/hashCode() needed here
            static class Student {
                final String regNo, name;
                Student(String r, String n) { regNo = r; name = n; }
                public String toString() { return regNo + " " + name; }
            }
        
            public static void main(String[] args) {
                String[][] scans = {{"192311001", "Arun"}, {"192311002", "Divya"},
                        {"192311001", "Arun"}};
        
                TreeSet<Student> tree =
                        new TreeSet<>(Comparator.comparing((Student s) -> s.regNo));
                Map<String, Student> map = new HashMap<>();
                for (String[] sc : scans) {
                    Student s = new Student(sc[0], sc[1]);
                    // comparator result 0 => duplicate, ignored
                    tree.add(s);
                    map.putIfAbsent(s.regNo, s);           // existing key => ignored
                }
                System.out.println("TreeSet size = " + tree.size() + " " + tree);
                System.out.println("HashMap size = " + map.size() + " " + map.values());
            }
        }
    }

    // Q3/HashContractTests.java
    static class Unit8 {
        public static class HashContractTests {
        
            // equals() on regNo, hashCode() NOT overridden (the junior developer's class)
            static class BrokenStudent {
                final String regNo, name;
                BrokenStudent(String r, String n) { regNo = r; name = n; }
                @Override public boolean equals(Object o) {
                    if (!(o instanceof BrokenStudent)) return false;
                    return regNo.equals(((BrokenStudent) o).regNo);
                }
            }
        
            // corrected: hashCode() uses the same field as equals()
            static class GoodStudent {
                final String regNo, name;
                GoodStudent(String r, String n) { regNo = r; name = n; }
                @Override public boolean equals(Object o) {
                    if (!(o instanceof GoodStudent)) return false;
                    return regNo.equals(((GoodStudent) o).regNo);
                }
                @Override public int hashCode() { return regNo.hashCode(); }
            }
        
            // hashCode() is correct, but the field it uses is NOT final
            static class MutableStudent {
                String regNo; final String name;
                MutableStudent(String r, String n) { regNo = r; name = n; }
                @Override public boolean equals(Object o) {
                    if (!(o instanceof MutableStudent)) return false;
                    return regNo.equals(((MutableStudent) o).regNo);
                }
                @Override public int hashCode() { return regNo.hashCode(); }
            }
        
            public static void main(String[] args) {
                // Test 1: the same object is scanned twice (predicted size 2 for both
                // classes)
                Set<BrokenStudent> b1 = new HashSet<>();
                BrokenStudent arunB = new BrokenStudent("192311001", "Arun");
                b1.add(arunB);
                b1.add(new BrokenStudent("192311002", "Divya"));
                b1.add(arunB);                                     // same reference again
                Set<GoodStudent> g1 = new HashSet<>();
                GoodStudent arunG = new GoodStudent("192311001", "Arun");
                g1.add(arunG);
                g1.add(new GoodStudent("192311002", "Divya"));
                g1.add(arunG);
                System.out.println("Test 1  broken class: " + b1.size()
                        + "   corrected class: " + g1.size());
        
                // Test 2: two DIFFERENT objects with the same regNo (predicted 3 vs 2)
                Set<BrokenStudent> b2 = new HashSet<>();
                b2.add(new BrokenStudent("192311001", "Arun"));
                b2.add(new BrokenStudent("192311002", "Divya"));
                b2.add(new BrokenStudent("192311001", "Arun"));
                Set<GoodStudent> g2 = new HashSet<>();
                g2.add(new GoodStudent("192311001", "Arun"));
                g2.add(new GoodStudent("192311002", "Divya"));
                g2.add(new GoodStudent("192311001", "Arun"));
                System.out.println("Test 2  broken class: " + b2.size()
                        + "   corrected class: " + g2.size());
                BrokenStudent x = new BrokenStudent("192311001", "Arun");
                BrokenStudent y = new BrokenStudent("192311001", "Arun");
                System.out.println("        broken: equals=" + x.equals(y)
                    + ", same hashCode=" + (x.hashCode() == y.hashCode()));
        
                // Test 3: regNo is changed AFTER the object is stored in the set
                Set<MutableStudent> m = new HashSet<>();
                MutableStudent s = new MutableStudent("192311001", "Arun");
                m.add(s);
                System.out.println("Test 3  before change: size=" + m.size()
                        + ", contains=" + m.contains(s));
                s.regNo = "192311999";
                System.out.println("        after change : size=" + m.size()
                        + ", contains=" + m.contains(s)
                    + ", remove=" + m.remove(s));
                m.add(new MutableStudent("192311001", "Arun"));
                System.out.println("        add original regNo again: size=" + m.size());
            }
        }
    }

    // Q3/after_TODO/Attendance.java
    static class Unit9 {
        static class Student {
            private final String regNo;
            private final String name;
        
            Student(String regNo, String name) {
                this.regNo = regNo;
                this.name = name;
            }
        
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Student)) return false;
                return regNo.equals(((Student) o).regNo);
            }
        
            // TODO 1 (completed): same field as equals()
            @Override
            public int hashCode() {
                return regNo.hashCode();
            }
        
            @Override
            public String toString() {
                return regNo + " " + name;
            }
        }
        
        public static class Attendance {
            public static void main(String[] args) {
                Set<Student> present = new HashSet<>();
                present.add(new Student("192311001", "Arun")); // scanned at Gate 1
                present.add(new Student("192311002", "Divya"));
                present.add(new Student("192311001", "Arun")); // scanned again at Gate 3
                System.out.println("Unique students: " + present.size());
            }
        }
    }

    // Q3/before_TODO/Attendance.java
    static class Unit10 {
        static class Student {
            private final String regNo;
            private final String name;
        
            Student(String regNo, String name) {
                this.regNo = regNo;
                this.name = name;
            }
        
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Student)) return false;
                return regNo.equals(((Student) o).regNo);
            }
        
            // TODO 1: Override hashCode() so that it is consistent with equals()
        
            @Override
            public String toString() {
                return regNo + " " + name;
            }
        }
        
        public static class Attendance {
            public static void main(String[] args) {
                Set<Student> present = new HashSet<>();
                present.add(new Student("192311001", "Arun")); // scanned at Gate 1
                present.add(new Student("192311002", "Divya"));
                present.add(new Student("192311001", "Arun")); // scanned again at Gate 3
                System.out.println("Unique students: " + present.size());
            }
        }
    }

    // Q4/PharmacyBenchmark.java
    static class Unit11 {
        public static class PharmacyBenchmark {
        
            static class Medicine {
                final String code, name;
                // seq = position in which it was added
                final int seq, stock;
                Medicine(String code, String name, int seq, int stock) {
                    this.code = code; this.name = name; this.seq = seq;
                    this.stock = stock;
                }
                String line() { return code + " " + name + " " + stock; }
            }
        
            static final int LOOKUPS = 100_000, INSERTS = 1_000, TRIALS = 5, WARMUP = 2;
            static final Comparator<Medicine> BY_CODE = Comparator.comparing(m -> m.code);
            static final Comparator<Medicine> BY_SEQ =
                    Comparator.comparingInt(m -> m.seq);
            static final String[] KINDS = {"HashMap", "TreeMap", "LinkedHashMap",
                    "SortedArray"};
            static long sink;
            static PrintWriter csv;
            static int size;
        
            // Array kept sorted by code and searched with Arrays.binarySearch()
            static class SortedArray {
                final String[] codes; final Medicine[] meds; int n;
                SortedArray(Medicine[] src, int spare) {
                    Medicine[] c = src.clone();
                    Arrays.sort(c, BY_CODE);
                    codes = new String[c.length + spare];
                    meds = new Medicine[c.length + spare];
                    for (int i = 0; i < c.length; i++) {
                        codes[i] = c[i].code;
                        meds[i] = c[i];
                    }
                    n = c.length;
                }
                Medicine get(String code) {
                    int i = Arrays.binarySearch(codes, 0, n, code);
                    return i >= 0 ? meds[i] : null;
                }
                void insert(Medicine m) {             // shifts every later element: O(n)
                    int i = -Arrays.binarySearch(codes, 0, n, m.code) - 1;
                    System.arraycopy(codes, i, codes, i + 1, n - i);
                    System.arraycopy(meds, i, meds, i + 1, n - i);
                    codes[i] = m.code; meds[i] = m; n++;
                }
            }
        
            static Medicine[] makeMedicines(int n, String suffix, long seed) {
                List<Integer> ids = new ArrayList<>();
                for (int i = 0; i < n; i++) ids.add(i);
                Collections.shuffle(ids, new Random(seed));   // added order != code order
                Medicine[] m = new Medicine[n];
                for (int i = 0; i < n; i++) {
                    int id = ids.get(i);
                    String code = String.format("M%06d%s", id, suffix);
                    m[i] = new Medicine(code, "Drug" + id, i, 10 + id % 90);
                }
                return m;
            }
        
            static Map<String, Medicine> newMap(int kind) {
                if (kind == 0) return new HashMap<>();
                if (kind == 1) return new TreeMap<>();
                return new LinkedHashMap<>();
            }
        
            static Map<String, Medicine> fill(int kind, Medicine[] data) {
                Map<String, Medicine> m = newMap(kind);
                for (Medicine d : data) m.put(d.code, d);
                return m;
            }
        
            // builds the report text (no printing)
            static int render(Collection<Medicine> c) {
                StringBuilder sb = new StringBuilder();
                for (Medicine m : c) sb.append(m.line()).append('\n');
                return sb.length();
            }
        
            // report in code order: free for TreeMap, otherwise copy + sort
            static Collection<Medicine> codeOrder(Map<String, Medicine> m) {
                if (m instanceof TreeMap) return m.values();
                List<Medicine> l = new ArrayList<>(m.values());
                l.sort(BY_CODE);
                return l;
            }
        
            // report in insertion order: free for LinkedHashMap, otherwise copy + sort by
            // seq
            static Collection<Medicine> insertionOrder(Map<String, Medicine> m) {
                if (m instanceof LinkedHashMap) return m.values();
                List<Medicine> l = new ArrayList<>(m.values());
                l.sort(BY_SEQ);
                return l;
            }
        
            static boolean inOrder(Collection<Medicine> c, Comparator<Medicine> cmp) {
                Medicine prev = null;
                for (Medicine m : c) {
                    if (prev != null && cmp.compare(prev, m) > 0) return false;
                    prev = m;
                }
                return true;
            }
        
            // setup is untimed; the Runnable it returns is the timed work
            static void bench(String kind, String op, int ops, Supplier<Runnable> setup) {
                for (int w = 0; w < WARMUP; w++) setup.get().run();
                double sum = 0;
                for (int t = 1; t <= TRIALS; t++) {
                    Runnable task = setup.get();
                    long t0 = System.nanoTime();
                    task.run();
                    long ns = System.nanoTime() - t0;
                    sum += ns;
                    csv.println(size + "," + kind + "," + op + "," + t + ","
                            + ops + "," + ns);
                }
                System.out.printf("%7d %-14s %-18s avg total = %14.0f ns"
                        + "  (%10.1f ns/op)%n",
                        size, kind, op, sum / TRIALS, sum / TRIALS / ops);
            }
        
            public static void main(String[] args) throws Exception {
                csv = new PrintWriter("q4_raw.csv");
                csv.println("size,structure,operation,trial,ops,total_ns");
                for (int n : new int[]{1_000, 10_000, 100_000}) {
                    size = n;
                    Medicine[] data = makeMedicines(n, "", 11L);
                    Medicine[] extra = Arrays.copyOf(makeMedicines(n, "X", 22L), INSERTS);
                    Random r = new Random(33);
                    String[] keys = new String[LOOKUPS];
                    for (int i = 0; i < LOOKUPS; i++) keys[i] = data[r.nextInt(n)].code;
        
                    for (int k = 0; k < 4; k++) {
                        final int kind = k;
                        String name = KINDS[k];
                        if (k < 3) {
                            Map<String, Medicine> m = fill(k, data);
                            System.out.println("order check " + name + " n=" + n
                                + ": natural code order=" + inOrder(m.values(), BY_CODE)
                                + ", natural insertion order="
                                        + inOrder(m.values(), BY_SEQ)
                                + ", reports correct=" + (inOrder(codeOrder(m), BY_CODE)
                                && inOrder(insertionOrder(m), BY_SEQ)));
                            bench(name, "build(put all)", n,
                                    () -> () -> sink += fill(kind, data).size());
                            bench(name, "lookup x100000", LOOKUPS, () -> () -> {
                                for (String key : keys) sink += m.get(key).stock;
                            });
                            bench(name, "insert x1000", INSERTS, () -> {
                                Map<String, Medicine> mm = fill(kind, data);
                                return () -> {
                                    for (Medicine e : extra) mm.put(e.code, e);
                                };
                            });
                            bench(name, "report code order", n,
                                    () -> () -> sink += render(codeOrder(m)));
                            bench(name, "report added order", n,
                                    () -> () -> sink += render(insertionOrder(m)));
                        } else {
                            SortedArray a = new SortedArray(data, 0);
                            List<Medicine> view = Arrays.asList(a.meds).subList(0, a.n);
                            System.out.println("order check SortedArray n=" + n
                                    + ": natural code order="
                                + inOrder(view, BY_CODE) + ", natural insertion order="
                                        + inOrder(view, BY_SEQ));
                            bench(name, "build(put all)", n,
                                    () -> () -> sink += new SortedArray(data, 0).n);
                            bench(name, "lookup x100000", LOOKUPS, () -> () -> {
                                for (String key : keys) sink += a.get(key).stock;
                            });
                            bench(name, "insert x1000", INSERTS, () -> {
                                SortedArray s = new SortedArray(data, INSERTS);
                                return () -> { for (Medicine e : extra) s.insert(e); };
                            });
                            bench(name, "report code order", n,
                                    () -> () -> sink += render(view));
                            bench(name, "report added order", n, () -> () -> {
                                List<Medicine> l = new ArrayList<>(view);
                                l.sort(BY_SEQ);
                                sink += render(l);
                            });
                        }
                    }
                }
                csv.close();
                System.out.println("sink (ignore) = " + sink);
            }
        }
    }

    // Q4/PharmacyDemo.java
    static class Unit12 {
        static class Medicine {
            final String code, name;
            int stock;
        
            Medicine(String code, String name, int stock) {
                this.code = code;
                this.name = name;
                this.stock = stock;
            }
        
            public String toString() {
                return code + " " + name + " stock=" + stock;
            }
        }
        
        public static class PharmacyDemo {
            public static void main(String[] args) {
                Map<String, Medicine> sorted = new TreeMap<>();
                Map<String, Medicine> added = new LinkedHashMap<>();
                Medicine[] data = {
                    new Medicine("M103", "Paracetamol", 120),
                    new Medicine("M101", "Saline", 45),
                    new Medicine("M102", "Antiseptic", 30)
                };
                for (Medicine m : data) {
                    sorted.put(m.code, m);
                    added.put(m.code, m);
                }
                System.out.println("Lookup: " + sorted.get("M102"));
                System.out.println("Code order: " + sorted.values());
                System.out.println("Insertion order: " + added.values());
            }
        }
    }

    // Q5/ComparatorTests.java
    static class Unit13 {
        public static class ComparatorTests {
            static class Team {
                final String name; final int points, goalDiff;
                Team(String n, int p, int g) { name = n; points = p; goalDiff = g; }
                public String toString() { return name + "(" + points + "," + goalDiff
                        + ")"; }
            }
        
            static final Comparator<Team> GOALDIFF_DESC =
                Comparator.comparingInt((Team t) -> t.goalDiff).reversed();
        
            // Correct: points desc, goal difference desc, name A-Z (no subtraction)
            static final Comparator<Team> CORRECT =
                Comparator.comparingInt((Team t) -> t.points).reversed()
                    .thenComparing(GOALDIFF_DESC)
                    .thenComparing(t -> t.name);
        
            // Faulty 1: compares points only (ignores both tie-breakers)
            static final Comparator<Team> POINTS_ONLY =
                (a, b) -> Integer.compare(b.points, a.points);
        
            // Faulty 2: points and goal difference, but no name tie-breaker
            static final Comparator<Team> NO_NAME =
                Comparator.comparingInt((Team t) -> t.points).reversed()
                .thenComparing(GOALDIFF_DESC);
        
            // Faulty 3: goal difference compared by subtraction (can overflow)
            static final Comparator<Team> SUBTRACT = (a, b) ->
                b.points != a.points ? Integer.compare(b.points, a.points)
                : b.goalDiff - a.goalDiff != 0 ? b.goalDiff - a.goalDiff
                : a.name.compareTo(b.name);
        
            static List<Team> sorted(List<Team> in, Comparator<Team> c) {
                List<Team> copy = new ArrayList<>(in);
                copy.sort(c);                                   // List.sort is stable
                return copy;
            }
        
            static String names(List<Team> list) {
                StringBuilder sb = new StringBuilder();
                for (Team t : list) sb.append(t.name).append(' ');
                return sb.toString().trim();
            }
        
            static void show(String title, List<Team> data) {
                System.out.println(title);
                System.out.println("  input        : " + names(data));
                System.out.println("  correct      : " + names(sorted(data, CORRECT)));
                System.out.println("  points only  : "
                        + names(sorted(data, POINTS_ONLY)));
                System.out.println("  no name rule : " + names(sorted(data, NO_NAME)));
                System.out.println("  subtraction  : " + names(sorted(data, SUBTRACT)));
            }
        
            public static void main(String[] args) {
                // Data set 1: several ties at every level
                List<Team> set1 = List.of(new Team("Panthers", 9, 4), new Team("Eagles",
                        9, 6),
                    new Team("Cobras", 6, 1), new Team("Bisons", 9, 4),
                            new Team("Falcons", 9, 6),
                    new Team("Hawks", 6, 1), new Team("Dolphins", 6, 3), new Team("Ibex",
                            3, 0));
                show("Data set 1 (ties at every level)", set1);
        
                // Data set 2: built to break the faulty comparators
                List<Team> set2 = List.of(new Team("Zebras", 7, 3), new Team("Yaks", 7,
                        3),
                    new Team("Tigers", 7, -2), new Team("Lions", 7, 5),
                    new Team("Xerus", 5, 2_000_000_000), new Team("Wolves", 5,
                            -2_000_000_000),
                    new Team("Vipers", 5, 0), new Team("Aces", 9, 1));
                show("Data set 2 (designed to expose faults)", set2);
            }
        }
    }

    // Q5/GenericsIntro.java
    static class Unit14 {
        static class Box<T> {                       // T is chosen by the caller
            private final T value;
            Box(T value) { this.value = value; }
            T get() { return value; }
        }
        
        public static class GenericsIntro {
            public static void main(String[] args) {
                Box<String> name = new Box<>("Arun");
                String s = name.get();                    // no cast needed
                Box<Integer> marks = new Box<>(87);
                int m = marks.get();
                System.out.println(s + " " + m);
        
                List<String> teams = new ArrayList<>();
                teams.add("Eagles");
                // teams.add(42);   // compile-time error: only String is allowed
                System.out.println(teams);
            }
        }
    }

    // Q5/LeaderboardDemo.java
    static class Unit15 {
        static class Team {
            final String name;
            final int points;
            final int goalDiff;
        
            Team(String name, int points, int goalDiff) {
                this.name = name;
                this.points = points;
                this.goalDiff = goalDiff;
            }
        
            @Override
            public String toString() {
                return name + "(" + points + "," + goalDiff + ")";
            }
        }
        
        static class Leaderboard<T> {
            private final List<T> items = new ArrayList<>();
            private final Comparator<? super T> order;
        
            Leaderboard(Comparator<? super T> order) {
                this.order = order;
            }
        
            void add(T item) {
                items.add(item);
            }
        
            List<T> ranking() {
                // TODO 1 (completed): sorted copy; the original list is not modified
                List<T> copy = new ArrayList<>(items);
                copy.sort(order);
                return copy;
            }
        }
        
        public static class LeaderboardDemo {
            public static void main(String[] args) {
                // TODO 2 (completed): points, goal difference (both high first), name A-Z
                Comparator<Team> byRank =
                        Comparator.comparingInt((Team t) -> t.points).reversed()
                        .thenComparing(
                            Comparator.comparingInt((Team t) -> t.goalDiff).reversed())
                        .thenComparing(t -> t.name);
        
                Leaderboard<Team> board = new Leaderboard<>(byRank);
                board.add(new Team("Panthers", 9, 4));
                board.add(new Team("Eagles", 9, 6));
                board.add(new Team("Cobras", 6, 1));
                board.add(new Team("Bisons", 9, 4));
                System.out.println(board.ranking());
            }
        }
    }

    // Q5/RankingExtras.java
    static class Unit16 {
        public static class RankingExtras {
        
            // Comparable alternative
            static class Team implements Comparable<Team> {
                final String name; final int points, goalDiff;
                Team(String n, int p, int g) { name = n; points = p; goalDiff = g; }
                // one natural order only
                @Override public int compareTo(Team o) {
                    if (points != o.points) return Integer.compare(o.points, points);
                    if (goalDiff != o.goalDiff) {
                        return Integer.compare(o.goalDiff, goalDiff);
                    }
                    return name.compareTo(o.name);
                }
                public String toString() {
                    return name + "(" + points + "," + goalDiff + ")";
                }
            }
        
            static class VarsityTeam extends Team {
                VarsityTeam(String n, int p, int g) { super(n, p, g); }
            }
        
            // Bounded type: T must be able to compare itself (or a supertype of itself)
            static <T extends Comparable<? super T>> List<T> rankNatural(List<T> in) {
                List<T> copy = new ArrayList<>(in);
                Collections.sort(copy);
                return copy;
            }
        
            // Wildcard: reads from a List of any subtype of T; comparator may be for a
            // supertype
            static <T> List<T> rank(List<? extends T> source,
                    Comparator<? super T> order) {
                List<T> copy = new ArrayList<>(source);
                copy.sort(order);
                return copy;
            }
        
            public static void main(String[] args) {
                List<Team> teams = List.of(new Team("Panthers", 9, 4), new Team("Eagles",
                        9, 6),
                    new Team("Cobras", 6, 1), new Team("Bisons", 9, 4));
                System.out.println("Comparable : " + rankNatural(teams));
        
                // three stable sorts: least important key first
                List<Team> s = new ArrayList<>(teams);
                s.sort(Comparator.comparing((Team t) -> t.name));
                s.sort(Comparator.comparingInt((Team t) -> t.goalDiff).reversed());
                s.sort(Comparator.comparingInt((Team t) -> t.points).reversed());
                System.out.println("3 stable sorts: " + s);
                System.out.println("same as Comparable: " + s.toString()
                        .equals(rankNatural(teams).toString()));
        
                List<VarsityTeam> v = List.of(new VarsityTeam("Rams", 4, 0),
                        new VarsityTeam("Owls", 6, 2));
                Comparator<Team> byPoints = Comparator.comparingInt((Team t) -> t.points)
                        .reversed();
                // List<VarsityTeam> accepted
                List<Team> ranked = rank(v, byPoints);
                System.out.println("Wildcard   : " + ranked);
            }
        }
    }

    // Q6/AgingTriage.java
    static class Unit17 {
        // Fairness rule: for every AGE_STEP service ticks a patient has waited, the
        // effective severity improves by one level (never better than S1).
        public static class AgingTriage {
            static class Patient {
                final String name; final int severity; final long arrival;
                final long arrivedAt;
                int effective;
                Patient(String n, int s, long a, long t) {
                    name = n; severity = s; arrival = a; arrivedAt = t; effective = s;
                }
                public String toString() { return name + "(S" + severity + ")"; }
            }
        
            private final int ageStep;           // 0 = ageing switched off
            private long counter = 0, now = 0;   // now = number of patients served so far
            private final PriorityQueue<Patient> queue = new PriorityQueue<>(
                Comparator.comparingInt((Patient p) -> p.effective)
                          .thenComparingLong(p -> p.arrival));
        
            AgingTriage(int ageStep) { this.ageStep = ageStep; }
        
            void admit(String name, int severity) {
                queue.add(new Patient(name, severity, counter++, now));
            }
        
            Patient next() {
                // re-rank everybody before serving
                if (ageStep > 0) {
                    List<Patient> all = new ArrayList<>(queue);
                    queue.clear();
                    for (Patient p : all) {
                        p.effective = Math.max(1,
                                p.severity - (int) ((now - p.arrivedAt) / ageStep));
                        queue.add(p);
                    }
                }
                now++;
                return queue.poll();
            }
        
            static String simulate(int ageStep, int rounds) {
                AgingTriage er = new AgingTriage(ageStep);
                // minor case, waiting since time 0
                er.admit("Asha", 5);
                StringBuilder log = new StringBuilder();
                for (int r = 1; r <= rounds; r++) {
                    // a new critical patient every round
                    er.admit("C" + r, 1);
                    // the doctor sees one patient per round
                    Patient p = er.next();
                    log.append(p.name).append(' ');
                    if (p.name.equals("Asha")) { log.append("<-- Asha served in round "
                            + r); break; }
                }
                return log.toString().trim();
            }
        
            public static void main(String[] args) {
                System.out.println("Without ageing: " + simulate(0, 30));
                System.out.println("With ageing (step 5): " + simulate(5, 30));
            }
        }
    }

    // Q6/Triage.java
    static class Unit18 {
        static class Patient {
            final String name;
            final int severity;
            final long arrival;
        
            Patient(String name, int severity, long arrival) {
                this.name = name;
                this.severity = severity;
                this.arrival = arrival;
            }
        
            @Override
            public String toString() {
                return name + "(S" + severity + ")";
            }
        }
        
        public static class Triage {
            private long counter = 0;
        
            // TODO 1 (completed): lowest severity first, then earliest arrival
            private PriorityQueue<Patient> queue = new PriorityQueue<>(
                    Comparator.comparingInt((Patient p) -> p.severity)
                              .thenComparingLong(p -> p.arrival));
        
            void admit(String name, int severity) {
                queue.add(new Patient(name, severity, counter++));
            }
        
            Patient next() {
                return queue.poll();
            }
        
            public static void main(String[] args) {
                Triage er = new Triage();
                er.admit("Ravi", 3);
                er.admit("Meena", 1);
                er.admit("John", 3);
                er.admit("Asha", 5);
                er.admit("Kumar", 1);
                er.admit("Lata", 2);
        
                StringBuilder order = new StringBuilder();
                Patient p;
                while ((p = er.next()) != null) order.append(p).append(' ');
                System.out.println(order.toString().trim());
            }
        }
    }

    // Q6/TriageBenchmark.java
    static class Unit19 {
        public static class TriageBenchmark {
            static class Patient {
                final int severity; final long arrival;
                Patient(int s, long a) { severity = s; arrival = a; }
            }
        
            static final int K = 1000, TRIALS = 5, WARMUP = 2;
            static final Comparator<Patient> RULE =
                Comparator.comparingInt((Patient p) -> p.severity)
                        .thenComparingLong(p -> p.arrival);
            static final String[] DESIGNS = {"PriorityQueue", "SortedLinkedList",
                    "FiveQueues"};
            static long sink;
        
            // The three designs share one tiny interface so the same workload drives all
            // of them
            interface Triage { void admit(Patient p); Patient next(); }
        
            static Triage create(int design, List<Patient> preload) {
                if (design == 0) {
                    PriorityQueue<Patient> pq = new PriorityQueue<>(RULE);
                    pq.addAll(preload);
                    return new Triage() {
                        public void admit(Patient p) { pq.add(p); }
                        public Patient next() { return pq.poll(); }
                    };
                } else if (design == 1) {
                    LinkedList<Patient> list = new LinkedList<>(preload);
                    // start from a sorted list
                    list.sort(RULE);
                    return new Triage() {
                        // walk to the correct position: O(n)
                        public void admit(Patient p) {
                            ListIterator<Patient> it = list.listIterator();
                            while (it.hasNext()) {
                                if (it.next().severity > p.severity) { it.previous();
                                break; }
                            }
                            it.add(p);
                        }
                        public Patient next() { return list.pollFirst(); }
                    };
                } else {
                    List<ArrayDeque<Patient>> q = new ArrayList<>();
                    for (int i = 0; i < 5; i++) q.add(new ArrayDeque<>());
                    for (Patient p : preload) q.get(p.severity - 1).addLast(p);
                    return new Triage() {
                        public void admit(Patient p) { q.get(p.severity - 1).addLast(p); }
                        public Patient next() {
                            for (ArrayDeque<Patient> level : q)
                                if (!level.isEmpty()) return level.pollFirst();
                            return null;
                        }
                    };
                }
            }
        
            public static void main(String[] args) throws Exception {
                try (PrintWriter csv = new PrintWriter("q6_raw.csv")) {
                    csv.println("size,design,operation,trial,ops,total_ns");
                    for (int n : new int[]{1_000, 10_000, 100_000}) {
                        Random r = new Random(5);
                        List<Patient> preload = new ArrayList<>();
                        for (int i = 0; i < n; i++) {
                            preload.add(new Patient(1 + r.nextInt(5), i));
                        }
                        List<Patient> arrivals = new ArrayList<>();
                        for (int i = 0; i < K; i++) {
                            arrivals.add(new Patient(1 + r.nextInt(5), n + i));
                        }
        
                        double[][] add = new double[3][TRIALS];
                        double[][] poll = new double[3][TRIALS];
                        for (int d = 0; d < 3; d++)                       // warm-up
                            for (int w = 0; w < WARMUP; w++) {
                                Triage t = create(d, preload);
                                for (Patient p : arrivals) t.admit(p);
                                for (int i = 0; i < K; i++) sink += t.next().severity;
                            }
                        for (int trial = 0; trial < TRIALS; trial++) {
                            for (int d = 0; d < 3; d++) {
                                // untimed set-up
                                Triage t = create(d, preload);
                                long t0 = System.nanoTime();
                                for (Patient p : arrivals) t.admit(p);
                                add[d][trial] = System.nanoTime() - t0;
                                t0 = System.nanoTime();
                                for (int i = 0; i < K; i++) sink += t.next().severity;
                                poll[d][trial] = System.nanoTime() - t0;
                                String id = n + "," + DESIGNS[d];
                                String tail = "," + (trial + 1) + "," + K + ",";
                                csv.println(id + ",admit" + tail + (long) add[d][trial]);
                                csv.println(id + ",serve" + tail + (long) poll[d][trial]);
                            }
                        }
                        for (int d = 0; d < 3; d++) {
                            double a = 0, p = 0;
                            for (int i = 0; i < TRIALS; i++) { a += add[d][i];
                            p += poll[d][i]; }
                            System.out.printf("%7d %-17s admit %10.1f ns/op"
                                + "   serve %10.1f ns/op%n",
                                n, DESIGNS[d], a / TRIALS / K, p / TRIALS / K);
                        }
                    }
                }
                System.out.println("sink (ignore) = " + sink);
            }
        }
    }

    // Q6/TriageEight.java
    static class Unit20 {
        public static class TriageEight {
            static class Patient {
                final String name; final int severity; final long arrival;
                Patient(String n, int s, long a) { name = n; severity = s; arrival = a; }
                public String toString() { return name + "(S" + severity + ",#" + arrival
                        + ")"; }
            }
        
            // Shows the waiting list in treatment order without disturbing the real queue
            static String waitingList(PriorityQueue<Patient> queue) {
                PriorityQueue<Patient> copy = new PriorityQueue<>(queue);
                StringBuilder sb = new StringBuilder();
                Patient p;
                while ((p = copy.poll()) != null) sb.append(p.name).append(' ');
                return sb.toString().trim();
            }
        
            public static void main(String[] args) {
                PriorityQueue<Patient> queue = new PriorityQueue<>(
                    Comparator.comparingInt((Patient p) -> p.severity)
                              .thenComparingLong(p -> p.arrival));
        
                String[] names = {"Anil", "Bhavna", "Charan", "Deepa", "Esha", "Farid",
                        "Gita", "Hari"};
                int[] severity = {4, 2, 4, 1, 2, 5, 1, 3};
                for (int i = 0; i < names.length; i++) queue.add(new Patient(names[i],
                        severity[i], i));
        
                // Hand-worked order (see the step-by-step table in the report)
                List<String> expected = Arrays.asList("Deepa", "Gita", "Bhavna", "Esha",
                                                      "Hari", "Anil", "Charan", "Farid");
        
                System.out.println("Printing the PriorityQueue directly (heap order):");
                System.out.println("  " + queue);
                System.out.println("Waiting list in treatment order (copy is drained):");
                System.out.println("  " + waitingList(queue));
        
                List<String> served = new ArrayList<>();
                System.out.println("Serving with poll():");
                Patient p;
                while ((p = queue.poll()) != null) {
                    served.add(p.name);
                    System.out.println("  " + served.size() + ". " + p);
                }
                System.out.println("Matches hand-verified order: "
                        + served.equals(expected));
            }
        }
    }

    // Q7/AgingBenchmark.java
    static class Unit21 {
        public static class AgingBenchmark {
            static final int AGE_STEP = 20, ROUNDS = 200, TRIALS = 5, WARMUP = 1;
            static long sink;
        
            static class Patient {
                final int id, severity;
                final long arrivedAt;
                int effective;
                Patient(int id, int severity, long arrivedAt) {
                    this.id = id; this.severity = severity; this.arrivedAt = arrivedAt;
                    this.effective = severity;
                }
            }
        
            // Fairness rule: one level better for every AGE_STEP services waited, never
            // better than S1
            static int effective(Patient p, long now) {
                return Math.max(1, p.severity - (int) ((now - p.arrivedAt) / AGE_STEP));
            }
        
            interface Triage { void admit(int severity); int next(); long visits(); }
        
            // ORIGINAL (Q6): before every service, re-rank all waiting patients -> O(n)
            // per service
            static class RescanTriage implements Triage {
                int ids = 0; long now = 0, visits = 0;
                final PriorityQueue<Patient> queue = new PriorityQueue<>(
                    Comparator.comparingInt((Patient p) -> p.effective)
                            .thenComparingInt(p -> p.id));
                public void admit(int s) { queue.add(new Patient(ids++, s, now)); }
                public int next() {
                    List<Patient> all = new ArrayList<>(queue);
                    queue.clear();
                    for (Patient p : all) { p.effective = effective(p, now);
                    queue.add(p); visits++; }
                    now++;
                    return queue.poll().id;
                }
                public long visits() { return visits; }
            }
        
            // REDESIGN: one FIFO queue per ORIGINAL severity. Inside one queue the oldest
            // patient
            // is always the best-ranked, so only the five queue heads need to be
            // compared.
            static class HeadsTriage implements Triage {
                int ids = 0; long now = 0, visits = 0;
                final List<ArrayDeque<Patient>> level = new ArrayList<>();
                HeadsTriage() {
                    for (int i = 0; i < 5; i++) level.add(new ArrayDeque<>());
                }
                public void admit(int s) { level.get(s - 1).addLast(new Patient(ids++, s,
                        now)); }
                public int next() {
                    Patient best = null;
                    int bestEff = 0;
                    for (ArrayDeque<Patient> q : level) {
                        Patient h = q.peekFirst();
                        if (h == null) continue;
                        visits++;
                        int e = effective(h, now);
                        boolean better = best == null || e < bestEff
                                || (e == bestEff && h.id < best.id);
                        if (better) {
                            best = h; bestEff = e;
                        }
                    }
                    now++;
                    level.get(best.severity - 1).pollFirst();
                    return best.id;
                }
                public long visits() { return visits; }
            }
        
            static Triage create(int design) { return design == 0 ? new RescanTriage()
                    : new HeadsTriage(); }
            static final String[] DESIGNS = {"RescanPQ(original)", "FiveHeads(redesign)"};
        
            // Correctness: random interleaved admit/serve stream must give identical
            // service order
            static boolean sameOrder(int operations, long seed) {
                Random r = new Random(seed);
                Triage a = create(0), b = create(1);
                int waiting = 0;
                for (int i = 0; i < operations; i++) {
                    if (waiting == 0 || r.nextInt(10) < 6) {
                        int s = 1 + r.nextInt(5);
                        a.admit(s); b.admit(s); waiting++;
                    } else {
                        if (a.next() != b.next()) return false;
                        waiting--;
                    }
                }
                return true;
            }
        
            public static void main(String[] args) throws Exception {
                System.out.println("Random-stream equivalence (3 seeds): "
                    + (sameOrder(5000, 1) && sameOrder(5000, 2) && sameOrder(5000, 3)));
                try (PrintWriter csv = new PrintWriter("q7_raw.csv")) {
                    csv.println("size,design,trial,rounds,total_ns,patient_visits");
                    for (int n : new int[]{1_000, 10_000, 100_000}) {
                        Random r = new Random(9);
                        int[] pre = new int[n], later = new int[ROUNDS];
                        for (int i = 0; i < n; i++) pre[i] = 1 + r.nextInt(5);
                        for (int i = 0; i < ROUNDS; i++) later[i] = 1 + r.nextInt(5);
        
                        int[][] served = new int[2][ROUNDS];
                        double[] ns = new double[2], visits = new double[2];
                        for (int d = 0; d < 2; d++)                       // warm-up
                            for (int w = 0; w < WARMUP; w++) {
                                Triage t = create(d);
                                for (int s : pre) t.admit(s);
                                for (int i = 0; i < ROUNDS; i++) { sink += t.next();
                                t.admit(later[i]); }
                            }
                        for (int trial = 1; trial <= TRIALS; trial++) {
                            for (int d = 0; d < 2; d++) {
                                // untimed set-up
                                Triage t = create(d);
                                for (int s : pre) t.admit(s);
                                long t0 = System.nanoTime();
                                for (int i = 0; i < ROUNDS; i++) {
                                    // serve one patient ...
                                    served[d][i] = t.next();
                                    // ... and one new arrival
                                    t.admit(later[i]);
                                }
                                long total = System.nanoTime() - t0;
                                ns[d] += total; visits[d] = t.visits();
                                String id = n + "," + DESIGNS[d] + "," + trial;
                                csv.println(id + "," + ROUNDS + ","
                                        + total + "," + t.visits());
                            }
                        }
                        System.out.println("size " + n + ": identical service order = "
                            + Arrays.equals(served[0], served[1]));
                        for (int d = 0; d < 2; d++)
                            System.out.printf("  %-20s avg %14.0f ns per %d rounds,"
                                + " patient visits = %d%n",
                                DESIGNS[d], ns[d] / TRIALS, ROUNDS, (long) visits[d]);
                        System.out.printf("  speed-up (original / redesign) = %.1f x%n",
                                ns[0] / ns[1]);
                    }
                }
                System.out.println("sink (ignore) = " + sink);
            }
        }
    }
}
