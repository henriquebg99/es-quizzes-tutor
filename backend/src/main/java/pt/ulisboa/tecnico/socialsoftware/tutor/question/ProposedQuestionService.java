package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class ProposedQuestionService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ProposedQuestionRepository proposedQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto findProposedQuestionById(Integer questionId) {
        return proposedQuestionRepository.findById(questionId).map(ProposedQuestionDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ProposedQuestionDto> findProposedQuestions(int courseId) {
        return proposedQuestionRepository.findProposedQuestions(courseId).stream().map(ProposedQuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto createProposedQuestion(int courseId, ProposedQuestionDto proposedQuestionDto, int userId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (proposedQuestionDto.getKey() == null) {
            int maxQuestionNumber = proposedQuestionRepository.getMaxProposedQuestionNumber() != null ?
                    proposedQuestionRepository.getMaxProposedQuestionNumber() : 0;
            proposedQuestionDto.setKey(maxQuestionNumber + 1);
        }

        ProposedQuestion proposedQuestion = new ProposedQuestion(proposedQuestionDto, course, user);
        course.addProposedQuestion(proposedQuestion);
        this.entityManager.persist(proposedQuestion);
        return new ProposedQuestionDto(proposedQuestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void uploadImage(Integer questionId, String type) {
        ProposedQuestion proposedQuestion = proposedQuestionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        Image image = proposedQuestion.getImage();

        if (image == null) {
            image = new Image();

            proposedQuestion.setImage(image);

            entityManager.persist(image);
        }

        proposedQuestion.getImage().setUrl(proposedQuestion.getKey() + "." + type);
    }

}
