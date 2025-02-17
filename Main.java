package FinalAssessment;
import java.sql.*;
import java.util.Scanner;


/**
 * Main class for the competition database application.
 * Provides options to generate reports, display top performers, generate statistics,
 * and search for competitors by ID.
 */
public class Main {

    private static final String DB_URL = "jdbc:mysql://localhost/CompetitionDB";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    
    /**
     * Main method to display menu options and handle user input.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            // Display menu
            displayMenu();

            // Take user input
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            // Handle user's choice
            handleChoice(choice);

        } while (choice != 5); // Exit when user chooses option 5

        scanner.close(); // Close scanner when done
        
        
    }

    // Method to display the menu to the user
    
    /**
     * Displays the menu options to the user.
     */
    private static void displayMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Generate full report");
        System.out.println("2. Display Top Performer");
        System.out.println("3. Generate Statistics");
        System.out.println("4. Search competitor by ID");
        System.out.println("5. Exit");
    }

    // Method to handle the user's choice
    
    /**
     * Handles the user's menu selection.
     * @param choice The user's selected menu option.
     */
    private static void handleChoice(int choice) {
        switch (choice) {
            case 1:
                generateReport();
                break;
            case 2:
                displayTopPerformer();
                break;
            case 3:
                generateStatistics();
                break;
            case 4:
                searchCompetitorById();
                break;
            case 5:
                System.out.println("Exiting program.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // Method to generate a report of all competitors
    
    /**
     * Generates a report of all competitors in the database.
     */
    private static void generateReport() {
        String query = "SELECT * FROM Competitors";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n--- Competitor Report ---");
            while (rs.next()) {
                System.out.println("Competitor_ID: " + rs.getInt("Competitor_ID"));
                System.out.println("Name: " + rs.getString("Name"));
                System.out.println("Level: " + rs.getString("Level"));
                System.out.println("Age: " + rs.getInt("Age"));
                System.out.print("Scores: ");
                // Retrieve scores from columns
                int[] scores = new int[5];
                for (int i = 1; i <= 5; i++) {
                    scores[i - 1] = rs.getInt("Score" + i);
                    System.out.print(scores[i - 1] + " ");
                }
                System.out.println("\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to display the top performer based on the highest score
    
    /**
     * Displays the top performer based on the highest score among competitors.
     */
    private static void displayTopPerformer() {
        String query = "SELECT Competitor_ID, Name, Level, Score1, Score2, Score3, Score4, Score5 FROM Competitors";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int topScore = -1;
            String topPerformer = "";
            int topCompetitorId = -1;

            while (rs.next()) {
                int[] scores = new int[5];
                for (int i = 1; i <= 5; i++) {
                    scores[i - 1] = rs.getInt("Score" + i);
                }

                int maxScore = getMaxScore(scores);

                if (maxScore > topScore) {
                    topScore = maxScore;
                    topPerformer = rs.getString("Name");
                    topCompetitorId = rs.getInt("Competitor_ID");
                }
            }

            if (topCompetitorId != -1) {
                System.out.println("\n--- Top Performer ---");
                System.out.println("Competitor_ID: " + topCompetitorId);
                System.out.println("Name: " + topPerformer);
                System.out.println("Highest Score: " + topScore);
            } else {
                System.out.println("No competitors found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to get the max score from an array of scores
     * @param scores Array of scores.
     * @return max score The highest score.
     */
    private static int getMaxScore(int[] scores) {
        int max = -1;
        for (int score : scores) {
            if (score > max) {
                max = score;
            }
        }
        return max;
    }

    // Method to generate statistics for scores (e.g., frequency of each score)
    
    /**
     * Generates statistics for scores (e.g., frequency of each score).
     */
    private static void generateStatistics() {
        String query = "SELECT Score1, Score2, Score3, Score4, Score5 FROM Competitors";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n--- Score Statistics ---");
            int[] scoreFrequencies = new int[6]; // Assume scores are between 1 and 5
            while (rs.next()) {
                for (int i = 1; i <= 5; i++) {
                    int score = rs.getInt("Score" + i);
                    if (score >= 1 && score <= 5) {
                        scoreFrequencies[score]++;
                    }
                }
            }

            for (int i = 1; i <= 5; i++) {
                System.out.println("Score " + i + ": " + scoreFrequencies[i] + " occurrences");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Method to search for a competitor by Competitor_ID
     */
    private static void searchCompetitorById() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter competitor ID: ");
        int competitorId = scanner.nextInt();

        String query = "SELECT * FROM Competitors WHERE Competitor_ID = ?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, competitorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- Competitor Found ---");
                System.out.println("Competitor_ID: " + rs.getInt("Competitor_ID"));
                System.out.println("Name: " + rs.getString("Name"));
                System.out.println("Level: " + rs.getString("Level"));
                System.out.println("Age: " + rs.getInt("Age"));
                System.out.print("Scores: ");
                for (int i = 1; i <= 5; i++) {
                    System.out.print(rs.getInt("Score" + i) + " ");
                }
                System.out.println("\n");
            } else {
                System.out.println("No competitor found with Competitor_ID: " + competitorId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
//        scanner.close();
    }
}
