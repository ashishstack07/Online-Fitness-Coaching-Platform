public class WorkoutPlan {

    private String planId;
    private String planName;
    private String coachId;
    private String exercises;
    private String schedule;
    private String description;
    private String status;

    public WorkoutPlan(String planId, String planName, String coachId,
                       String exercises, String schedule, String description) {
        this.planId = planId;
        this.planName = planName;
        this.coachId = coachId;
        this.exercises = exercises;
        this.schedule = schedule;
        this.description = description;
        this.status = "Pending";
    }

    public String getPlanId() {
        return planId;
    }

    public String getPlanName() {
        return planName;
    }

    public String getCoachId() {
        return coachId;
    }

    public String getExercises() {
        return exercises;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public void setExercises(String exercises) {
        this.exercises = exercises;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void approve() {
        this.status = "Approved";
    }

    public void reject() {
        this.status = "Rejected";
    }

    public void resetApproval() {
        this.status = "Pending";
    }

    public boolean isApproved() {
        return status.equalsIgnoreCase("Approved");
    }

    @Override
    public String toString() {
        return "Plan ID: " + planId
                + " | Name: " + planName
                + " | Coach ID: " + coachId
                + " | Exercises: " + exercises
                + " | Schedule: " + schedule
                + " | Status: " + status;
    }
}
