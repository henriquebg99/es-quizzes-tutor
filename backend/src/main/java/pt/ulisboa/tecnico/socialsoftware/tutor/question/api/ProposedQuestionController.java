package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class ProposedQuestionController {
    private static final Logger logger = LoggerFactory.getLogger(ProposedQuestionController.class);

    private final ProposedQuestionService proposedQuestionService;

    @Value("${figures.dir}")
    private String figuresDir;

    ProposedQuestionController(ProposedQuestionService proposedQuestionService) {
        this.proposedQuestionService = proposedQuestionService;
    }

    @GetMapping("/courses/{courseId}/listproposedquestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<ProposedQuestionDto> getCourseProposedQuestions(@PathVariable int courseId){
        return this.proposedQuestionService.findProposedQuestions(courseId);
    }

    @PostMapping("/courses/{courseId}/proposedquestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public ProposedQuestionDto createProposedQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody ProposedQuestionDto proposedQuestion) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        proposedQuestion.setStatus(ProposedQuestion.Status.DEPENDENT.name());
        proposedQuestion.setJustification("");
        return this.proposedQuestionService.createProposedQuestion(courseId, proposedQuestion, user.getId());
    }
2
    @PutMapping("/proposedquestions/{proposedQuestionId}/image")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#proposedQuestionId, 'PROPOSEDQUESTION.ACCESS')")
    public String uploadImage(@PathVariable Integer proposedQuestionId, @RequestParam("file") MultipartFile file) throws IOException {
        logger.debug("uploadImage  proposedQuestionId: {}: , filename: {}", proposedQuestionId, file.getContentType());

        ProposedQuestionDto proposedQuestionDto = proposedQuestionService.findProposedQuestionById(proposedQuestionId);
        String url = proposedQuestionDto.getImage() != null ? proposedQuestionDto.getImage().getUrl() : null;
        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        int lastIndex = Objects.requireNonNull(file.getContentType()).lastIndexOf('/');
        String type = file.getContentType().substring(lastIndex + 1);

        proposedQuestionService.uploadImage(proposedQuestionId, type);

        url = proposedQuestionService.findProposedQuestionById(proposedQuestionId).getImage().getUrl();
        Files.copy(file.getInputStream(), getTargetLocation(url), StandardCopyOption.REPLACE_EXISTING);

        return url;
    }

    @PostMapping("/proposedquestions/{proposedQuestionId}/status")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#proposedQuestionId, 'COURSE.ACCESS')")
    public ResponseEntity changeStatus(@PathVariable Integer proposedQuestionId, @Valid @RequestBody String newStatus, @Valid @RequestBody String justification) {
        proposedQuestionService.changeStatus(proposedQuestionId, newStatus, justification);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/proposedquestions/{proposedQuestionId}/status/justification")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#proposedQuestionId, 'PROPOSEDQUESTION.ACCESS')")
    public String seeJustification(@PathVariable Integer proposedQuestionId) {
        return this.proposedQuestionService.seeJustification(proposedQuestionId);
    }

    @PostMapping("/proposedquestions/{proposedQuestionId}/delete")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#proposedQuestionId, 'COURSE.ACCESS')")
    public ResponseEntity removeProposedQuestion(@PathVariable Integer proposedQuestionId) throws IOException {
        ProposedQuestionDto proposedQuestionDto = proposedQuestionService.findProposedQuestionById(proposedQuestionId);
        String url = proposedQuestionDto.getImage() != null ? proposedQuestionDto.getImage().getUrl() : null;

        proposedQuestionService.removeProposedQuestion(proposedQuestionId);

        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        return ResponseEntity.ok().build();
    }


    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }

}