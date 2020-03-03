package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spock.lang.Specification

@DataJpaTest
class CreateTournamentTest extends Specification {
    @Autowired
    TournamentRepository questionRepository

    def setup() {

    }

    def 'create a quiz' () {
        expected: true
    }

    def 'create a quiz with begin date empty' () {
        expected: true
    }

    def 'create a quiz with end date empty' () {
        expected: true
    }

    def 'create a quiz with a negative number of questions' () {
        expected: true
    }

    def 'create a quiz with 0 questions' () {
        expected: true
    }
    /*FIXME add more tests about topics*/
    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}

