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
class SubmitAnswerTest extends Specification {
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
    public static final int SELECTED = 0, SELECTED2 = 1

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

    def question, question2
    def questions, questions2
    def user, user2
    def tournamentDto
    def course
    def courseExecution
    def topic
    def topics
    def topicDto
    def answerDto, answerDto2

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

        def questionDto = new QuestionDto();
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

        question2 = new Question(course, questionDto);
        questionRepository.save(question2);
        questions2 = new HashSet<Question>();
        questions2.add(question)

        tournamentDto = new TournamentDto ()
        tournamentDto.setBeginDate(beginDateString)
        tournamentDto.setEndDate(endDateString)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setTopics(topics)

        answerDto = new TournamentAnswerDto()
        answerDto.setQuestionId(question.getId())
        answerDto.setSelected(SELECTED)

        answerDto2 = new TournamentAnswerDto()
        answerDto2.setQuestionId(question.getId())
        answerDto2.setSelected(SELECTED2)
    }

    def "submit correct answer" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'submit an answer for this question'
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), answerDto);

        then: 'an answer is created'
        answerRepository.count() == 1L
        def answer = answerRepository.findAll().get(0)
        answer.getUser().getId() == user2.getId()
        answer.getQuestion().getId() == question.getId()
        answer.getSelectedAnswer() == SELECTED
        answer.getTournament().getId() == tournament.getId()

        and: 'is added to the tournament'
        tournament.getAnswers().size() == 1
        tournament.getAnswers().toList().get(0).getId() == answer.getId()
    }

    def "submit an answer that the user had already answered" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'submit 2 answers for this question'
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), answerDto);
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), answerDto2);

        then: 'an answer is created'
        answerRepository.count() == 1L
        def answer = answerRepository.findAll().get(0)
        answer.getUser().getId() == user2.getId()
        answer.getQuestion().getId() == question.getId()
        answer.getSelectedAnswer() == SELECTED2
        answer.getTournament().getId() == tournament.getId()

        and: 'is added to the tournament'
        tournament.getAnswers().size() == 1
        tournament.getAnswers().toList().get(0).getId() == answer.getId()
    }

    def "submit an answer but the user is not enrolled in the tournament" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)

        when: 'submit 2 answers for this question'
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), answerDto);

        then: 'a exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_NOT_ENROLLED_IN_TOURNAMENT
    }

    def "submit an answer but the question does not exists" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        and: 'an answer with a question that does not exists'
        def incorrectAnswerDto = new TournamentAnswerDto()
        incorrectAnswerDto.setSelected(SELECTED)
        incorrectAnswerDto.setQuestionId(10000)

        when: 'submit answers for a question that does not exists'
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), incorrectAnswerDto);

        then: 'a exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.QUESTION_NOT_FOUND
    }

    def "submit an answer but the question does not belong to the tournament" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        and: 'an answer with a question that does not belong to the tournament'
        def incorrectAnswerDto = new TournamentAnswerDto()
        incorrectAnswerDto.setSelected(SELECTED)
        incorrectAnswerDto.setQuestionId(question2.getId())

        when: 'submit answers for a question that does not exists'
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), incorrectAnswerDto);

        then: 'a exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.QUESTION_NOT_IN_TOURNAMENT
    }

    def "submit a answer but the tournament does not exists" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'submit answer for a tournment that does not exists'
        tournamentService.submitAnswer(USER_USERNAME2, 1006, answerDto);

        then: 'a exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.TOURNAMENT_ID_NOT_FOUND
    }

    def "submit a answer but the question but selected is invalid I" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        and: 'a answer with a question that does not belong to the tournament'
        def incorrectAnswerDto = new TournamentAnswerDto()
        incorrectAnswerDto.setSelected(-1)
        incorrectAnswerDto.setQuestionId(question.getId())

        when: 'submit answers with selected option invalid'
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), incorrectAnswerDto);

        then: 'an exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.INVALID_SELECTED_OPTION
    }

    def "submit a answer but the question but selected is invalid II" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        and: 'a answer with a question that does not belong to the tournament'
        def incorrectAnswerDto = new TournamentAnswerDto()
        incorrectAnswerDto.setSelected(2)
        incorrectAnswerDto.setQuestionId(question.getId())

        when: 'submit answers with selected option invalid'
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), incorrectAnswerDto);

        then: 'an exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.INVALID_SELECTED_OPTION
    }

    def "submit answer but user do not exists" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'submit an answer for this question'
        tournamentService.submitAnswer("User1234567", tournament.getId(), answerDto);

        then: 'an exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USERNAME_NOT_FOUND
    }

    def "submit answer but username is null" () {
        given: 'a valid tournament with one question'
        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setQuestions(questions)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'submit an answer for this question'
        tournamentService.submitAnswer(null, tournament.getId(), answerDto);

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