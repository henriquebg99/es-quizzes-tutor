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
import spock.lang.Specification

import javax.persistence.EntityNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CancelTournamentTest extends Specification {

    public static final String USER_USERNAME = "username"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME_NOT_CREATOR = "username2"
    public static final int    USER_KEY = 1
    public static final int    BEGIN_DAYS = 1
    public static final int    END_DAYS = 2
    public static final String COURSE_NAME  = "course"
    public static final String ACRONYM  = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final int ID_NOT_EXISTING = 2
    public static final int    NUMBER_QUESTIONS = 5
    public static final String TOPIC_NAME = "topic"

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
    def course
    def courseExecution
    def topic
    def topics
    def topicDto

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

        topic = new Topic(course, topicDto)
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournament = new TournamentDto()
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_QUESTIONS)
        tournament.setTopics(topics)
    }

    def 'cancel a tournament' () {
        given: 'a valid tournament canceled'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        def tournaments = tournamentRepository.findAll()
        def tournament = tournaments[0]
        def tournamentId = tournament.getId()

        when:
        tournamentService.cancelTournament(USER_USERNAME, tournamentId)

        then: "the tournament is set as canceled"
        tournament.getCanceled()
    }

    def 'a tournament is canceled by a student with a empty username' () {
        given: 'a tournament'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        def tournaments = tournamentRepository.findAll()
        def tournament = tournaments[0]
        def tournamentId = tournament.getId()

        when:
        tournamentService.cancelTournament(null, tournamentId)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.USERNAME_EMPTY
    }

    def 'cancel a tournament with empty id' () {
        given: 'a tournament with an empty id'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        when:
        tournamentService.cancelTournament(USER_USERNAME, null)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.TOURNAMENT_ID_EMPTY
    }

    def 'a tournament is canceled by a user that did not create it' () {
        given: 'a tournament'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        def tournaments = tournamentRepository.findAll()
        def tournament = tournaments[0]
        def tournamentId = tournament.getId()

        when:
        tournamentService.cancelTournament(USER_USERNAME_NOT_CREATOR, tournamentId)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.USER_USERNAME_NOT_CREATOR
    }

    def 'cancel a tournament that does not exit' () {
        given: 'a tournament with an id'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        when:
        tournamentService.cancelTournament(USER_USERNAME, ID_NOT_EXISTING)

        then: 'an exception is thrown'
        thrown EntityNotFoundException
    }


    def 'cancel a tournament that is happening or has already ended' () {
        given: 'a tournament'
        beginDate = LocalDateTime.now().plusDays(-2)
        endDate = LocalDateTime.now().plusDays(-1)
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        def tournaments = tournamentRepository.findAll()
        def tournament = tournaments[0]
        def tournamentId = tournament.getId()

        when:
        tournamentService.cancelTournament(USER_USERNAME, tournamentId)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.TOURNAMENT_HAPPENING_OR_ENDED
    }

    def 'cancel a tournament that is already canceled' () {
        given: 'a tournament with an id'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
        def tournaments = tournamentRepository.findAll()
        def tournament = tournaments[0]
        def tournamentId = tournament.getId()
        tournamentService.cancelTournament(USER_USERNAME, tournamentId)

        when:
        tournamentService.cancelTournament(USER_USERNAME, tournamentId)

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.TOURNAMENT_ALREADY_CANCELED
    }

    /*FIXME add more tests about topics*/
    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}