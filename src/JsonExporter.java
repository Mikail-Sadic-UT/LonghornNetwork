import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Runs all algorithms on Main test cases and writes
 * results to JSON file for React.
 * Separate from Main.java
 *
 * Usage: java JsonExporter [outputPath]
 * defaults to longhorn-ui/public/data.json
 * 
 * Got help from AI to make this one work correctly
 */
public class JsonExporter {

    public static void main(String[] args) throws Exception {
        String outputPath = args.length > 0 ? args[0] : "longhorn-ui/public/data.json"; // Default output

        // Grab all 3 test cases from Main
        List<List<UniversityStudent>> testCases = new ArrayList<>();
        testCases.add(Main.generateTestCase1());
        testCases.add(Main.generateTestCase2());
        testCases.add(Main.generateTestCase3());

        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < testCases.size(); i++) {
            List<UniversityStudent> students = testCases.get(i);

            // Run all the algorithms in order
            GaleShapley.assignRoommates(students);               // Roommates first
            StudentGraph graph = new StudentGraph(students);     // Graph after (incl +4 bonus)
            PodFormation podFormation = new PodFormation(graph); // Pods from graph
            podFormation.formPods(4);

            // Set up referral path finder
            ReferralPathFinder pathFinder = new ReferralPathFinder(graph);

            // Run friend request + chat threads (like Main)
            if (students.size() >= 2) {
                ExecutorService executor = Executors.newFixedThreadPool(4);
                UniversityStudent s1 = students.get(0);
                UniversityStudent s2 = students.get(1);
                executor.submit(new FriendRequestThread(s1, s2));
                executor.submit(new ChatThread(s1, s2, "Hello there!"));
                executor.submit(new FriendRequestThread(s2, s1));
                executor.submit(new ChatThread(s2, s1, "Hi back!"));
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }

            // Build JSON for test case
            json.append("  {\n");
            json.append("    \"name\": \"Test Case ").append(i + 1).append("\",\n");

            // All student data
            json.append("    \"students\": [\n");
            for (int j = 0; j < students.size(); j++) {
                UniversityStudent s = students.get(j);
                json.append("      {\n");
                json.append("        \"name\": ").append(escapeJson(s.name)).append(",\n");
                json.append("        \"age\": ").append(s.age).append(",\n");
                json.append("        \"gender\": ").append(escapeJson(s.gender)).append(",\n");
                json.append("        \"year\": ").append(s.year).append(",\n");
                json.append("        \"major\": ").append(escapeJson(s.major)).append(",\n");
                json.append("        \"gpa\": ").append(s.gpa).append(",\n");
                json.append("        \"roommate\": ")
                        .append(s.getRoommate() != null ? escapeJson(s.getRoommate().name) : "null").append(",\n");
                json.append("        \"roommatePreferences\": ").append(toJsonArray(s.roommatePreferences))
                        .append(",\n");
                json.append("        \"previousInternships\": ").append(toJsonArray(s.previousInternships))
                        .append(",\n");
                json.append("        \"friends\": ").append(toJsonArray(getFriendNames(s))).append(",\n");
                json.append("        \"chatHistory\": ").append(toJsonArray(s.chatHistory)).append("\n");
                json.append("      }").append(j < students.size() - 1 ? "," : "").append("\n");
            }
            json.append("    ],\n");

            // Graph edges (deduplicated, only one entry per undirected edge)
            json.append("    \"edges\": [\n");
            Set<String> seen = new HashSet<>(); // Track which pairs we already wrote
            boolean firstEdge = true;
            for (UniversityStudent s : graph.getAllNodes()) {
                for (StudentGraph.Edge edge : graph.getNeighbors(s)) {
                    // Make a consistent key so A-B and B-A are the same
                    String key = s.name.compareTo(edge.neighbor.name) < 0
                            ? s.name + "-" + edge.neighbor.name
                            : edge.neighbor.name + "-" + s.name;
                    if (seen.contains(key))
                        continue; // Already wrote this edge
                    seen.add(key);
                    if (!firstEdge)
                        json.append(",\n");
                    firstEdge = false;
                    json.append("      {\"source\": ").append(escapeJson(s.name))
                            .append(", \"target\": ").append(escapeJson(edge.neighbor.name))
                            .append(", \"weight\": ").append(edge.weight).append("}");
                }
            }
            json.append("\n    ],\n");

            // Pod groupings
            json.append("    \"pods\": [\n");
            List<List<UniversityStudent>> pods = podFormation.getPods();
            for (int p = 0; p < pods.size(); p++) {
                json.append("      ").append(toJsonArray(getNames(pods.get(p))));
                json.append(p < pods.size() - 1 ? "," : "").append("\n");
            }
            json.append("    ],\n");

            // Referral paths - run from each student to each unique company they don't have
            json.append("    \"referralPaths\": [\n");
            Set<String> allCompanies = new LinkedHashSet<>();
            for (UniversityStudent s : students) {
                for (String intern : s.previousInternships) {
                    if (!intern.equalsIgnoreCase("None"))
                        allCompanies.add(intern);
                }
            }
            boolean firstPath = true;
            for (UniversityStudent s : students) {
                for (String company : allCompanies) {
                    // Skip if this student already has the internship
                    if (s.previousInternships.contains(company))
                        continue;
                    List<UniversityStudent> path = pathFinder.findReferralPath(s, company);
                    if (path.isEmpty())
                        continue; // no path, skip
                    if (!firstPath)
                        json.append(",\n");
                    firstPath = false;
                    json.append("      {\"from\": ").append(escapeJson(s.name))
                            .append(", \"company\": ").append(escapeJson(company))
                            .append(", \"path\": ").append(toJsonArray(getNames(path))).append("}");
                }
            }
            // Also add a "no path found" example if there's a disconnected student
            for (UniversityStudent s : students) {
                boolean hasAnyConnection = false;
                for (StudentGraph.Edge edge : graph.getNeighbors(s)) {
                    hasAnyConnection = true;
                    break;
                }
                if (!hasAnyConnection && !allCompanies.isEmpty()) {
                    String company = allCompanies.iterator().next();
                    if (!s.previousInternships.contains(company)) {
                        if (!firstPath)
                            json.append(",\n");
                        firstPath = false;
                        json.append("      {\"from\": ").append(escapeJson(s.name))
                                .append(", \"company\": ").append(escapeJson(company))
                                .append(", \"path\": []}");
                    }
                }
            }
            json.append("\n    ]\n");

            json.append("  }").append(i < testCases.size() - 1 ? "," : "").append("\n");
        }

