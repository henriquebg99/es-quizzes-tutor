package pt.ulisboa.tecnico.socialsoftware.tutor.statistics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class StatisticsTest extends Specification{
    public static final String TOPIC_NAME = "topic"
    public static final String TOPIC_NAME_NOT_EXISTING = "topic2"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final String USER_USERNAME_NOT_EXISTING = "username2"
    public static final int USER_KEY = 1
    public static final int USER_ID = 12345
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
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    private StatsService statsService;

    @Autowired
    TournamentRepository tournamentRepository

    def topic
    def userDto, user
    def course
    def courseExecution
    def topics, notExistingTopics, emptyTopics
    def tournament1, tournament2
    def enrollments

    def setup() {
        def topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        topics = new HashSet<TopicDto>()
        topics.add(topicDto)

        user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        user.setId(USER_ID)
        userDto = new UserDto(user)
        userRepository.save(user)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO);
        courseExecutionRepository.save(courseExecution);

        courseExecution.addUser(user);

        topic = new Topic (course, topicDto)
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournament1 = new TournamentDto()
        tournament1.setBeginDate(beginDate.format(formatter))
        tournament1.setEndDate(endDate.format(formatter))
        tournament1.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament1.setTopics(topics)
    }

    def 'participate in a tournament and see stats' () {
        given:
        user.addEnrolledTournament(new Tournament(user, tournament1, courseExecution))
        StatsDto stats = new StatsDto()

        when:
        statsService.getStats(user.getUsername(), courseExecution.getId(), stats)

        then:
        stats.getTotalTournaments() == 1
        stats.getTotalCreatedTournaments() == 0
    }

    def 'see stats without participating in any tournament' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.getStats(user.getUsername(), courseExecution.getId(), stats)

        then:
        stats.getTotalTournaments() == 0
        stats.getTotalCreatedTournaments() == 0

    }

    def 'create a tournament and see stats' () {
        given:
        tournament2 = new TournamentDto()
        tournament2.setBeginDate(beginDate.format(formatter))
        tournament2.setEndDate(endDate.format(formatter))
        tournament2.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament2.setTopics(topics)
        tournament2.setCreator(userDto)
        StatsDto stats = new StatsDto()

        when:
        statsService.getStats(user.getUsername(), courseExecution.getId(), stats)

        then:
        stats.getTotalTournaments() == 0
        stats.getTotalCreatedTournaments() == 1

    }

    def 'create a tournament and participate in it and see stats' () {
        given:
        tournament2 = new TournamentDto()
        tournament2.setBeginDate(beginDate.format(formatter))
        tournament2.setEndDate(endDate.format(formatter))
        tournament2.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament2.setTopics(topics)
        tournament2.setCreator(userDto)
        user.addEnrolledTournament(new Tournament(user, tournament2, courseExecution))
        StatsDto stats = new StatsDto()

        when:
        statsService.getStats(user.getUsername(), courseExecution.getId(), stats)

        then:
        stats.getTotalTournaments() == 1
        stats.getTotalCreatedTournaments() == 1
    }

    @TestConfiguration
    static class StatisticsServiceImplTestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}
