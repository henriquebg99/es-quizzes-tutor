package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND;

@Service
public class TournamentService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament (String username, int courseExecutionId, TournamentDto tournamentDto) {
        User user = userRepository.findByUsername(username);
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (user == null)
            throw new TutorException(ErrorMessage.USERNAME_NOT_FOUND, username);

        if (tournamentDto.getBeginDate() == null)
            throw new TutorException(ErrorMessage.BEGIN_DATE_IS_EMPTY);

        if (tournamentDto.getEndDate() == null)
            throw new TutorException(ErrorMessage.END_DATE_IS_EMPTY);

        if (tournamentDto.getNumberOfQuestions() < 0)
            throw new TutorException(ErrorMessage.NEGATIVE_NUMBER_OF_QUESTIONS);

        if (tournamentDto.getNumberOfQuestions() == 0)
            throw new TutorException(ErrorMessage.NUMBER_OF_QUESTIONS_IS_ZERO);

        LocalDateTime beginDate = LocalDateTime.parse(tournamentDto.getBeginDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(tournamentDto.getEndDate(), formatter);

        if (endDate.isBefore(beginDate))
            throw new TutorException(ErrorMessage.END_DATE_IS_BEFORE_BEGIN);

        Tournament tournament = new Tournament (user, tournamentDto, courseExecution);
        tournament.setBeginDate(beginDate);
        tournament.setEndDate(endDate);

        courseExecution.addTournament(tournament);

        int courseId = courseExecution.getCourse().getId();

        if (tournamentDto.getTopics() == null || tournamentDto.getTopics().isEmpty())
            throw new TutorException(ErrorMessage.NO_TOPICS);

        for (TopicDto topicDto : tournamentDto.getTopics()) {
            Topic topic = topicRepository.findTopicByName(courseId, topicDto.getName());
            if (topic == null)
                throw new TutorException(ErrorMessage.TOPIC_NOT_FOUND_NAME, topicDto.getName());
            tournament.addTopic(topic);
            topic.addTournament(tournament);
        }

        tournamentRepository.save(tournament);

        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void enrollTournament (String username, Integer tournamentId) {

    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listOpenTournaments () {
        return null;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelTournament (String username, Integer tournamentId) {

    }
}