        json.append("]\n");

        // Write it out
        File outFile = new File(outputPath);
        outFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(outFile)) {
            writer.write(json.toString());
        }
        System.out.println("Exported data to " + outFile.getAbsolutePath());
    }

    // Find a company that the START student does NOT have, so there's an actual path
    private static String findTargetCompany(List<UniversityStudent> students) {
        if (students.isEmpty())
            return null;
        Set<String> startInternships = new HashSet<>(students.get(0).previousInternships);
        // First pass: find a company the start student doesn't have
        for (UniversityStudent s : students) {
            for (String internship : s.previousInternships) {
                if (!internship.equalsIgnoreCase("None") && !startInternships.contains(internship))
                    return internship;
            }
        }
        // Fallback: if everyone shares the same internships, just pick the first real one
        for (UniversityStudent s : students) {
            for (String internship : s.previousInternships) {
                if (!internship.equalsIgnoreCase("None"))
                    return internship;
            }
        }
        return null;
    }

    // Pull friend names out of student's friend list
    private static List<String> getFriendNames(UniversityStudent s) {
        List<String> names = new ArrayList<>();
        for (UniversityStudent f : s.friendList)
            names.add(f.name);
        return names;
    }

    // Pull names from a list of students
    private static List<String> getNames(List<UniversityStudent> students) {
        List<String> names = new ArrayList<>();
        for (UniversityStudent s : students)
            names.add(s.name);
        return names;
    }

    // Wraps a string in quotes w/ escaping, or returns null
    private static String escapeJson(String s) {
        if (s == null)
            return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    // Turns a List<String> into a JSON array string like ["a", "b"]
    private static String toJsonArray(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(escapeJson(list.get(i)));
            if (i < list.size() - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
