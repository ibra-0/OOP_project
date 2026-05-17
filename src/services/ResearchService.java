package services;

import comparators.PaperComparators;
import research.ResearchManager;
import research.ResearchPaper;
import research.ResearchProject;
import research.Researcher;

import java.util.Comparator;
import java.util.List;

/** Facade over research papers, projects, and supervisor assignment. */
public class ResearchService {
    private static ResearchService instance;

    private ResearchService() {}

    public static ResearchService getInstance() {
        if (instance == null) instance = new ResearchService();
        return instance;
    }

    public void addPaper(Researcher r, ResearchPaper paper) {
        if (r == null || paper == null) return;
        r.addPaper(paper);
        Database.getInstance().save();
    }

    public void joinProject(Researcher r, ResearchProject project) {
        if (r == null || project == null) return;
        project.addParticipant(r);
        r.addProject(project);
        Database.getInstance().save();
    }

    public Researcher topCited(List<? extends Researcher> researchers) {
        if (researchers == null) return null;
        return ResearchManager.topCitedResearcher(researchers);
    }

    public void printPapers(Researcher r) {
        if (r == null) return;
        r.printPapers(PaperComparators.BY_CITATIONS);
    }

    public void setSupervisor(Object candidate, int minHIndex) {
        ResearchManager.assignSupervisor(candidate, minHIndex);
    }

    public void printAllPapers(List<? extends Researcher> researchers, Comparator<ResearchPaper> comparator) {
        ResearchManager.printAllPapers(researchers, comparator);
    }
}

