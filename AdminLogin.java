package FinalAssessment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;


/**
 * The AdminLogin class provides a graphical user interface (GUI) for admin users to log in.
 * It verifies the admin credentials against a MySQL database and grants access to the AdminPanel if successful.
 */
public class AdminLogin extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    
    /**
     * Constructs the AdminLogin frame, setting up the UI components.
     */
    public AdminLogin() {
        setTitle("Admin Login");
        setSize(367, 283);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        usernameField = new JTextField(20);
        usernameField.setBounds(165, 82, 166, 20);

        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setBounds(44, 124, 78, 14);
        passwordField = new JPasswordField(20);
        passwordField.setBounds(165, 121, 166, 20);
        getContentPane().setLayout(null);
        getContentPane().add(usernameField);
        
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setBounds(44, 85, 78, 14);
                
        getContentPane().add(usernameLabel);
        getContentPane().add(passwordLabel);
        getContentPane().add(passwordField);
        
                loginButton = new JButton("Login");
                loginButton.setBounds(116, 178, 87, 23);
                getContentPane().add(loginButton);
                
                JLabel lblNewLabel = new JLabel("Admin Login");
                lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                lblNewLabel.setBounds(119, 11, 106, 25);
                getContentPane().add(lblNewLabel);
                
                        loginButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String username = usernameField.getText();
                                String password = new String(passwordField.getPassword());
                
                                if (verifyAdminCredentials(username, password)) {
                                    JOptionPane.showMessageDialog(null, "Login Successful.");
                                    new AdminPanel().setVisible(true);  // Open the admin panel
                                    dispose();  // Close the login page
                                } else {
                                    JOptionPane.showMessageDialog(null, "Invalid credentials. Try again.");
                                }
                            }
                        });
    }

    
    /**
     * Verifies admin credentials by checking the database.
     * @param username	The admin username.
     * @param password	The admin password.
     * @return true if credentials are valid, false otherwise.
     */
    private boolean verifyAdminCredentials(String username, String password) {
        String url = "jdbc:mysql://localhost/CompetitionDB";
        String dbUsername = "root";
        String dbPassword = "";

        String query = "SELECT * FROM Admin WHERE Username = ? AND Password = ?";

        try (Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    
    /**
     * The main method to launch the AdminLogin GUI.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLogin().setVisible(true));
    }
}
