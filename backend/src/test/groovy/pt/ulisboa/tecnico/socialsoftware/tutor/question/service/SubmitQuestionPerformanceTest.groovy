package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class SubmitQuestionPerformanceTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'titulo'
    public static final String QUESTION_CONTENT = 'conteudo'
    public static final String OPTION_CONTENT = "opcao-conteudo"

    @Autowired
    ProposedQuestionService proposedQuestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    ProposedQuestionRepository proposedQuestionRepository

    @Autowired
    UserRepository userRepository

    def "performance testing to get 3000 proposed questions"() {
        given: "a course, a courseExecution, a user, options and a proposedQuestionDto"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def user = new User('nome', "demo-student", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)

        def optionDto = new OptionDto()
        def options = new ArrayList<OptionDto>()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)

        def proposedQuestionDto = new ProposedQuestionDto()
        proposedQuestionDto.setKey(1)
        proposedQuestionDto.setTitle(QUESTION_TITLE)
        proposedQuestionDto.setContent(QUESTION_CONTENT)
        proposedQuestionDto.setUsername(user.getUsername())
        proposedQuestionDto.setOptions(options)
        proposedQuestionDto.setStatus(ProposedQuestion.Status.DEPENDENT.name())

        when: 'are submited 3000 questions'
        1.upto(3000, {
            proposedQuestionService.createProposedQuestion(course.getId(), proposedQuestionDto, user.getId())
            proposedQuestionDto.setKey(null)
        })

        then:
        true
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

