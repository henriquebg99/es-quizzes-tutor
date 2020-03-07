package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentService {
    //see AnswerService
    public TournamentDto createTournament (String username, TournamentDto tournamentDto) {
        return null;
    }

    public void enrollTournament (String username, Integer tournamentId) {

    }

    public List<TournamentDto> listOpenTournaments () {
        return null;
    }


    public void cancelTournament (String username, Integer tournamentId) {

    }
}