// CSA0907 - Assignment 5 | Combined Java source
// Student: NITHIYASRI S | Register: 192572082
// Compile: javac Main.java  |  Run: java Main a1
// MySQL tasks require Connector/J and the database schema from schema.sql.

import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.Arrays;
import java.util.List;

// ===== TaskA1 / BusPassBinary.java =====
class BusPassBinary {
    public static void main(String[] args)throws Exception {
        Path p=Files.createTempFile("buspass", ".bin");
        try(DataOutputStream out=new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(p)))){
            out.writeUTF("P1001");out.writeUTF("192572082");out.writeUTF("Anu|Sri");
            out.writeInt(12);out.writeUTF("Poonamallee");out.writeUTF("2027-05-31");out.writeDouble(4500.0);
        }
        try(DataInputStream in=new DataInputStream(new BufferedInputStream(Files.newInputStream(p)))){
            System.out.printf("%s %s %s %d %s %s %.2f%n",in.readUTF(),in.readUTF(),in.readUTF(),in.readInt(),in.readUTF(),in.readUTF(),in.readDouble());
        }Files.delete(p);
    }
}

// ===== TaskA2 / BufferedCount.java =====
class BufferedCount {
    public static void main(String[] args)throws Exception {
        if(args.length!=1){System.out.println("Usage: java BufferedCount <file>");return;}
        long lines=0;try(InputStream in=new BufferedInputStream(Files.newInputStream(Path.of(args[0])))){
            byte[] bytes=new byte[65536];int n;while((n=in.read(bytes))!=-1)for(int i=0;i<n;i++)if(bytes[i]=='\n')lines++;}
        System.out.println("Newline count: "+lines);
    }
}

