import java.io.*;
import java.util.*;

public class DataParser {

    /**
     * Parses a student data file and returns a list of UniversityStudent objects.
     *
     * Expected file format per student block:
     * Student:
     * Name: <name>
     * Age: <int>
     * Gender: <string>
     * Year: <int>
     * Major: <string>
     * GPA: <double>
     * RoommatePreferences: <comma-separated names, or empty>
     * PreviousInternships: <comma-separated names, or empty>
     *
     * @param filename path to the input file
     * @return list of parsed UniversityStudent objects
     * @throws IOException              if the file cannot be read
     * @throws IllegalArgumentException if a line is missing the ':' separator
     *                                  or a required field is absent
     */
    public static List<UniversityStudent> parseStudents(String filename) throws IOException { // Parses entire file at once
        List<UniversityStudent> students = new ArrayList<>();   // List of students in file

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {    // Read file
            String line;

            while ((line = reader.readLine()) != null) {          // Keep on readin
                line = line.trim();

                if (!line.equals("Student:")) continue; // Look for the start of a student block

                // Required fields
                String  name = null; Integer  age = null; String gender = null;
                Integer year = null; String major = null; Double    gpa = null;
                List<String> roommatePreferences = new ArrayList<>();
                List<String> previousInternships = new ArrayList<>();

                // Read lines until we hit a blank line or EOF
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) break;

                    // Pretty much every non-empty line has ":"
                    int colonIdx = line.indexOf(':');
                    if (colonIdx == -1)
                        throw new IllegalArgumentException("Incorrect format — missing ':' separator in line: \"" + line + "\"");
                    
                    String key   = line.substring(0, colonIdx).trim();            // Find field
                    String value = line.substring(colonIdx + 1).trim();           // Get data of field

                    switch (key) {
                        case "Name":   name   = value; break;   //
                        case "Gender": gender = value; break;   // All are already strings
                        case "Major":  major  = value; break;   //
                        
                        case "Age":                             // Catch parsing errors
                            try { age = Integer.parseInt(value); } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid age value: \"" + value + "\"");
                            } break;
                        case "Year":
                            try { year = Integer.parseInt(value); } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid year value: \"" + value + "\"");
                            } break;
                        case "GPA":
                            try { gpa = Double.parseDouble(value); } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid GPA value: \"" + value + "\"");
                            } break;
                           
                        case "RoommatePreferences":             // Loop thru these 
                            if (!value.isEmpty()) {
                                for (String pref : value.split(",")) {
                                    String trimmed = pref.trim();
                                    if (!trimmed.isEmpty()) roommatePreferences.add(trimmed);
                                }
                            } break;
                        case "PreviousInternships":
                            if (!value.isEmpty()) {
                                for (String intern : value.split(",")) {
                                    String trimmed = intern.trim();
                                    if (!trimmed.isEmpty()) previousInternships.add(trimmed);
                                }
                            } break;

                        default: /* ignore */ break;
                    }
                }

                // Make sure everything required is there
                if (name == null)   throw new IllegalArgumentException("Missing field: Name");
                if (age == null)    throw new IllegalArgumentException("Missing field: Age");
                if (gender == null) throw new IllegalArgumentException("Missing field: Gender");
                if (year == null)   throw new IllegalArgumentException("Missing field: Year");
                if (major == null)  throw new IllegalArgumentException("Missing field: Major");
                if (gpa == null)    throw new IllegalArgumentException("Missing field: GPA");
                // RoommatePreferences / PreviousInternships optional

                students.add(new UniversityStudent(name, age, gender, year, major, gpa,
                                                   roommatePreferences, previousInternships)); // add student if all went well
            }
        }
        return students;
    }
}
