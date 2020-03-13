package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionApprovalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class QuestionApprovalTest extends Specification{
    public static final String USERNAME = 'username'
    public static final String STATUSOK = "approved"
    public static final String STATUSKO = "rejected"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"




    @Autowired
    QuestionApprovalService questionApprovalService

    @Autowired
    UserRepository userRepository

    @Autowired
    ProposedQuestionRepository proposedQuestionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    CourseRepository courseRepository






    def teacher
    def student
    def course


    def setup(){
        student = new User('name', USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        
    }
    def "approve question "() {
        given: "create proposedQuestion"
        def proposedQuestion = new ProposedQuestion()
        proposedQuestion.setKey(1)
        proposedQuestion.setContent(QUESTION_CONTENT)
        proposedQuestion.setCourse(course)
        and: 'two options'
        def optionOK = new Option()
        optionOK.setContent(OPTION_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setQuestion(proposedQuestion)
        optionRepository.save(optionOK)
        proposedQuestion.addOption(optionOK)
        def optionKO = new Option()
        optionKO.setContent(OPTION_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setQuestion(proposedQuestion)
        optionRepository.save(optionKO)
        proposedQuestion.addOption(optionKO)
        proposedQuestionRepository.save(proposedQuestion)

        when:
        def result = questionApprovalService.findQuestionById(proposedQuestion.getId())

        then: "change status to approved and verify if its changed"
        questionApprovalService.changeStatus(result,STATUSOK)
        result.getStatus() == ProposedQuestion.Status.APPROVED
    }

    def "reject question"() {
        given: "create proposedQuestion"
        def proposedQuestion = new ProposedQuestion()
        proposedQuestion.setKey(1)
        proposedQuestion.setContent(QUESTION_CONTENT)
        proposedQuestion.setCourse(course)// isto foi copiado do teste FindQuestionTest , que ja tem la o erro
        and: 'two options'
        def optionOK = new Option()
        optionOK.setContent(OPTION_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setQuestion(proposedQuestion)
        optionRepository.save(optionOK)
        proposedQuestion.addOption(optionOK)
        def optionKO = new Option()
        optionKO.setContent(OPTION_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setQuestion(proposedQuestion)
        optionRepository.save(optionKO)
        proposedQuestion.addOption(optionKO)
        proposedQuestionRepository.save(proposedQuestion)

        when:
        def result = questionApprovalService.findQuestionById(proposedQuestion.getId())

        then: "change status to approved and verify if its changed"
        questionApprovalService.changeStatus(result,STATUSKO)
        result.getStatus() == ProposedQuestion.Status.REJECTED
    }

    def "User approving is a teacher"(){
        given:"a teacher"
        teacher = new User('name', USERNAME, 1, User.Role.TEACHER)
        userRepository.save(teacher)

        when:
        def result = questionApprovalService.findUserByUsername(teacher.getUsername())
        then:"verfiy its a teacher"
        result >> true

    }
    def "User approving is not a teacher"(){
        given:"a teacher"
        teacher = new User('name', USERNAME, 1, User.Role.TEACHER)
        userRepository.save(teacher)

        when:
        def result = questionApprovalService.findUserByUsername(teacher.getUsername())
        then:"verfiy its a teacher"
        result >> false


    }

    @TestConfiguration
    static class QuestionApprovalServiceImplTestContextConfiguration {

        @Bean
        QuestionApprovalService questionApprovalService() {
            return new QuestionApprovalService()
        }
    }


}




