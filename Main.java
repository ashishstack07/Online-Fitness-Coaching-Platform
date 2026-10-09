import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FitnessPlatform platform = new FitnessPlatform();

        while (true) {
            System.out.println("========================================");
            System.out.println("     ONLINE FITNESS COACHING PLATFORM");
            System.out.println("========================================");
            System.out.println();
            System.out.println("1. Admin");
            System.out.println("2. Coach");
            System.out.println("3. User");
            System.out.println("4. Exit");
            System.out.println();

            int choice = FitnessPlatform.readInt(sc, "Enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1:
                    System.out.print("Enter Admin User ID: ");
                    String adminId = sc.nextLine().trim();
                    if (platform.isRole(adminId, "Admin")) {
                        platform.adminDashboard(sc);
                    } else {
                        System.out.println("Invalid Admin ID.");
                    }
                    break;
                case 2:
                    System.out.print("Enter Coach User ID: ");
                    String coachId = sc.nextLine().trim();
                    if (platform.isRole(coachId, "Coach")) {
                        platform.coachDashboard(sc, coachId);
                    } else {
                        System.out.println("Invalid Coach ID.");
                    }
                    break;
                case 3:
                    System.out.print("Enter User User ID: ");
                    String userId = sc.nextLine().trim();
                    if (platform.isRole(userId, "User")) {
                        platform.userDashboard(sc, userId);
                    } else {
                        System.out.println("Invalid User ID.");
                    }
                    break;
                case 4:
                    System.out.println("Thank you for using the Online Fitness Coaching Platform!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }
}
