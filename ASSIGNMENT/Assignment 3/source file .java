import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/*
 * CSA09 - Programming in Java - Assignment 3
 * Single-file reference implementation/demonstration for Q1-Q7.
 *
 * Compile: javac Assignment3Solutions.java
 * Run:     java Assignment3Solutions
 *
 * Uses standard Java APIs and keeps all helper classes nested so that
 * the complete source can be submitted as one .java file.
 */
public class Assignment3Solutions {

    // ---------------- Q1: Exception handling / ATM ----------------

    static class InsufficientFundsException extends RuntimeException {
        InsufficientFundsException(String message) { super(message); }
    }

    static void withdraw(String accountText, String amountText, double balance)
            throws IOException {
        int account = Integer.parseInt(accountText);
        double amount = Double.parseDouble(amountText);

        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient account balance");
        }

        // Demonstrates a checked file exception; try-with-resources closes the file.
        try (BufferedReader reader =
                     Files.newBufferedReader(Paths.get("card-reader.txt"))) {
            System.out.println("ATM: withdrawal approved for account " + account);
        }
    }

    static void demonstrateATM() {
        System.out.println("\n--- Q1: ATM exception handling ---");
        try {
            withdraw("12345", "500", 1000);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction declined: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("Card-reader file not found.");
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        } finally {
            System.out.println("ATM transaction attempt finished.");
        }
    }

    // ---------------- Q2: User-defined exceptions / chaining ----------------

    static class ExamException extends Exception {
        ExamException(String message) { super(message); }
        ExamException(String message, Throwable cause) { super(message, cause); }
    }

    static class ExamWindowClosedException extends ExamException {
        ExamWindowClosedException(String message) { super(message); }
    }

    static class StudentNotRegisteredException extends ExamException {
        StudentNotRegisteredException(String message) { super(message); }
    }

    static class SubmissionTooLargeException extends ExamException {
        SubmissionTooLargeException(String message) { super(message); }
    }

    static class SubmissionStorageException extends ExamException {
        SubmissionStorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static class SubmissionStorage {
        void save(String fileName, long sizeKb) throws SubmissionStorageException {
            try {
                if (sizeKb > 500) {
                    throw new IOException("Disk quota exceeded");
                }
                System.out.println("Saved " + fileName);
            } catch (IOException e) {
                // Wrap the low-level exception and retain it as the cause.
                throw new SubmissionStorageException(
                        "Could not store " + fileName, e);
            }
        }
    }

    static class ExamService {
        private final SubmissionStorage storage = new SubmissionStorage();

        void submit(boolean windowOpen, boolean registered, long sizeKb)
                throws ExamException {
            if (!windowOpen) {
                throw new ExamWindowClosedException("Exam window is closed");
            }
            if (!registered) {
                throw new StudentNotRegisteredException(
                        "Student is not registered for this course");
            }
            if (sizeKb > 500) {
                throw new SubmissionTooLargeException(
                        "Submission exceeds the 500 KB limit");
            }
            storage.save("answer.pdf", sizeKb);
        }
    }

    static void demonstrateUserExceptions() {
        System.out.println("\n--- Q2: User-defined exceptions and chaining ---");
        try {
            new ExamService().submit(true, true, 800);
        } catch (ExamException e) {
            System.out.println("Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Cause: " + e.getCause().getMessage());
            }
        }
    }

    // ---------------- Q3: Resource safety / try-with-resources ----------------

    static void demonstrateResourceSafety() {
        System.out.println("\n--- Q3: Resource safety ---");
        Path input = null;
        Path output = null;
        try {
            input = Files.createTempFile("store-sales-", ".txt");
            output = Files.createTempFile("daily-report-", ".txt");
            Files.write(input, Arrays.asList("100", "200", "150"));

            int total = 0;
            try (BufferedReader reader = Files.newBufferedReader(input);
                 BufferedWriter writer = Files.newBufferedWriter(output)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int sale = Integer.parseInt(line.trim());
                    total += sale;
                }
                writer.write("Total sales = " + total);
                writer.newLine();
            }
            System.out.println(new String(Files.readAllBytes(output)).trim());
            System.out.println("Input and output resources closed automatically.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Report generation failed: " + e.getMessage());
        } finally {
            try {
                if (input != null) Files.deleteIfExists(input);
                if (output != null) Files.deleteIfExists(output);
            } catch (IOException e) {
                System.out.println("Temporary-file cleanup failed: " + e.getMessage());
            }
        }
    }

    // ---------------- Q4: Thread creation and life cycle ----------------

    static class ScannerTask implements Runnable {
        private final int station;

        ScannerTask(int station) {
            this.station = station;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(100L * station);
                System.out.println("Scanner station " + station + " completed.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Scanner station " + station + " interrupted.");
            }
        }
    }

    static class ScannerThread extends Thread {
        private final int station;

        ScannerThread(int station) {
            this.station = station;
        }

        @Override
        public void run() {
            new ScannerTask(station).run();
        }
    }

    static void demonstrateThreads() {
        System.out.println("\n--- Q4: Thread creation ---");
        Thread[] stations = new Thread[4];

        // Runnable-based creation.
        for (int i = 0; i < stations.length; i++) {
            stations[i] = new Thread(new ScannerTask(i + 1));
            stations[i].start();
        }

        // join() ensures main waits for all station threads.
        for (Thread station : stations) {
            try {
                station.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Main thread interrupted while joining.");
                return;
            }
        }
        System.out.println("All scanner stations have finished.");

        // Example of extending Thread:
        Thread example = new ScannerThread(5);
        example.start();
        try {
            example.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------------- Q5: Race condition / synchronization ----------------

    static class SeatBooking {
        private final boolean[] booked;
        private int bookingsMade;

        SeatBooking(int seats) {
            booked = new boolean[seats];
        }

        // The check-and-book operation is atomic for this SeatBooking object.
        public synchronized boolean book(int seat) {
            if (seat < 0 || seat >= booked.length) {
                throw new IllegalArgumentException("Invalid seat number");
            }
            if (booked[seat]) {
                return false;
            }
            booked[seat] = true;
            bookingsMade++;
            return true;
        }

        synchronized int getBookingsMade() {
            return bookingsMade;
        }
    }

    static void demonstrateSeatBooking() {
        System.out.println("\n--- Q5: Synchronized seat booking ---");
        final SeatBooking screen = new SeatBooking(10);
        Thread[] users = new Thread[100];

        for (int i = 0; i < users.length; i++) {
            final int seat = i % 10;
            users[i] = new Thread(() -> screen.book(seat));
            users[i].start();
        }

        for (Thread user : users) {
            try {
                user.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Booking wait interrupted.");
                return;
            }
        }
        System.out.println("Seats sold: " + screen.getBookingsMade());
    }

    // ---------------- Q6: Deadlock prevention / producer-consumer ----------------

    static class Wallet {
        private final int id;
        private int balance;
        private final ReentrantLock lock = new ReentrantLock();

        Wallet(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }
    }

    // Lock wallets in ascending ID order to prevent circular wait.
    static boolean transfer(Wallet from, Wallet to, int amount) {
        if (amount <= 0) return false;
        Wallet first = from.id < to.id ? from : to;
        Wallet second = from.id < to.id ? to : from;

        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (from.balance < amount) return false;
                from.balance -= amount;
                to.balance += amount;
                return true;
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }

    static class TokenCounter {
        private final int[] buffer = new int[5];
        private int count;
        private int in;
        private int out;

        public synchronized void put(int token) throws InterruptedException {
            while (count == buffer.length) {
                wait();
            }
            buffer[in] = token;
            in = (in + 1) % buffer.length;
            count++;
            notifyAll();
        }

        public synchronized int take() throws InterruptedException {
            while (count == 0) {
                wait();
            }
            int token = buffer[out];
            out = (out + 1) % buffer.length;
            count--;
            notifyAll();
            return token;
        }
    }

    static void demonstrateTokenCounter() {
        System.out.println("\n--- Q6: Producer-consumer token counter ---");
        final TokenCounter counter = new TokenCounter();
        final int[] collected = new int[2];

        Thread cook = new Thread(() -> {
            try {
                for (int token = 1; token <= 20; token++) {
                    counter.put(token);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread[] students = new Thread[2];
        for (int i = 0; i < students.length; i++) {
            final int student = i;
            students[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        collected[student] += counter.take();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        cook.start();
        for (Thread student : students) student.start();

        try {
            cook.join();
            for (Thread student : students) student.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        System.out.println("Sum of tokens collected: "
                + (collected[0] + collected[1]) + " (expected 210)");

        Wallet a = new Wallet(1, 1000);
        Wallet b = new Wallet(2, 500);
        System.out.println("Wallet transfer successful: " + transfer(a, b, 100));
    }

    // ---------------- Q7: ExecutorService / Callable / Future ----------------

    static class ProcessingResult {
        final String fileName;
        final boolean success;
        final double score;
        final String message;

        ProcessingResult(String fileName, boolean success,
                         double score, String message) {
            this.fileName = fileName;
            this.success = success;
            this.score = score;
            this.message = message;
        }
    }

    static class ScoreTask implements Callable<ProcessingResult> {
        private final Path path;

        ScoreTask(Path path) {
            this.path = path;
        }

        @Override
        public ProcessingResult call() {
            try {
                List<String> lines = Files.readAllLines(path);
                if (lines.isEmpty()) {
                    throw new IllegalArgumentException("Empty score file");
                }
                double score = Double.parseDouble(lines.get(0).trim());
                if (score < 0 || score > 100) {
                    throw new IllegalArgumentException("Score outside 0-100");
                }
                return new ProcessingResult(path.toString(), true, score, "OK");
            } catch (IOException | IllegalArgumentException e) {
                // Return a failure result so one bad file does not stop the run.
                return new ProcessingResult(
                        path.toString(), false, 0, e.getMessage());
            }
        }
    }

    static void demonstrateExecutor() {
        System.out.println("\n--- Q7: Concurrent result processing ---");
        Path valid = null;
        Path invalid = null;
        ExecutorService pool = Executors.newFixedThreadPool(2);
        List<Future<ProcessingResult>> futures = new ArrayList<>();

        try {
            valid = Files.createTempFile("score-valid-", ".txt");
            invalid = Files.createTempFile("score-invalid-", ".txt");
            Files.write(valid, Arrays.asList("85"));
            Files.write(invalid, Arrays.asList("not-a-number"));

            futures.add(pool.submit(new ScoreTask(valid)));
            futures.add(pool.submit(new ScoreTask(invalid)));

            int successes = 0;
            int failures = 0;
            for (Future<ProcessingResult> future : futures) {
                try {
                    ProcessingResult result = future.get();
                    if (result.success) {
                        successes++;
                        System.out.println(result.fileName
                                + ": success, score = " + result.score);
                    } else {
                        failures++;
                        System.out.println(result.fileName
                                + ": failed, reason = " + result.message);
                    }
                } catch (ExecutionException e) {
                    failures++;
                    System.out.println("Unexpected task failure: " + e.getCause());
                }
            }
            System.out.println("Summary: successes = " + successes
                    + ", failures = " + failures);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Processor setup/interruption error: " + e.getMessage());
        } finally {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
            try {
                if (valid != null) Files.deleteIfExists(valid);
                if (invalid != null) Files.deleteIfExists(invalid);
            } catch (IOException e) {
                System.out.println("Temporary-file cleanup failed: " + e.getMessage());
            }
        }
    }

    // ---------------- Main: run all demonstrations ----------------

    public static void main(String[] args) {
        demonstrateATM();
        demonstrateUserExceptions();
        demonstrateResourceSafety();
        demonstrateThreads();
        demonstrateSeatBooking();
        demonstrateTokenCounter();
        demonstrateExecutor();
    }
}