// ===== TaskA3 / DaoExample.java =====
interface Dao<T,K> {void insert(T x)throws SQLException;Optional<T> findById(K id)throws SQLException;List<T> findAll()throws SQLException;void update(T x)throws SQLException;void delete(K id)throws SQLException;}
class Student {final String regNo,name;Student(String regNo,String name){this.regNo=regNo;this.name=name;}}
class StudentDao implements Dao<Student,String> {
    private final Connection c;StudentDao(Connection c){this.c=c;}
    public void insert(Student x)throws SQLException {try(PreparedStatement p=c.prepareStatement("INSERT INTO student(reg_no,name) VALUES(?,?)")){p.setString(1,x.regNo);p.setString(2,x.name);p.executeUpdate();}}
    public Optional<Student> findById(String id)throws SQLException {try(PreparedStatement p=c.prepareStatement("SELECT reg_no,name FROM student WHERE reg_no=?")){p.setString(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(new Student(r.getString(1),r.getString(2))):Optional.empty();}}}
    public List<Student> findAll()throws SQLException {List<Student> out=new ArrayList<>();try(Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT reg_no,name FROM student")){while(r.next())out.add(new Student(r.getString(1),r.getString(2)));}return out;}
    public void update(Student x)throws SQLException {try(PreparedStatement p=c.prepareStatement("UPDATE student SET name=? WHERE reg_no=?")){p.setString(1,x.name);p.setString(2,x.regNo);p.executeUpdate();}}
    public void delete(String id)throws SQLException {try(PreparedStatement p=c.prepareStatement("DELETE FROM student WHERE reg_no=?")){p.setString(1,id);p.executeUpdate();}}
}
class DaoExample {public static void main(String[] args){System.out.println("Dao<Student,String> interface and JDBC implementation compiled successfully.");}}

// ===== TaskA4 / HostelSerialization.java =====
class HostelSerialization {
    static class Resident implements Serializable {
        private static final long serialVersionUID=1L;
        String name, regNo, phone;
        transient String password;
        transient java.sql.Connection connection;
        Resident(String n,String r,String p){name=n;regNo=r;password=p;}
    }
    static class Room implements Serializable {private static final long serialVersionUID=1L;List<Resident> residents=new ArrayList<>();}
    static class Hostel implements Serializable {private static final long serialVersionUID=1L;List<Room> rooms=new ArrayList<>();}
    public static void main(String[] args)throws Exception {
        Hostel h=new Hostel();Room r=new Room();r.residents.add(new Resident("Divya","192311003","secret"));h.rooms.add(r);
        byte[] bytes;try(ByteArrayOutputStream b=new ByteArrayOutputStream();ObjectOutputStream o=new ObjectOutputStream(b)){o.writeObject(h);o.flush();bytes=b.toByteArray();}
        try(ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(bytes))){Hostel loaded=(Hostel)in.readObject();Resident x=loaded.rooms.get(0).residents.get(0);
            System.out.println("Resident: "+x.name+", password after load: "+x.password+", connection: "+x.connection);}
    }
}

// ===== TaskA5 / WalletTransfer.java =====
class WalletTransfer {
    static void transfer(Connection c,BigDecimal amount)throws SQLException {
        boolean previous=c.getAutoCommit();c.setAutoCommit(false);
        try(PreparedStatement debit=c.prepareStatement("UPDATE wallet SET balance=balance-? WHERE id=? AND balance>=?");
            PreparedStatement credit=c.prepareStatement("UPDATE wallet SET balance=balance+? WHERE id=?")){
            debit.setBigDecimal(1,amount);debit.setString(2,"hostel");debit.setBigDecimal(3,amount);
            if(debit.executeUpdate()!=1)throw new SQLException("Insufficient funds");
            credit.setBigDecimal(1,amount);credit.setString(2,"canteen");
            if(credit.executeUpdate()!=1)throw new SQLException("Missing destination wallet");
            c.commit();
        }catch(SQLException|RuntimeException e){try{c.rollback();}catch(SQLException r){e.addSuppressed(r);}throw e;}
        finally{c.setAutoCommit(previous);}
    }
    public static void main(String[] args)throws Exception {
        try(Connection c=DriverManager.getConnection(System.getenv().getOrDefault("JDBC_URL","jdbc:mysql://localhost:3306/assignment5_test"),System.getenv().getOrDefault("JDBC_USER","root"),System.getenv().getOrDefault("JDBC_PASSWORD",""))){transfer(c,new BigDecimal("500.00"));System.out.println("Transfer committed");}
    }
}

// ===== TaskB1 / BusPassFileStore.java =====
class BusPassFileStore {
    static String escape(String s) { return s.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n"); }
    static String unescape(String s) {
        StringBuilder b = new StringBuilder(); boolean slash=false;
        for (char c : s.toCharArray()) { if (slash) { b.append(c=='p'?'|':c=='n'?'\n':c); slash=false; }
            else if(c=='\\') slash=true; else b.append(c); }
        if(slash) throw new IllegalArgumentException("Bad escape"); return b.toString();
    }
    static String[] parse(String line) {
        String[] x=line.split("\\|",-1); if(x.length!=7) return null;
        try { for(int i=0;i<x.length;i++) x[i]=unescape(x[i]);
            if(x[0].isBlank() || x[1].isBlank() || x[2].isBlank() || x[4].isBlank()) return null;
            Integer.parseInt(x[3]); java.time.LocalDate.parse(x[5]);
            if(Double.parseDouble(x[6])<0) return null; return x;
        } catch(RuntimeException e) { return null; }
    }
    static void add(Path f,String[] x) throws IOException {
        if(x.length!=7) throw new IllegalArgumentException("Seven fields required");
        String[] y=x.clone(); for(int i=0;i<y.length;i++) y[i]=escape(y[i]);
        Files.writeString(f,String.join("|",y)+System.lineSeparator(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
    }
    static String[] find(Path f,String id) throws IOException {
        if(!Files.exists(f)) return null;
        try(BufferedReader r=Files.newBufferedReader(f)) { String line;
            while((line=r.readLine())!=null) {String[] x=parse(line); if(x!=null && x[0].equals(id)) return x;} }
        return null;
    }
    static List<String[]> route(Path f,int route) throws IOException {
        List<String[]> out=new ArrayList<>(); if(!Files.exists(f)) return out;
        try(BufferedReader r=Files.newBufferedReader(f)) {String line;
            while((line=r.readLine())!=null) {String[] x=parse(line); if(x!=null && Integer.parseInt(x[3])==route) out.add(x);} }
        return out;
    }
    public static void main(String[] args) throws Exception {
        Path f=Files.createTempFile("buspass-full", ".txt");
        add(f,new String[]{"P1","192572082","Anu|Sri","12","Poonamallee","2027-05-31","4500"});
        Files.writeString(f,"BAD|ROW\n",StandardOpenOption.APPEND);
        add(f,new String[]{"P2","192572083","Devi","7","Avadi","2027-05-31","3800"});
        System.out.println("Find P1: "+Arrays.toString(find(f,"P1")));
        System.out.println("Missing P9: "+Arrays.toString(find(f,"P9")));
        System.out.println("Route 12 count: "+route(f,12).size()); Files.delete(f);
    }
}

// ===== TaskB1 / LineCounterBenchmark.java =====
class LineCounterBenchmark {
    static long unbuffered(Path p) throws IOException {long count=0;try(FileInputStream in=new FileInputStream(p.toFile())) {int x;while((x=in.read())!=-1)if(x==10)count++;}return count;}
    static long buffered(Path p) throws IOException {long count=0;try(InputStream in=new BufferedInputStream(Files.newInputStream(p),65536)){byte[] b=new byte[65536];int n;while((n=in.read(b))!=-1)for(int i=0;i<n;i++)if(b[i]==10)count++;}return count;}
    static void make(Path p,int mb) throws IOException {byte[] line="192572082,Present,2026-11-21\n".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        try(OutputStream o=new BufferedOutputStream(Files.newOutputStream(p))){long left=mb*1024L*1024L;while(left>0){int n=(int)Math.min(line.length,left);o.write(line,0,n);left-=n;}}}
    public static void main(String[] args)throws Exception {System.out.println("size_mb,method,trial,ns,lines");
        for(int mb:new int[]{1,10,50}){Path p=Files.createTempFile("attendance", ".log");try{make(p,mb);
            for(int trial=1;trial<=3;trial++){long t=System.nanoTime(),n=unbuffered(p);long elapsed=System.nanoTime()-t;
                System.out.printf("%d,unbuffered,%d,%d,%d%n",mb,trial,elapsed,n);
                t=System.nanoTime();long m=buffered(p);elapsed=System.nanoTime()-t;
                if(m!=n)throw new AssertionError("Mismatch");
                System.out.printf("%d,buffered,%d,%d,%d%n",mb,trial,elapsed,m);}}
            finally{Files.deleteIfExists(p);}}}
}

// ===== TaskB1 / PassGrowthBenchmark.java =====
class PassGrowthBenchmark {
    public static void main(String[] args)throws Exception {
        System.out.println("records,trial,find_last_ns,found");
        for(int n:new int[]{1000,10000,100000}){
            Path p=Files.createTempFile("passgrowth", ".csv");
            try{PassStore s=new PassStore(p);
                try(java.io.BufferedWriter w=Files.newBufferedWriter(p)){
                    for(int i=0;i<n;i++) {w.write("P"+i+",R"+i+",Student"+i+",12,4500.0");w.newLine();}}
                for(int t=1;t<=5;t++){long start=System.nanoTime();String[] found=s.find("P"+(n-1));long elapsed=System.nanoTime()-start;
                    System.out.println(n+","+t+","+elapsed+","+(found!=null));}}
            finally{Files.deleteIfExists(p);}}
    }
}

// ===== TaskB1 / PassStore.java =====
class PassStore {
    private final Path file;
    PassStore(Path file) { this.file = file; }

    public void add(String passId, String regNo, String name, int route, double amount) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(String.join(",", passId, regNo, name,
                    String.valueOf(route), String.valueOf(amount)));
            w.newLine();
        }
    }

    /** Returns the five fields of the pass with the given ID, or null if not found. */
    public String[] find(String passId) throws IOException {
        // TODO 1 completed: scan, skip malformed records, match pass ID.
        if (!Files.exists(file)) return null;
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length != 5) continue;
                try {
                    Integer.parseInt(fields[3]);
                    Double.parseDouble(fields[4]);
                } catch (NumberFormatException e) { continue; }
                if (fields[0].equals(passId)) return fields;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        Path f = Files.createTempFile("passes", ".csv");
        PassStore store = new PassStore(f);
        store.add("P1001", "192311001", "Arun", 12, 4500);
        Files.writeString(f, "P1002,DAMAGED LINE\n", StandardOpenOption.APPEND);
        store.add("P1003", "192311003", "Divya", 7, 3800);
        System.out.println(Arrays.toString(store.find("P1003")));
        System.out.println(Arrays.toString(store.find("P1002")));
        Files.delete(f);
    }
}

// ===== TaskB2 / AdversarialLogin.java =====
class AdversarialLogin {
    public static void main(String[] args)throws Exception {
        String url=System.getenv().getOrDefault("JDBC_URL","jdbc:mysql://localhost:3306/assignment5_test");
        String user=System.getenv().getOrDefault("JDBC_USER","root");
        String pwd=System.getenv().getOrDefault("JDBC_PASSWORD","");
        String id="student' -- "; String invalidPassword="wrong";
        try(Connection c=DriverManager.getConnection(url,user,pwd)){
            String unsafe="SELECT * FROM users WHERE id='"+id+"' AND pwd='"+invalidPassword+"'";
            try(Statement s=c.createStatement();ResultSet rs=s.executeQuery(unsafe)){
                System.out.println("Unsafe login accepted invalid password: "+rs.next());}
            try(PreparedStatement ps=c.prepareStatement("SELECT * FROM users WHERE id=? AND pwd=?")){
                ps.setString(1,id);ps.setString(2,invalidPassword);
                try(ResultSet rs=ps.executeQuery()){
                    System.out.println("Prepared login accepted invalid password: "+rs.next());}}
        }
    }
}

// ===== TaskB2 / DamagedPassTest.java =====
class DamagedPassTest {
    public static void main(String[] args)throws Exception {
        Path p=Files.createTempFile("damaged", ".csv");try{
            Files.writeString(p,"P1,R1,Good,12,4500\nP2,R2,Wrong\nP3,R3,Invalid,7,not-number\n\nP4,R4,Valid,9,3800\n");
            PassStore s=new PassStore(p);
            for(String id:new String[]{"P1","P2","P3","P4","P9"})System.out.println(id+" -> "+Arrays.toString(s.find(id)));
        }finally{Files.deleteIfExists(p);}
    }
}

// ===== TaskB3 / JdbcBench.java =====
class JdbcBench {
    static String url(){return System.getenv().getOrDefault("JDBC_URL","jdbc:mysql://localhost:3306/assignment5_test");}
    static Connection connect()throws SQLException{return DriverManager.getConnection(url(),System.getenv().getOrDefault("JDBC_USER","root"),System.getenv().getOrDefault("JDBC_PASSWORD",""));}
    static void clear(Connection c)throws SQLException {try(Statement s=c.createStatement()){s.executeUpdate("DELETE FROM bus_pass");}}
    static String[] pass(int i){return new String[]{"P"+i,"R"+i,"Student"+i,""+(i%30+1),""+(1000+i%500)};}
    static int insert(Connection c,int n,String mode)throws SQLException {
        String sql="INSERT INTO bus_pass(pass_id,reg_no,name,route_no,amount) VALUES(?,?,?,?,?)";
        if(mode.equals("Statement")){try(Statement s=c.createStatement()){for(int i=0;i<n;i++){String[] p=pass(i);s.executeUpdate("INSERT INTO bus_pass VALUES('"+p[0]+"','"+p[1]+"','"+p[2]+"',"+p[3]+","+p[4]+")");}}return n;}
        if(mode.equals("Prepared")){try(PreparedStatement ps=c.prepareStatement(sql)){for(int i=0;i<n;i++){bind(ps,pass(i));ps.executeUpdate();}}return n;}
        List<String[]> list=new ArrayList<>();for(int i=0;i<n;i++)list.add(pass(i));return PassLoader.loadAll(c,list);
    }
    static void bind(PreparedStatement ps,String[] p)throws SQLException {ps.setString(1,p[0]);ps.setString(2,p[1]);ps.setString(3,p[2]);ps.setInt(4,Integer.parseInt(p[3]));ps.setDouble(5,Double.parseDouble(p[4]));}
    static void verify(Connection c,int expected)throws SQLException {try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT * FROM bus_pass")){ResultSetMetaData m=rs.getMetaData();int rows=0;while(rs.next())rows++;if(rows!=expected)throw new AssertionError(rows+" != "+expected);System.out.print("columns=");for(int i=1;i<=m.getColumnCount();i++)System.out.print(m.getColumnName(i)+":"+m.getColumnTypeName(i)+" ");System.out.println();}}
    static void failureDemo(Connection c)throws SQLException {clear(c);List<String[]> rows=new ArrayList<>();for(int i=0;i<10;i++)rows.add(pass(i));rows.get(5)[4]="not-a-number";
        try{PassLoader.loadAll(c,rows);throw new AssertionError("Expected failure");}catch(NumberFormatException expected){System.out.println("Forced invalid amount rejected: "+expected.getMessage());}verify(c,0);}
    public static void main(String[] args)throws Exception {try(Connection c=connect()){
        System.out.println("mode,records,elapsed_ms");
        for(int n:new int[]{10,1000,10000,100000})for(String mode:new String[]{"Statement","Prepared","Batch"}){
            clear(c);long start=System.nanoTime();int count=insert(c,n,mode);double ms=(System.nanoTime()-start)/1e6;
            verify(c,count);System.out.printf(Locale.ROOT,"%s,%d,%.3f%n",mode,n,ms);}
        failureDemo(c);
    }}
}

// ===== TaskB3 / PassLoader.java =====
class PassLoader {
    /** Loads all passes in one transaction, in batches of 1,000 rows.
     * Returns the number of rows loaded. */
    public static int loadAll(Connection con, List<String[]> passes) throws SQLException {
        String sql = "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) " + "VALUES (?, ?, ?, ?, ?)";
        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int n = 0;
            for (String[] p : passes) {
                // TODO 1 completed: bind, batch and flush every 1,000 rows.
                ps.setString(1, p[0]); ps.setString(2, p[1]); ps.setString(3, p[2]);
                ps.setInt(4, Integer.parseInt(p[3]));
                ps.setDouble(5, Double.parseDouble(p[4]));
                ps.addBatch(); n++;
                if (n % 1000 == 0) ps.executeBatch();
            }
            ps.executeBatch();
            // TODO 2 completed: commit and return count.
            con.commit(); return n;
        } catch (SQLException | RuntimeException e) {
            // TODO 3 completed: roll back and rethrow.
            try { con.rollback(); } catch (SQLException rollbackFailure) { e.addSuppressed(rollbackFailure); }
            throw e;
        } finally { con.setAutoCommit(oldAutoCommit); }
    }
}

// ===== TaskB4 / PassMigration.java =====
class PassMigration {
    public static void main(String[] args)throws Exception {
        if(args.length!=1){System.err.println("Usage: java PassMigration <seven-field-pass-file>");return;}
        Path file=Path.of(args[0]);List<String[]> good=new ArrayList<>();List<String> bad=new ArrayList<>();
        int lineNumber=0;for(String line:Files.readAllLines(file)){lineNumber++;
            String[] p=BusPassFileStore.parse(line);
            if(p==null){bad.add("line "+lineNumber+": "+line);continue;}
            good.add(new String[]{p[0],p[1],p[2],p[3],p[6]});}
        String url=System.getenv().getOrDefault("JDBC_URL","jdbc:mysql://localhost:3306/assignment5_test");
        try(Connection c=DriverManager.getConnection(url,System.getenv().getOrDefault("JDBC_USER","root"),System.getenv().getOrDefault("JDBC_PASSWORD",""))){
            DatabaseMetaData md=c.getMetaData();System.out.println("DB: "+md.getDatabaseProductName()+" "+md.getDatabaseProductVersion());
            System.out.println("Driver: "+md.getDriverName()+" "+md.getDriverVersion());
            try(ResultSet tables=md.getTables(null,null,"%",new String[]{"TABLE"})){while(tables.next())System.out.println("Table: "+tables.getString("TABLE_NAME"));}
            int loaded=PassLoader.loadAll(c,good);System.out.println("Imported: "+loaded);}
        Files.write(Path.of("migration_rejected.txt"),bad);
        System.out.println("Rejected: "+bad.size()+" (migration_rejected.txt)");
    }
}

/** Single-file launcher for Assignment 5. Run: java Main <task> */
public class Main {
    public static void main(String[] args) throws Exception {
        String task = args.length == 0 ? "help" : args[0].toLowerCase(java.util.Locale.ROOT);
        switch (task) {
            case "a1": BusPassBinary.main(new String[0]); break;
            case "a2": BufferedCount.main(new String[0]); break;
            case "a3": DaoExample.main(new String[0]); break;
            case "a4": HostelSerialization.main(new String[0]); break;
            case "a5": WalletTransfer.main(new String[0]); break;
            case "b1": BusPassFileStore.main(new String[0]); break;
            case "b1pass": PassStore.main(new String[0]); break;
            case "b1lines": LineCounterBenchmark.main(new String[0]); break;
            case "b1growth": PassGrowthBenchmark.main(new String[0]); break;
            case "b2": DamagedPassTest.main(new String[0]); break;
            case "b2login": AdversarialLogin.main(new String[0]); break;
            case "b3": JdbcBench.main(new String[0]); break;
            case "b4": PassMigration.main(new String[0]); break;
            default:
                System.out.println("Assignment 5 - combined source (all task classes included)");
                System.out.println("Usage: java Main <a1|a2|a3|a4|a5|b1|b1pass|b1lines|b1growth|b2|b2login|b3|b4>");
                System.out.println("JDBC tasks need MySQL, schema.sql and MySQL Connector/J on the classpath.");
                System.out.println("Part A written pseudocode and report analysis remain in the Word document.");
        }
    }
}
