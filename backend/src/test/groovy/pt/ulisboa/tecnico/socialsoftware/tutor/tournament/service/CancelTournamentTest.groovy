package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spock.lang.Specification

@DataJpaTest
class CancelTournamentTest extends Specification {
    @Autowired
    TournamentRepository questionRepository

    def setup() {

    }

    def 'cancel a tournament that does not exit' () {
        expected: true
    }

    def 'cancel a tournament that has already ended' () {
        expected: true
    }

    def 'cancel a tournament while it is happening' () {
        expected: true
    }

    def 'a tournament is canceled by a student that did not create it' () {
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