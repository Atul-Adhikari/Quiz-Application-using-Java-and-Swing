package FinalAssessment;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/**
 * he SimpleDisplay class serves as a simple interactive program to manage and display 
 * competitor information. It provides a text-based menu that allows the user to view 
 * top performers, competitor details, score frequency, and generate a report. It also 
 * allows users to view short details of competitors and interact with the competitor data.
 */
public class SimpleDisplay {

    
	/**
	 * The main method of the SimpleDisplay class that provides a user interface to interact 
     * with the competitor management system. The user can choose from a menu of options 
     * to display top performers, competitor details, score frequencies, short competitor details, 
     * or a full competitor report.
     * The main menu runs in a loop until the user chooses to exit the application.
	 * @param args Command-line arguments (not used).
	 */
	public static void main(String[] args) {
        // Creating a sample list of competitors (you can replace this with actual data from DB)
        Competitor competitor1 = new Competitor(1, new Name("Atul", "Adhikari"), "beginner", 25, "Nepal", new int[]{80, 85, 90, 88, 92});
        Competitor competitor2 = new Competitor(2, new Name("Adhikari", "Atul"), "intermediate", 28, "Canada", new int[]{78, 84, 91, 86, 93});
        Competitor competitor3 = new Competitor(3, new Name("Herald", "College"), "advanced", 22, "UK", new int[]{85, 87, 90, 91, 88});
        
        // Saving competitors to the database
//        competitor1.saveToDatabase();
//        competitor2.saveToDatabase();
//        competitor3.saveToDatabase();
        
        // Creating the CompetitorList and Manager objects
        List<Competitor> competitors = Arrays.asList(competitor1, competitor2, competitor3);
        CompetitorList competitorList = new CompetitorList(competitors);
        Manager manager = new Manager(competitorList);
        
        // Main menu to interact with the user
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            System.out.println("\n----- Main Menu -----");
            System.out.println("1. Print Top Performer");
            System.out.println("2. Print Competitor Details");
            System.out.println("3. Show Score Frequency");
            System.out.println("4. View Competitor Short Details");
            System.out.println("5. Generate Competitor Report");
            System.out.println("6. Exit");
            System.out.print("Choose an option (1-6): ");
            
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1:
                    manager.printTopPerformer();
                    break;
                case 2:
                    System.out.print("Enter competitor ID to view details: ");
                    int id = scanner.nextInt();
                    manager.printCompetitorDetails(id);
                    break;
                case 3:
                    manager.showScoreFrequency();
                    break;
                case 4:
                    manager.promptUserInput();
                    break;
                case 5:
                    competitorList.generateReport();
                    break;
                case 6:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
}
