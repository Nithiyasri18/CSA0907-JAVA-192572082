// Combined assignment. Requires JDK 17+; JDBC driver and MySQL for database features.
import java.awt.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--test")) {
            FeeServiceValidationTest.main(new String[0]);
            ServiceSmokeTest.main(new String[0]); return;
        }
        SwingUtilities.invokeLater(Main::menu);
    }
    static void add(JPanel p, String label, Runnable action) {
        JButton b = new JButton(label); b.addActionListener(e -> action.run()); p.add(b);
    }
    static void showText(String title, String text) {
        JFrame f = new JFrame(title); JTextArea a = new JTextArea(text); a.setEditable(false);
        a.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13)); a.setCaretPosition(0);
        f.add(new JScrollPane(a)); f.setSize(850, 600); f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); f.setVisible(true);
    }
    static void menu() {
        JFrame f = new JFrame("CSA09 - Java Assignment 4");
        JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        p.add(new JLabel("Event-Driven GUI Design | 192572082", SwingConstants.CENTER));
        add(p, "Task 1 - Student Fee Payment", () -> FeeWindow.main(new String[0]));
        add(p, "Task 2 - Hospital Appointment Design", () -> showText("Task 2: Design and pseudocode", TASK2));
        add(p, "Task 3 - Library Search", () -> LibrarySearch.main(new String[0]));
        add(p, "Task 4 - Drawing Pad Versions", () -> variants(4));
        add(p, "Task 5 - Converter Listener Styles", () -> variants(5));
        add(p, "Task 6 - Campus Event Registration", () -> App.main(new String[0]));
        add(p, "Database setup SQL (Tasks 1 and 6)", () -> showText("MySQL setup scripts", SQL));
        add(p, "Exit", () -> System.exit(0));
        f.setContentPane(p); f.pack(); f.setSize(500, 470); f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); f.setVisible(true);
    }
    static void variants(int task) {
        JFrame f = new JFrame("Task " + task + " - Choose implementation");
        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        if (task == 4) {
            add(p, "Task04/CompleteTheCode", () -> DrawPad_CompleteTheCode.main(new String[0]));
            add(p, "Task04/buggy", () -> DrawPad_buggy.main(new String[0]));
            add(p, "Task04/experiment_without_override", () -> DrawPad_experiment_without_override.main(new String[0]));
            add(p, "Task04/naive_fixed", () -> DrawPad_naive_fixed.main(new String[0]));
            add(p, "Task04/repaired", () -> DrawPad_repaired.main(new String[0]));
            add(p, "Compile-error experiment (source only)", () -> showText("Intentional @Override error", OVERRIDE));
        } else {
            add(p, "Task05/CompleteTheCode", () -> Converter_CompleteTheCode.main(new String[0]));
            add(p, "Task05/v1/A_FrameImplements", () -> Converter_v1_A_FrameImplements.main(new String[0]));
            add(p, "Task05/v1/B_NamedInner", () -> Converter_v1_B_NamedInner.main(new String[0]));
            add(p, "Task05/v1/C_Anonymous", () -> Converter_v1_C_Anonymous.main(new String[0]));
            add(p, "Task05/v1/D_Lambda", () -> Converter_v1_D_Lambda.main(new String[0]));
            add(p, "Task05/v2/A_FrameImplements", () -> Converter_v2_A_FrameImplements.main(new String[0]));
            add(p, "Task05/v2/B_NamedInner", () -> Converter_v2_B_NamedInner.main(new String[0]));
            add(p, "Task05/v2/C_Anonymous", () -> Converter_v2_C_Anonymous.main(new String[0]));
            add(p, "Task05/v2/D_Lambda", () -> Converter_v2_D_Lambda.main(new String[0]));
        }
        f.setContentPane(p); f.pack(); f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); f.setVisible(true);
    }
    static final String TASK2 = "# Task 2.1 – Event table (Hospital Outpatient Appointment Desk)\n\n| Event source | Event object | Listener interface | Action performed |\n|---|---|---|---|\n| JFrame (window shown) | WindowEvent WINDOW_OPENED | WindowListener.windowOpened (WindowAdapter) | Load departments and doctor map in background; focus patient ID |\n| patientId JTextField (Enter) | ActionEvent | ActionListener | Validate format; focus department |\n| department JComboBox | ItemEvent (SELECTED) | ItemListener | Refresh doctor list from the map; clear slots |\n| doctor JComboBox | ItemEvent (SELECTED) | ItemListener | Load free slots of the doctor |\n| slot JComboBox | ActionEvent | ActionListener | Remember slot; enable Book |\n| book JButton | ActionEvent | ActionListener | Validate; book in background; confirmation or error |\n| clear JButton | ActionEvent | ActionListener | Reset fields |\n| SwingWorker (finished) | done() / state PropertyChangeEvent | done() / PropertyChangeListener | Show result; clear bookingInProgress |\n| JFrame (iconified/restored) | WindowEvent | WindowListener | No business action |\n| JFrame (close button) | WindowEvent WINDOW_CLOSING | WindowListener.windowClosing | Confirm if booking in progress |\n| JFrame (disposed) | WindowEvent WINDOW_CLOSED | WindowListener.windowClosed | Release resources |\n\nComponent hierarchy, diagrams and the full analysis are in the report (Task 2).\n\n\n# Task 2.3 – Handler pseudocode\n\n## A. Refresh the doctor list\n```\nALGORITHM RefreshDoctorList\nINPUT  : e, an ItemEvent from departmentCombo\n         doctorsByDept, a Map<String, List<Doctor>> loaded at start-up\nOUTPUT : doctorCombo re-populated, slotCombo cleared, status updated\nBEGIN\n  1. IF e.getStateChange() is not SELECTED THEN RETURN     // ignore the DESELECTED half\n  2. IF updating THEN RETURN\n  3. updating <- true\n  4. dept <- departmentCombo.getSelectedItem()\n  5. doctorCombo.removeAllItems();  slotCombo.removeAllItems()\n  6. bookButton.setEnabled(false)\n  7. IF dept is null OR dept = \"-- Select department --\" THEN\n        doctorCombo.setEnabled(false);  slotCombo.setEnabled(false)\n        status <- \"Select a department.\"\n        updating <- false;  RETURN\n  8. list <- doctorsByDept.get(dept)\n  9. IF list is null OR list is empty THEN\n        doctorCombo.setEnabled(false)\n        status <- \"No doctors available in \" + dept\n        updating <- false;  RETURN\n 10. FOR EACH doctor IN list DO\n        doctorCombo.addItem(doctor)\n 11. doctorCombo.setEnabled(true)\n 12. updating <- false\n 13. doctorCombo.setSelectedIndex(0)          // fires the doctor handler, which loads slots\n 14. status <- size(list) + \" doctor(s) available in \" + dept\nEND\n```\n\n## B. Validate and book\n```\nALGORITHM ValidateAndBook\nINPUT  : patientIdField, departmentCombo, doctorCombo, slotCombo, flag bookingInProgress\nOUTPUT : confirmation or error message; one appointment row saved on success\nBEGIN\n  1. IF bookingInProgress THEN RETURN                       // ignore double clicks\n  2. id <- trim(patientIdField.getText());  errors <- empty list\n  3. IF id is empty THEN add \"Patient ID is required\" to errors\n     ELSE IF id does not match [A-Z0-9]{4,12} THEN add \"Patient ID format is invalid\" to errors\n  4. IF department is not selected THEN add \"Select a department\" to errors\n  5. IF doctorCombo.getSelectedItem() is null THEN add \"Select a doctor\" to errors\n  6. IF slotCombo.getSelectedItem() is null THEN add \"Select a time slot\" to errors\n  7. IF errors is not empty THEN\n        show error dialog listing all messages;  focus the first invalid field;  RETURN\n  8. bookingInProgress <- true;  bookButton.setEnabled(false);  status <- \"Booking...\"\n  9. START background task (SwingWorker.doInBackground):\n        TRY\n           IF patient id does not exist in the database THEN THROW PatientNotFound\n           IF slot is already booked for this doctor THEN THROW SlotTaken\n           INSERT appointment(patient, doctor, slot) using a parameterised statement\n           result <- SUCCESS(appointment number)\n        CATCH PatientNotFound, SlotTaken  -> result <- FAILURE(business message)\n        CATCH SQLException                -> result <- FAILURE(\"Database error; try again\")\n 10. WHEN the task completes (SwingWorker.done, on the EDT):\n        bookingInProgress <- false;  bookButton.setEnabled(true)\n        IF result is SUCCESS THEN\n           show confirmation (appointment number, doctor, slot);  remove the slot from slotCombo\n           clear patientIdField;  status <- \"Appointment booked.\"\n        ELSE\n           show error dialog with result.message;  keep the typed values so the user can correct them\n           status <- \"Booking failed.\"\nEND\n```\n";
    static final String SQL = "CREATE DATABASE IF NOT EXISTS simats_fees;\nUSE simats_fees;\nCREATE TABLE IF NOT EXISTS fees (\n    id      BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,\n    reg_no  VARCHAR(15)   NOT NULL,\n    amount  DECIMAL(10,2) NOT NULL CHECK (amount > 0),\n    paid_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    INDEX idx_fees_reg_no (reg_no)\n);\n\n-- Campus Event Registration: schema and sample data (MySQL 8.0+)\nCREATE DATABASE IF NOT EXISTS campus_events\n    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\nUSE campus_events;\n\n-- Least-privilege account used by the application (change the password!)\nCREATE USER IF NOT EXISTS 'campus_app'@'localhost' IDENTIFIED BY 'ChangeMe#123';\nGRANT SELECT, INSERT, UPDATE ON campus_events.* TO 'campus_app'@'localhost';\n\nCREATE TABLE IF NOT EXISTS students (\n    student_id    VARCHAR(15)  NOT NULL,\n    name          VARCHAR(60)  NOT NULL,\n    email         VARCHAR(100) NOT NULL,\n    department    VARCHAR(40)  NOT NULL,\n    year_of_study TINYINT      NOT NULL,\n    PRIMARY KEY (student_id),\n    CONSTRAINT chk_year CHECK (year_of_study BETWEEN 1 AND 4)\n) ENGINE=InnoDB;\n\nCREATE TABLE IF NOT EXISTS events (\n    event_id   INT          NOT NULL AUTO_INCREMENT,\n    title      VARCHAR(80)  NOT NULL,\n    venue      VARCHAR(60)  NOT NULL,\n    event_date DATE         NOT NULL,\n    PRIMARY KEY (event_id),\n    UNIQUE KEY uk_event_title_date (title, event_date)\n) ENGINE=InnoDB;\n\nCREATE TABLE IF NOT EXISTS registrations (\n    reg_id        BIGINT      NOT NULL AUTO_INCREMENT,\n    student_id    VARCHAR(15) NOT NULL,\n    event_id      INT         NOT NULL,\n    registered_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    PRIMARY KEY (reg_id),\n    -- one registration per student per event, enforced by the database itself\n    UNIQUE KEY uk_student_event (student_id, event_id),\n    -- supports \"WHERE event_id = ? ORDER BY reg_id LIMIT ? OFFSET ?\"\n    KEY idx_reg_event_regid (event_id, reg_id),\n    CONSTRAINT fk_reg_student FOREIGN KEY (student_id) REFERENCES students (student_id),\n    CONSTRAINT fk_reg_event   FOREIGN KEY (event_id)   REFERENCES events (event_id)\n) ENGINE=InnoDB;\n\nINSERT INTO events (title, venue, event_date) VALUES\n  ('Cultural Night',        'Main Auditorium', '2026-12-05'),\n  ('Inter-Department Quiz', 'Seminar Hall A',  '2026-12-08'),\n  ('Dance Competition',     'Open Air Theatre','2026-12-12'),\n  ('Music Fest',            'Main Auditorium', '2026-12-15'),\n  ('Poster Making',         'Art Gallery',     '2026-12-18');\n\nINSERT INTO students (student_id, name, email, department, year_of_study) VALUES\n  ('192500001', 'Asha Raman',   'asha@example.edu',   'CSE',    2),\n  ('192500002', 'Bala Kumar',   'bala@example.edu',   'ECE',    3),\n  ('192500003', 'Charu Nair',   'charu@example.edu',  'AI&DS',  1);\n\nINSERT INTO registrations (student_id, event_id) VALUES\n  ('192500001', 1), ('192500001', 2), ('192500002', 1), ('192500003', 3);\n\n-- Generates 20,000 students x 5 events = 100,000 registrations for the large-load test.\nUSE campus_events;\nSET SESSION cte_max_recursion_depth = 20000;\n\nINSERT INTO students (student_id, name, email, department, year_of_study)\nWITH RECURSIVE seq (n) AS (\n    SELECT 1 UNION ALL SELECT n + 1 FROM seq WHERE n < 20000\n)\nSELECT CONCAT('LT', LPAD(n, 6, '0')), CONCAT('Load Test Student ', n),\n       CONCAT('lt', n, '@example.edu'),\n       ELT(1 + MOD(n, 4), 'CSE', 'ECE', 'MECH', 'AI&DS'), 1 + MOD(n, 4)\nFROM seq;\n\nINSERT INTO registrations (student_id, event_id)\nSELECT s.student_id, e.event_id\nFROM students s CROSS JOIN events e\nWHERE s.student_id LIKE 'LT%'\nORDER BY s.student_id, e.event_id;\n\nSELECT COUNT(*) AS total_registrations FROM registrations;\n";
    static final String OVERRIDE = "import java.awt.BorderLayout;\nimport java.awt.Frame;\nimport java.awt.Graphics;\nimport java.awt.Label;\nimport java.awt.Point;\nimport java.awt.event.MouseAdapter;\nimport java.awt.event.MouseEvent;\nimport java.awt.event.WindowAdapter;\nimport java.awt.event.WindowEvent;\nimport java.util.ArrayList;\nimport java.util.List;\n\npublic class DrawPad extends Frame {\n    private final List<Point> points = new ArrayList<>();\n    private final Label status = new Label(\"Clicks: 0\");\n\n    public DrawPad() {\n        super(\"Draw Pad\");\n        add(status, BorderLayout.SOUTH);\n        addMouseListener(new MouseAdapter() {\n            // TODO 1: Override mouseClicked correctly: store the point, update the // label and request a repaint\n            @Override public void mouseclicked(MouseEvent e) {\n                points.add(e.getPoint());\n                status.setText(\"Clicks: \" + points.size());\n                repaint();\n            }\n        });\n        addWindowListener(new WindowAdapter() {\n            @Override public void windowClosing(WindowEvent e) { dispose(); } });\n        setSize(400, 300);\n    }\n\n    @Override public void paint(Graphics g) {\n        // TODO 2: Draw every stored point as a 6 × 6 filled circle centred on the point\n        super.paint(g);\n        for (Point p : points) {\n            g.fillOval(p.x - 3, p.y - 3, 6, 6);\n        }\n    }\n\n    public static void main(String[] args) { new DrawPad().setVisible(true); }\n}\n";
}


