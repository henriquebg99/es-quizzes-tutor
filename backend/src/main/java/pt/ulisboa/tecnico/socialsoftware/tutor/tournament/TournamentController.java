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
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto createTournament (Principal principal, @PathVariable int executionId, @Valid @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.createTournament(user.getUsername(), executionId, tournamentDto);
    }

    @PutMapping("/student/course/executions/{executionId}/tournaments/{tournamentId}/enroll")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto enrollTournament (Principal principal, @PathVariable int executionId, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.enrollTournament(user.getUsername(), tournamentId);
    }

    @GetMapping("/student/course/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> listOpenTournaments(@PathVariable int executionId) {
        return tournamentService.listOpenTournaments(executionId);
    }

    @PutMapping("/student/course/executions/{executionId}/tournaments{tournamentId}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public void cancelTournament (Principal principal, @PathVariable int executionId, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.cancelTournament(user.getUsername(), tournamentId);

    }
}
