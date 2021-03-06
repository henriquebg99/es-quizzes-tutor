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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class StatisticsTest extends Specification{
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final String USER_USERNAME_NOT_FOUND = "username1"
    public static final int USER_KEY = 1
    public static final int NUMBER_OF_QUESTIONS = 1
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

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

    def user
    def course
    def courseExecution

    def setup () {
        user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(user)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution);
    }

    def 'user that does not belong to course execution checks stats' () {
        when:
        statsService.getStats(USER_USERNAME, courseExecution.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_NOT_IN_COURSE_EXECUTION

    }

    def 'check stats for user with empty username' () {
        when:
        statsService.getStats(null, courseExecution.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USERNAME_EMPTY
    }

    def 'check stats of student with username that does not exists' () {
        when:
        statsService.getStats(USER_USERNAME_NOT_FOUND, courseExecution.getId())

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
    }
}