// Original source: Task01/FeeService.java
/** Business logic of the fee module: validation, bank confirmation and persistence. */
class FeeService {

    /** Supplies a fresh connection for every operation (keeps the service testable). */
    public interface ConnectionProvider { Connection get() throws SQLException; }

    /** Thrown when user input breaks a business rule; the message is safe to show. */
    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    /** Immutable result of a successful payment or receipt lookup. */
    public record Receipt(long id, String regNo, BigDecimal amount, Timestamp paidAt) { }

    public static final BigDecimal MAX_AMOUNT = new BigDecimal("500000.00");
    private static final Pattern REG_NO = Pattern.compile("[A-Za-z0-9]{6,15}");

    private final ConnectionProvider provider;
    private final long bankDelayMillis;   // simulated bank-confirmation delay

    public FeeService(ConnectionProvider provider, long bankDelayMillis) {
        this.provider = provider;
        this.bankDelayMillis = bankDelayMillis;
    }

    /** Pure validation: no I/O, so it is cheap to call on the event-dispatch thread. */
    public static String validateRegNo(String regNo) throws ValidationException {
        String r = regNo == null ? "" : regNo.trim();
        if (r.isEmpty()) throw new ValidationException("Register number is required.");
        if (!REG_NO.matcher(r).matches())
            throw new ValidationException("Register number must be 6-15 letters or digits.");
        return r;
    }

