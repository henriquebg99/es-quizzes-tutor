package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class ListAvailableTournamentsTest extends Specification{

    static final String USER_USERNAME = "username"
    static final String USER_NAME     = "name"
    static final int    USER_KEY      = 1

    def setup() {
        // criar utilizador -> adicionar torneios

        //Crio um utilizador que é um estudante
        user = new User(USER_NAME, USER_USERNAME, USER_KEY, User.Role.STUDENT)
        //Crio torneios com o utilizador

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
