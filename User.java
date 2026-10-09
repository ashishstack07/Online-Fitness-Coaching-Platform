public class User {

    private String userId;
    private String name;
    private String email;
    private String role;

    public User(String userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static boolean isValidRole(String role) {
        return role != null
                && (role.equalsIgnoreCase("Admin")
                || role.equalsIgnoreCase("Coach")
                || role.equalsIgnoreCase("User"));
    }

    @Override
    public String toString() {
        return "User ID: " + userId
                + " | Name: " + name
                + " | Email: " + email
                + " | Role: " + role;
    }
}
