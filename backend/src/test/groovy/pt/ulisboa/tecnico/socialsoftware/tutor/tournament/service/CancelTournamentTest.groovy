package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CancelTournamentTest extends Specification {

    public static final String USER_USERNAME = "username"
    public static final String USER_USERNAME_NOT_CREATOR = "username2"
    public static final String USER_USERNAME_NOT_EXISTING = "username3"
    public static final int ID = 2
    public static final int ID_NOT_EXISTING = 2

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository
    def setup() {

    }
    def 'cancel a tournament' () {
        given: 'a valid tournament canceled'
        def tournament = new TournamentDto ()
        tournament.setId(ID)
        tournamentRepository.count() == 1L

        when:
        tournamentService.cancelTournament(USER_USERNAME, tournament.getId())

        then: "the tournament is removed from the repository"
        tournamentRepository.count() == 0L
    }

    def 'cancel a tournament that does not exit' () {
        given: 'a tournament with an id'
        def tournament = new TournamentDto ()
        tournament.setId(ID_NOT_EXISTING)

        when:
        tournamentService.cancelTournament(USER_USERNAME, tournament.getId())

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.TOURNAMENT_ID_NOT_FOUND
    }

    def 'cancel a tournament with empty id' () {
        given: 'a tournament with an empty id'
        def tournament = new TournamentDto ()
        tournament.setId(null)

        when:
        tournamentService.cancelTournament(USER_USERNAME, tournament.getId())

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.TOURNAMENT_ID_EMPTY
    }

/*   def 'cancel a tournament while it is happening' () {
        given: 'a tournament'
        def tournament = new TournamentDto ()
        tournament.setId(ID)

        when:
        tournamentService.cancelTournament(USER_USERNAME, tournament.getId())

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.TOURNAMENT_HAPPENING
    }

    def 'cancel a tournament that has already ended' () {
        given: 'a tournament'
        def tournament = new TournamentDto ()
        tournament.setId(ID)

        when:
        tournamentService.cancelTournament(USER_USERNAME, tournament.getId())

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.TOURNAMENT_ENDED
    }*/

    def 'a tournament is canceled by a student that does not exist' () {
        given: 'a tournament'
        def tournament = new TournamentDto ()
        tournament.setId(ID)

        when:
        tournamentService.cancelTournament(USER_USERNAME_NOT_EXISTING, tournament.getId())

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.USER_NOT_FOUND
    }

    def 'a tournament is canceled by a user that did not create it' () {
        given: 'a tournament'
        def tournament = new TournamentDto ()
        tournament.setId(ID)

        when:
        tournamentService.cancelTournament(USER_USERNAME_NOT_CREATOR, tournament.getId())

        then: 'an exception is thrown'
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.USER_DID_NOT_CREATE_TOURNAMENT
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