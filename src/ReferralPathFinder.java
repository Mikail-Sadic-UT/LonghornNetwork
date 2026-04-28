import java.util.*;

/**
 * Dijkstra's algorithm.
 * 
 * Finds the shortest path from a starting node (student) to any student
 * who has interned at a target company.
 * Edge weights inverted (10 - weight) Bcs lower number is shorter path.
 */
public class ReferralPathFinder {

    private StudentGraph graph;

    // Comparator impl
    private static class Node implements Comparable<Node> {
        UniversityStudent student;
        int distance;

        Node(UniversityStudent student, int distance) {
            this.student = student;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    public ReferralPathFinder(StudentGraph graph) { this.graph = graph; }

    /**
     * Finds the shortest path from 'start' to any student whose
     * previousInternships contains 'targetCompany'.
     *
     * @param start         the student to start searching from
     * @param targetCompany the company name to look for
     * @return ordered list of students along the path (start -> ... -> target),
     *         or an empty list if no reachable student has that internship
     */
    public List<UniversityStudent> findReferralPath(UniversityStudent start, String targetCompany) {
        // If the start student already interned there, just return them
        if (hasInternship(start, targetCompany)) {
            List<UniversityStudent> path = new ArrayList<>();
            path.add(start);
            return path;
        }

        // Dijkstra init
        Map<UniversityStudent, Integer> dist = new HashMap<>();
        Map<UniversityStudent, UniversityStudent> prev = new HashMap<>();
        Set<UniversityStudent> visited = new HashSet<>();

        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Initialize distances as max (inf). Set starting node
        for (UniversityStudent s : graph.getAllNodes())
            dist.put(s, Integer.MAX_VALUE);

        dist.put(start, 0);
        pq.add(new Node(start, 0));

        // Dijkstra loop
        while (!pq.isEmpty()) { // Exhaust pq
            Node currentNode = pq.poll();
            UniversityStudent current = currentNode.student;

            if (visited.contains(current)) continue; // Skip visited
            visited.add(current); // Add visited

            // Found a student with the target internship, return path
            if (!current.equals(start) && hasInternship(current, targetCompany))
                return buildPath(prev, start, current);

            for (StudentGraph.Edge edge : graph.getNeighbors(current)) {
                if (visited.contains(edge.neighbor)) continue; // Skip visited

                int invWeight = 10 - edge.weight; // stronger connection = shorter path
                int newDist = dist.get(current) + invWeight;
                
                // Dijke it up
                if (newDist < dist.getOrDefault(edge.neighbor, Integer.MAX_VALUE)) {
                    dist.put(edge.neighbor, newDist);
                    prev.put(edge.neighbor, current);
                    pq.add(new Node(edge.neighbor, newDist));
                }
            }
        }

        return new ArrayList<>(); // No reachable student has the target internship
    }

    /**
     * Checks whether a student has interned at the given company.
     * Ignores the placeholder "None".
     */
    private boolean hasInternship(UniversityStudent student, String company) {
        for (String internship : student.previousInternships)
            if (internship.equals(company)) return true;

        return false;
    }

    /**
     * Reconstructs the path from start to target using the prev map.
     */
    private List<UniversityStudent> buildPath(Map<UniversityStudent, UniversityStudent> prev,
            UniversityStudent start, UniversityStudent target) {

        LinkedList<UniversityStudent> path = new LinkedList<>();
        UniversityStudent current = target;
        while (current != null) {
            path.addFirst(current);
            if (current.equals(start)) break;
            current = prev.get(current);
        }
        
        return path;
    }
}