    public static BigDecimal validateAmount(String amountText) throws ValidationException {
        String t = amountText == null ? "" : amountText.trim();
        if (t.isEmpty()) throw new ValidationException("Amount is required.");
        BigDecimal a;
        try {
            a = new BigDecimal(t);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Amount must be a number, e.g. 12500.00.");
        }
        if (a.signum() <= 0) throw new ValidationException("Amount must be greater than zero.");
        if (a.scale() > 2) throw new ValidationException("Amount can have at most two decimal places.");
        if (a.compareTo(MAX_AMOUNT) > 0) throw new ValidationException("Amount exceeds the limit of " + MAX_AMOUNT + ".");
        return a;
    }

    /** Validates, simulates bank confirmation and records the payment. Call from a worker thread. */
    public Receipt pay(String regNo, String amountText)
            throws ValidationException, SQLException, InterruptedException {
        String reg = validateRegNo(regNo);
        BigDecimal amount = validateAmount(amountText);
        Thread.sleep(bankDelayMillis);                       // stands in for the bank call
        String sql = "INSERT INTO fees (reg_no, amount) VALUES (?, ?)";
        try (Connection con = provider.get()) {              // always closed, even on error
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, reg);
                ps.setBigDecimal(2, amount);
                ps.executeUpdate();
            }
            return findLatest(con, reg).orElseThrow(() -> new SQLException("Payment not found after insert."));
        }
    }

    /** Looks up the most recent receipt for a student. Call from a worker thread. */
    public Optional<Receipt> latestReceipt(String regNo) throws ValidationException, SQLException {
        String reg = validateRegNo(regNo);
        try (Connection con = provider.get()) {
            return findLatest(con, reg);
        }
    }

    private Optional<Receipt> findLatest(Connection con, String reg) throws SQLException {
        String sql = "SELECT id, reg_no, amount, paid_at FROM fees WHERE reg_no = ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reg);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    ? Optional.of(new Receipt(rs.getLong("id"), rs.getString("reg_no"),
                                              rs.getBigDecimal("amount"), rs.getTimestamp("paid_at")))
                    : Optional.empty();
            }
        }
    }
}


// Original source: Task01/FeeServiceValidationTest.java
/** Console test of FeeService validation; needs no database and no GUI. */
class FeeServiceValidationTest {
    private static int failures = 0;

    private static void expectOk(String label, String reg, String amt) {
        try {
            FeeService.validateRegNo(reg); FeeService.validateAmount(amt);
            System.out.println("PASS  " + label);
        } catch (FeeService.ValidationException e) {
            failures++; System.out.println("FAIL  " + label + " -> " + e.getMessage());
        }
    }

