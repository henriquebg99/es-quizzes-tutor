package pt.ulisboa.tecnico.socialsoftware.tutor.question.service


import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService
import spock.lang.Specification

class QuestionApprovalTest extends Specification{
    def proposedQuestionService

    def setup(){
        proposedQuestionService = new ProposedQuestionService()
    }

    def "approve question and add to the question bank"() {
        //add question
        expect: true
    }

    def "reject question"() {
        //reject question
        expect: true
    }

    def "question already exists"(){
        //an exception is thrown
        expect: true
    }

    def "submission is blank"(){
        //an exception is thrown
        expect: true
    }

    def "submission is empty"(){
        //an exception is thrown
        expect: true
    }

}




