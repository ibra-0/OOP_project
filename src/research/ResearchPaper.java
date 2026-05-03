package research;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResearchPaper implements Comparable<ResearchPaper>, Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private List<String> authors;
    private String journal;
    private int pages;
    private LocalDate publicationDate;
    private String doi;
    private int citations;

    public ResearchPaper(String title,
                         List<String> authors,
                         String journal,
                         int pages,
                         LocalDate publicationDate,
                         String doi,
                         int citations) {
        this.title = title;
        this.authors = authors != null ? new ArrayList<>(authors) : new ArrayList<>();
        this.journal = journal;
        this.pages = pages;
        this.publicationDate = publicationDate;
        this.doi = doi;
        this.citations = citations;
    }

    public String getTitle() { return title; }
    public List<String> getAuthors() { return authors; }
    public String getJournal() { return journal; }
    public int getPages() { return pages; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public String getDoi() { return doi; }
    public int getCitations() { return citations; }

    public void setCitations(int citations) { this.citations = citations; }

    // Default ordering: most recent first. Chosen because a paper's
    // recency is its most stable, objective property; citations change
    // over time, pages are a weak ordering signal.
    @Override
    public int compareTo(ResearchPaper other) {
        return other.publicationDate.compareTo(this.publicationDate);
    }

    // Identity is the DOI when present; otherwise title + date.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        ResearchPaper that = (ResearchPaper) o;
        if (doi != null && that.doi != null) {
            return Objects.equals(doi, that.doi);
        }
        return Objects.equals(title, that.title)
                && Objects.equals(publicationDate, that.publicationDate);
    }

    @Override
    public int hashCode() {
        return doi != null ? Objects.hash(doi) : Objects.hash(title, publicationDate);
    }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" - %s, %s, %d pages, cited %d times (DOI: %s)",
                publicationDate, title, String.join(", ", authors),
                journal, pages, citations, doi);
    }
}