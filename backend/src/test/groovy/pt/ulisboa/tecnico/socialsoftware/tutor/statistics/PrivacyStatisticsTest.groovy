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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class PrivacyStatisticsTest extends Specification{
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final int USER_KEY = 1
    public static final String USER_NAME2 = "name2"
    public static final String USER_USERNAME2 = "username2"
    public static final int USER_KEY2 = 2
    public static final String USER_USERNAME_NOT_FOUND = "username3"
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
    StatsService statsService;

    def user, user2
    def course
    def courseExecution


    def setup () {
        user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(user)

        user2 = new User(USER_NAME2, USER_USERNAME2, USER_KEY2, User.Role.STUDENT)
        userRepository.save(user2)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.addUser(user)
        courseExecutionRepository.save(courseExecution);
    }


    def 'turn information private and check' () {
        given:
        StatsDto stats = new StatsDto()
        when:
        statsService.setPrivacy(USER_USERNAME, courseExecution.getId(), stats, true)
        then:
        stats.getPrivacy() == true
    }

    def 'turn information private then public again' () {
        given:
        StatsDto stats = new StatsDto()
        statsService.setPrivacy(USER_USERNAME, courseExecution.getId(), stats, true)
        when:
        statsService.setPrivacy(USER_USERNAME, courseExecution.getId(), stats, false)
        then:
        stats.getPrivacy() == false
    }

    def 'turn information private then public again and private again' () {
        given:
        StatsDto stats = new StatsDto()
        statsService.setPrivacy(USER_USERNAME, courseExecution.getId(), stats, true)
        statsService.setPrivacy(USER_USERNAME, courseExecution.getId(), stats, false)
        when:
        statsService.setPrivacy(USER_USERNAME, courseExecution.getId(), stats, true)
        then:
        stats.getPrivacy() == true
    }

    def 'set user privacy that does not belong to course execution' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.setPrivacy(USER_USERNAME2, courseExecution.getId(), stats, true)

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_NOT_IN_COURSE_EXECUTION
    }

    def 'set user privacy with empty username' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.setPrivacy(null, courseExecution.getId(), stats, true)

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USERNAME_EMPTY
    }

    def 'set privacy of student with username that does not exists' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.setPrivacy(USER_USERNAME_NOT_FOUND, courseExecution.getId(), stats, true)

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USERNAME_NOT_FOUND

    }

    def 'set dashboard to public when it already is' () {
        given:
        StatsDto stats = new StatsDto()

        when:
        statsService.setPrivacy(USER_USERNAME, courseExecution.getId(), stats, false)

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_PRIVACY_ALREADY_FALSE

    }

    def 'set dashboard to private when it already is' () {
        given:
        StatsDto stats = new StatsDto()
        statsService.setPrivacy(USER_USERNAME, courseExecution.getId(), stats, true)

        when:
        statsService.setPrivacy(USER_USERNAME, courseExecution.getId(), stats, true)

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_PRIVACY_ALREADY_TRUE
    }



    @TestConfiguration
    static class StatisticsServiceImplTestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}
