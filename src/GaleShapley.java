import java.util.*;

/**
 * Students with not empty preference lists are proposers.
 * Each proposer works down list and receiver accepts if
 * are unpaired or like new proposer over their current.
 * Students w/ empty preferences stay unpaired.
 */
public class GaleShapley {

    /**
     * Assigns roommates by setting each student's roommate field.
     * @param students the full list of students to match
     */
    public static void assignRoommates(List<UniversityStudent> students) {
        Map<String, UniversityStudent> nameMap = new HashMap<>();
        for (UniversityStudent s : students) { nameMap.put(s.name, s); } // Make name map

        Map<UniversityStudent, Integer> nextProposal = new HashMap<>(); // Track next proposals
        Queue<UniversityStudent> free = new LinkedList<>(); // Queue of free students not matched

        // Fill Q and Proposal map
        for (UniversityStudent s : students) {
            if (!s.roommatePreferences.isEmpty()) {
                free.add(s);
                nextProposal.put(s, 0);
            }
            s.setRoommate(null); // Reset prior roommate state
        }

        while (!free.isEmpty()) { // Exhaust Q
            UniversityStudent proposer = free.poll();

            // Skip if student already got matched while waiting in queue
            if (proposer.getRoommate() != null) continue;

            // If student has exhausted their list, stay unpaired
            int idx = nextProposal.get(proposer);
            if (idx >= proposer.roommatePreferences.size()) continue;

            // Get the next person to propose to
            String targetName = proposer.roommatePreferences.get(idx);
            nextProposal.put(proposer, idx + 1);

            UniversityStudent target = nameMap.get(targetName);

            if (target == null) { // Skip & re-queue if target does not exist
                free.add(proposer);
                continue;
            }

            if (target.getRoommate() == null) { // Target is free, match students
                proposer.setRoommate(target);
                target.setRoommate(proposer);
            } else {                            // Target taken, prefer proposer over current?
                UniversityStudent current = target.getRoommate();
                if (prefers(target, proposer, current)) { // Target dumps current for proposer :(
                    current.setRoommate(null);
                    free.add(current); // current goes back to Q

                    proposer.setRoommate(target);
                    target.setRoommate(proposer);
                } else free.add(proposer); // Rejected
            }
        }
    }

    // Returns true if 'student' prefers 'candidate' over 'current'
    private static boolean prefers(UniversityStudent student, UniversityStudent candidate,
                                   UniversityStudent current) {
        int candidateRank = student.roommatePreferences.indexOf(candidate.name);
        int currentRank = student.roommatePreferences.indexOf(current.name);

        if (candidateRank == -1) return false; // If candidate isn't in list, can't be preferred
        if (currentRank == -1)   return true;  // If current isn't in list, anyone in list is preferred

        return candidateRank < currentRank;
    }
}
