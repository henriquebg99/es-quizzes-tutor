package pt.ulisboa.tecnico.socialsoftware.tutor.question.service


import pt.ulisboa.tecnico.socialsoftware.tutor.question.PpaService
import spock.lang.Specification

class QuestionApprovalTest extends Specification{
    def ppaService

    def setup(){
        ppaService = new PpaService()
    }

    def "approve question and add to the question bank"() {
        //add question
        expect: false
    }


    def "reject question"() {
        //reject question
        expect: false
    }

    def "question already exists"(){
        //an exception is thrown
        expect: false
    }

    def "submission is blank"(){
        //an exception is thrown
        expect: false
    }

    def "submission is empty"(){
        //an exception is thrown
        expect: false
    }



}




