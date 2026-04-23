package research;

import exceptions.NotAResearcherException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ResearchProject implements Serializable {

    private static final long serialVersionUID = 1L;

    private String topic;
    private final List<Researcher> participants = new ArrayList<>();
    private final List<ResearchPaper> publishedPapers = new ArrayList<>();

    public ResearchProject(String topic) {
        this.topic = topic;
    }

    public String getTopic() { return topic; }

    public List<Researcher> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public List<ResearchPaper> getPublishedPapers() {
        return Collections.unmodifiableList(publishedPapers);
    }

    // Accepts Object to enforce the runtime check required by spec:
    // "if someone who is not a Researcher tries to join, throw".
    public void addParticipant(Object candidate) {
        if (!(candidate instanceof Researcher)) {
            String who = candidate == null ? "null" : candidate.getClass().getSimpleName();
            throw new NotAResearcherException(
                    "Cannot join research project \"" + topic + "\": " + who + " is not a Researcher.");
        }
        participants.add((Researcher) candidate);
    }

    public void addPublishedPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchProject)) return false;
        return Objects.equals(topic, ((ResearchProject) o).topic);
    }

    @Override
    public int hashCode() { return Objects.hash(topic); }

    @Override
    public String toString() {
        return String.format("ResearchProject{topic='%s', participants=%d, papers=%d}",
                topic, participants.size(), publishedPapers.size());
    }
}