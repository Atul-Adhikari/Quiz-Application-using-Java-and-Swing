package FinalAssessment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;

/**
 * The AdminPanel class provides a graphical user interface for managing quiz questions.
 * This panel allows an Admin to add, update, delete, and view quiz questions stored in a database.
 * It also provides functionality to generate reports.
 */
public class AdminPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField questionField, optionAField, optionBField, optionCField, optionDField, answerField, levelField;
    private JButton addButton, updateButton, deleteButton, viewButton, reportButton, confirmUpdateButton;
    private JTable questionTable;
    private DefaultTableModel tableModel;
    private JButton logout;

    /**
     * Constructs the AdminPanel with necessary UI components.
     * Initialises the UI, sets up buttons, text fields, and a table for displaying questions.
     */
    public AdminPanel() {
        setTitle("Admin Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(8, 2));
        inputPanel.add(new JLabel("Question:"));
        questionField = new JTextField();
        inputPanel.add(questionField);

        inputPanel.add(new JLabel("Option A:"));
        optionAField = new JTextField();
        inputPanel.add(optionAField);

        inputPanel.add(new JLabel("Option B:"));
        optionBField = new JTextField();
        inputPanel.add(optionBField);

        inputPanel.add(new JLabel("Option C:"));
        optionCField = new JTextField();
        inputPanel.add(optionCField);

        inputPanel.add(new JLabel("Option D:"));
        optionDField = new JTextField();
        inputPanel.add(optionDField);

        inputPanel.add(new JLabel("Correct Answer (a/b/c/d):"));
        answerField = new JTextField();
        inputPanel.add(answerField);

        inputPanel.add(new JLabel("Difficulty Level (Beginner/Intermediate/Advanced):"));
        levelField = new JTextField();
        inputPanel.add(levelField);

        getContentPane().add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Question", "A", "B", "C", "D", "Answer", "Level"}, 0);
        questionTable = new JTable(tableModel);
        loadQuestions();
        getContentPane().add(new JScrollPane(questionTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        viewButton = new JButton("View All");
        reportButton = new JButton("View Reports");
        confirmUpdateButton = new JButton("Confirm Update");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(confirmUpdateButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        logout = new JButton("Logout");
        logout.setBackground(new Color(128, 128, 255));
        buttonPanel.add(logout);

        addButton.addActionListener(e -> addQuestion());
        updateButton.addActionListener(e -> updateQuestion());
        deleteButton.addActionListener(e -> deleteQuestion());
        viewButton.addActionListener(e -> loadQuestions());
        reportButton.addActionListener(e -> new Report());
        confirmUpdateButton.addActionListener(e -> confirmUpdate());

        // Logout button action listener - navigate to RoleSelection
        logout.addActionListener(e -> {
        	RoleSelection roleSelection = new RoleSelection();
            roleSelection.setVisible(true);
            dispose();  // Close the AdminPanel window
        });
    }

    /**
     * Loads all questions from the database and populates the table.
     */
    private void loadQuestions() {
        tableModel.setRowCount(0);
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/CompetitionDB", "root", "");
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Questions")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("ID"), rs.getString("Question"),
                        rs.getString("OptionA"), rs.getString("OptionB"), rs.getString("OptionC"),
                        rs.getString("OptionD"), rs.getString("CorrectAnswer"), rs.getString("Level")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new question to the database after validating input fields.
     */
    private void addQuestion() {
        // Validate if any required fields are empty
        if (questionField.getText().trim().isEmpty() ||
            optionAField.getText().trim().isEmpty() ||
            optionBField.getText().trim().isEmpty() ||
            optionCField.getText().trim().isEmpty() ||
            optionDField.getText().trim().isEmpty() ||
            answerField.getText().trim().isEmpty() ||
            levelField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields (Question, Options, Correct Answer, and Difficulty Level) must be filled.");
            return; // Stop execution if any field is empty
        }

        // Validate Correct Answer
        String correctAnswer = answerField.getText().toLowerCase();
        if (!correctAnswer.equals("a") && !correctAnswer.equals("b") && !correctAnswer.equals("c") && !correctAnswer.equals("d")) {
            JOptionPane.showMessageDialog(this, "Correct Answer must be one of the following: a, b, c, d.");
            return;
        }

        // Validate Difficulty Level and enforce capitalization
        String level = levelField.getText().trim().toLowerCase();
        if (!level.equals("beginner") && !level.equals("intermediate") && !level.equals("advanced")) {
            JOptionPane.showMessageDialog(this, "Difficulty Level must be one of the following: Beginner, Intermediate, Advanced.");
            return;
        }
        level = level.substring(0, 1).toUpperCase() + level.substring(1);  // Capitalize the first letter

        // If all validations pass, proceed to add the question
        String query = "INSERT INTO Questions (Question, OptionA, OptionB, OptionC, OptionD, CorrectAnswer, Level) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/CompetitionDB", "root", "");
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, questionField.getText());
            stmt.setString(2, optionAField.getText());
            stmt.setString(3, optionBField.getText());
            stmt.setString(4, optionCField.getText());
            stmt.setString(5, optionDField.getText());
            stmt.setString(6, correctAnswer);
            stmt.setString(7, level);
            stmt.executeUpdate();
            loadQuestions();  // Refresh the question list
            
            JOptionPane.showMessageDialog(this, "Question added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Prepares the selected question for updating by populating input fields.
     */
    private void updateQuestion() {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a question to update.");
            return;
        }

        // Get the ID of the selected question
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Load selected data into the fields (to avoid overwriting with blank values)
        questionField.setText((String) tableModel.getValueAt(selectedRow, 1));
        optionAField.setText((String) tableModel.getValueAt(selectedRow, 2));
        optionBField.setText((String) tableModel.getValueAt(selectedRow, 3));
        optionCField.setText((String) tableModel.getValueAt(selectedRow, 4));
        optionDField.setText((String) tableModel.getValueAt(selectedRow, 5));
        answerField.setText((String) tableModel.getValueAt(selectedRow, 6));
        levelField.setText((String) tableModel.getValueAt(selectedRow, 7));

        confirmUpdateButton.setEnabled(true); // Enable the confirm update button
    }

    /**
     * Confirms and updates the selected question in the database.
     */
    private void confirmUpdate() {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a question to update.");
            return;
        }

        // Validate if any required fields are empty
        if (questionField.getText().trim().isEmpty() ||
            optionAField.getText().trim().isEmpty() ||
            optionBField.getText().trim().isEmpty() ||
            optionCField.getText().trim().isEmpty() ||
            optionDField.getText().trim().isEmpty() ||
            answerField.getText().trim().isEmpty() ||
            levelField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields (Question, Options, Correct Answer, and Difficulty Level) must be filled.");
            return; // Stop execution if any field is empty
        }

        // Validate Correct Answer
        String correctAnswer = answerField.getText().toLowerCase();
        if (!correctAnswer.equals("a") && !correctAnswer.equals("b") && !correctAnswer.equals("c") && !correctAnswer.equals("d")) {
            JOptionPane.showMessageDialog(this, "Correct Answer must be one of the following: a, b, c, d.");
            return;
        }

        // Validate Difficulty Level and enforce capitalization
        String level = levelField.getText().trim().toLowerCase();
        if (!level.equals("beginner") && !level.equals("intermediate") && !level.equals("advanced")) {
            JOptionPane.showMessageDialog(this, "Difficulty Level must be one of the following: Beginner, Intermediate, Advanced.");
            return;
        }
        level = level.substring(0, 1).toUpperCase() + level.substring(1);  // Capitalize the first letter

        // Proceed to update the question in the database
        String query = "UPDATE Questions SET Question=?, OptionA=?, OptionB=?, OptionC=?, OptionD=?, CorrectAnswer=?, Level=? WHERE ID=?";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/CompetitionDB", "root", "");
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, questionField.getText());
            stmt.setString(2, optionAField.getText());
            stmt.setString(3, optionBField.getText());
            stmt.setString(4, optionCField.getText());
            stmt.setString(5, optionDField.getText());
            stmt.setString(6, correctAnswer);
            stmt.setString(7, level);
            stmt.setInt(8, (int) tableModel.getValueAt(selectedRow, 0)); // Use the ID to update the correct question
            stmt.executeUpdate();
            loadQuestions();  // Refresh the question list with the updated data
            confirmUpdateButton.setEnabled(false); // Disable the button after update
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the selected question from the database.
     */
    private void deleteQuestion() {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a question to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String query = "DELETE FROM Questions WHERE ID=?";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/CompetitionDB", "root", "");
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            loadQuestions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method to launch the Admin Panel UI.
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPanel().setVisible(true));
    }
}
