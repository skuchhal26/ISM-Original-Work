import java.util.*;

public class SecuritySimulation {

    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new SecuritySimulation().start();
    }
    
// This method controls the entire program.
// It shows a menu to the user, takes their choice,
// and runs the selected cybersecurity attack simulation.
// The program keeps running until the user chooses to exit.

    private void start() {
        System.out.println("=== CYBERSECURITY ATTACK SIMULATION ===");

        while (true) {
            System.out.println("\nChoose an attack scenario:");
            System.out.println("1. SQL Injection Attack");
            System.out.println("2. Phishing Attack");
            System.out.println("3. Brute Force Password Attack");
            System.out.println("4. Man-in-the-Middle Attack");
            System.out.println("5. Run All Scenarios");
            System.out.println("0. Exit");
            System.out.print("\nYour choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
               sqlInjectionAttack();
            } else if (choice == 2) {
               phishingAttack();
            } else if (choice == 3) {
               bruteForceAttack();
            } else if (choice == 4) { 
               manInTheMiddleAttack();
            } else if (choice == 5) {
               sqlInjectionAttack();
               phishingAttack();
               bruteForceAttack();
               manInTheMiddleAttack();
            } else if (choice == 0) {
                System.out.println("Thank You For Trying This");
                return;
            } else {
                System.out.println("Invalid choice");
            }
        }
    }

// The methods below simulate different types of cyber attacks.
// Each method represents one attack scenario and prints
// step-by-step messages showing how the attack happens
// and how it can be prevented.
  
    private void sqlInjectionAttack() {
        title("SQL INJECTION ATTACK");
        println("[SCENARIO] Weak login system found");
        println("Target website: vulnerable-bank.com");
        println("\n[ATTACKER] Enters special input instead of a password");
        println("Username entered: admin");
        println("Password entered: trick input that always works");
        println("\n[SERVER] Login system fails to check input");
        println("\n[COMPROMISED] Login successful!");
        println("Logged in as: Shaurya K (Admin)");
        defense("Use prepared statements", "Validate user input", "Never trust user data", "Limit database access");
    }

    private void phishingAttack() {
        title("PHISHING ATTACK");
        println("[SCENARIO] Fake email attack");
        println("\n[ATTACKER] Sends fake email");
        println("From: security@-paypol.com");
        println("Link: paypol-login.com");
        println("The Link is different paypol vs paypal. The A is switiched with the O.");
        println("\n[VICTIM] Shaurya K clicks the link");
        println("Enters login details");
        println("\n[COMPROMISED] Credentials stolen!");
        println("User: Shaurya K");
        defense("Check sender email", "Do not click unknown links", "Enable MFA");
    }

    private void bruteForceAttack() {
        title("BRUTE FORCE ATTACK");
        println("[SCENARIO] Guessing weak password");
        println("Target user: shaurya.k@company.com");
        println("\n[ATTACKER] Trying common passwords...");
        println("123456 - FAILED");
        println("password - FAILED");
        println("admin123 - FAILED");
        println("password123 - SUCCESS");
        println("\n[COMPROMISED] Account accessed!");
        println("Owner: Shaurya K");
        defense("Use strong passwords", "Lock account after failures", "Enable CAPTCHA", "Enable MFA");
    }

    private void manInTheMiddleAttack() {
        title("MAN-IN-THE-MIDDLE ATTACK");
        println("[SCENARIO] Public WiFi attack");
        println("Network: CoffeeShop_WiFi");
        println("\n[ATTACKER] Intercepts data");
        println("\n[VICTIM] Shaurya K logs into bank site");
        println("\n[INTERCEPTED DATA]");
        println("Username: Shaurya K");
        println("Password: hidden");
        defense("Use HTTPS", "Avoid public WiFi", "Use a VPN");
    }

// The methods below are helper functions.
// They are used to format the output,
// such as printing titles, defense tips,
// and displaying text on the screen.

    private void title(String t) {
        println("\n=== " + t + " ===");
    }

    private void defense(String... tips) {
        println("\n[DEFENSE] How to prevent:");
        for (String t : tips) {
            println(" - " + t);
        }
    }

    private void println(String text) {
        System.out.println(text);
    }
}