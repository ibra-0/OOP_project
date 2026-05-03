package research;

import java.util.Comparator;
import java.util.List;

// Interface, not abstract class: a Researcher role can be mixed into
// Students, Teachers, or plain Employees. Multiple inheritance of type
// is required, so an interface fits better than a single superclass.
public interface Researcher {

    String getResearcherId();

    String getFullName();

    int getHIndex();

    List<ResearchPaper> getPapers();

    List<ResearchProject> getProjects();

    void addPaper(ResearchPaper paper);

    void addProject(ResearchProject project);

    default int getTotalCitations() {
        int total = 0;
        for (ResearchPaper p : getPapers()) {
            total += p.getCitations();
        }
        return total;
    }

    default void printPapers(Comparator<ResearchPaper> comparator) {
        getPapers().stream()
                .sorted(comparator)
                .forEach(System.out::println);
    }
}