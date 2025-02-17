package FinalAssessment;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * The Name class represents a competitor's name, providing methods to retrieve the full name, initials, first name, and last name.
 */
class Name {
    private String firstName;
    private String lastName;

    
    /**
     * Constructs a Name object with a given first and last name.
     * @param firstName The first name of the competitor.
     * @param lastName The last name of the competitor.
     */
    public Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    
    
    /**
     * Returns the full name of the competitor.
     * @return A string representing the competitor's full name.
     */
    public String getFullName() { return firstName + " " + lastName; }
    
    
    /** Returns the full name of the competitor.
     * @return A string representing the competitor name initials.
     */
    public String getInitials() { return firstName.charAt(0) + ""  + lastName.charAt(0); }
}


/**
 * The Competitor class represents a competitor in a competition, storing their
 * details such as ID, name, competition level, age, country, and scores. It includes
 * methods for calculating scores, saving to a database, and retrieving competitors.
 */
public class Competitor {
    private int competitorId;
    private Name competitorName;
    private String competitionLevel;
    private int age;
//    private String country;
    private int[] scores;
    

    private static final String DB_URL = "jdbc:mysql://localhost/CompetitionDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Constructor
    
    /**
     * Constructs a Competitor object with the provided details.
     * @param competitorId The unique ID of the competitor.
     * @param competitorName The name of the competitor.
     * @param competitionLevel The level of competition (e.g., beginner, intermediate).
     * @param age The age of the competitor.
     * @param country The competitor's country.
     * @param scores The scores achieved by the competitor.
     * @throws IllegalArgumentException if an invalid competition level is provided.
     */
    public Competitor(int competitorId, Name competitorName, String competitionLevel, int age, String country, int[] scores) {
        if (!Arrays.asList("beginner", "intermediate", "advanced", "professional", "expert").contains(competitionLevel.toLowerCase())) {
            throw new IllegalArgumentException("Invalid competition level: " + competitionLevel);
        }
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.competitionLevel = competitionLevel;
        this.age = age;
//        this.country = country;
        this.scores = scores;
    }

 // Getter method for competitorName
    public Name getCompetitorName() {
        return competitorName;
    }

    // Getters

    public int[] getScoreArray() { return scores; }

    // Corrected getOverallScore method
    /**
     * Calculates and returns the competitor's overall score based on the competition level.
     * @return The overall score as a  double.
     */
    public double getOverallScore() {
    	
        if (scores.length == 0) return 0; // No scores, return 0

        Arrays.sort(scores);  // Sort scores (ascending)

        double total = 0;
        switch (competitionLevel.toLowerCase()) {
            case "beginner":
                return scores[scores.length - 1]; // Highest score only
            case "intermediate":
                return (scores[scores.length - 1] + scores[scores.length - 2]) / 2.0; // Top 2 scores avg
            case "advanced":
                return Arrays.stream(scores).average().orElse(0); // Average of all
            case "professional":
                double weightSum = 0, weightedTotal = 0;
                for (int i = 0; i < scores.length; i++) {
                    double weight = (i + 1) * 1.0 / scores.length;
                    weightedTotal += scores[i] * weight;
                    weightSum += weight;
                }
                return weightedTotal / weightSum; // Weighted average
            case "expert":
                if (scores.length > 2) {
                    return Arrays.stream(scores, 1, scores.length - 1).average().orElse(0); // Ignore min/max
                }
                return Arrays.stream(scores).average().orElse(0); // Fallback if <3 scores
            default:
                return Arrays.stream(scores).average().orElse(0); // Default to full average
        }
    }

    // Save to database
    
    /**
     * Saves the competitor's details to the database.
     */
    public void saveToDatabase() {
        String sql = "INSERT INTO Competitors (competitor_id, name, level, age, score1, score2, score3, score4, score5) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, competitorId);
            stmt.setString(2, competitorName.getFullName());
            stmt.setString(3, competitionLevel);
            stmt.setInt(4, age);

            // Fill scores dynamically (handling cases where fewer than 5 scores exist)
            for (int i = 0; i < 5; i++) {
                stmt.setInt(i + 5, (i < scores.length) ? scores[i] : 0);
            }

            stmt.executeUpdate();
            System.out.println("Competitor added to database successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve all competitors from database
    
    /**
     * Retrieves and displays all competitors from the database.
     */
    public static void getAllCompetitors() {
        String sql = "SELECT * FROM Competitors";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Competitor Table:");
            System.out.println("ID | Name | Level | Age | Scores | Overall Score");
            while (rs.next()) {
                int id = rs.getInt("competitor_id");
                String fullName = rs.getString("name");
                String level = rs.getString("level");
                int age = rs.getInt("age");
                int[] scores = {
                    rs.getInt("score1"), 
                    rs.getInt("score2"), 
                    rs.getInt("score3"), 
                    rs.getInt("score4"), 
                    rs.getInt("score5")
                };

                // Calculating overall score (using a simple average here for now)
                double overallScore = Arrays.stream(scores).average().orElse(0);

                System.out.println(id + " | " + fullName + " | " + level + " | " + age + " | " + Arrays.toString(scores) + " | " + overallScore);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve top performer from database
    
    /**
     * Retrieves and displays the top-performing competitor from the database.
     */
    public static void getTopPerformer() {
        String sql = "SELECT * FROM Competitors";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            Competitor topCompetitor = null;
            double highestScore = 0;

            while (rs.next()) {
                int id = rs.getInt("competitor_id");
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

                Competitor competitor = new Competitor(id, new Name(name, ""), level, age, "Unknown", scores);
                double overallScore = competitor.getOverallScore();

                if (overallScore > highestScore) {
                    highestScore = overallScore;
                    topCompetitor = competitor;
                }
            }

            if (topCompetitor != null) {
                System.out.println("Top Performer: " + topCompetitor.competitorName.getFullName() + 
                                   " with an overall score of " + highestScore);
            } else {
                System.out.println("No competitors found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get competitor's full details
    
    /**
     * Displays the full details of a competitor.
     * @return A string consisting of full details.
     */
    public String getFullDetails() {
        return "CompetitorID " + competitorId + ", name " + competitorName.getFullName() +
                ". " + competitorName.getFirstName() + " is a " + competitionLevel + " aged " +
                age + " and has an overall score of " + getOverallScore() + ".";
    }

    // Get competitor's short details
    
    /**
     * Displays the short details of a competitor.
     * @return A string consisting of  the short details.
     */
    public String getShortDetails() {
        return "CN " + competitorId + " (" + competitorName.getInitials() + ") has an overall score of " +
                getOverallScore() + ".";
    }

    // Get score frequency from database
    
    /**
     * Retrieves and displays the frequency of scores in the database.
     */
    public static void getScoreFrequency() {
        String sql = "SELECT score1, score2, score3, score4, score5 FROM Competitors";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            HashMap<Integer, Integer> scoreFrequency = new HashMap<>();

            while (rs.next()) {
                for (int i = 1; i <= 5; i++) {
                    int score = rs.getInt("score" + i);
                    scoreFrequency.put(score, scoreFrequency.getOrDefault(score, 0) + 1);
                }
            }

            System.out.println("Score Frequency:");
            for (Map.Entry<Integer, Integer> entry : scoreFrequency.entrySet()) {
                System.out.println("Score: " + entry.getKey() + " Frequency: " + entry.getValue());
            }

        } catch (SQLException e) {
        	System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
