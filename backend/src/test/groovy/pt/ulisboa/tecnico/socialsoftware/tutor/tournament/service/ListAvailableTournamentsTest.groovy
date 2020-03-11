package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage

@DataJpaTest
class ListAvailableTournamentsTest extends Specification{

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

        topic = new Topic(course, topicDto)
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournament = new TournamentDto()
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_QUESTIONS)
        tournament.setTopics(topics)
    }

    def 'tournaments available'(){
        given:
            tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
            tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        when:
            def tournaments = tournamentService.listOpenTournaments()

        then: "the number of tournaments is correct"
            tournaments.size() == 2
        and: "the content of the tournaments is correct"
            tournaments.each { tour ->
                tour.getBeginDate() == beginDate
                tour.getEndDate() == endDate
                tour.getNumberOfQuestions() == NUMBER_QUESTIONS
                (tour.getTopics() as HashSet) == (topics as HashSet)
            }
    }

    //Testar se dá para listar um torneio acabado
    def 'the tournament is over'() {
        given:
            beginDate = LocalDateTime.now().plusDays(-2)
            endDate = LocalDateTime.now().plusDays(-1)
            tournament.setBeginDate(beginDate.format(formatter))
            tournament.setEndDate(endDate.format(formatter))
            tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)

        when:
            def tournaments = tournamentService.listOpenTournaments()

        then: "no tournaments are listed"
            tournaments.size() == 0
    }

    //Testar se dá para listar um torneio cancelado
    def 'the tournament is canceled'() {
        given:
            tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
            //tournament.setCanceled(true)

        when:
            def tournaments = tournamentService.listOpenTournaments()

        then: "no tournaments are listed"
            tournaments.size() == 0
    }

    //Testar se dá bem se não houver nenhum torneio
    def 'no tournaments are available'() {
        when:
            def tournaments = tournamentService.listOpenTournaments()

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
