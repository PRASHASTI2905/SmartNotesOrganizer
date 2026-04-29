import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server Started...");

            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }

        } catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }
}

class ClientHandler extends Thread {

    Socket socket;

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            String request = br.readLine();

            if (request == null || request.trim().isEmpty()) {
                pw.println("Invalid Request");
                return;
            }

            System.out.println("REQUEST: " + request);

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/notesdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    "root",
                    "mishika@1234"
            );

            // ================= ADD =================
            if (request.startsWith("ADD")) {

                try {
                    String[] parts = request.split("\\|", 5);

                    if (parts.length < 5) {
                        pw.println("Invalid Data");
                        return;
                    }

                    String user = parts[1];
                    String subject = parts[2];
                    String title = parts[3];
                    String content = parts[4];

                    String time = new SimpleDateFormat("dd-MM-yyyy HH:mm")
                            .format(new Date());

                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO notes(username, subject, title, content, time) VALUES (?, ?, ?, ?, ?)"
                    );

                    ps.setString(1, user);
                    ps.setString(2, subject);
                    ps.setString(3, title);
                    ps.setString(4, content);
                    ps.setString(5, time);

                    int rows = ps.executeUpdate();

                    pw.println(rows > 0 ? "Note Added Successfully" : "Insert Failed");

                } catch (Exception e) {
                    e.printStackTrace();
                    pw.println("Server Error: " + e.getMessage());
                }
            }

            // ================= VIEW =================
            else if (request.equals("VIEW")) {

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM notes ORDER BY id DESC");

                while (rs.next()) {
                    pw.println("ID: " + rs.getInt("id"));
                    pw.println("User: " + rs.getString("username"));
                    pw.println("Subject: " + rs.getString("subject"));
                    pw.println("Title: " + rs.getString("title"));
                    pw.println("Content: " + rs.getString("content"));
                    pw.println("Time: " + rs.getString("time"));
                    pw.println("----------------------");
                }

                pw.println("END");
            }

            // ================= SEARCH =================
            else if (request.startsWith("SEARCH")) {

                String key = "%" + request.split("\\|")[1] + "%";

                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM notes WHERE title LIKE ? OR content LIKE ? OR subject LIKE ? ORDER BY id DESC"
                );

                ps.setString(1, key);
                ps.setString(2, key);
                ps.setString(3, key);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    pw.println("ID: " + rs.getInt("id"));
                    pw.println("User: " + rs.getString("username"));
                    pw.println("Subject: " + rs.getString("subject"));
                    pw.println("Title: " + rs.getString("title"));
                    pw.println("Content: " + rs.getString("content"));
                    pw.println("Time: " + rs.getString("time"));
                    pw.println("----------------------");
                }

                pw.println("END");
            }

            else if (request.startsWith("FILTER")) {

                try {
                    String subject = request.split("\\|")[1];

                    PreparedStatement ps = con.prepareStatement(
                            "SELECT * FROM notes WHERE subject=? ORDER BY id DESC"
                    );

                    ps.setString(1, subject);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        pw.println("ID: " + rs.getInt("id"));
                        pw.println("User: " + rs.getString("username"));
                        pw.println("Subject: " + rs.getString("subject"));
                        pw.println("Title: " + rs.getString("title"));
                        pw.println("Content: " + rs.getString("content"));
                        pw.println("Time: " + rs.getString("time"));
                        pw.println("----------------------");
                    }

                    pw.println("END");

                } catch (Exception e) {
                    e.printStackTrace();
                    pw.println("Filter Error");
                }
            }

            else {
                pw.println("Invalid Request");
            }

            con.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}