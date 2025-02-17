package FinalAssessment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


/**
 * The Manager class handles the operations related to competitors in the competition.
 * It interacts with the database to retrieve and manage competitor information,
 * prints details about top performers, and provides functionality to show score frequency.
 * It also includes functionality to prompt user input for viewing competitor details.
 */
public class Manager {
    private CompetitorList competitorList;

    // Database connection details 
    private static final String DB_URL = "jdbc:mysql://localhost:3306/CompetitionDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    
    /**
     * Constructor for the Manager class.
     * Initializes the Manager with a given CompetitorList.
     * @param competitorList The list of competitors managed by the Manager.
     */
    public Manager(CompetitorList competitorList) {
        this.competitorList = competitorList;
    }

    
    /**
     * Prints the details of the top performer from the competition.
     */
    public void printTopPerformer() {
        Competitor.getTopPerformer();
    }

    
    /**
     * Prints the details of a specific competitor identified by their ID.
     * In this example, it retrieves all competitors, but it could be modified to retrieve specific competitor data.
     * @param competitorId The ID of the competitor whose details need to be displayed.
     */
    public void printCompetitorDetails(int competitorId) {
        Competitor.getAllCompetitors();
    }

    
    /**
     * Displays the frequency of the scores of all competitors.
     */
    public void showScoreFrequency() {
        Competitor.getScoreFrequency();
    }

    
    /**
     * Prompts the user for a competitor ID and displays the short details of the competitor.
     * It retrieves the competitor details from the database using the provided ID.
     */
    public void promptUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter competitor ID to view short details: ");
        int id = scanner.nextInt();
        
        // Retrieve the competitor from the database using the given ID
        Competitor competitor = getCompetitorById(id);
        
        if (competitor != null) {
            System.out.println(competitor.getShortDetails());
        } else {
            System.out.println("Competitor with ID " + id + " not found.");
        }
        
        scanner.close();
    }

    // Retrieve the competitor by ID from the database
    
    /**
     * Retrieves a competitor from the database based on their ID.
     * @param id The ID of the competitor to retrieve.
     * @return A Competitor object representing the competitor with the given ID, or null if not found.
     */
    private Competitor getCompetitorById(int id) {
        String sql = "SELECT * FROM Competitors WHERE competitor_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String level = rs.getString("level");
                int age = rs.getInt("age");
                int[] scores = {
                    rs.getInt("score1"),
                    rs.getInt("score2"),
                    rs.getInt("score3"),
                    rs.getInt("score4"),
                    rs.getInt("score5")
                };
                return new Competitor(id, new Name(name, ""), level, age, "Unknown", scores);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
