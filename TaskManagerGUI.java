import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class TaskManagerGUI extends JFrame {
    private ArrayList<Task> tasks;
    private String tasksFile;
    protected static final String USERS_FILE = "users.txt";
    protected static final String BAN_FILE = "banned_users.txt";
    private static final String ADMIN_PASSWORD = "admin123"; // Admin password
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskField;

    public TaskManagerGUI(String username) {
        this.tasksFile = "tasks_" + username + ".txt";
        tasks = new ArrayList<>();
        loadTasks();
        createGUI(username);
    }

    private void createGUI(String username) {
        setTitle("Task Manager - User: " + username);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Task List Model and JList
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        loadTasksToListModel();

        // Scroll Pane for Task List
        JScrollPane scrollPane = new JScrollPane(taskList);

        // Task Field and Add Button
        taskField = new JTextField();
        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(e -> addTask(taskField.getText()));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(taskField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        // Complete and Remove Buttons
        JButton completeButton = new JButton("Complete Task");
        completeButton.addActionListener(e -> completeTask(taskList.getSelectedIndex()));

        JButton removeButton = new JButton("Remove Task");
        removeButton.addActionListener(e -> removeTask(taskList.getSelectedIndex()));

        JPanel actionPanel = new JPanel();
        actionPanel.add(completeButton);
        actionPanel.add(removeButton);

        // Admin Panel Button
        JButton adminButton = new JButton("Admin Panel");
        adminButton.addActionListener(e -> openAdminPanel());

        // Add Components to Frame
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.SOUTH);
        add(adminButton, BorderLayout.EAST);

        setVisible(true);
    }

    private void openAdminPanel() {
        String password = JOptionPane.showInputDialog("Enter Admin Password:");
        if (ADMIN_PASSWORD.equals(password)) {
            new AdminPanel();  // Open admin panel if password is correct
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect password!");
        }
    }

    private void loadTasksToListModel() {
        for (Task task : tasks) {
            taskListModel.addElement(task.toString());
        }
    }

    public void addTask(String description) {
        if (!description.isEmpty()) {
            tasks.add(new Task(description));
            taskListModel.addElement(tasks.get(tasks.size() - 1).toString());
            taskField.setText("");
            saveTasks();
        }
    }

    public void completeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted();
            taskListModel.set(index, tasks.get(index).toString());
            saveTasks();
        }
    }

    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            taskListModel.remove(index);
            saveTasks();
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tasksFile))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    private void loadTasks() {
        File file = new File(tasksFile);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            tasks = (ArrayList<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
    }

    public static boolean isUserBanned(String username) {
        File file = new File(BAN_FILE);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking banned users: " + e.getMessage());
        }

        return false;
    }

    public static void registerUser(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(username);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");

        // Register the user if not banned
        if (isUserBanned(username)) {
            JOptionPane.showMessageDialog(null, "You are banned and cannot access the task manager.");
        } else {
            registerUser(username);
            new TaskManagerGUI(username);
        }
    }
}
