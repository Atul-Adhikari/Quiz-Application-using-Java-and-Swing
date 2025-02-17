package FinalAssessment;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * The signupclass represents a graphical user interface (GUI) for user registration.
 * Users can enter their name, age, and select their experience level before signing up.
 * If the user already exists in the database, an appropriate message is displayed.
 * Upon successful signup, the user is redirected to the RoleSelection window.
 * This class interacts with a MySQL database to store user information.
 */
public class signup extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    signup frame = new signup();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public signup() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(230, 240, 250));

        JLabel lblNewLabel = new JLabel("Signup Page");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel.setBounds(176, 21, 100, 17);
        contentPane.add(lblNewLabel);

        String[] levels = {"Beginner", "Intermediate", "Advanced"};
        JComboBox<String> levelBox = new JComboBox<>(levels);
        levelBox.setBounds(250, 144, 96, 22);
        contentPane.add(levelBox);

        JLabel lblNewLabel_1 = new JLabel("Name: ");
        lblNewLabel_1.setBounds(123, 69, 64, 14);
        contentPane.add(lblNewLabel_1);

        textField = new JTextField();
        textField.setBounds(250, 66, 96, 20);
        contentPane.add(textField);
        textField.setColumns(10);

        JLabel lblNewLabel_2 = new JLabel("Level:");
        lblNewLabel_2.setBounds(123, 148, 47, 14);
        contentPane.add(lblNewLabel_2);

        JLabel lblNewLabel_3 = new JLabel("Age: ");
        lblNewLabel_3.setBounds(123, 107, 47, 14);
        contentPane.add(lblNewLabel_3);

        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(250, 104, 96, 20);
        contentPane.add(textField_1);

        JButton btnNewButton = new JButton("Signup");
        btnNewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                String url = "jdbc:mysql://localhost/CompetitionDB";
                String username = "root";
                String password = "";

                String name = textField.getText();
                // Check if name is empty
                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter your name.");
                    return;
                }

                int age = 0;
                // Validate age input
                try {
                    age = Integer.parseInt(textField_1.getText());
                    if (age <= 0) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid age greater than 0.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid age (integer).");
                    return;
                }

                String level = (String) levelBox.getSelectedItem();

                try (Connection con = DriverManager.getConnection(url, username, password)) {
                    // Check if the user already exists
                    String checkQuery = "SELECT COUNT(*) FROM Competitors WHERE Name = ?";
                    try (PreparedStatement ps = con.prepareStatement(checkQuery)) {
                        ps.setString(1, name);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(null, "User already exists.");
                        } else {
                            // Insert the new user
                            String insertQuery = "INSERT INTO Competitors (Name, Age, Level) VALUES (?, ?, ?)";
                            try (PreparedStatement insertPs = con.prepareStatement(insertQuery)) {
                                insertPs.setString(1, name);
                                insertPs.setInt(2, age);
                                insertPs.setString(3, level);
                                insertPs.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Signup successful.");
                                
                              signup signup = new signup();
                              signup.setVisible(false);

                              RoleSelection roleSelection = new RoleSelection();
                              roleSelection.setVisible(true);

                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error.");
                }
            }
        });
        btnNewButton.setBackground(new Color(50, 150, 250));
        btnNewButton.setBounds(175, 177, 89, 23);
        contentPane.add(btnNewButton);

        JLabel lblNewLabel_4 = new JLabel("Already have an account: ");
        lblNewLabel_4.setBounds(89, 220, 158, 14);
        contentPane.add(lblNewLabel_4);

        JButton btnNewButton_1 = new JButton("Login");
        btnNewButton_1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RoleSelection roleSelection = new RoleSelection();
                roleSelection.setVisible(true);
//                 Close the Signup window (optional)
                dispose();
            }
        });
        btnNewButton_1.setBounds(257, 216, 89, 23);
        btnNewButton_1.setBackground(new Color(50, 150, 250));
        contentPane.add(btnNewButton_1);
    }
}
