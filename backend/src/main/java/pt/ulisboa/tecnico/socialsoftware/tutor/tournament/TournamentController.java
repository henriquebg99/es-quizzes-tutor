package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

//import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {
    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/executions/{executionId}/tournaments")
    // missing access verification
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto createTournament (Principal principal, @PathVariable int executionId, @Valid @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.createTournament(user.getUsername(), executionId, tournamentDto);
    }

    @PutMapping("/tournaments/{tournamentId}/enroll")
    // missing access verification
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public void enrollTournament (Principal principal, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.enrollTournament(user.getUsername(), tournamentId);
    }

    // unnecessary long url - executions/{executionId}/tournaments
    @GetMapping("/student/course/executions/{executionId}/tournaments")
    // missing access verification
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> listOpenTournaments(@PathVariable int executionId) {
        return tournamentService.listOpenTournaments(executionId);
    }

    // unnecessary long url - tournaments/{tournamentId}/cancel
    // should be a put
    @PostMapping("/student/course/executions/{executionId}/tournaments/{tournamentId}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.CREATOR')")
    public TournamentDto cancelTournament (Principal principal, @PathVariable int executionId, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.cancelTournament(user.getUsername(), tournamentId);

    }
}