    private static void expectReject(String label, String reg, String amt, String expectedPart) {
        try {
            FeeService.validateRegNo(reg); FeeService.validateAmount(amt);
            failures++; System.out.println("FAIL  " + label + " -> accepted");
        } catch (FeeService.ValidationException e) {
            boolean ok = e.getMessage().contains(expectedPart);
            if (!ok) failures++;
            System.out.println((ok ? "PASS  " : "FAIL  ") + label + " -> " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        expectOk("valid input", "192572082", "12500.50");
        expectReject("empty register number", "  ", "100", "required");
        expectReject("bad register number", "12 34", "100", "6-15");
        expectReject("empty amount", "192572082", "", "required");
        expectReject("non-numeric amount", "192572082", "abc", "number");
        expectReject("zero amount", "192572082", "0", "greater than zero");
        expectReject("negative amount", "192572082", "-5", "greater than zero");
        expectReject("three decimals", "192572082", "10.123", "two decimal");
        expectReject("above limit", "192572082", "500000.01", "limit");
        expectReject("SQL text as reg no", "x'); DROP TABLE fees;--", "100", "6-15");
        System.out.println(failures == 0 ? "ALL TESTS PASSED" : failures + " TEST(S) FAILED");
        if (failures > 0) System.exit(1);
    }
}


// Original source: Task01/FeeWindow.java
/** User interface only: collects input, delegates to FeeService, shows results. */
class FeeWindow extends JFrame {
    private final FeeService service;
    private final JTextField regNo = new JTextField(15);
    private final JTextField amount = new JTextField(15);
    private final JButton pay = new JButton("Pay");
    private final JButton clear = new JButton("Clear");
    private final JButton receipt = new JButton("Receipt");
    private final JLabel status = new JLabel("Enter the register number and amount.");
    private boolean busy = false;

    public FeeWindow(FeeService service) {
        super("Student Fee Payment");
        this.service = service;

        JPanel form = new JPanel(new GridLayout(2, 2, 6, 6));
        form.add(new JLabel("Register number:")); form.add(regNo);
        form.add(new JLabel("Amount (INR):"));    form.add(amount);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(pay); buttons.add(clear); buttons.add(receipt);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(form, BorderLayout.NORTH);
        root.add(buttons, BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        setContentPane(root);

        pay.addActionListener(e -> onPay());          // one handler per button
        clear.addActionListener(e -> onClear());
        receipt.addActionListener(e -> onReceipt());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // closing is decided in windowClosing
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { onClose(); }
        });
        pack();
        setLocationRelativeTo(null);
    }

    private void onPay() {
        try {                                           // fast, I/O-free checks on the EDT
            FeeService.validateRegNo(regNo.getText());
            FeeService.validateAmount(amount.getText());
        } catch (FeeService.ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        setBusy(true, "Processing payment...");
        final String reg = regNo.getText(), amt = amount.getText();
        new SwingWorker<FeeService.Receipt, Void>() {
            @Override protected FeeService.Receipt doInBackground() throws Exception {
                return service.pay(reg, amt);           // sleep + JDBC run off the EDT
            }
            @Override protected void done() {
                try {
                    FeeService.Receipt r = get();
                    setBusy(false, "Payment recorded (receipt no. " + r.id() + ").");
                    JOptionPane.showMessageDialog(FeeWindow.this, "Paid INR " + r.amount() + " for " + r.regNo());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    fail(ex.getCause());
                }
            }
        }.execute();
    }

    private void onReceipt() {
        setBusy(true, "Fetching receipt...");
        final String reg = regNo.getText();
        new SwingWorker<Optional<FeeService.Receipt>, Void>() {
            @Override protected Optional<FeeService.Receipt> doInBackground() throws Exception {
                return service.latestReceipt(reg);
            }
            @Override protected void done() {
                try {
                    Optional<FeeService.Receipt> r = get();
                    setBusy(false, r.isPresent() ? "Receipt loaded." : "No payment found.");
                    JOptionPane.showMessageDialog(FeeWindow.this,
                        r.map(x -> "Receipt " + x.id() + "\n" + x.regNo() + "\nINR " + x.amount() + "\n" + x.paidAt())
                         .orElse("No payment found for this register number."), "Receipt",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    fail(ex.getCause());
                }
            }
        }.execute();
    }

    private void onClear() {
        regNo.setText(""); amount.setText("");
        status.setText("Fields cleared.");
        regNo.requestFocusInWindow();
    }

    private void onClose() {
        if (busy) {
            int c = JOptionPane.showConfirmDialog(this, "A payment is in progress. Close anyway?",
                    "Confirm exit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c != JOptionPane.YES_OPTION) return;
        }
        dispose();
        
    }

    private void fail(Throwable cause) {
        setBusy(false, "Operation failed.");
        String msg = (cause instanceof FeeService.ValidationException) ? cause.getMessage()
                   : (cause instanceof SQLException) ? "Database error: " + cause.getMessage()
                   : "Unexpected error: " + cause;
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setBusy(boolean b, String message) {
        busy = b;
        pay.setEnabled(!b); receipt.setEnabled(!b); clear.setEnabled(!b);
        status.setText(message);
    }

    public static void main(String[] args) {
        String url = System.getenv().getOrDefault("FEE_DB_URL", "jdbc:mysql://localhost:3306/simats_fees");
        String user = System.getenv().getOrDefault("FEE_DB_USER", "fee_app");
        String pwd = System.getenv().getOrDefault("FEE_DB_PASSWORD", "");
        FeeService service = new FeeService(() -> DriverManager.getConnection(url, user, pwd), 3000);
        SwingUtilities.invokeLater(() -> new FeeWindow(service).setVisible(true));
    }
}


// Original source: Task03/LibrarySearch.java
/** Campus library search screen built from nested BorderLayout and FlowLayout panels. */
class LibrarySearch extends JFrame {
    private record Book(String title, String author, String category) { }

    private static final int PAGE_SIZE = 4;
    private static final List<Book> CATALOGUE = List.of(
        new Book("Introduction to Algorithms", "Cormen", "Computer Science"),
        new Book("Effective Java", "Bloch", "Computer Science"),
        new Book("Java: The Complete Reference", "Schildt", "Computer Science"),
        new Book("Database System Concepts", "Silberschatz", "Computer Science"),
        new Book("Operating System Concepts", "Galvin", "Computer Science"),
        new Book("Linear Algebra and Its Applications", "Strang", "Mathematics"),
        new Book("Discrete Mathematics", "Rosen", "Mathematics"),
        new Book("Probability and Statistics", "DeGroot", "Mathematics"),
        new Book("Fundamentals of Physics", "Halliday", "Physics"),
        new Book("Concepts of Modern Physics", "Beiser", "Physics"),
        new Book("Wings of Fire", "Kalam", "Literature"),
        new Book("The Guide", "Narayan", "Literature"));

    private final JTextField searchField = new JTextField(20);
    private final JComboBox<String> category =
        new JComboBox<>(new String[]{"All", "Computer Science", "Mathematics", "Physics", "Literature"});
    private final JButton searchButton = new JButton("Search");
    private final JButton prevButton = new JButton("Prev");
    private final JButton nextButton = new JButton("Next");
    private final JButton reserveButton = new JButton("Reserve");
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> results = new JList<>(listModel);
    private final JLabel status = new JLabel("Enter a search term and press Search.");

    private final List<Book> matches = new ArrayList<>();
    private final Set<String> reserved = new HashSet<>();
    private int page = 0;

    public LibrarySearch() {
        super("Campus Library Search");

        // Header: BorderLayout -> title (NORTH) and search row (CENTER)
        JLabel title = new JLabel("Campus Library Search", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        searchRow.add(new JLabel("Search:"));
        searchRow.add(searchField);
        searchRow.add(category);
        searchRow.add(searchButton);
        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.NORTH);
        header.add(searchRow, BorderLayout.CENTER);

        // Footer: BorderLayout -> button row (CENTER) and status message (SOUTH)
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        buttonRow.add(prevButton);
        buttonRow.add(nextButton);
        buttonRow.add(reserveButton);
        JPanel footer = new JPanel(new BorderLayout());
        footer.add(buttonRow, BorderLayout.CENTER);
        footer.add(status, BorderLayout.SOUTH);

        results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane resultsPane = new JScrollPane(results);

        // Root: BorderLayout -> only CENTER grows when the window is resized
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(header, BorderLayout.NORTH);
        root.add(resultsPane, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);

        // Event handling: one listener serves both the Search button and the Enter key
        searchButton.addActionListener(e -> search());
        searchField.addActionListener(e -> search());
        prevButton.addActionListener(e -> { page--; showPage(); });
        nextButton.addActionListener(e -> { page++; showPage(); });
        reserveButton.addActionListener(e -> reserveSelected());
        results.addListSelectionListener(e -> updateButtons());

        updateButtons();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(560, 320));
        setSize(640, 420);
        setLocationRelativeTo(null);
    }

    private void search() {
        String q = searchField.getText().trim().toLowerCase();
        String cat = (String) category.getSelectedItem();
        matches.clear();
        for (Book b : CATALOGUE) {
            boolean text = q.isEmpty() || b.title().toLowerCase().contains(q)
                                       || b.author().toLowerCase().contains(q);
            boolean inCategory = "All".equals(cat) || b.category().equals(cat);
            if (text && inCategory) matches.add(b);
        }
        page = 0;
        showPage();
    }

    private void showPage() {
        listModel.clear();
        int from = page * PAGE_SIZE, to = Math.min(from + PAGE_SIZE, matches.size());
        for (int i = from; i < to; i++) {
            Book b = matches.get(i);
            listModel.addElement(b.title() + " - " + b.author() + " [" + b.category() + "]"
                                 + (reserved.contains(b.title()) ? "  (reserved)" : ""));
        }
        int pages = Math.max(1, (matches.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        status.setText(matches.isEmpty() ? "No books found."
            : matches.size() + " result(s). Page " + (page + 1) + " of " + pages + ".");
        updateButtons();
    }

    private void reserveSelected() {
        int i = results.getSelectedIndex();
        if (i < 0) { status.setText("Select a book first."); return; }
        Book b = matches.get(page * PAGE_SIZE + i);
        status.setText(reserved.add(b.title()) ? "Reserved: " + b.title()
                                               : "Already reserved: " + b.title());
        int keep = i;
        showPage();
        results.setSelectedIndex(keep);
    }

    private void updateButtons() {
        prevButton.setEnabled(page > 0);
        nextButton.setEnabled((page + 1) * PAGE_SIZE < matches.size());
        reserveButton.setEnabled(results.getSelectedIndex() >= 0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearch().setVisible(true));
    }
}


// Original source: Task04/CompleteTheCode/DrawPad.java
class DrawPad_CompleteTheCode extends Frame {
    private final List<Point> points = new ArrayList<>();
    private final Label status = new Label("Clicks: 0");

    public DrawPad_CompleteTheCode() {
        super("Draw Pad");
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            // TODO 1: Override mouseClicked correctly: store the point, update the // label and request a repaint
            @Override public void mouseClicked(MouseEvent e) {
                points.add(e.getPoint());
                status.setText("Clicks: " + points.size());
                repaint();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); } });
        setSize(400, 300);
    }

    @Override public void paint(Graphics g) {
        // TODO 2: Draw every stored point as a 6 × 6 filled circle centred on the point
        super.paint(g);
        for (Point p : points) {
            g.fillOval(p.x - 3, p.y - 3, 6, 6);
        }
    }

    public static void main(String[] args) { new DrawPad_CompleteTheCode().setVisible(true); }
}


// Original source: Task04/buggy/DrawPad.java
class DrawPad_buggy extends Frame {
    int count = 0;
    Label status = new Label("Clicks: 0");

    public DrawPad_buggy() {
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            public void mouseclicked(MouseEvent e) {      // wrong name: never called
                count++;
                Graphics g = getGraphics();
                g.fillOval(e.getX(), e.getY(), 6, 6);
            }
        });
        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { dispose(); } });
        setSize(400, 300);
        setVisible(true);
    }

    public static void main(String[] args) { new DrawPad_buggy(); }
}


// Original source: Task04/experiment_without_override/DrawPad.java
class DrawPad_experiment_without_override extends Frame {
    private final List<Point> points = new ArrayList<>();
    private final Label status = new Label("Clicks: 0");

    public DrawPad_experiment_without_override() {
        super("Draw Pad");
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            // TODO 1: Override mouseClicked correctly: store the point, update the // label and request a repaint
            public void mouseclicked(MouseEvent e) {
                points.add(e.getPoint());
                status.setText("Clicks: " + points.size());
                repaint();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); } });
        setSize(400, 300);
    }

    @Override public void paint(Graphics g) {
        // TODO 2: Draw every stored point as a 6 × 6 filled circle centred on the point
        super.paint(g);
        for (Point p : points) {
            g.fillOval(p.x - 3, p.y - 3, 6, 6);
        }
    }

    public static void main(String[] args) { new DrawPad_experiment_without_override().setVisible(true); }
}


// Original source: Task04/naive_fixed/DrawPad.java
class DrawPad_naive_fixed extends Frame {
    int count = 0;
    Label status = new Label("Clicks: 0");

