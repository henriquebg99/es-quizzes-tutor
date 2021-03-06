package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class SubmitQuestionTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'

    @Autowired
    ProposedQuestionService proposedQuestionService

    @Autowired
    QuestionService questionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    ProposedQuestionRepository proposedQuestionRepository

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution

    def user
    def userTwo

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('name', "username", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)

        userTwo = new User('name2', "username2", 2, User.Role.STUDENT)
        userTwo.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(userTwo)
        userRepository.save(userTwo)
    }

    def "submit a question with 2 options and no image"() {
        given: "a proposedQuestionDto"
        def proposedQuestionDto = new ProposedQuestionDto()
        proposedQuestionDto.setKey(1)
        proposedQuestionDto.setTitle(QUESTION_TITLE)
        proposedQuestionDto.setContent(QUESTION_CONTENT)
        proposedQuestionDto.setUsername(user.getUsername())
        proposedQuestionDto.setStatus(ProposedQuestion.Status.DEPENDENT.name())

        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        proposedQuestionDto.setOptions(options)

        when:
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, user.getId())

        then: "the correct question is inside the repository"
        proposedQuestionRepository.count() == 1L
        def result = proposedQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getCourse().getName() == COURSE_NAME
        result.getUsername() == user.getUsername()
        result.getOptions().size() == 2
        course.getProposedQuestions().contains(result)
        def resOption = result.getOptions().get(0)
        def resOption2 = result.getOptions().get(1)
        resOption.getContent() == OPTION_CONTENT
        resOption2.getContent() == OPTION_CONTENT
        resOption.getCorrect()
    }

    def "submit a question with 2 options and an image"() {
        given: "a proposedQuestionDto"
        def proposedQuestionDto = new ProposedQuestionDto()
        proposedQuestionDto.setKey(1)
        proposedQuestionDto.setTitle(QUESTION_TITLE)
        proposedQuestionDto.setContent(QUESTION_CONTENT)
        proposedQuestionDto.setUsername(user.getUsername())
        proposedQuestionDto.setStatus(ProposedQuestion.Status.DEPENDENT.name())

        and: 'an image'
        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)
        proposedQuestionDto.setImage(image)

        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        proposedQuestionDto.setOptions(options)

        when:
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, user.getId())

        then: "the correct question is inside the repository"
        proposedQuestionRepository.count() == 1L
        def result = proposedQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getCourse().getName() == COURSE_NAME
        result.getUsername() == user.getUsername()
        result.getOptions().size() == 2
        course.getProposedQuestions().contains(result)
        def resOption = result.getOptions().get(0)
        def resOption2 = result.getOptions().get(1)
        resOption.getContent() == OPTION_CONTENT
        resOption2.getContent() == OPTION_CONTENT
        resOption.getCorrect()
    }

    def "1 user submit 2 questions"() {
        given: "a proposedQuestionDto"
        def proposedQuestionDto = new ProposedQuestionDto()
        proposedQuestionDto.setKey(1)
        proposedQuestionDto.setTitle(QUESTION_TITLE)
        proposedQuestionDto.setContent(QUESTION_CONTENT)
        proposedQuestionDto.setUsername(user.getUsername())
        proposedQuestionDto.setStatus(ProposedQuestion.Status.DEPENDENT.name())

        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        proposedQuestionDto.setOptions(options)

        when: 'are submited two questions'
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, user.getId())
        proposedQuestionDto.setKey(null)
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, user.getId())

        then: "the 2 question are created in the repository"
        proposedQuestionRepository.count() == 2L
        def result = proposedQuestionRepository.findAll().get(0)
        def result2 = proposedQuestionRepository.findAll().get(1)
        result.getKey() + result2.getKey() == 3
    }

    def "2 user submit 1 question each"() {
        given: "a proposedQuestionDto"
        def proposedQuestionDto = new ProposedQuestionDto()
        proposedQuestionDto.setKey(1)
        proposedQuestionDto.setTitle(QUESTION_TITLE)
        proposedQuestionDto.setContent(QUESTION_CONTENT)
        proposedQuestionDto.setUsername(user.getUsername())
        proposedQuestionDto.setStatus(ProposedQuestion.Status.DEPENDENT.name())

        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        proposedQuestionDto.setOptions(options)

        when: 'are submited one question by each user'
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, user.getId())
        proposedQuestionDto.setKey(null)
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, userTwo.getId())

        then: "the 2 question are created in the repository"
        proposedQuestionRepository.count() == 2L
        def result = proposedQuestionRepository.findAll().get(0)
        def result2 = proposedQuestionRepository.findAll().get(1)
        result.getKey() + result2.getKey() == 3
        result.getUsername() == 'username'
        result2.getUsername() == 'username2'
    }

    def "submit a question with blank option"() {
        given: "a proposedQuestionDto"
        def proposedQuestionDto = new ProposedQuestionDto()
        proposedQuestionDto.setKey(1)
        proposedQuestionDto.setTitle(QUESTION_TITLE)
        proposedQuestionDto.setContent(QUESTION_CONTENT)
        proposedQuestionDto.setUsername(user.getUsername())
        proposedQuestionDto.setStatus(ProposedQuestion.Status.DEPENDENT.name())

        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(" ")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(" ")
        optionDto.setCorrect(false)
        options.add(optionDto)
        proposedQuestionDto.setOptions(options)

        when:
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, user.getId())

        then: "should throw exception"
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_MISSING_DATA
    }


    def "submit a question with blank question"() {
        given: "a proposedQuestionDto"
        def proposedQuestionDto = new ProposedQuestionDto()
        proposedQuestionDto.setKey(1)
        proposedQuestionDto.setTitle(" ")
        proposedQuestionDto.setContent(" ")
        proposedQuestionDto.setUsername(user.getUsername())
        proposedQuestionDto.setStatus(ProposedQuestion.Status.DEPENDENT.name())

        and: 'an option'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)

        when:
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, user.getId())

        then: "should throw exception"
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_MISSING_DATA
    }

    def "submit a question with 2 correct option"() {
        given: "a proposedQuestionDto"
        def proposedQuestionDto = new ProposedQuestionDto()
        proposedQuestionDto.setKey(1)
        proposedQuestionDto.setTitle(QUESTION_TITLE)
        proposedQuestionDto.setContent(QUESTION_CONTENT)
        proposedQuestionDto.setUsername(user.getUsername())
        proposedQuestionDto.setStatus(ProposedQuestion.Status.DEPENDENT.name())

        and: '2 correct option'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        proposedQuestionDto.setOptions(options)

        when:
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, user.getId())

        then: "should throw exception"
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_MULTIPLE_CORRECT_OPTIONS
    }

    @TestConfiguration
    static class ProposedQuestionServiceImplTestContextConfiguration {

        @Bean
        ProposedQuestionService proposedQuestionService1() {
            return new ProposedQuestionService()
        }

        @Bean
        QuestionService questionService1() {
            return new QuestionService()
        }
    }

}

