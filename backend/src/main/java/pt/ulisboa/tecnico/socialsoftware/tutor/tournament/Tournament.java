package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "begin_date")
    private LocalDateTime beginDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "number_of_questions")
    private Integer numberOfQuestions;

    @Column(name = "tournament_cancel_status")
    private Boolean isCanceled;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToMany(mappedBy = "enrolled_tournaments")
    private Set<User> enrollments = new HashSet<>();

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    @ManyToMany(mappedBy = "tournaments")
    private Set<Topic> topics = new HashSet<Topic>();

    public Tournament() {

    }

    public Tournament(User user, TournamentDto tournamentDto, CourseExecution courseExecution) {
        this.creator = user;
        this.courseExecution = courseExecution;
        this.numberOfQuestions = tournamentDto.getNumberOfQuestions();
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

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void addTopic (Topic topic) {
        this.topics.add(topic);
    }

    public void addEnrollment(User user) {
        if (!this.enrollments.contains(user)){
            this.enrollments.add(user);
        }
    }

    public Set<User> getEnrollments() {
        return this.enrollments;
    }

    public Boolean getCanceled() {
        return isCanceled;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
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
}