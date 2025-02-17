package FinalAssessment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;


/**
 * The Report class generates a quiz report displaying overall scores,
 * leaderboards, and player statistics from the database.
 * This class fetches data from the Competitors table, organizes it into
 * different leaderboards based on quiz levels (Beginner, Intermediate, Advanced),
 * and displays it in a graphical user interface using Swing components.
 */
public class Report extends JFrame {
    private JTable overallTable;
    private DefaultTableModel overallModel;

    private JTable beginnerTable, intermediateTable, advancedTable;
    private DefaultTableModel beginnerModel, intermediateModel, advancedModel;

    private JLabel totalPlayersLabel;

    /**
     * Constructs a Report window, initializes the UI components,
     * and populates the tables with data from the database.
     */
    public Report() {
        setTitle("Quiz Report");
        setSize(1000, 600); // Adjusted window size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Message Panel at the top
        JPanel messagePanel = new JPanel();
        JLabel messageLabel = new JLabel("Play 5 times to get the actual overall score", JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messagePanel.add(messageLabel);

        // Create tabs for different leaderboards
        JTabbedPane tabbedPane = new JTabbedPane();

        // Overall Scores Table
        overallModel = new DefaultTableModel();
        overallTable = new JTable(overallModel);
        overallModel.addColumn("Player Name");
        overallModel.addColumn("Level");
        overallModel.addColumn("Score1");
        overallModel.addColumn("Score2");
        overallModel.addColumn("Score3");
        overallModel.addColumn("Score4");
        overallModel.addColumn("Score5");
        overallModel.addColumn("Overall Score");
        fetchAndDisplayReport();
        tabbedPane.addTab("Overall Scores", new JScrollPane(overallTable));

        // Beginner Leaderboard
        beginnerModel = new DefaultTableModel();
        beginnerTable = new JTable(beginnerModel);
        beginnerModel.addColumn("Player Name");
        beginnerModel.addColumn("Total Score (/25)");
        fetchLeaderboard("Beginner", beginnerModel);
        tabbedPane.addTab("Beginner Leaderboard", new JScrollPane(beginnerTable));

        // Intermediate Leaderboard
        intermediateModel = new DefaultTableModel();
        intermediateTable = new JTable(intermediateModel);
        intermediateModel.addColumn("Player Name");
        intermediateModel.addColumn("Total Score (/25)");
        fetchLeaderboard("Intermediate", intermediateModel);
        tabbedPane.addTab("Intermediate Leaderboard", new JScrollPane(intermediateTable));

        // Advanced Leaderboard
        advancedModel = new DefaultTableModel();
        advancedTable = new JTable(advancedModel);
        advancedModel.addColumn("Player Name");
        advancedModel.addColumn("Total Score (/25)");
        fetchLeaderboard("Advanced", advancedModel);
        tabbedPane.addTab("Advanced Leaderboard", new JScrollPane(advancedTable));

        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 1));
        totalPlayersLabel = new JLabel();
        statsPanel.add(totalPlayersLabel);
        fetchStatistics();

        // Add components
        add(messagePanel, BorderLayout.NORTH); // Add message at the top
        add(tabbedPane, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    
    /**
     * Fetch and display all players' scores from the database sorted by level
     */
    private void fetchAndDisplayReport() {
        String url = "jdbc:mysql://localhost:3306/CompetitionDB";
        String user = "root";
        String password = "";

        String query = "SELECT Name, Level, Score1, Score2, Score3, Score4, Score5, " +
                       "(COALESCE(Score1, 0) + COALESCE(Score2, 0) + COALESCE(Score3, 0) + " +
                       "COALESCE(Score4, 0) + COALESCE(Score5, 0)) / 5.0 AS OverallScore " + 
                       "FROM Competitors ORDER BY " +
                       "CASE Level " +
                       "WHEN 'Beginner' THEN 1 " +
                       "WHEN 'Intermediate' THEN 2 " +
                       "WHEN 'Advanced' THEN 3 END, OverallScore DESC";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("Name");
                String level = rs.getString("Level");
                int score1 = rs.getInt("Score1");
                int score2 = rs.getInt("Score2");
                int score3 = rs.getInt("Score3");
                int score4 = rs.getInt("Score4");
                int score5 = rs.getInt("Score5");
                double overallScore = rs.getDouble("OverallScore");

                overallModel.addRow(new Object[]{name, level, score1, score2, score3, score4, score5, overallScore});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    /**
     * Fetches the leaderboard for a specific level and updates the given table model.
     * @param level The difficulty level of the quiz (Beginner, Intermediate, Advanced).
     * @param model The table model to populate with leaderboard data.
     */
    private void fetchLeaderboard(String level, DefaultTableModel model) {
        String url = "jdbc:mysql://localhost:3306/CompetitionDB";
        String user = "root";
        String password = "";

        String query = "SELECT Name, (Score1 + Score2 + Score3 + Score4 + Score5) AS TotalScore " +
                       "FROM Competitors WHERE Level = ? ORDER BY TotalScore DESC LIMIT 10"; // Top 10 Players

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, level);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("Name");
                int totalScore = rs.getInt("TotalScore");
                model.addRow(new Object[]{name, totalScore});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fetch total players
    
    /**
     * Fetches and displays the total number of players in the competition.
     */
    private void fetchStatistics() {
        String url = "jdbc:mysql://localhost:3306/CompetitionDB";
        String user = "root";
        String password = "";

        String totalPlayersQuery = "SELECT COUNT(*) AS TotalPlayers FROM Competitors";
        

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(totalPlayersQuery);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int totalPlayers = rs.getInt("TotalPlayers");
                totalPlayersLabel.setText("Total Players: " + totalPlayers);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    /**
     * The main method to launch the report window.
     * @param args  Command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Report::new);
    }
}
