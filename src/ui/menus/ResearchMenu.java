package ui.menus;

import comparators.PaperComparators;
import research.ResearchManager;
import research.ResearchPaper;
import research.ResearchProject;
import research.Researcher;
import services.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResearchMenu {
    private final Scanner scanner;
    private final Database db = Database.getInstance();

    public ResearchMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void show(Researcher researcher) {
        while (true) {
            System.out.println("\n--- Research Menu ---");
            System.out.println("1. Set h-index");
            System.out.println("2. Add research paper");
            System.out.println("3. Create research project");
            System.out.println("4. View my papers (by citations)");
            System.out.println("5. View top cited researcher");
            System.out.println("0. Back");
            String choice = scanner.nextLine();

            switch (choice) {
                case "0":
                    return;
                case "1":
                    int h = readScore("h-index: ", 0, 1000);
                    // Only Teacher currently implements Researcher; keep it simple.
                    if (researcher instanceof models.Teacher) {
                        ((models.Teacher) researcher).setHIndex(h);
                        db.save();
                        System.out.println("h-index updated.");
                    }
                    break;
                case "2":
                    addResearchPaper(researcher);
                    break;
                case "3":
                    createResearchProject(researcher);
                    break;
                case "4":
                    printMyPapersByCitations(researcher);
                    break;
                case "5":
                    printTopCitedResearcher();
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }
    }

    private void addResearchPaper(Researcher researcher) {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Authors (comma separated, empty for your name): ");
        String authorsInput = scanner.nextLine();
        System.out.print("Journal: ");
        String journal = scanner.nextLine();
        int pages = readScore("Pages: ", 1, 10000);
        System.out.print("Publication date (YYYY-MM-DD): ");
        String dateRaw = scanner.nextLine();
        System.out.print("DOI: ");
        String doi = scanner.nextLine();
        int citations = readScore("Citations: ", 0, 1000000);

        try {
            LocalDate date = LocalDate.parse(dateRaw);
            List<String> authors = parseAuthors(authorsInput, researcher.getFullName());
            ResearchPaper paper = new ResearchPaper(title, authors, journal, pages, date, doi, citations);
            researcher.addPaper(paper);
            db.save();
            System.out.println("Research paper added.");
        } catch (Exception e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private void createResearchProject(Researcher researcher) {
        System.out.print("Project topic: ");
        String topic = scanner.nextLine();
        if (topic == null || topic.isBlank()) {
            System.out.println("Topic cannot be empty.");
            return;
        }
        ResearchProject project = new ResearchProject(topic);
        project.addParticipant(researcher);
        researcher.addProject(project);
        db.save();
        System.out.println("Research project created and you were added as participant.");
    }

    private void printMyPapersByCitations(Researcher researcher) {
        System.out.println("\n--- My Papers (by citations) ---");
        if (researcher.getPapers().isEmpty()) {
            System.out.println("No papers yet.");
            return;
        }
        researcher.printPapers(PaperComparators.BY_CITATIONS);
    }

    private void printTopCitedResearcher() {
        List<Researcher> researchers = new ArrayList<>();
        for (base.User u : db.getUsersByRole(enums.Role.TEACHER)) {
            if (u instanceof Researcher) researchers.add((Researcher) u);
        }
        if (researchers.isEmpty()) {
            System.out.println("No researchers found.");
            return;
        }
        Researcher top = ResearchManager.topCitedResearcher(researchers);
        if (top == null) {
            System.out.println("No citation data yet.");
            return;
        }
        System.out.println("Top cited researcher: " + top.getFullName()
                + " (total citations: " + top.getTotalCitations() + ")");
    }

    private List<String> parseAuthors(String authorsInput, String fallbackAuthor) {
        List<String> authors = new ArrayList<>();
        if (authorsInput != null && !authorsInput.isBlank()) {
            for (String item : authorsInput.split(",")) {
                String a = item.trim();
                if (!a.isEmpty()) authors.add(a);
            }
        }
        if (authors.isEmpty()) authors.add(fallbackAuthor);
        return authors;
    }

    private int readScore(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            try {
                int value = Integer.parseInt(raw);
                if (value < min || value > max) {
                    System.out.println("Value must be between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }
}

