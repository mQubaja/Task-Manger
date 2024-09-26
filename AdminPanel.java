import javax.swing.*;
import java.awt.*;
import java.io.*;

public class AdminPanel extends JFrame {
    private DefaultListModel<String> userListModel;
    private JList<String> userList;

    public AdminPanel() {
        setTitle("Admin Panel - Manage Users");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        loadUsers();

        JScrollPane scrollPane = new JScrollPane(userList);

        JButton banButton = new JButton("Ban User");
        banButton.addActionListener(e -> banUser(userList.getSelectedValue()));

        JButton unbanButton = new JButton("Unban User");
        unbanButton.addActionListener(e -> unbanUser(userList.getSelectedValue()));

        JPanel actionPanel = new JPanel();
        actionPanel.add(banButton);
        actionPanel.add(unbanButton);

        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadUsers() {
        File file = new File(TaskManagerGUI.USERS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                userListModel.addElement(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private void banUser(String username) {
        if (username == null) {
            JOptionPane.showMessageDialog(null, "Please select a user to ban.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TaskManagerGUI.BAN_FILE, true))) {
            writer.write(username);
            writer.newLine();
            JOptionPane.showMessageDialog(null, username + " has been banned.");
        } catch (IOException e) {
            System.out.println("Error banning user: " + e.getMessage());
        }
    }

    private void unbanUser(String username) {
        if (username == null) {
            JOptionPane.showMessageDialog(null, "Please select a user to unban.");
            return;
        }

        File inputFile = new File(TaskManagerGUI.BAN_FILE);
        File tempFile = new File("temp_ban_file.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equalsIgnoreCase(username)) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            if (inputFile.delete()) {
                tempFile.renameTo(inputFile);
                JOptionPane.showMessageDialog(null, username + " has been unbanned.");
            }

        } catch (IOException e) {
            System.out.println("Error unbanning user: " + e.getMessage());
        }
    }
}
