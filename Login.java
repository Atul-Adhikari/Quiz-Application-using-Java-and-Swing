package FinalAssessment;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTextField;
import javax.swing.JComboBox;


/**
 * The Login class provides a GUI-based login interface for the Quiz App.
 * Users enter their username, age, and select their level to log in.
 * The system validates the credentials against a MySQL database.
 */
public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;
    private JComboBox<String> comboBox;

    
    /**
     * The main method to launch the Login window.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login frame = new Login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    
    /**
     * Constructs the Login frame, initializes UI components, and sets up event handling.
     */
    public Login() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setBackground(new Color(230, 240, 250));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Welcome to Quiz App");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel.setBackground(new Color(0, 0, 0));
        lblNewLabel.setBounds(159, 21, 165, 24);
        contentPane.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Username: ");
        lblNewLabel_1.setBounds(100, 67, 76, 14);
        contentPane.add(lblNewLabel_1);

        JLabel lblNewLabel_1_1 = new JLabel("Level:");
        lblNewLabel_1_1.setBounds(100, 141, 76, 14);
        contentPane.add(lblNewLabel_1_1);

        JLabel lblNewLabel_1_1_1 = new JLabel("Age:");
        lblNewLabel_1_1_1.setBounds(100, 101, 76, 14);
        contentPane.add(lblNewLabel_1_1_1);

        JButton btnNewButton = new JButton("Login");
        btnNewButton.addMouseListener(new MouseAdapter() {
            /**
             *Handles the login process by validating user credentials against the database.
             * If successful, it launches the QuizGame window; otherwise, it displays an error message.
             */
            @Override
            
            public void mouseClicked(MouseEvent e) {

                String url = "jdbc:mysql://localhost/CompetitionDB";
                String username = "root";
                String password = "";

                String name = textField.getText();

                int age = 0;
                try {
                    age = Integer.parseInt(textField_1.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid age (integer).");
                    return;
                }

                String level = (String) comboBox.getSelectedItem();

                try (Connection con = DriverManager.getConnection(url, username, password)) {
                    String checkQuery = "SELECT * FROM Competitors WHERE Name = ? AND Age = ? AND Level = ?";
                    try (PreparedStatement ps = con.prepareStatement(checkQuery)) {
                        ps.setString(1, name);
                        ps.setInt(2, age);
                        ps.setString(3, level);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(null, "Login successful.");
                            int competitorId = rs.getInt("Competitor_ID");
                            new QuizGame(competitorId, level).setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid credentials, please try again.");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database connection failed.");
                }
            }
        });
        btnNewButton.setBounds(159, 176, 89, 23);
        contentPane.add(btnNewButton);

        textField = new JTextField();
        textField.setBounds(186, 64, 86, 20);
        contentPane.add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setBounds(186, 98, 86, 20);
        contentPane.add(textField_1);
        textField_1.setColumns(10);

        comboBox = new JComboBox<>();
        comboBox.setBounds(186, 138, 86, 20);
        comboBox.addItem("Beginner");
        comboBox.addItem("Intermediate");
        comboBox.addItem("Advanced");
        contentPane.add(comboBox);
    }
}
