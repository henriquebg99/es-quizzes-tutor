package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/executions/{executionId}/stats")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatsDto getStats(Principal principal, @PathVariable int executionId, @Valid @RequestBody StatsDto statsDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statsService.getStats(user.getUsername(), executionId, statsDto);
    }

    @PostMapping("/executions/{executionId}/stats/{privacy}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatsDto getStats(Principal principal, @PathVariable int executionId, @Valid @RequestBody StatsDto statsDto, @PathVariable int privacy) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statsService.setPrivacy(user.getUsername(), executionId, statsDto, privacy > 0 ? true : false);
    }


}