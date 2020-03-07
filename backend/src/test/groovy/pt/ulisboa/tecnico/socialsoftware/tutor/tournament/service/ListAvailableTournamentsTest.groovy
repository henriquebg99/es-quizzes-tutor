package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

class ListAvailableTournamentsTest extends Specification{

    def tourService

    def setup() {
        tourService = new TournamentService()
    }

    def 'get the tournaments for the student'(){
        expect: true
    }

    def 'the tournament is not available'() {
        expect: true
    }

    def 'the tournament is already created'() {
        expect: true
    }

    def 'the tournament is completed'() {
        expect: true
    }
}
