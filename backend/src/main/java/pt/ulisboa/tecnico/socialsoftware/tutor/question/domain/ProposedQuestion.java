package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(
        name = "proposedQuestions",
        indexes = {
                @Index(name = "proposedQuestion_indx_0", columnList = "key")
        })
public class ProposedQuestion {
    @SuppressWarnings("unused")

    public enum Status{
        REJECTED,APPROVED,DEPENDENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@Column(unique=true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "proposedQuestion")
    private Image image;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proposedQuestion", fetch = FetchType.LAZY, orphanRemoval=true)
    private List<Option> options = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProposedQuestion.Status status = ProposedQuestion.Status.DEPENDENT;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public ProposedQuestion() {}

    public ProposedQuestion(ProposedQuestionDto proposedQuestionDto, Course course, User user) {
        checkConsistentProposedQuestion(proposedQuestionDto);
        this.title = proposedQuestionDto.getTitle();
        this.key = proposedQuestionDto.getKey();
        this.content = proposedQuestionDto.getContent();
        this.course = course;
        this.user = user;
        this.status = Status.valueOf(proposedQuestionDto.getStatus());
        this.justification = proposedQuestionDto.getJustification();

        int index = 0;
        for (OptionDto optionDto : proposedQuestionDto.getOptions()) {
            optionDto.setSequence(index++);
            Option option = new Option(optionDto);
            this.options.add(option);
            option.setProposedQuestion(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        image.setProposedQuestion(this);
    }

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername () { return user.getUsername();}

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) { this.status = status; }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) { this.justification = justification; }

    public List<OptionDto> getOptionsDto() {
        return this.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return "ProposedQuestionDto{" +
                "id=" + id +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", justification='" + justification + '\'' +
                ", image=" + image +
                ", options=" + options +
                ", user="+ user +
                '}';
    }

    public Integer getCorrectOptionId() {
        return this.getOptions().stream()
                .filter(Option::getCorrect)
                .findAny()
                .map(Option::getId)
                .orElse(null);
    }

    private void checkConsistentProposedQuestion(ProposedQuestionDto proposedQuestionDto) {
        if (proposedQuestionDto.getTitle().trim().length() == 0 ||
                proposedQuestionDto.getContent().trim().length() == 0 ||
                proposedQuestionDto.getOptions().stream().anyMatch(optionDto -> optionDto.getContent().trim().length() == 0)) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (proposedQuestionDto.getOptions().stream().filter(OptionDto::getCorrect).count() != 1) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }
    }

    private Option getOptionById(Integer id) {
        return getOptions().stream().filter(option -> option.getId().equals(id)).findAny().orElse(null);
    }
}