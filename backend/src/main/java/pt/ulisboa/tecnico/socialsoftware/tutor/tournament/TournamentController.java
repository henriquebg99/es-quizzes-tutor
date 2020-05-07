package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

//import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {
    @PostMapping("/student/course/executions/{executionId}/tournaments/{tournamentId}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.CREATOR')")
    public TournamentDto cancelTournament (Principal principal, @PathVariable int executionId, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.cancelTournament(user.getUsername(), tournamentId);

    }

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/student/course/executions/{executionId}/tournaments")
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
    public void enrollTournament (Principal principal, @PathVariable int executionId, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.enrollTournament(user.getUsername(), tournamentId);
    }

    @GetMapping("/student/course/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> listOpenTournaments(@PathVariable int executionId) {
        return tournamentService.listOpenTournaments(executionId);
    }

    @GetMapping("/student/course/executions/{executionId}/closedTournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> listClosedTournaments(@PathVariable int executionId) {
        return tournamentService.listClosedTournaments(executionId);
    }

    @GetMapping("/student/course/executions/{executionId}/tournaments/{tournamentId}/questions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<QuestionDto> listQuestions(Principal principal, @PathVariable int executionId, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.listQuestions(user.getUsername(), tournamentId);
    }

    @GetMapping("/student/course/executions/{executionId}/tournaments/{tournamentId}/answers")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentAnswerDto> listAnswers(Principal principal, @PathVariable int executionId, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.listAnswers(user.getUsername(), tournamentId);
    }

    @PutMapping("/student/course/executions/{executionId}/tournaments/{tournamentId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.CREATOR')")
    public void submitQuestion (Principal principal, @PathVariable int executionId, @PathVariable int tournamentId,
                                @Valid @RequestBody TournamentAnswerDto answerDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.submitAnswer(user.getUsername(), tournamentId, answerDto);
    }
}

