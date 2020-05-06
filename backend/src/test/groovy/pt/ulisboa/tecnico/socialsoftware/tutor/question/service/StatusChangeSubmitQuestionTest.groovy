package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.access.method.P
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class StatusChangeSubmitQuestionTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String JUSTIFICATION = "justification of choice"

    @Autowired
    ProposedQuestionService proposedQuestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    ProposedQuestionRepository proposedQuestionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuestionService questionService

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution

    def userStudent
    def userTeacher

    def proposedQuestionDto

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        userStudent = new User('name', "username", 1, User.Role.STUDENT)
        userStudent.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(userStudent)
        userRepository.save(userStudent)

        userTeacher = new User('name2', "username2", 2, User.Role.TEACHER)
        userTeacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(userTeacher)
        userRepository.save(userTeacher)

        proposedQuestionDto = new ProposedQuestionDto()
        proposedQuestionDto.setKey(1)
        proposedQuestionDto.setTitle(QUESTION_TITLE)
        proposedQuestionDto.setContent(QUESTION_CONTENT)
        proposedQuestionDto.setUsername(userStudent.getUsername())
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
        proposedQuestionDto.setStatus(ProposedQuestion.Status.DEPENDENT.name())
    }

    def "approve submitted question without justification"() {
        given: "a submitted question"
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, userStudent.getId())

        when: "teacher approves question"
        proposedQuestionService.changeStatus(proposedQuestionDto.getKey(), ProposedQuestion.Status.APPROVED, '')

        then: "submitted question has approved status and is in question repository"
        proposedQuestionDto.getStatus() == ProposedQuestion.Status.APPROVED
        //FIXME missing check on question creation in question repository
    }

    def "reject submitted question with justification"() {
        given: "a submitted question"
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, userStudent.getId())

        when: "teacher rejects question and gives justification"
        proposedQuestionService.changeStatus(proposedQuestionDto.getKey(), ProposedQuestion.Status.REJECTED, JUSTIFICATION)

        then: "question has rejected status, justification and is not in question repository"
        proposedQuestionDto.getStatus() == ProposedQuestion.Status.REJECTED
        proposedQuestionDto.getJustification() == JUSTIFICATION
    }

    def "no choice with justification"() {
        given: "a submitted question"
        proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, userStudent.getId())

        when: "teacher gives justification without status"
        proposedQuestionService.changeStatus(proposedQuestionDto.getKey(), ProposedQuestion.Status.DEPENDENT, JUSTIFICATION)

        then: "should throw exception"
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_NEEDS_STATUS
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


