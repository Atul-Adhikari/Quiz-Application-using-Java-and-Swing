package FinalAssessment;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;



/**
 * The QuizGame class represents a quiz game where a competitor answers multiple-choice questions based on their selected level.
 */
public class QuizGame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel questionPanel;
    private JLabel questionLabel;
    private JRadioButton optionA, optionB, optionC, optionD;
    private ButtonGroup optionsGroup;
    private JButton nextButton;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<Question> questions;
    private int competitorId;
    private String level;
    private int currentGameIndex;

    
    /**
     *Constructs a new  QuizGame instance for the given competitor and level.

     * @param competitorId	The unique ID of the competitor.
     * @param level	The difficulty level of the quiz (Beginner, Intermediate, Advanced).
     */
    public QuizGame(int competitorId, String level) {
        this.competitorId = competitorId;
        this.level = level;
        this.currentGameIndex = getCurrentGameIndex();

        if (currentGameIndex > 5) {
            JOptionPane.showMessageDialog(this, "You have already played 5 times. No more attempts allowed.", "Game Over", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }

        setTitle("Quiz Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Question Panel
        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

        questionLabel = new JLabel();
        questionPanel.add(questionLabel);

        optionA = new JRadioButton();
        optionB = new JRadioButton();
        optionC = new JRadioButton();
        optionD = new JRadioButton();
        optionsGroup = new ButtonGroup();
        optionsGroup.add(optionA);
        optionsGroup.add(optionB);
        optionsGroup.add(optionC);
        optionsGroup.add(optionD);

        questionPanel.add(optionA);
        questionPanel.add(optionB);
        questionPanel.add(optionC);
        questionPanel.add(optionD);

        nextButton = new JButton("Next");
        questionPanel.add(nextButton);

        nextButton.addActionListener(e -> {
            checkAnswer();
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                showNextQuestion();
            } else {
                storeScore();
            }
        });

        add(questionPanel, BorderLayout.CENTER);
        
        // Load questions
        questions = loadQuestions(level);
        if (!questions.isEmpty()) {
            startQuiz();
        } else {
            JOptionPane.showMessageDialog(this, "No questions available for this level.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    
    /**
     * Starts the quiz by resetting the score and displaying the first question.
     */
    private void startQuiz() {
        score = 0;
        currentQuestionIndex = 0;
        showNextQuestion();
    }

    
    /**
     * Displays the next question in the quiz.
     */
    private void showNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            questionLabel.setText(question.getQuestion());
            optionA.setText(question.getOptionA());
            optionB.setText(question.getOptionB());
            optionC.setText(question.getOptionC());
            optionD.setText(question.getOptionD());
            optionsGroup.clearSelection();
        }
    }

    
    /**
     * Checks the selected answer against the correct answer and updates the score.
     */
    private void checkAnswer() {
        Question question = questions.get(currentQuestionIndex);
        String selectedAnswer = null;

        if (optionA.isSelected()) selectedAnswer = "a";
        else if (optionB.isSelected()) selectedAnswer = "b";
        else if (optionC.isSelected()) selectedAnswer = "c";
        else if (optionD.isSelected()) selectedAnswer = "d";

        if (selectedAnswer != null && selectedAnswer.equals(question.getCorrectAnswer())) {
            score++;
        }
    }

    
    /**
     * Stores the competitor's score in the database and displays a summary.
     */
    private void storeScore() {
        String columnName = "Score" + currentGameIndex;
        String query = "UPDATE Competitors SET " + columnName + " = ? WHERE Competitor_ID = ?";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/CompetitionDB", "root", "");
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, score);
            stmt.setInt(2, competitorId);
            stmt.executeUpdate();

            // Show score in "X/5" format
            JOptionPane.showMessageDialog(this, "You scored: " + score + "/5", "Quiz Results", JOptionPane.INFORMATION_MESSAGE);

            // Display Report after showing the score
            SwingUtilities.invokeLater(() -> {
                new Report();
                dispose(); // Close the quiz window
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    
    /**
     * Retrieves the current game index for the competitor based on their past attempts.
     * 
     * @return The next available game index (1-5). If the competitor has played 5 times, returns 6.
     */
    private int getCurrentGameIndex() {
        String query = "SELECT Score1, Score2, Score3, Score4, Score5 FROM Competitors WHERE Competitor_ID = ?";
        int gameIndex = 1;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/CompetitionDB", "root", "");
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, competitorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {  
                for (int i = 1; i <= 5; i++) {
                    Object score = rs.getObject("Score" + i);
                    if (score == null || (score instanceof Integer && (Integer) score == 0)) {
                        return i;
                    }
                }
                return 6;
            } else {
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 6;
    }

    
    
    /**
     * Loads a random selection of five questions from the database for the specified level.
     * @param level The quiz difficulty level (Beginner, Intermediate, Advanced).
     * @return A list of up to 5 Question objects.
     */
    private List<Question> loadQuestions(String level) {
        List<Question> questionList = new ArrayList<>();
        String query = "SELECT * FROM Questions WHERE Level = ?";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/CompetitionDB", "root", "");
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, level);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Question question = new Question(
                        rs.getString("Question"),
                        rs.getString("OptionA"),
                        rs.getString("OptionB"),
                        rs.getString("OptionC"),
                        rs.getString("OptionD"),
                        rs.getString("CorrectAnswer")
                );
                questionList.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collections.shuffle(questionList);
        return questionList.subList(0, Math.min(5, questionList.size()));
    }
}
