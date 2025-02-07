package FinalAssessment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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
                JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score);
                System.exit(0);
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

    private void startQuiz() {
        score = 0;
        currentQuestionIndex = 0;
        showNextQuestion();
    }

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

    private void storeScore() {
        String columnName = "Score" + currentGameIndex;
        String query = "UPDATE Competitors SET " + columnName + " = ? WHERE Competitor_ID = ?";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/CompetitionDB", "root", "");
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, score);
            stmt.setInt(2, competitorId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCurrentGameIndex() {
        String query = "SELECT Score1, Score2, Score3, Score4, Score5 FROM Competitors WHERE Competitor_ID = ?";
        int gameIndex = 1;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/CompetitionDB", "root", "");
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, competitorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {  // Check if a record exists
                for (int i = 1; i <= 5; i++) {
                    Object score = rs.getObject("Score" + i);
                    if (score == null || (score instanceof Integer && (Integer) score == 0)) {
                        return i; // Found an empty slot
                    }
                }
                return 6; // All 5 slots are filled
            } else {
                return 1; // No record found, must be the first game
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 6; // Default to 6 if an error occurs
    }


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
