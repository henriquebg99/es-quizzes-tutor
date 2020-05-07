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
class GenerateQuestionsTest extends Specification {

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

    def question, question2, question3
    def questions, questions2
    def user, user2
    def tournamentDto
    def course
    def courseExecution
    def topic, topic2
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

        topic2 = new Topic(course, topicDto)
        topic2.setName(TOPIC_NAME + "2")
        topicRepository.save(topic2)

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

        question3 = new Question(course, questionDto);
        questionRepository.save(question3);

        topic.addQuestion(question)
        topic.addQuestion(question2)
        topic2.addQuestion(question3)

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

        tournamentService.createTournament(USER_USERNAME, courseExecution.getId(), tournamentDto);
    }


    def "generate quiz with one topic" () {
        given: 'one tournament with 2 questions with 1  topic that has 2 questions'
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setNumberOfQuestions(2);

        when: 'the tournament is generated'
        tournament.generateQuestions()

        then: 'the tournament has two questions'
        tournament.getQuestions().size() == 2
        tournament.getQuestions()
                .stream()
                .filter({ question -> topic.getQuestions().contains(question) })
                .count() == 2

        tournament.getQuestions().stream().distinct().count() == 2
    }

    def "generate one question quiz with one topic" () {
        given: 'one tournament with 1 questions with 1 topic that has 2 questions'
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setNumberOfQuestions(1);

        when: 'the tournament is generated'
        tournament.generateQuestions()

        then: 'the tournament has one question'
        tournament.getQuestions().size() == 1
        tournament.getQuestions()
                .stream()
                .filter({ question -> topic.getQuestions().contains(question) })
                .count() == 1
    }

    def "generate quiz with two topics and 3 questions" () {
        given: 'one tournament with 2 questions with 1  topic that has 2 questions'
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setNumberOfQuestions(3);
        tournament.setTopics(new HashSet ([topic, topic2]));

        when: 'the tournament is generated'
        tournament.generateQuestions()

        then: 'the tournament has two questions'
        tournament.getQuestions().size() == 3
        tournament.getQuestions()
                .stream()
                .filter({ question -> topic.getQuestions().contains(question) })
                .count() == 2
        tournament.getQuestions()
                .stream()
                .filter({ question -> topic2.getQuestions().contains(question) })
                .count() == 1
        tournament.getQuestions().stream().distinct().count() == 3
    }

    def "generate quiz with two topics and 2 questions" () {
        given: 'one tournament with 2 questions with 1  topic that has 2 questions'
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setNumberOfQuestions(2);
        tournament.setTopics(new HashSet ([topic, topic2]));

        when: 'the tournament is generated'
        tournament.generateQuestions()

        then: 'the tournament has two questions'
        tournament.getQuestions().size() == 2
        tournament.getQuestions()
                .stream()
                .filter({ question -> topic.getQuestions().contains(question) ||
                                                        topic2.getQuestions().contains(question)})
                .count() == 2
        tournament.getQuestions().stream().distinct().count() == 2
    }

    def "generate quiz but not enough questions" () {
        given: 'one tournament with 2 questions with 1  topic that has 2 questions'
        def tournament = tournamentRepository.findAll().get(0)
        tournament.setNumberOfQuestions(4);
        tournament.setTopics(new HashSet ([topic, topic2]));

        when: 'the tournament is generated'
        tournament.generateQuestions()

        then: 'the tournament has two questions'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.NOT_ENOUGH_QUESTIONS
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}