    public DrawPad_naive_fixed() {
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {      // name fixed, but still draws with getGraphics()
                count++; status.setText("Clicks: " + count);
                Graphics g = getGraphics();
                g.fillOval(e.getX(), e.getY(), 6, 6);
            }
        });
        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { dispose(); } });
        setSize(400, 300);
        setVisible(true);
    }

    public static void main(String[] args) { new DrawPad_naive_fixed(); }
}


// Original source: Task04/repaired/DrawPad.java
/** Repaired version of the original DrawPad_repaired: model (points) is separated from drawing. */
class DrawPad_repaired extends Frame {
    private int count = 0;
    private final Label status = new Label("Clicks: 0");
    private final ArrayList<Point> points = new ArrayList<>();   // the drawing model

    public DrawPad_repaired() {
        super("Draw Pad (repaired)");
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {   // correct name, checked by @Override
                points.add(e.getPoint());                        // 1. store the point
                count++;                                         // 2. update the counter
                status.setText("Clicks: " + count);              // 3. update the label
                repaint();                                       // 4. ask AWT to call paint()
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
        setSize(400, 300);
    }

    @Override public void paint(Graphics g) {
        super.paint(g);                                          // paint the frame's children first
        for (Point p : points)                                   // redraw the whole model every time
            g.fillOval(p.x - 3, p.y - 3, 6, 6);                  // 6 x 6 dot centred on the click
    }

    public static void main(String[] args) { new DrawPad_repaired().setVisible(true); }
}


// Original source: Task05/CompleteTheCode/Converter.java
class Converter_CompleteTheCode extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target =
        new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter_CompleteTheCode() {
        super("Currency Converter_CompleteTheCode");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);

        // TODO 1: Register ONE lambda listener for both the Convert button and the // Enter key in the amount field
        ActionListener convertListener = e -> convert();
        convertButton.addActionListener(convertListener);
        amount.addActionListener(convertListener);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    private void convert() {
        // TODO 2: Parse the amount and show the converted value with 2 decimals, or // "Invalid amount" if parsing fails
        try {
            double inr = Double.parseDouble(amount.getText().trim());
            if (!(inr >= 0) || Double.isInfinite(inr)) throw new NumberFormatException();
            String cur = (String) target.getSelectedItem();
            result.setText(String.format("%.2f %s", inr * rate(cur), cur));
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter_CompleteTheCode().setVisible(true));
    }
}


// Original source: Task05/v1/A_FrameImplements/Converter.java
class Converter_v1_A_FrameImplements extends JFrame implements ActionListener {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    /** Illustrative rates taken from the assignment code; NOT live market rates. */
    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter_v1_A_FrameImplements() {
        super("Currency Converter_v1_A_FrameImplements");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);
        convertButton.addActionListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose();  }
        });
        pack();
    }

    @Override public void actionPerformed(ActionEvent e) {
        try {
            double inr = Double.parseDouble(amount.getText().trim());
            if (!(inr >= 0) || Double.isInfinite(inr)) throw new NumberFormatException();
            String cur = (String) target.getSelectedItem();
            result.setText(String.format("%.2f %s", inr * rate(cur), cur));
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter_v1_A_FrameImplements().setVisible(true));
    }
}


// Original source: Task05/v1/B_NamedInner/Converter.java
class Converter_v1_B_NamedInner extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    /** Illustrative rates taken from the assignment code; NOT live market rates. */
    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter_v1_B_NamedInner() {
        super("Currency Converter_v1_B_NamedInner");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);
        convertButton.addActionListener(new ConvertHandler());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose();  }
        });
        pack();
    }

    private class ConvertHandler implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            try {
                double inr = Double.parseDouble(amount.getText().trim());
                if (!(inr >= 0) || Double.isInfinite(inr)) throw new NumberFormatException();
                String cur = (String) target.getSelectedItem();
                result.setText(String.format("%.2f %s", inr * rate(cur), cur));
            } catch (NumberFormatException ex) {
                result.setText("Invalid amount");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter_v1_B_NamedInner().setVisible(true));
    }
}


// Original source: Task05/v1/C_Anonymous/Converter.java
class Converter_v1_C_Anonymous extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    /** Illustrative rates taken from the assignment code; NOT live market rates. */
    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter_v1_C_Anonymous() {
        super("Currency Converter_v1_C_Anonymous");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);
        convertButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                try {
                    double inr = Double.parseDouble(amount.getText().trim());
                    if (!(inr >= 0) || Double.isInfinite(inr)) throw new NumberFormatException();
                    String cur = (String) target.getSelectedItem();
                    result.setText(String.format("%.2f %s", inr * rate(cur), cur));
                } catch (NumberFormatException ex) {
                    result.setText("Invalid amount");
                }
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose();  }
        });
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter_v1_C_Anonymous().setVisible(true));
    }
}


// Original source: Task05/v1/D_Lambda/Converter.java
class Converter_v1_D_Lambda extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    /** Illustrative rates taken from the assignment code; NOT live market rates. */
    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter_v1_D_Lambda() {
        super("Currency Converter_v1_D_Lambda");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);
        ActionListener convert = e -> {
            try {
                double inr = Double.parseDouble(amount.getText().trim());
                if (!(inr >= 0) || Double.isInfinite(inr)) throw new NumberFormatException();
                String cur = (String) target.getSelectedItem();
                result.setText(String.format("%.2f %s", inr * rate(cur), cur));
            } catch (NumberFormatException ex) {
                result.setText("Invalid amount");
            }
        };
        convertButton.addActionListener(convert);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose();  }
        });
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter_v1_D_Lambda().setVisible(true));
    }
}


// Original source: Task05/v2/A_FrameImplements/Converter.java
class Converter_v2_A_FrameImplements extends JFrame implements ActionListener {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    /** Illustrative rates taken from the assignment code; NOT live market rates. */
    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter_v2_A_FrameImplements() {
        super("Currency Converter_v2_A_FrameImplements");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);
        convertButton.addActionListener(this);
        amount.addActionListener(this);                 // Enter key
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose();  }
        });
        pack();
    }

    @Override public void actionPerformed(ActionEvent e) {
        try {
            double inr = Double.parseDouble(amount.getText().trim());
            if (!(inr >= 0) || Double.isInfinite(inr)) throw new NumberFormatException();
            String cur = (String) target.getSelectedItem();
            String msg = String.format("%.2f %s", inr * rate(cur), cur);
            result.setText(msg);
            JOptionPane.showMessageDialog(this, msg);
        } catch (NumberFormatException ex) {
            String msg = "Invalid amount";
            result.setText(msg);
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter_v2_A_FrameImplements().setVisible(true));
    }
}


// Original source: Task05/v2/B_NamedInner/Converter.java
class Converter_v2_B_NamedInner extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    /** Illustrative rates taken from the assignment code; NOT live market rates. */
    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter_v2_B_NamedInner() {
        super("Currency Converter_v2_B_NamedInner");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);
        ConvertHandler handler = new ConvertHandler();
        convertButton.addActionListener(handler);
        amount.addActionListener(handler);              // Enter key
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose();  }
        });
        pack();
    }

    private class ConvertHandler implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            try {
                double inr = Double.parseDouble(amount.getText().trim());
                if (!(inr >= 0) || Double.isInfinite(inr)) throw new NumberFormatException();
                String cur = (String) target.getSelectedItem();
                String msg = String.format("%.2f %s", inr * rate(cur), cur);
                result.setText(msg);
                JOptionPane.showMessageDialog(Converter_v2_B_NamedInner.this, msg);
            } catch (NumberFormatException ex) {
                String msg = "Invalid amount";
                result.setText(msg);
                JOptionPane.showMessageDialog(Converter_v2_B_NamedInner.this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter_v2_B_NamedInner().setVisible(true));
    }
}


