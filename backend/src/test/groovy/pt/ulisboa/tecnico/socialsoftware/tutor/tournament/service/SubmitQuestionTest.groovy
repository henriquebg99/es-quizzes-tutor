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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class SubmitQuestionTest extends Specification {
    public static final String TOPIC_NAME = "topic"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final int USER_KEY = 1
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
    public static final int SELECTED = 1

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

    def question
    def questions
    def user
    def tournamentDto
    def course
    def courseExecution
    def topic
    def topics
    def topicDto
    def answerDto

    def setup() {
        user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(user)

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
        questionDto.setCreationDate(beginDateString);
        questionDto.setNumberOfAnswers(2);
        questionDto.setNumberOfCorrect(1);
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

        answerDto = new TournamentAnswerDto()
        answerDto.setQuestionId(question.getId())
        answerDto.setSelected(SELECTED)
    }

    def "submit question" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions);

        when: 'submit a answer for this question'
        tournamentService.submitAnswer(USER_USERNAME, tournament.getId(), answerDto);

        then: 'an answer is created'
        answerRepository.count() == 1L
        answerRepository.findAll().get(0).getUser().getId() ==

        and: ''
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}