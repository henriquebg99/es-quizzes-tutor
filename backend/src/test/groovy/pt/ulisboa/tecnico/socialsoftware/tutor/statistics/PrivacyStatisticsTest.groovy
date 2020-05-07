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
        except:true
    }

    def 'turn stats ' () {
        except:true
    }

    def 'turn information private and check' () {
        except:true
    }

    def 'turn information private then public again' () {
        except:true
    }

    def 'turn information private then public again and private again' () {
        except:true
    }

    def 'check for user privacy when course execution is empty' () {
        except:true
    }

    def 'check for user privacy with empty username' () {
        except:true
    }

    def 'check stats privacy of student with username that does not exists' () {
        except:true
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
