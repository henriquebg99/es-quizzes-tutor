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

    public static final String USER_USERNAME             = "username"
    public static final String USER_NAME                 = "name"
    public static final String USER_USERNAME_NOT_CREATOR = "username2"
    public static final int    USER_KEY                  = 1
    public static final int    BEGIN_DAYS                = 1
    public static final int    END_DAYS                  = 2
    public static final String COURSE_NAME               = "course"
    public static final String ACRONYM                   = "AS1"
    public static final String ACADEMIC_TERM             = "1 SEM"
    public static final int    NON_EXISTING_ID           = 20
    public static final int    NUMBER_QUESTIONS          = 5
    public static final String TOPIC_NAME                = "topic"
    public static final int    BEGIN_MINUTES_END         = 1
    public static final int    END_MINUTES_END           = 2
    public static final int    SLEEP_END                 = 130000
    public static final int    BEGIN_MINUTES_HAPPENING   = 1
    public static final int    END_MINUTES_HAPPENING     = 10
    public static final int    SLEEP_HAPPENING           = 80000
    public static final int    USER_KEY_NOT_CREATED      = 2


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

    def 'cancel a tournament' () {
        given: 'a valid tournament canceled'
            tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

            def tournaments = tournamentRepository.findAll()
            def tournament = tournaments[0]
            def tournamentId = tournament.getId()

        when:
            tournamentService.cancelTournament(USER_USERNAME, tournamentId)

        then: 'the tournament is set as canceled'
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
        and: 'user that exists but is not the creater'
            def user_other = new User(USER_NAME, USER_USERNAME_NOT_CREATOR, USER_KEY_NOT_CREATED, User.Role.STUDENT)
            userRepository.save(user_other)

        when:
            tournamentService.cancelTournament(USER_USERNAME_NOT_CREATOR, tournamentId)

        then: 'an exception is thrown'
            def exception = thrown(TutorException)
            exception.errorMessage == ErrorMessage.USER_USERNAME_NOT_CREATOR
    }

    def 'cancel a tournament that does not exit' () {
        when:
            tournamentService.cancelTournament(USER_USERNAME, NON_EXISTING_ID)

        then: 'exception is thrown'
            def exception = thrown(TutorException)
            exception.errorMessage == ErrorMessage.TOURNAMENT_ID_NOT_FOUND
    }


    def 'cancel a tournament that has already ended' () {
        given: 'a tournament'
            beginDate = LocalDateTime.now().plusMinutes(BEGIN_MINUTES_END)
            endDate = LocalDateTime.now().plusMinutes(END_MINUTES_END)
            tournament.setBeginDate(beginDate.format(formatter))
            tournament.setEndDate(endDate.format(formatter))
            tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
            sleep(SLEEP_END)

            def tournaments = tournamentRepository.findAll()
            def tournament = tournaments[0]
            def tournamentId = tournament.getId()

        when:
            tournamentService.cancelTournament(USER_USERNAME, tournamentId)

        then: 'an exception is thrown'
            def exception = thrown(TutorException)
            exception.errorMessage == ErrorMessage.TOURNAMENT_ENDED
    }

    def 'cancel a tournament that is happening'(){
        given: 'a tournament'
            beginDate = LocalDateTime.now().plusMinutes(BEGIN_MINUTES_HAPPENING)
            endDate = LocalDateTime.now().plusMinutes(END_MINUTES_HAPPENING)
            tournament.setBeginDate(beginDate.format(formatter))
            tournament.setEndDate(endDate.format(formatter))
            tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
            sleep(SLEEP_HAPPENING)

            def tournaments = tournamentRepository.findAll()
            def tournament = tournaments[0]
            def tournamentId = tournament.getId()

        when:
            tournamentService.cancelTournament(USER_USERNAME, tournamentId)

        then: 'an exception is thrown'
            def exception = thrown(TutorException)
            exception.errorMessage == ErrorMessage.TOURNAMENT_HAPPENING
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

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}