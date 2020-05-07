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
class ListClosedTournamentsTest extends Specification{

    public static final String USER_USERNAME      = "username"
    public static final String USER_NAME          = "name"
    public static final int    USER_KEY           = 1
    public static final int    BEGIN_DAYS         = 1
    public static final int    END_DAYS           = 2
    public static final String TOPIC_NAME         = "topic"
    public static final String COURSE_NAME        = "course"
    public static final String ACRONYM            = "AS1"
    public static final String ACADEMIC_TERM      = "1 SEM"
    public static final int    NUMBER_QUESTIONS   = 5
    public static final int    BEGIN_MINUTES      = 1
    public static final int    END_MINUTES        = 2
    public static final int    SLEEP              = 130000

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

    def user
    def formatter
    def beginDate
    def endDate
    def tournament
    def topic
    def topics
    def topicDto
    def course
    def courseExecution

    def setup() {
        user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(user)

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        beginDate = LocalDateTime.now().plusDays(BEGIN_DAYS)
        endDate = LocalDateTime.now().plusDays(END_DAYS)

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)
        topics = new HashSet<TopicDto>()
        topics.add(topicDto)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO);
        courseExecutionRepository.save(courseExecution);

        courseExecution.addUser(user);

        topic = new Topic(course, topicDto)
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournament = new TournamentDto()
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_QUESTIONS)
        tournament.setTopics(topics)
    }

    def 'tournament closed'() {
        given:
        beginDate = LocalDateTime.now().plusMinutes(BEGIN_MINUTES)
        endDate = LocalDateTime.now().plusMinutes(END_MINUTES)
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
        sleep(SLEEP)

        when:
        def tournaments = tournamentService.listClosedTournaments(courseExecution.getId())

        then: "the number of tournaments is correct"
        tournaments.size() == 1
        and: "the content of the tournament is correct"
        tournaments[0].getBeginDate() == tournament.getBeginDate()
        tournaments[0].getEndDate() == tournament.getEndDate()
        tournaments[0].getNumberOfQuestions() == NUMBER_QUESTIONS
        def tourTopics = tournaments[0].getTopics()
        tourTopics.size() == 1
        tourTopics[0].getName() == TOPIC_NAME
    }

    def 'the tournament is canceled'() {
        given:
        beginDate = LocalDateTime.now().plusMinutes(BEGIN_MINUTES)
        endDate = LocalDateTime.now().plusMinutes(END_MINUTES)
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
        def t = tournamentRepository.findAll()
        def tournament = t[0]
        def tournamentId = tournament.getId()
        tournamentService.cancelTournament(USER_USERNAME, tournamentId)
        sleep(SLEEP)

        when:
        def tournaments = tournamentService.listClosedTournaments(courseExecution.getId())

        then: "no tournaments are listed"
        tournaments.size() == 0
    }

    def 'tournaments available but none are closed'() {
        when:
        def tournaments = tournamentService.listClosedTournaments(courseExecution.getId())

        then:
        tournaments.size() == 0
    }

    def 'no tournaments'() {
        given:
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        when:
        def tournaments = tournamentService.listClosedTournaments(courseExecution.getId())

        then:
        tournaments.size() == 0

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
