import java.util.*;
/*
 * Object for University Students
 * Has constructor that extends Student params, and adds other required ones
 * Connection strength calculator used for building graph
 */
public class UniversityStudent extends Student {

    // Extra not from `Student`
    private UniversityStudent roommate;
    public List<UniversityStudent> friendList;
    public List<String> chatHistory;

    /**
     * Constructs a UniversityStudent with all required attributes.
     *
     * @param name                student's name (unique identifier)
     * @param age                 student's age
     * @param gender              student's gender
     * @param year                academic year (1-4)
     * @param major               student's major
     * @param gpa                 student's GPA
     * @param roommatePreferences ordered list of preferred roommate names
     * @param previousInternships list of companies the student has interned at
     */
    public UniversityStudent(String name, int age, String gender, int year, String major, double gpa,
                             List<String> roommatePreferences, List<String> previousInternships) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.year = year;
        this.major = major;
        this.gpa = gpa;
        this.roommatePreferences = new ArrayList<>(roommatePreferences);
        this.previousInternships = new ArrayList<>(previousInternships);
        this.roommate = null;
        this.friendList = new ArrayList<>();
        this.chatHistory = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Connection strength formula (used to build StudentGraph edges)
    // +4 if they are roommates
    // +3 for each shared internship (ignores "None")
    // +2 if same major
    // +1 if same age
    // -------------------------------------------------------------------------

    @Override
    public int calculateConnectionStrength(Student other) {
        if (!(other instanceof UniversityStudent)) return 0;
        UniversityStudent o = (UniversityStudent) other;
        int strength = 0;

        if (this.roommate != null && this.roommate.equals(o)) strength += 4;    // Roommate
        if (this.major != null && this.major.equals(o.major)) strength += 2;    // Same major
        if (this.age == o.age)                                strength += 1;    // Same age
        for (String internship : this.previousInternships) {                    // Shared internships
            if (!internship.equalsIgnoreCase("None") && o.previousInternships.contains(internship))
                strength += 3;
        }
        
        return strength;
    }

    // ------------------ Chat stuff (synchronized) -------------------------
    
    // Adds the given student to this student's friend list if not already present.
    public synchronized void addFriend(UniversityStudent student) {
        if (!friendList.contains(student)) friendList.add(student);
    }
    
    // Appends a message to this student's chat history. 
    public synchronized void addChatMessage(String message) { chatHistory.add(message); }

    // Roommate accessors
    public UniversityStudent getRoommate() { return roommate; }

    public void setRoommate(UniversityStudent roommate) { this.roommate = roommate; }

    // ---------------------- Standard overrides ------------------------
    @Override
    public String toString() {
        return "UniversityStudent{name='" + name + "', age=" + age +
                ", gender='" + gender + "', year=" + year +
                ", major='" + major + "', gpa=" + gpa +
                ", roommatePreferences=" + roommatePreferences +
                ", previousInternships=" + previousInternships + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UniversityStudent)) return false;
        return this.name.equals(((UniversityStudent) obj).name);
    }

    @Override
    public int hashCode() { return name.hashCode(); }
}
