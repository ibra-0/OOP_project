package research;

import exceptions.InvalidSupervisorException;
import exceptions.NotAResearcherException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// System-level utility for cross-researcher queries. Kept static:
// acts purely on the caller-supplied registry, so no shared state.
public final class ResearchManager {

    private ResearchManager() { }

    public static void assignSupervisor(Object candidate, int minHIndex) {
        if (!(candidate instanceof Researcher)) {
            throw new NotAResearcherException("Supervisor candidate is not a Researcher.");
        }
        Researcher r = (Researcher) candidate;
        if (r.getHIndex() < minHIndex) {
            throw new InvalidSupervisorException(
                    "Researcher " + r.getFullName()
                            + " has h-index " + r.getHIndex()
                            + " (< required " + minHIndex + ").");
        }
    }

    public static List<ResearchPaper> collectAllPapers(List<? extends Researcher> researchers) {
        List<ResearchPaper> all = new ArrayList<>();
        for (Researcher r : researchers) all.addAll(r.getPapers());
        return all;
    }

    public static void printAllPapers(List<? extends Researcher> researchers,
                                      Comparator<ResearchPaper> comparator) {
        collectAllPapers(researchers).stream()
                .sorted(comparator)
                .forEach(System.out::println);
    }

    public static Researcher topCitedResearcher(List<? extends Researcher> researchers) {
        Researcher top = null;
        int best = -1;
        for (Researcher r : researchers) {
            int total = r.getTotalCitations();
            if (total > best) { best = total; top = r; }
        }
        return top;
    }
}