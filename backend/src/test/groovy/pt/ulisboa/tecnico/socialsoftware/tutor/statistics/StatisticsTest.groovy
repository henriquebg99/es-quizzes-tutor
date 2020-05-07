package pt.ulisboa.tecnico.socialsoftware.tutor.statistics

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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.*
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class StatisticsTest extends Specification{
    public static final String TOPIC_NAME = "topic"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final int USER_KEY = 1
    public static final String USER_NAME2 = "name2"
    public static final String USER_USERNAME2 = "username2"
    public static final int USER_KEY2 = 2
    public static final String USER_NAME3 = "name3"
    public static final String USER_USERNAME3 = "username3"
    public static final int USER_KEY3 = 3
    public static final String USER_USERNAME_NOT_FOUND = "username4"
    public static final int NUMBER_OF_QUESTIONS = 1
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final LocalDateTime beginDate = LocalDateTime.now().plusDays(1)
    public static final LocalDateTime endDate = LocalDateTime.now().plusDays(2)
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    public static final String beginDateString = beginDate.format(formatter)
    public static final String endDateString  = endDate.format(formatter)

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

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    TournamentAnswerRepository answerRepository

    @Autowired
    StatsService statsService;

    def questions
    def user, user2, user3
    def tournamentDto
    def course
    def courseExecution
    def topic
    def topics
    def topicDto

    def setup () {
        user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(user)

        user2 = new User(USER_NAME2, USER_USERNAME2, USER_KEY2, User.Role.STUDENT)
        userRepository.save(user2)

        user3 = new User(USER_NAME3, USER_USERNAME3, USER_KEY3, User.Role.STUDENT)
        userRepository.save(user3)

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)
        topics = new HashSet<TopicDto>()
        topics.add(topicDto)
        def topicList = new LinkedList<TopicDto> ()
        topicList.add(topicList)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.addUser(user)
        courseExecution.addUser(user2)
        courseExecutionRepository.save(courseExecution);

        topic = new Topic(course, topicDto)
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournamentDto = new TournamentDto ()
        tournamentDto.setBeginDate(beginDateString)
        tournamentDto.setEndDate(endDateString)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setTopics(topics)

        tournamentService.createTournament(USER_USERNAME2, courseExecution.getId(), tournamentDto)
    }

    def 'participate in a tournament and see stats' () {
        given:
        StatsDto stats = new StatsDto()
        def tournament = tournamentRepository.findAll().get(0)
        tournamentService.enrollTournament(USER_USERNAME, tournament.getId())

        when:
        statsService.getStats(USER_USERNAME, courseExecution.getId(), stats)

        then:
        stats.getTotalTournaments() == 1
        stats.getTotalCreatedTournaments() == 0
    }

    def 'see stats without participating in any tournament' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.getStats(USER_USERNAME, courseExecution.getId(), stats)

        then:
        stats.getTotalTournaments() == 0
        stats.getTotalCreatedTournaments() == 0

    }

    def 'see stats after creating a tournament' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.getStats(USER_USERNAME2, courseExecution.getId(), stats)

        then:
        stats.getTotalTournaments() == 0
        stats.getTotalCreatedTournaments() == 1

    }

    def 'participate in created tournament and see stats' () {
        given:
        StatsDto stats = new StatsDto()
        def tournament = tournamentRepository.findAll().get(0)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when:
        statsService.getStats(USER_USERNAME2, courseExecution.getId(), stats)

        then:
        stats.getTotalTournaments() == 1
        stats.getTotalCreatedTournaments() == 1
    }

    def 'user that does not belong to course execution checks stats' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.getStats(USER_USERNAME3, courseExecution.getId(), stats)

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_NOT_IN_COURSE_EXECUTION

    }

    def 'check stats for user with empty username' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.getStats(null, courseExecution.getId(), stats)

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USERNAME_EMPTY
    }

    def 'check stats of student with username that does not exists' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.getStats(USER_USERNAME_NOT_FOUND, courseExecution.getId(), stats)

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USERNAME_NOT_FOUND
    }



    @TestConfiguration
    static class StatisticsServiceImplTestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
