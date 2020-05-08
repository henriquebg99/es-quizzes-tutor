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
class ListAnswersTest extends Specification {
    public static final String TOPIC_NAME = "topic"
    public static final String USER_NAME = "name"
    public static final String USER_USERNAME = "username"
    public static final int USER_KEY = 1
    public static final String USER_NAME2 = "name2"
    public static final String USER_USERNAME2 = "username2"
    public static final int USER_KEY2 = 2
    public static final String USER_NAME3 = "name3"
    public static final String USER_USERNAME3 = "username3"
    public static final int USER_KEY3 = 3
    public static final int NUMBER_OF_QUESTIONS = 2
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
    def questions
    def user, user2, user3
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

        user3 = new User(USER_NAME3, USER_USERNAME3, USER_KEY3, User.Role.STUDENT)
        userRepository.save(user3)

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)
        topics = new HashSet<TopicDto>()
        topics.add(topicDto)
        def topicList = new LinkedList<TopicDto> ()
        topicList.add(topic)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.addUser(user)
        courseExecution.addUser(user2)
        courseExecution.addUser(user3)
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
        questions.add(question2)

        topic.addQuestion(question)
        topic.addQuestion(question2)
        topics.add(topicDto)

        tournamentDto = new TournamentDto ()
        tournamentDto.setBeginDate(beginDateString)
        tournamentDto.setEndDate(endDateString)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setTopics(topics)

        answerDto = new TournamentAnswerDto()
        answerDto.setQuestionId(question.getId())
        answerDto.setSelected(SELECTED)

        answerDto2 = new TournamentAnswerDto()
        answerDto2.setQuestionId(question2.getId())
        answerDto2.setSelected(SELECTED2)

        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
    }

    def "list answers of the user that has answered" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setTopics(new HashSet<Topic>([topic]))

        and: 'students enrolled'
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())
        tournamentService.enrollTournament(USER_USERNAME3, tournament.getId())

        and: 'answers submitted'
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), answerDto)
        tournamentService.submitAnswer(USER_USERNAME2, tournament.getId(), answerDto2)
        tournamentService.submitAnswer(USER_USERNAME3, tournament.getId(), answerDto2)

        when: 'list answers'
        List<TournamentAnswerDto> answersUser2 =
                tournamentService.listAnswers(USER_USERNAME2, tournament.getId());

        List<TournamentAnswerDto> answersUser3 =
                tournamentService.listAnswers(USER_USERNAME3, tournament.getId());

        then: 'each list has one answer'
        answersUser2.size() == 2
        answersUser3.size() == 1

        and: 'each answer is correct'
        // depends on the sort
        answersUser2.get(1).getQuestionId() == answerDto.getQuestionId() &&
                answersUser2.get(1).getSelected() == answerDto.getSelected() &&
                answersUser2.get(0).getQuestionId() == answerDto2.getQuestionId()  &&
                answersUser2.get(0).getSelected() == answerDto2.getSelected() ||
                answersUser2.get(0).getQuestionId() == answerDto.getQuestionId() &&
                answersUser2.get(0).getSelected() == answerDto.getSelected() &&
                answersUser2.get(1).getQuestionId() == answerDto2.getQuestionId()  &&
                answersUser2.get(1).getSelected() == answerDto2.getSelected() &&

        answersUser3.get(0).getQuestionId() == answerDto2.getQuestionId()
        answersUser3.get(0).getSelected() == answerDto2.getSelected()
    }

    def "list answers but the user is not enrolled in the tournament" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)

        when: 'list answers'
        tournamentService.listAnswers(USER_USERNAME2, tournament.getId())

        then: 'a exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_NOT_ENROLLED_IN_TOURNAMENT
    }

    def "list a answer but the tournament does not exists" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)

        when: 'list answer for a tournament that does not exists'
        tournamentService.listAnswers(USER_USERNAME2, 1006);

        then: 'a exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.TOURNAMENT_ID_NOT_FOUND
    }

    def "list answers of user that has not answered to this tournament" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)

        and: 'students enrolled'
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())
        tournamentService.enrollTournament(USER_USERNAME3, tournament.getId())

        when: 'list answers'
        List<TournamentAnswerDto> answersUser2 =
                tournamentService.listAnswers(USER_USERNAME2, tournament.getId());

        List<TournamentAnswerDto> answersUser3 =
                tournamentService.listAnswers(USER_USERNAME2, tournament.getId());

        then: 'each list has one answer'
        answersUser2.size() == 0
        answersUser3.size() == 0
    }

    def "list answer but user do not exists" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'list answers'
        tournamentService.listAnswers("User1234567", tournament.getId());

        then: 'an exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USERNAME_NOT_FOUND
    }

    def "list answers but username is null" () {
        given: 'a valid tournament with one question'
        def tournament = tournamentRepository.findAll().get(0)
        tournamentService.enrollTournament(USER_USERNAME2, tournament.getId())

        when: 'list answers'
        tournamentService.listAnswers(null, tournament.getId());

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