// Original source: Task05/v2/C_Anonymous/Converter.java
class Converter_v2_C_Anonymous extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    /** Illustrative rates taken from the assignment code; NOT live market rates. */
    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter_v2_C_Anonymous() {
        super("Currency Converter_v2_C_Anonymous");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);
        ActionListener convert = new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                try {
                    double inr = Double.parseDouble(amount.getText().trim());
                    if (!(inr >= 0) || Double.isInfinite(inr)) throw new NumberFormatException();
                    String cur = (String) target.getSelectedItem();
                    String msg = String.format("%.2f %s", inr * rate(cur), cur);
                    result.setText(msg);
                    JOptionPane.showMessageDialog(Converter_v2_C_Anonymous.this, msg);
                } catch (NumberFormatException ex) {
                    String msg = "Invalid amount";
                    result.setText(msg);
                    JOptionPane.showMessageDialog(Converter_v2_C_Anonymous.this, msg, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        convertButton.addActionListener(convert);
        amount.addActionListener(convert);              // Enter key
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose();  }
        });
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter_v2_C_Anonymous().setVisible(true));
    }
}


// Original source: Task05/v2/D_Lambda/Converter.java
class Converter_v2_D_Lambda extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    /** Illustrative rates taken from the assignment code; NOT live market rates. */
    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter_v2_D_Lambda() {
        super("Currency Converter_v2_D_Lambda");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);
        ActionListener convert = e -> {
            try {
                double inr = Double.parseDouble(amount.getText().trim());
                if (!(inr >= 0) || Double.isInfinite(inr)) throw new NumberFormatException();
                String cur = (String) target.getSelectedItem();
                String msg = String.format("%.2f %s", inr * rate(cur), cur);
                result.setText(msg);
                JOptionPane.showMessageDialog(this, msg);
            } catch (NumberFormatException ex) {
                String msg = "Invalid amount";
                result.setText(msg);
                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        convertButton.addActionListener(convert);
        amount.addActionListener(convert);              // Enter key
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose();  }
        });
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter_v2_D_Lambda().setVisible(true));
    }
}


// Original source: Task06/src/campus/events/App.java
/** Entry point: wires the three layers together and starts the UI on the EDT. */
class App {
    public static void main(String[] args) {
        RegistrationService service = new RegistrationService(new JdbcRegistrationRepository());
        SwingUtilities.invokeLater(() -> new MainFrame(service).setVisible(true));
    }
}


// Original source: Task06/src/campus/events/dao/Database.java
/** Connection factory; settings come from environment variables, never from source code. */
final class Database {
    private static final String URL = System.getenv().getOrDefault("CAMPUS_DB_URL",
        "jdbc:mysql://localhost:3306/campus_events?useSSL=false&allowPublicKeyRetrieval=true&useCursorFetch=true");
    private static final String USER = System.getenv().getOrDefault("CAMPUS_DB_USER", "campus_app");
    private static final String PASSWORD = System.getenv().getOrDefault("CAMPUS_DB_PASSWORD", "ChangeMe#123");

    private Database() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


// Original source: Task06/src/campus/events/dao/JdbcRegistrationRepository.java
/** JDBC implementation: every statement is parameterised and every resource is closed. */
class JdbcRegistrationRepository implements RegistrationRepository {

    private static final String SELECT_ROWS =
        "SELECT r.reg_id, s.student_id, s.name, s.department, e.title, r.registered_at "
      + "FROM registrations r JOIN students s ON s.student_id = r.student_id "
      + "JOIN events e ON e.event_id = r.event_id ";

    @Override public List<Event> findEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT event_id, title FROM events ORDER BY event_date");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) events.add(new Event(rs.getInt(1), rs.getString(2)));
        }
        return events;
    }

    @Override public boolean exists(String studentId, int eventId) throws SQLException {
        String sql = "SELECT 1 FROM registrations WHERE student_id = ? AND event_id = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, eventId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    @Override public boolean register(RegistrationRequest r) throws SQLException {
        String upsert = "INSERT INTO students (student_id, name, email, department, year_of_study) "
            + "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), "
            + "email = VALUES(email), department = VALUES(department), year_of_study = VALUES(year_of_study)";
        String insert = "INSERT INTO registrations (student_id, event_id) VALUES (?, ?)";
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);                          // both statements succeed or neither does
            try (PreparedStatement s = c.prepareStatement(upsert);
                 PreparedStatement g = c.prepareStatement(insert)) {
                s.setString(1, r.studentId()); s.setString(2, r.name()); s.setString(3, r.email());
                s.setString(4, r.department()); s.setInt(5, r.year());
                s.executeUpdate();
                g.setString(1, r.studentId()); g.setInt(2, r.eventId());
                g.executeUpdate();
                c.commit();
                return true;
            } catch (SQLException e) {
                c.rollback();
                if (e.getErrorCode() == 1062 && String.valueOf(e.getMessage()).contains("uk_student_event"))
                    return false;                            // UNIQUE constraint caught a duplicate
                throw e;
            }
        }
    }

    @Override public PageResult findPage(Integer eventId, int limit, int offset) throws SQLException {
        String where = eventId == null ? "" : "WHERE r.event_id = ? ";
        String countSql = "SELECT COUNT(*) FROM registrations r " + where;
        String pageSql = SELECT_ROWS + where + "ORDER BY r.reg_id LIMIT ? OFFSET ?";
        try (Connection c = Database.getConnection()) {
            long total;
            try (PreparedStatement ps = c.prepareStatement(countSql)) {
                if (eventId != null) ps.setInt(1, eventId);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); total = rs.getLong(1); }
            }
            List<RegistrationRow> rows = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(pageSql)) {
                int i = 1;
                if (eventId != null) ps.setInt(i++, eventId);
                ps.setInt(i++, limit);
                ps.setInt(i, offset);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) rows.add(map(rs));
                }
            }
            return new PageResult(rows, total);
        }
    }

    @Override public void forEach(Integer eventId, Consumer<RegistrationRow> consumer) throws SQLException {
        String sql = SELECT_ROWS + (eventId == null ? "" : "WHERE r.event_id = ? ") + "ORDER BY r.reg_id";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setFetchSize(1000);                           // stream in chunks (useCursorFetch=true)
            if (eventId != null) ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) consumer.accept(map(rs));
            }
        }
    }

    private static RegistrationRow map(ResultSet rs) throws SQLException {
        return new RegistrationRow(rs.getLong(1), rs.getString(2), rs.getString(3),
                                   rs.getString(4), rs.getString(5), rs.getTimestamp(6));
    }
}


// Original source: Task06/src/campus/events/dao/RegistrationRepository.java
/** Data-access contract; the service layer depends only on this interface. */
interface RegistrationRepository {
    List<Event> findEvents() throws SQLException;
    boolean exists(String studentId, int eventId) throws SQLException;
    /** Inserts student (upsert) and registration in one transaction; false if it is a duplicate. */
    boolean register(RegistrationRequest r) throws SQLException;
    /** eventId == null means all events. */
    PageResult findPage(Integer eventId, int limit, int offset) throws SQLException;
    /** Streams every matching row without holding them all in memory. */
    void forEach(Integer eventId, Consumer<RegistrationRow> consumer) throws SQLException;
}


// Original source: Task06/src/campus/events/model/Event.java
/** An event students can register for; id 0 is used by the UI for "All events". */
record Event(int id, String title) {
    @Override public String toString() { return title; }
}


// Original source: Task06/src/campus/events/model/PageResult.java
/** One page of rows plus the total number of matching registrations. */
record PageResult(List<RegistrationRow> rows, long total) { }


// Original source: Task06/src/campus/events/model/RegistrationRequest.java
/** Data entered on the registration form. */
record RegistrationRequest(String studentId, String name, String email,
                                  String department, int year, int eventId) { }


// Original source: Task06/src/campus/events/model/RegistrationRow.java
/** One row of the committee table (registration joined with student and event). */
record RegistrationRow(long id, String studentId, String name, String department,
                              String eventTitle, Timestamp registeredAt) { }


// Original source: Task06/src/campus/events/service/DuplicateRegistrationException.java
/** The student is already registered for the chosen event. */
class DuplicateRegistrationException extends Exception {
    public DuplicateRegistrationException(String message) { super(message); }
}


