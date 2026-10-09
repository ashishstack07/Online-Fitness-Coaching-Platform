import java.util.ArrayList;
import java.util.Scanner;

public class FitnessPlatform {

    ArrayList<User> users = new ArrayList<User>();
    ArrayList<WorkoutPlan> workoutPlans = new ArrayList<WorkoutPlan>();
    ArrayList<String> workoutLogs = new ArrayList<String>();
    ArrayList<String> messages = new ArrayList<String>();
    ArrayList<String> feedback = new ArrayList<String>();
    ArrayList<String> contentForModeration = new ArrayList<String>();
    ArrayList<String> systemSettings = new ArrayList<String>();

    public FitnessPlatform() {
        users.add(new User("A1", "Admin One", "admin@fitness.com", "Admin"));
        users.add(new User("C1", "Coach Mike", "mike@fitness.com", "Coach"));
        users.add(new User("U1", "John Doe", "john@fitness.com", "User"));

        systemSettings.add("Platform Name=Fitness Coaching Platform");
        systemSettings.add("Max Plans Per Coach=10");
        systemSettings.add("Max Logs Per User=50");
    }

    public static int readInt(Scanner sc, String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number input.");
            return -1;
        }
    }

    private String normalizeRole(String role) {
        if (role.equalsIgnoreCase("Admin")) {
            return "Admin";
        } else if (role.equalsIgnoreCase("Coach")) {
            return "Coach";
        }
        return "User";
    }

    public boolean isRole(String userId, String role) {
        User u = findUser(userId);
        return u != null && u.getRole().equalsIgnoreCase(role);
    }

    public User findUser(String userId) {
        for (User u : users) {
            if (u.getUserId().equalsIgnoreCase(userId)) {
                return u;
            }
        }
        return null;
    }

    public WorkoutPlan findWorkoutPlan(String planId) {
        for (WorkoutPlan p : workoutPlans) {
            if (p.getPlanId().equalsIgnoreCase(planId)) {
                return p;
            }
        }
        return null;
    }

    public int countRole(String role) {
        int count = 0;
        for (User u : users) {
            if (u.getRole().equalsIgnoreCase(role)) {
                count++;
            }
        }
        return count;
    }

    private int countPlansByCoach(String coachId) {
        int count = 0;
        for (WorkoutPlan p : workoutPlans) {
            if (p.getCoachId().equalsIgnoreCase(coachId)) {
                count++;
            }
        }
        return count;
    }

    private int countLogsByUser(String userId) {
        int count = 0;
        for (String log : workoutLogs) {
            String[] parts = log.split("\\|", -1);
            if (parts[0].equalsIgnoreCase(userId)) {
                count++;
            }
        }
        return count;
    }

    private int countStatus(String status) {
        int count = 0;
        for (WorkoutPlan p : workoutPlans) {
            if (p.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }

    private String getSetting(String key) {
        for (String setting : systemSettings) {
            String[] parts = setting.split("=", 2);
            if (parts[0].trim().equalsIgnoreCase(key)) {
                return parts[1].trim();
            }
        }
        return "";
    }

    private int getSettingInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getSetting(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private void removeFromModeration(String planId) {
        for (int i = contentForModeration.size() - 1; i >= 0; i--) {
            if (contentForModeration.get(i).startsWith("Plan " + planId + " ")) {
                contentForModeration.remove(i);
            }
        }
    }

    public void addUser(Scanner sc) {
        System.out.print("Enter new User ID: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("User ID cannot be empty.");
            return;
        }
        if (findUser(id) != null) {
            System.out.println("User ID already exists.");
            return;
        }
        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Enter Role (Admin/Coach/User): ");
        String role = sc.nextLine().trim();
        if (!User.isValidRole(role)) {
            System.out.println("Invalid role. User not added.");
            return;
        }
        users.add(new User(id, name, email, normalizeRole(role)));
        System.out.println("User added successfully!");
    }

    public void viewUsers() {
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        System.out.println("============= ALL USERS =============");
        for (User u : users) {
            System.out.println(u);
        }
    }

    public void updateUser(Scanner sc) {
        System.out.print("Enter User ID to update: ");
        String id = sc.nextLine().trim();
        User u = findUser(id);
        if (u == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("Enter new name (blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) {
            u.setName(name);
        }
        System.out.print("Enter new email (blank to keep): ");
        String email = sc.nextLine().trim();
        if (!email.isEmpty()) {
            u.setEmail(email);
        }
        System.out.print("Enter new role (blank to keep): ");
        String role = sc.nextLine().trim();
        if (!role.isEmpty()) {
            if (!User.isValidRole(role)) {
                System.out.println("Invalid role. Role not changed.");
            } else if (u.getRole().equalsIgnoreCase("Admin")
                    && !role.equalsIgnoreCase("Admin")
                    && countRole("Admin") <= 1) {
                System.out.println("Cannot change the role of the only Admin.");
            } else {
                u.setRole(normalizeRole(role));
            }
        }
        System.out.println("User updated successfully!");
        System.out.println(u);
    }

    public void deleteUser(Scanner sc) {
        System.out.print("Enter User ID to delete: ");
        String id = sc.nextLine().trim();
        User u = findUser(id);
        if (u == null) {
            System.out.println("User not found.");
            return;
        }
        if (u.getRole().equalsIgnoreCase("Admin") && countRole("Admin") <= 1) {
            System.out.println("Cannot delete the only Admin account.");
            return;
        }
        users.remove(u);
        System.out.println("User deleted successfully!");
    }

    public void createWorkoutPlan(Scanner sc, String coachId) {
        if (!isRole(coachId, "Coach")) {
            System.out.println("Invalid coach.");
            return;
        }
        int maxPlans = getSettingInt("Max Plans Per Coach", 10);
        if (countPlansByCoach(coachId) >= maxPlans) {
            System.out.println("Maximum workout plan limit reached for this coach.");
            return;
        }
        System.out.print("Plan ID: ");
        String planId = sc.nextLine().trim();
        if (planId.isEmpty()) {
            System.out.println("Plan ID cannot be empty.");
            return;
        }
        if (findWorkoutPlan(planId) != null) {
            System.out.println("Plan ID already exists.");
            return;
        }
        System.out.print("Plan Name: ");
        String planName = sc.nextLine().trim();
        System.out.print("Description: ");
        String description = sc.nextLine().trim();
        System.out.print("Exercises: ");
        String exercises = sc.nextLine().trim();
        System.out.print("Schedule: ");
        String schedule = sc.nextLine().trim();

        WorkoutPlan plan = new WorkoutPlan(planId, planName, coachId,
                exercises, schedule, description);
        workoutPlans.add(plan);
        contentForModeration.add("Plan " + planId + " (" + planName
                + ") by Coach " + coachId + " awaits approval");
        System.out.println();
        System.out.println("Workout plan submitted for approval!");
    }

    public void viewWorkoutPlans() {
        if (workoutPlans.isEmpty()) {
            System.out.println("No workout plans available.");
            return;
        }
        System.out.println("============= ALL WORKOUT PLANS =============");
        for (WorkoutPlan p : workoutPlans) {
            System.out.println(p);
        }
    }

    public void viewMyWorkoutPlans(String coachId) {
        boolean found = false;
        System.out.println("============= MY WORKOUT PLANS =============");
        for (WorkoutPlan p : workoutPlans) {
            if (p.getCoachId().equalsIgnoreCase(coachId)) {
                System.out.println(p);
                found = true;
            }
        }
        if (!found) {
            System.out.println("You have no workout plans.");
        }
    }

    public void updateWorkoutPlan(Scanner sc, String coachId) {
        System.out.print("Enter Plan ID to update: ");
        String planId = sc.nextLine().trim();
        WorkoutPlan p = findWorkoutPlan(planId);
        if (p == null) {
            System.out.println("Workout plan not found.");
            return;
        }
        if (!p.getCoachId().equalsIgnoreCase(coachId)) {
            System.out.println("You can only update your own workout plans.");
            return;
        }
        System.out.print("Enter new name (blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) {
            p.setPlanName(name);
        }
        System.out.print("Enter new description (blank to keep): ");
        String description = sc.nextLine().trim();
        if (!description.isEmpty()) {
            p.setDescription(description);
        }
        System.out.print("Enter new exercises (blank to keep): ");
        String exercises = sc.nextLine().trim();
        if (!exercises.isEmpty()) {
            p.setExercises(exercises);
        }
        System.out.print("Enter new schedule (blank to keep): ");
        String schedule = sc.nextLine().trim();
        if (!schedule.isEmpty()) {
            p.setSchedule(schedule);
        }
        p.resetApproval();
        System.out.println("Workout plan updated. It now needs Admin approval again.");
    }

    public void deleteWorkoutPlan(Scanner sc, String coachId) {
        System.out.print("Enter Plan ID to delete: ");
        String planId = sc.nextLine().trim();
        WorkoutPlan p = findWorkoutPlan(planId);
        if (p == null) {
            System.out.println("Workout plan not found.");
            return;
        }
        if (!p.getCoachId().equalsIgnoreCase(coachId)) {
            System.out.println("You can only delete your own workout plans.");
            return;
        }
        workoutPlans.remove(p);
        removeFromModeration(planId);
        System.out.println("Workout plan deleted successfully!");
    }

    public void approveWorkoutPlan(Scanner sc) {
        System.out.print("Enter Plan ID to approve: ");
        String planId = sc.nextLine().trim();
        WorkoutPlan p = findWorkoutPlan(planId);
        if (p == null) {
            System.out.println("Workout plan not found.");
            return;
        }
        p.approve();
        removeFromModeration(planId);
        System.out.println("Workout plan approved and now available to Users.");
    }

    public void rejectWorkoutPlan(Scanner sc) {
        System.out.print("Enter Plan ID to reject: ");
        String planId = sc.nextLine().trim();
        WorkoutPlan p = findWorkoutPlan(planId);
        if (p == null) {
            System.out.println("Workout plan not found.");
            return;
        }
        p.reject();
        removeFromModeration(planId);
        System.out.println("Workout plan rejected. It is not available to Users.");
    }

    public void logWorkout(Scanner sc, String userId) {
        if (!isRole(userId, "User")) {
            System.out.println("Only registered Users can log workouts.");
            return;
        }
        int maxLogs = getSettingInt("Max Logs Per User", 50);
        if (countLogsByUser(userId) >= maxLogs) {
            System.out.println("Maximum workout log limit reached for this user.");
            return;
        }
        System.out.print("Workout Date: ");
        String date = sc.nextLine().trim();
        System.out.print("Workout Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Duration in minutes: ");
        int duration;
        try {
            duration = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid duration. Please enter a number.");
            return;
        }
        if (duration <= 0) {
            System.out.println("Duration must be a positive number.");
            return;
        }
        System.out.print("Notes: ");
        String notes = sc.nextLine().trim();
        workoutLogs.add(userId + "|" + date + "|" + name + "|" + duration + "|" + notes);
        System.out.println("Workout logged successfully!");
    }

    public void viewWorkoutHistory(String userId) {
        System.out.println("========================================");
        System.out.println("           WORKOUT HISTORY");
        System.out.println("========================================");
        boolean found = false;
        for (String log : workoutLogs) {
            String[] parts = log.split("\\|", 5);
            if (parts[0].equalsIgnoreCase(userId)) {
                System.out.println("Date: " + parts[1]);
                System.out.println("Workout: " + parts[2]);
                System.out.println("Duration: " + parts[3] + " minutes");
                System.out.println("Notes: " + parts[4]);
                System.out.println();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No workout logs found.");
        }
    }

    public void viewUserProgress(String userId) {
        int count = 0;
        int totalDuration = 0;
        String lastWorkout = "";
        for (String log : workoutLogs) {
            String[] parts = log.split("\\|", 5);
            if (parts[0].equalsIgnoreCase(userId)) {
                count++;
                totalDuration += Integer.parseInt(parts[3]);
                lastWorkout = "Date: " + parts[1] + " | Workout: " + parts[2]
                        + " | Duration: " + parts[3] + " minutes";
            }
        }
        System.out.println("========================================");
        System.out.println("          FITNESS PROGRESS REPORT");
        System.out.println("========================================");
        System.out.println("Total completed workouts: " + count);
        System.out.println("Total workout duration: " + totalDuration + " minutes");
        if (count > 0) {
            System.out.println("Average workout duration: " + (totalDuration / count) + " minutes");
        } else {
            System.out.println("Average workout duration: 0 minutes");
        }
        if (!lastWorkout.isEmpty()) {
            System.out.println("Most recent workout: " + lastWorkout);
        } else {
            System.out.println("Most recent workout: None");
        }
    }

    public void viewUserWorkoutProgress(Scanner sc) {
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine().trim();
        if (!isRole(userId, "User")) {
            System.out.println("User not found.");
            return;
        }
        viewWorkoutHistory(userId);
        viewUserProgress(userId);
    }

    public void sendMessage(Scanner sc, String senderId) {
        User sender = findUser(senderId);
        if (sender == null) {
            System.out.println("Sender not found.");
            return;
        }
        System.out.print("Enter recipient ID: ");
        String receiverId = sc.nextLine().trim();
        User receiver = findUser(receiverId);
        if (receiver == null) {
            System.out.println("Recipient not found.");
            return;
        }
        if (sender.getRole().equalsIgnoreCase("User")
                && !receiver.getRole().equalsIgnoreCase("Coach")) {
            System.out.println("Users can only send messages to Coaches.");
            return;
        }
        if (sender.getRole().equalsIgnoreCase("Coach")
                && !receiver.getRole().equalsIgnoreCase("User")) {
            System.out.println("Coaches can only send messages to Users.");
            return;
        }
        System.out.print("Enter message: ");
        String text = sc.nextLine().trim();
        if (text.isEmpty()) {
            System.out.println("Message cannot be empty.");
            return;
        }
        messages.add(senderId + "|" + receiverId + "|" + text);
        System.out.println("Message sent successfully!");
    }

    public void viewMessages(String userId) {
        System.out.println("============= MY MESSAGES =============");
        boolean found = false;
        for (String message : messages) {
            String[] parts = message.split("\\|", 3);
            if (parts[1].equalsIgnoreCase(userId)) {
                System.out.println("From " + parts[0] + ": " + parts[2]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No messages found.");
        }
    }

    public void viewInteractionHistory(String userId) {
        System.out.println("============= INTERACTION HISTORY =============");
        boolean found = false;
        for (String message : messages) {
            String[] parts = message.split("\\|", 3);
            if (parts[0].equalsIgnoreCase(userId) || parts[1].equalsIgnoreCase(userId)) {
                System.out.println("From " + parts[0] + " to " + parts[1] + ": " + parts[2]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No interactions found.");
        }
    }

    public void submitFeedback(Scanner sc, String userId) {
        System.out.print("Enter your feedback: ");
        String text = sc.nextLine().trim();
        if (text.isEmpty()) {
            System.out.println("Feedback cannot be empty.");
            return;
        }
        feedback.add(userId + "|" + text);
        System.out.println("Thank you! Your feedback has been submitted.");
    }

    public void viewFeedback() {
        System.out.println("============= USER FEEDBACK =============");
        if (feedback.isEmpty()) {
            System.out.println("No feedback submitted yet.");
            return;
        }
        for (String item : feedback) {
            String[] parts = item.split("\\|", 2);
            System.out.println("User " + parts[0] + ": " + parts[1]);
        }
    }

    public void viewPendingContent() {
        System.out.println("============= PENDING WORKOUT PLANS =============");
        boolean found = false;
        for (WorkoutPlan p : workoutPlans) {
            if (p.getStatus().equalsIgnoreCase("Pending")) {
                System.out.println("Plan ID: " + p.getPlanId());
                System.out.println("Plan Name: " + p.getPlanName());
                System.out.println("Coach ID: " + p.getCoachId());
                System.out.println("Exercises: " + p.getExercises());
                System.out.println("Schedule: " + p.getSchedule());
                System.out.println("Status: " + p.getStatus());
                System.out.println();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No plans are awaiting approval.");
        }
    }

    public void viewContentOverview() {
        System.out.println("============= CONTENT OVERVIEW =============");
        System.out.println("Total registered users: " + users.size());
        System.out.println("Total coaches: " + countRole("Coach"));
        System.out.println("Total regular users: " + countRole("User"));
        System.out.println("Total workout plans: " + workoutPlans.size());
        System.out.println("Approved plans: " + countStatus("Approved"));
        System.out.println("Pending plans: " + countStatus("Pending"));
        System.out.println("Rejected plans: " + countStatus("Rejected"));
    }

    public void updateSystemSettings(Scanner sc) {
        System.out.println("============= SYSTEM SETTINGS =============");
        for (int i = 0; i < systemSettings.size(); i++) {
            System.out.println((i + 1) + ". " + systemSettings.get(i));
        }
        System.out.print("Enter setting number to update: ");
        int number;
        try {
            number = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (number < 1 || number > systemSettings.size()) {
            System.out.println("Invalid setting number.");
            return;
        }
        String oldSetting = systemSettings.get(number - 1);
        String key = oldSetting.split("=", 2)[0].trim();
        System.out.print("Enter new value for " + key + ": ");
        String value = sc.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println("Value cannot be empty.");
            return;
        }
        systemSettings.set(number - 1, key + "=" + value);
        System.out.println("Setting updated successfully!");
    }

    public void viewAvailablePlans() {
        System.out.println("============= AVAILABLE WORKOUT PLANS =============");
        boolean found = false;
        for (WorkoutPlan p : workoutPlans) {
            if (p.isApproved()) {
                System.out.println("Plan ID: " + p.getPlanId()
                        + " | Name: " + p.getPlanName()
                        + " | Coach ID: " + p.getCoachId());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No approved workout plans available.");
        }
    }

    public void viewWorkoutPlanDetails(Scanner sc) {
        System.out.print("Enter Plan ID: ");
        String planId = sc.nextLine().trim();
        WorkoutPlan p = findWorkoutPlan(planId);
        if (p == null || !p.isApproved()) {
            System.out.println("This workout plan is not available.");
            return;
        }
        System.out.println("Plan ID: " + p.getPlanId());
        System.out.println("Plan Name: " + p.getPlanName());
        System.out.println("Description: " + p.getDescription());
        System.out.println("Exercises: " + p.getExercises());
        System.out.println("Schedule: " + p.getSchedule());
        System.out.println("Coach ID: " + p.getCoachId());
    }

    public void viewMyProfile(String userId) {
        User u = findUser(userId);
        if (u == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.println("============= MY PROFILE =============");
        System.out.println(u);
    }

    public void updateMyProfile(Scanner sc, String userId) {
        User u = findUser(userId);
        if (u == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("Enter new name (blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) {
            u.setName(name);
        }
        System.out.print("Enter new email (blank to keep): ");
        String email = sc.nextLine().trim();
        if (!email.isEmpty()) {
            u.setEmail(email);
        }
        System.out.println("Profile updated successfully!");
        System.out.println(u);
    }

    public void adminDashboard(Scanner sc) {
        while (true) {
            System.out.println("========================================");
            System.out.println("             ADMIN DASHBOARD");
            System.out.println("========================================");
            System.out.println();
            System.out.println("1. Add User");
            System.out.println("2. View All Users");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println();
            System.out.println("5. View All Workout Plans");
            System.out.println("6. View Pending Content");
            System.out.println("7. Approve Workout Plan");
            System.out.println("8. Reject Workout Plan");
            System.out.println();
            System.out.println("9. View User Feedback");
            System.out.println("10. View Content Overview");
            System.out.println("11. Manage System Settings");
            System.out.println();
            System.out.println("12. Back");
            System.out.println();
            int choice = readInt(sc, "Enter your choice: ");
            switch (choice) {
                case 1:
                    addUser(sc);
                    break;
                case 2:
                    viewUsers();
                    break;
                case 3:
                    updateUser(sc);
                    break;
                case 4:
                    deleteUser(sc);
                    break;
                case 5:
                    viewWorkoutPlans();
                    break;
                case 6:
                    viewPendingContent();
                    break;
                case 7:
                    approveWorkoutPlan(sc);
                    break;
                case 8:
                    rejectWorkoutPlan(sc);
                    break;
                case 9:
                    viewFeedback();
                    break;
                case 10:
                    viewContentOverview();
                    break;
                case 11:
                    updateSystemSettings(sc);
                    break;
                case 12:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }

    public void coachDashboard(Scanner sc, String coachId) {
        while (true) {
            System.out.println("========================================");
            System.out.println("             COACH DASHBOARD");
            System.out.println("========================================");
            System.out.println();
            System.out.println("1. Create Workout Plan");
            System.out.println("2. View My Workout Plans");
            System.out.println("3. Update Workout Plan");
            System.out.println("4. Delete Workout Plan");
            System.out.println();
            System.out.println("5. View User Workout Progress");
            System.out.println("6. View Messages");
            System.out.println("7. Send Message to User");
            System.out.println("8. View Interaction History");
            System.out.println("9. View User Feedback");
            System.out.println();
            System.out.println("10. Back");
            System.out.println();
            int choice = readInt(sc, "Enter your choice: ");
            switch (choice) {
                case 1:
                    createWorkoutPlan(sc, coachId);
                    break;
                case 2:
                    viewMyWorkoutPlans(coachId);
                    break;
                case 3:
                    updateWorkoutPlan(sc, coachId);
                    break;
                case 4:
                    deleteWorkoutPlan(sc, coachId);
                    break;
                case 5:
                    viewUserWorkoutProgress(sc);
                    break;
                case 6:
                    viewMessages(coachId);
                    break;
                case 7:
                    sendMessage(sc, coachId);
                    break;
                case 8:
                    viewInteractionHistory(coachId);
                    break;
                case 9:
                    viewFeedback();
                    break;
                case 10:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }

    public void userDashboard(Scanner sc, String userId) {
        while (true) {
            System.out.println("========================================");
            System.out.println("              USER DASHBOARD");
            System.out.println("========================================");
            System.out.println();
            System.out.println("1. View Available Workout Plans");
            System.out.println("2. View Workout Plan Details");
            System.out.println("3. Log Completed Workout");
            System.out.println("4. View My Workout History");
            System.out.println("5. View My Fitness Progress");
            System.out.println();
            System.out.println("6. View Messages");
            System.out.println("7. Send Message to Coach");
            System.out.println("8. Submit Feedback");
            System.out.println();
            System.out.println("9. View My Profile");
            System.out.println("10. Update My Profile");
            System.out.println();
            System.out.println("11. Back");
            System.out.println();
            int choice = readInt(sc, "Enter your choice: ");
            switch (choice) {
                case 1:
                    viewAvailablePlans();
                    break;
                case 2:
                    viewWorkoutPlanDetails(sc);
                    break;
                case 3:
                    logWorkout(sc, userId);
                    break;
                case 4:
                    viewWorkoutHistory(userId);
                    break;
                case 5:
                    viewUserProgress(userId);
                    break;
                case 6:
                    viewMessages(userId);
                    break;
                case 7:
                    sendMessage(sc, userId);
                    break;
                case 8:
                    submitFeedback(sc, userId);
                    break;
                case 9:
                    viewMyProfile(userId);
                    break;
                case 10:
                    updateMyProfile(sc, userId);
                    break;
                case 11:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }
}
