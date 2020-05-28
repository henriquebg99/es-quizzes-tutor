package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "begin_date")
    private LocalDateTime beginDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "number_of_questions")
    private Integer numberOfQuestions;

    @Column(name = "tournament_cancel_status")
    private Boolean isCanceled;

    @Column(name = "tournament_generated")
    private Boolean isGenerated;

    @Column(name = "tournament_recommended")
    private Boolean isRecommended = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToMany(mappedBy = "enrolled_tournaments", fetch = FetchType.EAGER)
    private Set<User> enrollments = new HashSet<>();

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    @ManyToMany(mappedBy = "tournaments")
    private Set<Topic> topics = new HashSet<Topic>();

    @ManyToMany(mappedBy = "tournaments", fetch = FetchType.EAGER)
    private Set<Question> questions = new HashSet<Question>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournament", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<TournamentAnswer> answers = new HashSet<>();

    public Tournament() {
    }

    public Tournament(User user, TournamentDto tournamentDto, CourseExecution courseExecution) {
        this.creator = user;
        this.courseExecution = courseExecution;
        this.numberOfQuestions = tournamentDto.getNumberOfQuestions();
        setCanceled(false);
        this.isGenerated = false;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Boolean isCreator(User user) {
        return this.creator.equals(user);
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
    }

    public void addEnrollment(User user) {
        if (!this.userIsEnrolled(user.getId())) {
            this.enrollments.add(user);
        } else {
            throw new TutorException(ErrorMessage.ALREADY_ENROLLED_IN_TOURNAMENT);
        }

        // check if the tournament is generated. if need, generate
        if (this.isGenerated == false)
            generateQuestions();
    }

    public Set<User> getEnrollments() {
        return enrollments;
    }

    public Boolean userIsEnrolled(int id) {
        Iterator<User> itr = this.enrollments.iterator();

        while (itr.hasNext()) {
            if (itr.next().getId() == id) {
                return true;
            }
        }
        return false;
    }

    public Boolean getCanceled() {
        return isCanceled;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
    }

    public Boolean hasEnded() {
        return this.endDate.isBefore(LocalDateTime.now());
    }

    public Boolean getRecommended() {
        return isRecommended;
    }

    public void setRecommended(Boolean recommended) {
        isRecommended = recommended;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Tournament) {
            Tournament t = (Tournament) o;
            return this.courseExecution.equals(t.courseExecution) &&
                    this.numberOfQuestions.equals(t.numberOfQuestions) &&
                    this.creator.equals(t.creator) &&
                    this.beginDate.equals(t.beginDate) &&
                    this.endDate.equals(t.endDate) &&
                    this.topics.equals(t.topics);
        }
        return false;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public Set<TournamentAnswer> getAnswers() {
        return answers;
    }

    public void addTournamentAnswer(@NotNull Question question, @NotNull User user, int selected) {
        // check if the question belongs to the tournament
        if (this.questions.stream().filter(q -> q.getId() == question.getId()).count() != 1)
            throw new TutorException(ErrorMessage.QUESTION_NOT_IN_TOURNAMENT);

        // check if the user is enrolled in the quiz
        if (this.enrollments.stream().filter(user1 -> user1.getId() == user.getId()).count() != 1)
            throw new TutorException(ErrorMessage.USER_NOT_ENROLLED_IN_TOURNAMENT);

        // check if the selected answer is valid
        if (selected >= question.getOptions().size() || selected < 0)
            throw new TutorException(ErrorMessage.INVALID_SELECTED_OPTION);

        // check if there is already any answer
        List<TournamentAnswer> answers = this.answers.stream().filter(
                answer -> answer.getUser().getId() == user.getId() &&
                        question.getId() == answer.getQuestion().getId()).collect(Collectors.toList());

        // at most one is expected
        assert answers.size() == 1 || answers.size() == 0;

        // if exists, reuse
        if (answers.size() == 1) {
            answers.get(0).setSelectedAnswer(selected);
        } else {
            this.answers.add(new TournamentAnswer(this, question, user, selected));
        }
    }

    public List<TournamentAnswerDto> listAnswers(@NotNull User user) {
        // check if the user is enrolled in the quiz
        if (this.getEnrollments().stream().filter(user1 -> user1.getId() == user.getId()).count() != 1)
            throw new TutorException(ErrorMessage.USER_NOT_ENROLLED_IN_TOURNAMENT);

        return this.answers.stream()
                .filter(answer -> answer.getUser().getId() == user.getId())
                .map(TournamentAnswerDto::new)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> listQuestions(@NotNull User user) {
        if (!userIsEnrolled(user.getId()))
            throw new TutorException(ErrorMessage.USER_NOT_ENROLLED_IN_TOURNAMENT);

        return this.questions.stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    public void generateQuestions() {
        // check if the available questions are enough
        Set<Question> availableQuestions = new HashSet<>();
        for (Topic topic : this.topics)
            availableQuestions.addAll(topic.getQuestions());

        if (availableQuestions.size() < this.numberOfQuestions)
            throw new TutorException(ErrorMessage.NOT_ENOUGH_QUESTIONS);

        // randomly select questions
        List<Question> list = new LinkedList<Question>(availableQuestions);
        Collections.shuffle(list);
        this.questions.addAll((list.subList(0, this.numberOfQuestions)));
        this.isGenerated = true;

        for (Question question : this.questions)
            question.addTournament(this);
    }
}