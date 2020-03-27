package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CancelTournamentPerformanceTest extends Specification {
    public static final String TOPIC_NAME = "topic"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
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


    def "performance testing to cancel 10000 tournaments" () {
        given: "a curse, a courseExecution, a user, a topic and a list of topics"
        def topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        def topics = new HashSet<TopicDto>()
        topics.add(topicDto)

        def user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(user)

        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO);
        courseExecutionRepository.save(courseExecution);

        def topic = new Topic (course, topicDto)
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        and: "10000 created tests added"
        1.upto(10000, {
            def tournamentDto = new TournamentDto()
            tournamentDto.setBeginDate(beginDateString)
            tournamentDto.setEndDate(endDateString)
            tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
            tournamentDto.setTopics(topics)
            tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto)
        })

        def tournaments = tournamentRepository.findAll()
        def counter = 0

        when: "100000 tests are canceled"
        1.upto(10000, {
            def tournament = tournaments[counter]
            counter += 1
            def tournamentId = tournament.getId()
            tournamentService.cancelTournament(USER_USERNAME, tournamentId)
        })

        then:
        true
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}