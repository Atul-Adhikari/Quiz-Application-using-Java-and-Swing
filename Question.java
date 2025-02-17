package FinalAssessment;


/**
 * The Question class represents a multiple-choice question with four options.
 * It stores the question text, answer choices, and the correct answer.
 */
public class Question {
    private String question, optionA, optionB, optionC, optionD, correctAnswer;

    
    /**
     * @param question	The question text.
     * @param optionA	The first answer choice.
     * @param optionB	The second answer choice.
     * @param optionC	The third answer choice.
     * @param optionD	The fourth answer choice.
     * @param correctAnswer The correct answer among the options.
     */
    public Question(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    /**
     * Gets the question text.
     * @return The question as a string.
     */
    public String getQuestion() { return question; }
    
   
    
    /**
     * Gets the first answer choice.
     * @return Option A as a string.
     */
    public String getOptionA() { return optionA; }
    
    
    /**
     * Gets the second answer choice.
     * @return Option B as a String.
     */
    public String getOptionB() { return optionB; }
    
    
    
    /**
     * Gets the third answer choice.
     * @return Option C as a String.
     */
    public String getOptionC() { return optionC; }
    
    
    /**
     * Gets the fourth answer choice.
     * @return Option D as a String.
     */
    public String getOptionD() { return optionD; }
    
    
    
    /**
     * Gets the correct answer.
     * @return The correct answer as a String.
     */
    public String getCorrectAnswer() { return correctAnswer; }
}
