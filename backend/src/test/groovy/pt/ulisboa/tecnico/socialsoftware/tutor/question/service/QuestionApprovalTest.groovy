package pt.ulisboa.tecnico.socialsoftware.tutor.question.service


import pt.ulisboa.tecnico.socialsoftware.tutor.question.PpaService
import spock.lang.Specification

class QuestionApprovalTest extends Specification{
    def ppaService

    def setup(){
        ppaService = new PpaService()
    }

    def "question exists and approve question"() {
        //approve question
        expect: false
    }

    def "question is repeated"(){
        //an exception is thrown
        expect: false
    }
    def "question doesnt exist"(){
        //an exception is thrown
        expect : false
    }

    def "question submitted isn't related to  a topic"(){
        //an exception is thrown
        expect: false
    }

    def "question course is not in execution"() {
        //an exception is thrown
        expect: false
    }
    def "question course doesnt exist"(){
        //an exception is thrown
        expect: false

    }
    def "number of options is different than 4"(){
        //an exception is thrown
        expect: false
    }

    def "user is not a student"(){
        //an exception is thrown
        expect: false
    }
}




