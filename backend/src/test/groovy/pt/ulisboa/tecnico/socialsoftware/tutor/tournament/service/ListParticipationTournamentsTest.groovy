package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
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
class ListParticipationTournamentsTest extends Specification{

    public static final String USER_USERNAME      = "username"
    public static final String USER_NAME          = "name"
    public static final int    USER_KEY           = 1
    public static final int    BEGIN_DAYS         = 1
    public static final int    END_DAYS           = 2
    public static final String TOPIC_NAME         = "topic"
    public static final String COURSE_NAME        = "course"
    public static final String ACRONYM            = "AS1"
    public static final String ACADEMIC_TERM      = "1 SEM"
    public static final int    NUMBER_QUESTIONS   = 1
    public static final int    BEGIN_MINUTES      = 1
    public static final int    END_MINUTES        = 2
    public static final int    SLEEP              = 70000
    public static final int    SLEEP_CLOSE        = 130000
    public static final String CONTENT            = "Question"
    public static final String TITLE              = "Title"

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
        def topicList = new LinkedList<TopicDto> ()
        topicList.add(topicList)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO);
        courseExecution.addUser(user)
        courseExecutionRepository.save(courseExecution);

        topic = new Topic(course, topicDto)
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        def option1 = new OptionDto ()
        option1.setContent("Option1");
        option1.setCorrect(true);

        def option2 = new OptionDto ()
        option2.setContent("Option2");
        option2.setCorrect(false);

        def options = new LinkedList()
        options.add(option1)
        options.add(option2)

        def questionDto = new QuestionDto();
        questionDto.setTopics(topicList);
        questionDto.setTitle(TITLE);
        questionDto.setContent(CONTENT);
        questionDto.setCreationDate(beginDate.format(formatter));
        questionDto.setOptions(options)
        questionDto.setStatus("AVAILABLE")

        def question
        def questions

        question = new Question(course, questionDto);
        questionRepository.save(question);
        questions = new HashSet<Question>();
        questions.add(question)
        topic.addQuestion(question)

        question = new Question(course, questionDto);
        questionRepository.save(question);
        questions = new HashSet<Question>();
        questions.add(question)
        topic.addQuestion(question)

        tournament = new TournamentDto()
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament.setNumberOfQuestions(NUMBER_QUESTIONS)
        tournament.setTopics(topics)
    }

    def 'list ongoing tournaments'() {
        given:
        beginDate = LocalDateTime.now().plusMinutes(BEGIN_MINUTES)
        endDate = LocalDateTime.now().plusMinutes(END_MINUTES)
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament = tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
        tournamentService.enrollTournament(USER_USERNAME, tournament.getId())
        sleep(SLEEP)

        when:
        def tournaments = tournamentService.listParticipationTournaments(USER_USERNAME, courseExecution.getId())

        then: "the number of tournaments is correct"
        tournaments.size() == 1
        and: "the content of the tournament is correct"
        tournaments[0].getBeginDate() == tournament.getBeginDate()
        tournaments[0].getEndDate() == tournament.getEndDate()
        tournaments[0].getNumberOfQuestions() == NUMBER_QUESTIONS
        def tourTopics = tournaments[0].getTopics()
        tourTopics.size() == 1L
        tourTopics[0].getName() == TOPIC_NAME
    }

    def 'the tournament has not started'() {
        given:
        beginDate = LocalDateTime.now().plusMinutes(BEGIN_MINUTES)
        endDate = LocalDateTime.now().plusMinutes(END_MINUTES)
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament = tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
        tournamentService.enrollTournament(USER_USERNAME, tournament.getId())

        when:
        def tournaments = tournamentService.listParticipationTournaments(USER_USERNAME, courseExecution.getId())

        then: "no tournaments are listed"
        tournaments.size() == 0
    }

    def 'the tournament is closed'() {
        given:
        beginDate = LocalDateTime.now().plusMinutes(BEGIN_MINUTES)
        endDate = LocalDateTime.now().plusMinutes(END_MINUTES)
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament = tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
        tournamentService.enrollTournament(USER_USERNAME, tournament.getId())
        sleep(SLEEP_CLOSE)

        when:
        def tournaments = tournamentService.listParticipationTournaments(USER_USERNAME, courseExecution.getId())

        then: "no tournaments are listed"
        tournaments.size() == 0
    }

    def 'user is not enrolled in the tournament'() {
        given:
        beginDate = LocalDateTime.now().plusMinutes(BEGIN_MINUTES)
        endDate = LocalDateTime.now().plusMinutes(END_MINUTES)
        tournament.setBeginDate(beginDate.format(formatter))
        tournament.setEndDate(endDate.format(formatter))
        tournament = tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournament)
        sleep(SLEEP)

        when:
        def tournaments = tournamentService.listParticipationTournaments(USER_USERNAME, courseExecution.getId())

        then: "no tournaments are listed"
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
