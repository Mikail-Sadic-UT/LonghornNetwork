import java.util.*;

/**
 * Forms pods using Prim's
 *
 * Uses raw connection strengths (not inverted weights).
 */
public class PodFormation {

    private StudentGraph graph;
    private List<List<UniversityStudent>> pods;

    public PodFormation(StudentGraph graph) {
        this.graph = graph;
        this.pods = new ArrayList<>();
    }

    // Wraps an Edge so the PQ can sort by highest weight (max-heap).
    private static class WeightedEdge implements Comparable<WeightedEdge> {
        StudentGraph.Edge edge;

        WeightedEdge(StudentGraph.Edge edge) { this.edge = edge; }

        @Override
        public int compareTo(WeightedEdge other) {
            return Integer.compare(other.edge.weight, this.edge.weight); // descending
        }
    }

    /**
     * Forms pods of up to podSize students using Prim's.
     *
     * @param podSize max number of students per pod
     */
    public void formPods(int podSize) {
        pods.clear();
        Set<UniversityStudent> visited = new HashSet<>();

        // Go thru all students
        for (UniversityStudent student : graph.getAllNodes()) {
            if (visited.contains(student)) continue; // Skip visited

            // Start a pod from this node (student)
            List<UniversityStudent> pod = new ArrayList<>();
            pod.add(student);
            visited.add(student);

            // Prim's: use max-heap to pick strongest connection into current pod
            PriorityQueue<WeightedEdge> pq = new PriorityQueue<>();

            addEdges(pq, student, visited); // Add edges into PQ from starting student

            // Grow pod until full / no more reachable unvisited students
            while (pod.size() < podSize && !pq.isEmpty()) {
                StudentGraph.Edge best = pq.poll().edge;
                if (visited.contains(best.neighbor)) continue; // Skip visited

                pod.add(best.neighbor);     // Add
                visited.add(best.neighbor); //

                addEdges(pq, best.neighbor, visited); // Add new members edges to PQ
            }
            pods.add(pod); // Add pod
        }
    }

    // Adds all edges from student to unvisited neighbors into PQ
    private void addEdges(PriorityQueue<WeightedEdge> pq, UniversityStudent student, 
                          Set<UniversityStudent> visited) {
        for (StudentGraph.Edge edge : graph.getNeighbors(student)) {
            if (!visited.contains(edge.neighbor))
                pq.add(new WeightedEdge(edge));
        }
    }

    // Returns pods
    public List<List<UniversityStudent>> getPods() { return pods; }

    // Prints pod assignments
    public void displayPods() {
        System.out.println("Pod Assignments:");
        for (int i = 0; i < pods.size(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("  Pod ").append(i).append(": ");
            for (UniversityStudent s : pods.get(i)) {
                sb.append(s.name).append(", ");
            }
            System.out.println(sb.toString());
        }
    }
}
