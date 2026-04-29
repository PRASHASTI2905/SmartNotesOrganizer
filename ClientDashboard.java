import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientDashboard extends JFrame {

    JTextArea area, contentArea;
    JTextField titleField, searchField;
    JComboBox<String> subjectBox;

    JButton addBtn; // GLOBAL

    String username;
    JLabel status, countLabel;

    public ClientDashboard(String user) {

        this.username = user;

        setTitle("Smart Notes Organizer");
        setSize(1000, 600);
        setLayout(new BorderLayout(10, 10));

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(0,102,204));

        JLabel appName = new JLabel("  Smart Notes Organizer");
        appName.setForeground(Color.WHITE);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton logoutBtn = new JButton("Logout");

        topBar.add(appName, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // LEFT PANEL
        JPanel left = new JPanel(new GridLayout(5,1,10,10));
        left.setBorder(BorderFactory.createTitledBorder("Subjects"));

        String[] subjects = {"Java","DAA","Microprocessor","Automata","AI"};
        for(String sub : subjects){
            JButton btn = new JButton(sub);
            btn.addActionListener(e -> filter(sub));
            left.add(btn);
        }
        add(left, BorderLayout.WEST);

        // CENTER
        area = new JTextArea();
        JScrollPane scroll = new JScrollPane(area);
        add(scroll, BorderLayout.CENTER);

        // RIGHT 
        JPanel right = new JPanel(new GridLayout(6,1,10,10));
        right.setBorder(BorderFactory.createTitledBorder("Add Note"));

        subjectBox = new JComboBox<>(subjects);
        titleField = new JTextField();
        contentArea = new JTextArea();

        right.add(new JLabel("Subject"));
        right.add(subjectBox);
        right.add(new JLabel("Title"));
        right.add(titleField);
        right.add(new JLabel("Content"));
        right.add(new JScrollPane(contentArea));

        add(right, BorderLayout.EAST);

        // BOTTOM
        JPanel bottom = new JPanel();

        addBtn = new JButton("Add"); // GLOBAL USED
        JButton viewBtn = new JButton("View");
        JButton deleteBtn = new JButton("Delete");
        JButton updateBtn = new JButton("Update");
        JButton searchBtn = new JButton("Search");

        searchField = new JTextField(10);
        countLabel = new JLabel("Notes: 0");

        bottom.add(addBtn);
        bottom.add(viewBtn);
        bottom.add(deleteBtn);
        bottom.add(updateBtn);
        bottom.add(searchBtn);
        bottom.add(searchField);
        bottom.add(countLabel);

        status = new JLabel("Logged in as: " + username);

        JPanel south = new JPanel(new BorderLayout());
        south.add(bottom, BorderLayout.CENTER);
        south.add(status, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        // ACTIONS
        addBtn.addActionListener(e -> addNote());
        viewBtn.addActionListener(e -> viewNotes());
        deleteBtn.addActionListener(e -> deleteNote());
        updateBtn.addActionListener(e -> updateNote());
        searchBtn.addActionListener(e -> searchNotes());

        logoutBtn.addActionListener(e -> {
            new ClientLogin();
            dispose();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // ================= ADD =================
    void addNote() {

        addBtn.setEnabled(false); //disable

        new Thread(() -> {
            try {
                String subject = subjectBox.getSelectedItem().toString();
                String title = titleField.getText().trim();
                String content = contentArea.getText().trim();

                if (title.isEmpty() || content.isEmpty()) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Fill all fields!"));
                    addBtn.setEnabled(true);
                    return;
                }

                Socket s = new Socket("localhost", 1234);
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(s.getInputStream()));

                pw.println("ADD|" + username + "|" + subject + "|" + title + "|" + content);

                String response = br.readLine();

                s.close();

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, response);

                    status.setText(response); // STATUS

                    titleField.setText("");
                    contentArea.setText("");

                    viewNotes();

                    addBtn.setEnabled(true); // enable again
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Add Error");
                    addBtn.setEnabled(true);
                });
            }
        }).start();
    }

    // ================= VIEW =================
    void viewNotes() {
        try {
            Socket s = new Socket("localhost",1234);
            PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            pw.println("VIEW");

            area.setText("");
            String line;

            while(!(line = br.readLine()).equals("END")){
                area.append(line + "\n");
            }

            updateCount();
            status.setText("Showing all notes"); // OPTIONAL

            s.close();

        } catch(Exception e){
            area.setText("View Error");
        }
    }

    // ================= FILTER =================
    void filter(String subject){
        try{
            Socket s = new Socket("localhost",1234);
            PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            pw.println("FILTER|" + subject);

            area.setText("");
            String line;

            while(!(line = br.readLine()).equals("END")){
                area.append(line + "\n");
            }

            updateCount();
            status.setText("Showing: " + subject); //FILTER STATUS

            s.close();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Filter Error");
        }
    }

    // ================= SEARCH =================
    void searchNotes() {
        try {
            String key = searchField.getText().trim();
            if (key.isEmpty()) return;

            Socket s = new Socket("localhost", 1234);
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));

            pw.println("SEARCH|" + key);

            area.setText("");
            String line;

            while (!(line = br.readLine()).equals("END")) {
                area.append(line + "\n");
            }

            updateCount();
            status.setText("Search: " + key); //SEARCH STATUS

            s.close();

        } catch (Exception e) {
            area.setText("Search Error");
        }
    }

    // ================= DELETE =================
    void deleteNote(){
        try{
            String id = JOptionPane.showInputDialog("Enter ID:");
            if(id==null) return;

            Socket s = new Socket("localhost",1234);
            PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            pw.println("DELETE|" + id);
            status.setText(br.readLine());

            viewNotes();
            s.close();

        }catch(Exception e){
            status.setText("Delete Error");
        }
    }

    // ================= UPDATE =================
    void updateNote(){
        try{
            String id = JOptionPane.showInputDialog("Enter ID:");
            String title = JOptionPane.showInputDialog("New Title:");
            String content = JOptionPane.showInputDialog("New Content:");

            if(id==null || title==null || content==null) return;

            Socket s = new Socket("localhost",1234);
            PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            pw.println("UPDATE|" + id + "|" + title + "|" + content);
            status.setText(br.readLine());

            viewNotes();
            s.close();

        }catch(Exception e){
            status.setText("Update Error");
        }
    }

    // COUNT
    void updateCount(){
        int count = area.getText().split("ID:").length - 1;
        countLabel.setText("Notes: " + count);
    }
}