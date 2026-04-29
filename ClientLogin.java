import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class ClientLogin extends JFrame {

    JTextField userField;
    JPasswordField passField;
    JCheckBox showPass;
    ImageIcon smallIcon;

    public ClientLogin() {

        ImageIcon icon = new ImageIcon("icon.png");
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        smallIcon = new ImageIcon(scaledImg);

        setTitle("Smart Notes Organizer - Login");
        setSize(400, 280);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Login", JLabel.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        panel.add(new JLabel("Username:"));
        userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        panel.add(passField);

        showPass = new JCheckBox("Show Password");
        panel.add(showPass);

        showPass.addActionListener(e -> {
            passField.setEchoChar(showPass.isSelected() ? (char) 0 : '*');
        });

        add(panel, BorderLayout.CENTER);

        JPanel bottom = new JPanel();

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JButton exitBtn = new JButton("Exit");

        loginBtn.setBackground(new Color(0, 153, 76));
        loginBtn.setForeground(Color.WHITE);

        registerBtn.setBackground(new Color(0, 102, 204));
        registerBtn.setForeground(Color.WHITE);

        exitBtn.setBackground(Color.RED);
        exitBtn.setForeground(Color.WHITE);

        bottom.add(loginBtn);
        bottom.add(registerBtn);
        bottom.add(exitBtn);

        add(bottom, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());
        exitBtn.addActionListener(e -> System.exit(0));

        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // LOGIN
    void login() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty!", "Error",
                    JOptionPane.ERROR_MESSAGE, smallIcon);
            return;
        }

        if (checkLogin(user, pass)) {
            JOptionPane.showMessageDialog(this, "Login Successful!", "Success",
                    JOptionPane.INFORMATION_MESSAGE, smallIcon);

            new ClientDashboard(user);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password",
                    "Error", JOptionPane.ERROR_MESSAGE, smallIcon);
        }
    }

    // REGISTER 
    void register() {
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        Object[] message = {
                "Username:", user,
                "Password:", pass
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                "Register", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) return;

        String username = user.getText().trim();
        String password = new String(pass.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid Input!",
                    "Error", JOptionPane.ERROR_MESSAGE, smallIcon);
            return;
        }

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/notesdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "root",
                "mishika@1234")) {

            // CHECK DUPLICATE
            PreparedStatement check = con.prepareStatement(
                    "SELECT * FROM users WHERE username=?"
            );
            check.setString(1, username);

            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists!",
                        "Error", JOptionPane.ERROR_MESSAGE, smallIcon);
                return;
            }

            // INSERT
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users(username, password) VALUES (?, ?)"
            );

            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Registered Successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE, smallIcon);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Database Error!",
                    "Error", JOptionPane.ERROR_MESSAGE, smallIcon);
        }
    }

    // CHECK LOGIN
    boolean checkLogin(String user, String pass) {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/notesdb",
                "root",
                "mishika@1234")) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=?"
            );

            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Database Error!",
                    "Error", JOptionPane.ERROR_MESSAGE, smallIcon);
        }
        return false;
    }

    public static void main(String[] args) {
        new ClientLogin();
    }
}