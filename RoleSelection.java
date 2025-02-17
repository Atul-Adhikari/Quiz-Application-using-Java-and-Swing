package FinalAssessment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * The RoleSelection class provides a graphical user interface (GUI) for users to select their role
 * in the application. Users can choose to log in either as an Admin or as a regular User.
 * Selecting a role redirects the user to the corresponding login screen.
 */
public class RoleSelection extends JFrame {

    private static final long serialVersionUID = 1L;

    
    /**
     * Constructs the RoleSelection frame, setting up the UI components and event listeners.
     */
    public RoleSelection() {
    	getContentPane().setBackground(new Color(128, 255, 255));
        setTitle("Role Selection");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton adminButton = new JButton("Login as Admin");
        adminButton.setBackground(new Color(183, 255, 111));
        adminButton.setBounds(10, 74, 133, 35);
        JButton userButton = new JButton("Login as User");
        userButton.setBackground(new Color(0, 234, 234));
        userButton.setBounds(153, 74, 123, 35);

        adminButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new AdminLogin().setVisible(true);
                dispose(); // Close the role selection screen
            }
        });

        userButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Login().setVisible(true);  // Open the user login screen
                dispose(); // Close the role selection screen
            }
        });
        getContentPane().setLayout(null);

        getContentPane().add(adminButton);
        getContentPane().add(userButton);
        
        JLabel lblNewLabel = new JLabel("Choose your role. ");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblNewLabel.setBounds(90, 26, 133, 37);
        getContentPane().add(lblNewLabel);
    }

    /**
     * The main method initializes and displays the RoleSelection window.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoleSelection().setVisible(true));
    }
}
