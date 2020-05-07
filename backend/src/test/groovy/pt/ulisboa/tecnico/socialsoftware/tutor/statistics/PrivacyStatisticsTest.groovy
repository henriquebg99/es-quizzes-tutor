package pt.ulisboa.tecnico.socialsoftware.tutor.statistics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

@DataJpaTest
class PrivacyStatisticsTest extends Specification{

    @Autowired
    TournamentService tournamentService

    @Autowired
    StatsService statsService;


    def setup () {
    }

    def 'check if tournament privacy default is public ' () {
        true
    }

    def 'turn stats ' () {
        true
    }

    def 'turn information private and check' () {
        true
    }

    def 'turn information private then public again' () {
        true
    }

    def 'turn information private then public again and private again' () {
        true
    }

    def 'check for user privacy when course execution is empty' () {
        true
    }

    def 'check for user privacy with empty username' () {
        true
    }

    def 'check stats privacy of student with username that does not exists' () {
        true
    }

    @TestConfiguration
    static class StatisticsServiceImplTestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
