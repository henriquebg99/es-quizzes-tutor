package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@RestController
public class ProposedQuestionController {
    private static Logger logger = LoggerFactory.getLogger(ProposedQuestionController.class);

    private ProposedQuestionService proposedQuestionService;

    @Value("${figures.dir}")
    private String figuresDir;

    ProposedQuestionController(ProposedQuestionService proposedQuestionService) {
        this.proposedQuestionService = proposedQuestionService;
    }

    @GetMapping("/courses/{courseId}/proposedquestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<ProposedQuestionDto> getCourseProposedQuestions(@PathVariable int courseId){
        return this.proposedQuestionService.findProposedQuestions(courseId);
    }

    @PostMapping("/student/courses/{courseId}/{userId}/proposedquestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public ProposedQuestionDto createProposedQuestion(@PathVariable int courseId, @PathVariable int userId, @Valid @RequestBody ProposedQuestionDto proposedQuestion) {
        return this.proposedQuestionService.createProposedQuestion(courseId, proposedQuestion, userId);
    }

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

    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }

}