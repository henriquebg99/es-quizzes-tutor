package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;


@Service
public class QuestionApprovalService {

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
    public ProposedQuestionDto findQuestionById(Integer proposedQuestionId) {
        return proposedQuestionRepository.findById(proposedQuestionId).map(ProposedQuestionDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, proposedQuestionId));
    }

    /*@Transactional(isolation = Isolation.REPEATABLE_READ)
    public User findUserById(Integer userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userID));


    }*/




    public void changeStatus(ProposedQuestion pd, String status){
        if(status.equals("Approved"))
            pd.setStatus(ProposedQuestion.Status.APPROVED);
        if(status.equals("Rejected"))
            pd.setStatus(ProposedQuestion.Status.REJECTED);
    }





}