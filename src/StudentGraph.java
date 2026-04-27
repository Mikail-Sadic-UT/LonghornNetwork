import java.util.*;

public class StudentGraph {

    // Internal static class
    public static class Edge {
        public UniversityStudent neighbor;
        public int weight;

        // Edge is neighbor + weight of edge
        public Edge(UniversityStudent neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }
    }

    // Adjacency list (student -> edges): each student maps to its list of outgoing edges
    private Map<UniversityStudent, List<Edge>> adjList;

    // Map (name -> student) bcs names are unique
    private Map<String, UniversityStudent> nameMap;

    // Creates graph from list of students.
    public StudentGraph(List<UniversityStudent> students) {
        adjList = new LinkedHashMap<>();
        nameMap = new HashMap<>();

        // Add every student as node
        for (UniversityStudent s : students) {
            adjList.put(s, new ArrayList<>());
            nameMap.put(s.name, s);
        }

        // Build edges for every unique pair
        for (int i = 0; i < students.size(); i++) {
            for (int j = i + 1; j < students.size(); j++) { // all student combos
                int strength = students.get(i).calculateConnectionStrength(students.get(j));
                if (strength > 0) addEdge(students.get(i), students.get(j), strength); // Add edge if str > 0
            }
        }
    }

    // Adds an undirected edge (both directions) between two students.
    public void addEdge(UniversityStudent a, UniversityStudent b, int weight) {
        adjList.get(a).add(new Edge(b, weight));
        adjList.get(b).add(new Edge(a, weight));
    }

    // Returns the edge list for a given student.
    public List<Edge> getNeighbors(UniversityStudent student) {
        List<Edge> edges = adjList.get(student);
        return edges != null ? edges : new ArrayList<>();
    }

    // Prints the adjacency list. Format: StudentName -> [(Neighbor, weight), ...]
    public void displayGraph() {
        for (Map.Entry<UniversityStudent, List<Edge>> entry : adjList.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey().name).append(" -> [");
            List<Edge> edges = entry.getValue();
            for (int i = 0; i < edges.size(); i++) {
                Edge e = edges.get(i);
                sb.append("(").append(e.neighbor.name).append(", ").append(e.weight).append(")");
                if (i < edges.size() - 1)
                    sb.append(", ");
            }
            sb.append("]");
            System.out.println(sb.toString());
        }
    }

    // Returns all students (nodes) in the graph.
    public Set<UniversityStudent> getAllNodes() { return adjList.keySet(); }

    // Looks up a student by name. Returns null if not found.
    public UniversityStudent getStudent(String name) { return nameMap.get(name); }
}