// Original source: Task06/src/campus/events/service/RegistrationService.java
/** Business rules: validation, duplicate prevention, paging and CSV export. No Swing here. */
class RegistrationService {
    public static final List<String> DEPARTMENTS = List.of("CSE", "ECE", "EEE", "MECH", "CIVIL", "AI&DS");
    private static final Pattern STUDENT_ID = Pattern.compile("[A-Za-z0-9]{6,15}");
    private static final Pattern NAME = Pattern.compile("[\\p{L} .'-]{2,60}");
    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");

    private final RegistrationRepository repo;

    public RegistrationService(RegistrationRepository repo) { this.repo = repo; }

    /** Pure validation; returns the cleaned request or throws with all messages. */
    public RegistrationRequest validate(RegistrationRequest in) throws ValidationException {
        List<String> errors = new ArrayList<>();
        String id = in.studentId() == null ? "" : in.studentId().trim();
        String name = in.name() == null ? "" : in.name().trim();
        String email = in.email() == null ? "" : in.email().trim();
        if (!STUDENT_ID.matcher(id).matches()) errors.add("Register number must be 6-15 letters or digits.");
        if (!NAME.matcher(name).matches()) errors.add("Name must be 2-60 letters (spaces, . ' - allowed).");
        if (email.length() > 100 || !EMAIL.matcher(email).matches()) errors.add("Enter a valid e-mail address.");
        if (!DEPARTMENTS.contains(in.department())) errors.add("Select a department.");
        if (in.year() < 1 || in.year() > 4) errors.add("Year of study must be between 1 and 4.");
        if (in.eventId() <= 0) errors.add("Select an event.");
        if (!errors.isEmpty()) throw new ValidationException(errors);
        return new RegistrationRequest(id, name, email, in.department(), in.year(), in.eventId());
    }

    public void register(RegistrationRequest in)
            throws ValidationException, DuplicateRegistrationException, SQLException {
        RegistrationRequest r = validate(in);
        String msg = "Student " + r.studentId() + " is already registered for this event.";
        if (repo.exists(r.studentId(), r.eventId()))        // friendly early check
            throw new DuplicateRegistrationException(msg);
        if (!repo.register(r))                              // UNIQUE key is the final guard (race safe)
            throw new DuplicateRegistrationException(msg);
    }

    public List<Event> loadEvents() throws SQLException { return repo.findEvents(); }

    public PageResult loadPage(Integer eventId, int pageIndex, int pageSize) throws SQLException {
        return repo.findPage(eventId, pageSize, pageIndex * pageSize);
    }

    /** Streams all matching registrations to the writer as CSV; returns the number of data rows. */
    public long exportCsv(Integer eventId, Writer out) throws SQLException, IOException {
        long[] count = {0};
        out.write("Reg ID,Register Number,Name,Department,Event,Registered At\n");
        try {
            repo.forEach(eventId, row -> {
                try {
                    out.write(row.id() + "," + csv(row.studentId()) + "," + csv(row.name()) + ","
                        + csv(row.department()) + "," + csv(row.eventTitle()) + "," + row.registeredAt() + "\n");
                    count[0]++;
                } catch (IOException e) { throw new UncheckedIOException(e); }
            });
        } catch (UncheckedIOException e) { throw e.getCause(); }
        out.flush();
        return count[0];
    }

    static String csv(String s) {
        return (s.contains(",") || s.contains("\"") || s.contains("\n"))
            ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }
}


// Original source: Task06/src/campus/events/service/ValidationException.java
/** Carries every validation message so the user can fix all problems at once. */
class ValidationException extends Exception {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join("\n", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() { return errors; }
}


// Original source: Task06/src/campus/events/ui/CommitteePanel.java
/** Committee screen: filter, paged table and CSV export. All database work runs in SwingWorkers. */
class CommitteePanel extends JPanel {
    static final int PAGE_SIZE = 25;
    private final RegistrationService service;
    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"Reg ID", "Register No.", "Name", "Department", "Event", "Registered At"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JComboBox<Event> filter = new JComboBox<>();
    private final JButton refresh = new JButton("Refresh");
    private final JButton prev = new JButton("Prev");
    private final JButton next = new JButton("Next");
    private final JButton export = new JButton("Export CSV");
    private final JLabel info = new JLabel("No data loaded");
    private int page = 0;
    private long total = 0;
    private int requestId = 0;                 // used to ignore stale worker results

    CommitteePanel(RegistrationService service) {
        super(new BorderLayout(6, 6));
        this.service = service;
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Event:"));
        top.add(filter);
        top.add(refresh);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(prev); bottom.add(info); bottom.add(next); bottom.add(export);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        filter.addActionListener(e -> { page = 0; loadPage(); });
        refresh.addActionListener(e -> loadPage());
        prev.addActionListener(e -> { page--; loadPage(); });
        next.addActionListener(e -> { page++; loadPage(); });
        export.addActionListener(e -> exportCsv());
    }

    void setEvents(List<Event> events) {
        filter.removeAllItems();
        filter.addItem(new Event(0, "All events"));      // adding the first item fires the filter listener
        events.forEach(filter::addItem);
    }

    private Integer selectedEventId() {
        Event ev = (Event) filter.getSelectedItem();
        return (ev == null || ev.id() == 0) ? null : ev.id();
    }

