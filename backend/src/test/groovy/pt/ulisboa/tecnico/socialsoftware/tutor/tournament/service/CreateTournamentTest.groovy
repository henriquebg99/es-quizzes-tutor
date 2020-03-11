package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateTournamentTest extends Specification {
    public static final String TOPIC_NAME = "topic"
    public static final String TOPIC_NAME_NOT_EXISTING = "topic2"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final String USER_USERNAME_NOT_EXISTING = "username2"
    public static final int USER_KEY = 1
    public static final int NUMBER_OF_QUESTIONS = 5
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final LocalDateTime beginDate = LocalDateTime.now().plusDays(1)
    public static final LocalDateTime endDate = LocalDateTime.now().plusDays(2)
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    public static final String beginDateString = beginDate.format(formatter)
    public static final String endDateString  = endDate.format(formatter);

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def topic
    def user
    def course
    def courseExecution
    def topics, notExistingTopics, emptyTopics
    //FIXME other test: not enough questions, see StatementService

    def setup() {
        def topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        topics = new HashSet<TopicDto>()
        topics.add(topicDto)

        emptyTopics = new HashSet<TopicDto>()
        notExistingTopics = new HashSet<TopicDto>()

        def topicDto2 = new TopicDto()
        topicDto2.setName(TOPIC_NAME_NOT_EXISTING)
        notExistingTopics.add(topicDto2);

        user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(user)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO);
        courseExecutionRepository.save(courseExecution);

        topic = new Topic (course, topicDto)
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)
    }

    @Unroll
    def 'create a tournament with empty #unrollBeginDate, #unrollEndDate, #unrollUsername'() {
        given: 'null arguments'
        def tournament = new TournamentDto ()
        tournament.setBeginDate(unrollBeginDate)
        tournament.setEndDate(unrollEndDate)
        tournament.setNumberOfQuestions(unrollNumberOfQuestions)
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(unrollUsername, courseExecution.getId(), tournament)

        then:
        def error = thrown(TutorException)
        error.errorMessage == unrollErrorMessage

        where:

        unrollUsername | unrollBeginDate | unrollEndDate | unrollNumberOfQuestions || unrollErrorMessage
        null           | beginDateString | endDateString | NUMBER_OF_QUESTIONS     || ErrorMessage.USERNAME_EMPTY
        USER_USERNAME  | null            | endDateString | NUMBER_OF_QUESTIONS     || ErrorMessage.BEGIN_DATE_IS_EMPTY
        USER_USERNAME  | beginDateString | null          | NUMBER_OF_QUESTIONS     || ErrorMessage.END_DATE_IS_EMPTY
        USER_USERNAME  | beginDateString | endDateString | -5                      || ErrorMessage.INVALID_NUMBER_OF_QUESTIONS
        USER_USERNAME  | beginDateString | endDateString |  0                      || ErrorMessage.INVALID_NUMBER_OF_QUESTIONS
    }

    def 'create a tournament' () {
        given: 'a valid tournament'
        def tournament = new TournamentDto ()
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        then: "the correct tournament is inside the repository"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != null
        result.getBeginDate().format(formatter) == beginDate.format(formatter)
        result.getEndDate().format(formatter) == endDate.format(formatter)
        result.getCreator().getUsername() == user.getUsername()
        result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
        result.getCourseExecution().getId() == courseExecution.getId()
        result.getCourseExecution().getAcronym() == courseExecution.getAcronym()
        result.getTopics().size() == 1
        result.getTopics()[0].getId() == topic.getId()
        result.getTopics()[0].getTournaments().contains(result);
        result.getCourseExecution().getTournaments().contains(result);

    }

    def 'create a tournament with topics null' () {
        given: 'a tournament with not existing topics'
        def tournament = new TournamentDto ()
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament.setTopics(null)

        when:
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.NO_TOPICS
    }

    def 'create a tournament with topics empty set' () {
        given: 'a tournament with not existing topics'
        def tournament = new TournamentDto ()
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament.setTopics(emptyTopics)

        when:
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.NO_TOPICS
    }



    def 'create a tournament with not existing topics' () {
        given: 'a tournament with not existing topics'
        def tournament = new TournamentDto ()
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament.setTopics(notExistingTopics)

        when:
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.TOPIC_NOT_FOUND_NAME
    }

    def 'create a tournament with beginDate after endDate' () {
        given: 'a tournament with beginDate after endDate'
        def tournament = new TournamentDto ()
        tournament.setBeginDate(endDate.format(formatter))
        tournament.setEndDate(beginDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.END_DATE_IS_BEFORE_BEGIN
    }

    def 'create a tournament with user not existing' () {
        given: 'a tournament with user not existing'
        def tournament = new TournamentDto ()
        tournament.setBeginDate(endDate.format(formatter))
        tournament.setEndDate(beginDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(USER_USERNAME_NOT_EXISTING, courseExecution.getId(), tournament)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.USERNAME_NOT_FOUND
    }

    def 'create a tournament with course execution not existing' () {
        given: 'a tournament with course execution not existing'
        def tournament = new TournamentDto ()
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament.setTopics(topics)

        when:
        tournamentService.createTournament(USER_USERNAME, 1000, tournament)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}