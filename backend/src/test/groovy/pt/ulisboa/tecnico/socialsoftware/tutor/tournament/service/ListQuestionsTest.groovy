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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
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
class ListQuestionsTest extends Specification {
    public static final String TOPIC_NAME = "topic"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final int USER_KEY = 1
    public static final String USER_NAME2 = "name2"
    public static final String USER_USERNAME2 = "username2"
    public static final int USER_KEY2 = 2
    public static final int NUMBER_OF_QUESTIONS = 1
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final LocalDateTime beginDate = LocalDateTime.now().plusDays(1)
    public static final LocalDateTime endDate = LocalDateTime.now().plusDays(2)
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    public static final String beginDateString = beginDate.format(formatter)
    public static final String endDateString  = endDate.format(formatter);
    public static final String CONTENT = "Question"
    public static final String TITLE = "Title"

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

    def questionDto
    def question
    def questions
    def user, user2
    def tournamentDto
    def course
    def courseExecution
    def topic
    def topics
    def topicDto

    def setup() {
        user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(user)

        user2 = new User(USER_NAME2, USER_USERNAME2, USER_KEY2, User.Role.STUDENT)
        userRepository.save(user2)

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

        def option1 = new OptionDto ()
        option1.setContent("Option1");
        option1.setCorrect(true);

        def option2 = new OptionDto ()
        option2.setContent("Option2");
        option2.setCorrect(false);

        def options = new LinkedList()
        options.add(option1)
        options.add(option2)

        questionDto = new QuestionDto();
        questionDto.setTopics(topicList);
        questionDto.setTitle(TITLE);
        questionDto.setContent(CONTENT);
        questionDto.setCreationDate(beginDateString);
        questionDto.setOptions(options)
        questionDto.setStatus("AVAILABLE")

        question = new Question(course, questionDto);
        questionRepository.save(question);
        questions = new HashSet<Question>();
        questions.add(question)

        tournamentDto = new TournamentDto ()
        tournamentDto.setBeginDate(beginDateString)
        tournamentDto.setEndDate(endDateString)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setTopics(topics)

        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        tournamentRepository.findAll().get(0).setQuestions(questions)
    }

    def "list questions of a tournament" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)

        and: 'students enrolled'
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'list answers'
        List<QuestionDto> questions =
                tournamentService.listQuestions(USER_USERNAME2, tournament.getId());

        then: 'the list has one question'
        questions.size() == 1

        and: 'which is correct'
        questions.get(0).getId() == question.getId()
        questions.get(0).getContent().equals(questionDto.getContent())
        questions.get(0).getCreationDate().equals(questionDto.getCreationDate())
        questions.get(0).getOptions().size() == 2
        questions.get(0).getTitle().equals(questionDto.getTitle())
    }

    def "list questions but the user is not enrolled in the tournament" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)

        when: 'list answers'
        tournamentService.listQuestions(USER_USERNAME2, tournament.getId())

        then: 'a exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_NOT_ENROLLED_IN_TOURNAMENT
    }

    def "list questions but the tournament does not exists" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)

        when: 'list questions for a tournament that does not exists'
        tournamentService.listQuestions(USER_USERNAME2, 1006);

        then: 'a exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.TOURNAMENT_ID_NOT_FOUND
    }

    def "list questions but user do not exists" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'list questions'
        tournamentService.listQuestions("User1234567", tournament.getId());

        then: 'an exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USERNAME_NOT_FOUND
    }

    def "list questions but username is null" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'list answers'
        tournamentService.listQuestions(null, tournament.getId());

        then: 'an exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USERNAME_EMPTY
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}