    void loadPage() {
        final int myRequest = ++requestId;
        final int myPage = page;
        final Integer eventId = selectedEventId();
        setBusy(true);
        new SwingWorker<PageResult, Void>() {
            @Override protected PageResult doInBackground() throws SQLException {
                return service.loadPage(eventId, myPage, PAGE_SIZE);   // LIMIT/OFFSET query
            }
            @Override protected void done() {
                if (myRequest != requestId) return;                    // a newer request superseded this one
                try {
                    PageResult r = get();
                    total = r.total();
                    model.setRowCount(0);
                    for (RegistrationRow row : r.rows())
                        model.addRow(new Object[]{row.id(), row.studentId(), row.name(),
                                                  row.department(), row.eventTitle(), row.registeredAt()});
                    long pages = Math.max(1, (total + PAGE_SIZE - 1) / PAGE_SIZE);
                    info.setText("Page " + (page + 1) + " of " + pages + " (" + total + " registrations)");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    info.setText("Load failed");
                    JOptionPane.showMessageDialog(CommitteePanel.this, "Could not load registrations:\n"
                        + ex.getCause().getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
                }
                setBusy(false);
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        refresh.setEnabled(!busy); filter.setEnabled(!busy); export.setEnabled(!busy);
        prev.setEnabled(!busy && page > 0);
        next.setEnabled(!busy && (long) (page + 1) * PAGE_SIZE < total);
        if (busy) info.setText("Loading...");
    }

    void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("registrations.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        final Path path = chooser.getSelectedFile().toPath();
        if (Files.exists(path) && JOptionPane.showConfirmDialog(this, path.getFileName() + " exists. Overwrite?",
                "Confirm overwrite", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        final Integer eventId = selectedEventId();
        export.setEnabled(false);
        new SwingWorker<Long, Void>() {
            @Override protected Long doInBackground() throws SQLException, IOException {
                try (var w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                    return service.exportCsv(eventId, w);               // streams rows, constant memory
                }
            }
            @Override protected void done() {
                export.setEnabled(true);
                try {
                    JOptionPane.showMessageDialog(CommitteePanel.this, "Exported " + get() + " registrations to\n"
                        + path, "Export complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(CommitteePanel.this, "Export failed:\n"
                        + ex.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}


// Original source: Task06/src/campus/events/ui/MainFrame.java
/** Main window: menu bar plus a CardLayout holding the two screens. */
class MainFrame extends JFrame {
    private static final String FORM = "form", COMMITTEE = "committee";
    private final CardLayout cards = new CardLayout();
    private final JPanel deck = new JPanel(cards);
    private final CommitteePanel committee;
    private final RegistrationPanel form;
    private final RegistrationService service;

    public MainFrame(RegistrationService service) {
        super("Campus Event Registration");
        this.service = service;
        committee = new CommitteePanel(service);
        form = new RegistrationPanel(service, committee::loadPage);
        deck.add(form, FORM);
        deck.add(committee, COMMITTEE);
        setContentPane(deck);
        setJMenuBar(buildMenu());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { confirmExit(); }
        });
        setMinimumSize(new Dimension(640, 420));
        setSize(860, 520);
        setLocationRelativeTo(null);
        loadEvents();
    }

    private JMenuBar buildMenu() {
        JMenu file = new JMenu("File");
        JMenuItem export = new JMenuItem("Export CSV...");
        JMenuItem exit = new JMenuItem("Exit");
        export.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
        export.addActionListener(e -> { show(COMMITTEE); committee.exportCsv(); });
        exit.addActionListener(e -> confirmExit());
        file.add(export); file.addSeparator(); file.add(exit);

        JMenu view = new JMenu("View");
        JMenuItem register = new JMenuItem("Register Student");
        JMenuItem list = new JMenuItem("Committee View");
        JMenuItem refresh = new JMenuItem("Refresh");
        register.addActionListener(e -> show(FORM));
        list.addActionListener(e -> show(COMMITTEE));
        refresh.addActionListener(e -> committee.loadPage());
        view.add(register); view.add(list); view.addSeparator(); view.add(refresh);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Campus Event Registration\nCSA09 Assignment 4 - Task 6", "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);

        JMenuBar bar = new JMenuBar();
        bar.add(file); bar.add(view); bar.add(help);
        return bar;
    }

    private void show(String card) {
        cards.show(deck, card);
        if (COMMITTEE.equals(card)) committee.loadPage();
    }

    private void loadEvents() {
        new SwingWorker<List<Event>, Void>() {
            @Override protected List<Event> doInBackground() throws Exception { return service.loadEvents(); }
            @Override protected void done() {
                try {
                    List<Event> events = get();
                    form.setEvents(events);
                    committee.setEvents(events);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Cannot reach the database:\n"
                        + ex.getCause().getMessage(), "Connection error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void confirmExit() {
        int c = JOptionPane.showConfirmDialog(this, "Exit the application?", "Confirm exit",
                                              JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { dispose();  }
    }
}


// Original source: Task06/src/campus/events/ui/RegistrationPanel.java
/** Student registration screen: collects input, shows dialogs, delegates to the service. */
class RegistrationPanel extends JPanel {
    private final RegistrationService service;
    private final Runnable onRegistered;
    private final JTextField studentId = new JTextField(18);
    private final JTextField name = new JTextField(18);
    private final JTextField email = new JTextField(18);
    private final JComboBox<String> department =
        new JComboBox<>(RegistrationService.DEPARTMENTS.toArray(new String[0]));
    private final JComboBox<Integer> year = new JComboBox<>(new Integer[]{1, 2, 3, 4});
    private final JComboBox<Event> event = new JComboBox<>();
    private final JButton register = new JButton("Register");
    private final JButton clear = new JButton("Clear");
    private final JLabel status = new JLabel(" ");

    RegistrationPanel(RegistrationService service, Runnable onRegistered) {
        super(new GridBagLayout());
        this.service = service;
        this.onRegistered = onRegistered;
        addRow(0, "Register number:", studentId);
        addRow(1, "Name:", name);
        addRow(2, "E-mail:", email);
        addRow(3, "Department:", department);
        addRow(4, "Year of study:", year);
        addRow(5, "Event:", event);
        JPanel buttons = new JPanel();
        buttons.add(register);
        buttons.add(clear);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2; c.insets = new Insets(10, 6, 4, 6);
        add(buttons, c);
        c.gridy = 7;
        add(status, c);

        register.addActionListener(e -> onRegister());
        clear.addActionListener(e -> clearForm());
    }

    private void addRow(int row, String label, java.awt.Component field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row; c.insets = new Insets(4, 6, 4, 6); c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; add(new JLabel(label), c);
        c.gridx = 1; add(field, c);
    }

    void setEvents(List<Event> events) {
        event.removeAllItems();
        events.forEach(event::addItem);
    }

    private RegistrationRequest readForm() {
        Event ev = (Event) event.getSelectedItem();
        return new RegistrationRequest(studentId.getText(), name.getText(), email.getText(),
            (String) department.getSelectedItem(), (Integer) year.getSelectedItem(), ev == null ? 0 : ev.id());
    }

    private void onRegister() {
        final RegistrationRequest req;
        try {
            req = service.validate(readForm());                      // cheap, no I/O: safe on the EDT
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, "Please correct the following:\n- "
                + String.join("\n- ", ex.getErrors()), "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Register " + req.name() + " ("
            + req.studentId() + ") for " + event.getSelectedItem() + "?",
            "Confirm registration", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        register.setEnabled(false);
        status.setText("Saving registration...");
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                service.register(req);                               // JDBC runs off the EDT
                return null;
            }
            @Override protected void done() {
                register.setEnabled(true);
                try {
                    get();
                    status.setText("Registered " + req.studentId() + ".");
                    JOptionPane.showMessageDialog(RegistrationPanel.this, "Registration successful.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    onRegistered.run();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    Throwable t = ex.getCause();
                    status.setText("Registration failed.");
                    String msg = t instanceof DuplicateRegistrationException ? t.getMessage()
                        : t instanceof ValidationException ? t.getMessage()
                        : t instanceof SQLException ? "Database error: " + t.getMessage()
                        : "Unexpected error: " + t;
                    JOptionPane.showMessageDialog(RegistrationPanel.this, msg,
                        t instanceof DuplicateRegistrationException ? "Duplicate registration" : "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void clearForm() {
        studentId.setText(""); name.setText(""); email.setText("");
        department.setSelectedIndex(0); year.setSelectedIndex(0);
        studentId.requestFocusInWindow();
    }
}


// Original source: Task06/test/ServiceSmokeTest.java
/** Runs the service layer against an in-memory repository (no MySQL, no GUI). */
class ServiceSmokeTest {
    static class FakeRepo implements RegistrationRepository {
        final Set<String> keys = new HashSet<>();
        final List<RegistrationRow> rows = new ArrayList<>();
        public List<Event> findEvents() { return List.of(new Event(1, "Cultural Night")); }
        public boolean exists(String s, int e) { return keys.contains(s + "|" + e); }
        public boolean register(RegistrationRequest r) {
            if (!keys.add(r.studentId() + "|" + r.eventId())) return false;
            rows.add(new RegistrationRow(rows.size() + 1, r.studentId(), r.name(), r.department(),
                     "Cultural Night", new Timestamp(0)));
            return true;
        }
        public PageResult findPage(Integer ev, int limit, int offset) {
            int to = Math.min(rows.size(), offset + limit);
            return new PageResult(offset >= rows.size() ? List.of() : rows.subList(offset, to), rows.size());
        }
        public void forEach(Integer ev, Consumer<RegistrationRow> c) { rows.forEach(c); }
    }

    static int failures = 0;
    static void check(String label, boolean ok) {
        if (!ok) failures++;
        System.out.println((ok ? "PASS  " : "FAIL  ") + label);
    }

    public static void main(String[] args) throws Exception {
        FakeRepo repo = new FakeRepo();
        RegistrationService svc = new RegistrationService(repo);
        RegistrationRequest good = new RegistrationRequest("192572082", "Nithiya", "n@example.edu", "CSE", 2, 1);

        svc.register(good);
        check("successful registration stored", repo.rows.size() == 1);

        try { svc.register(good); check("duplicate rejected", false); }
        catch (DuplicateRegistrationException e) { check("duplicate rejected: " + e.getMessage(), true); }

        try {
            svc.register(new RegistrationRequest("12", "", "bad", "XYZ", 9, 0));
            check("invalid input rejected", false);
        } catch (ValidationException e) {
            check("invalid input rejected with " + e.getErrors().size() + " messages", e.getErrors().size() == 6);
            e.getErrors().forEach(m -> System.out.println("        - " + m));
        }
        check("nothing stored for invalid input", repo.rows.size() == 1);

        StringWriter sw = new StringWriter();
        long n = svc.exportCsv(null, sw);
        check("CSV export wrote " + n + " data row(s)", n == 1 && sw.toString().startsWith("Reg ID,"));
        check("paging returns total", svc.loadPage(null, 0, 25).total() == 1);
        System.out.println(failures == 0 ? "ALL TESTS PASSED" : failures + " TEST(S) FAILED");
        if (failures > 0) System.exit(1);
    }
}
