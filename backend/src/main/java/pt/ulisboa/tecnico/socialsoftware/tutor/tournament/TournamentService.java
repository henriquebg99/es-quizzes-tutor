package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import net.bytebuddy.implementation.bytecode.Throw;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.stream.Collectors;

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
        if (username == null)
            throw new TutorException(ErrorMessage.USERNAME_EMPTY);

        User user = userRepository.findByUsername(username);
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (user == null)
            throw new TutorException(ErrorMessage.USERNAME_NOT_FOUND, username);

        checkQuestionNumber(tournamentDto);

        Tournament tournament = new Tournament (user, tournamentDto, courseExecution);

        checkAndSetDates(tournamentDto, formatter, tournament);
        courseExecution.addTournament(tournament);
        int courseId = courseExecution.getCourse().getId();
        checkAndAndTopics(tournamentDto, tournament, courseId);

        tournamentRepository.save(tournament);

        return new TournamentDto(tournament);
    }

    private void checkQuestionNumber(TournamentDto tournamentDto) {
        if (tournamentDto.getNumberOfQuestions() <= 0)
            throw new TutorException(ErrorMessage.INVALID_NUMBER_OF_QUESTIONS);
    }

    private void checkAndSetDates(TournamentDto tournamentDto, DateTimeFormatter formatter, Tournament tournament) {
        if (tournamentDto.getBeginDate() == null)
            throw new TutorException(ErrorMessage.BEGIN_DATE_IS_EMPTY);

        if (tournamentDto.getEndDate() == null)
            throw new TutorException(ErrorMessage.END_DATE_IS_EMPTY);

        LocalDateTime beginDate = LocalDateTime.parse(tournamentDto.getBeginDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(tournamentDto.getEndDate(), formatter);

        if (!endDate.isAfter(beginDate))
            throw new TutorException(ErrorMessage.END_DATE_IS_NOT_AFTER_BEGIN_DATE);

        if (beginDate.isBefore(LocalDateTime.now()))
            throw  new TutorException(ErrorMessage.BEGIN_DATE_HAS_PASSED);

        tournament.setBeginDate(beginDate);
        tournament.setEndDate(endDate);
    }

    private void checkAndAndTopics(TournamentDto tournamentDto, Tournament tournament, int courseId) {
        if (tournamentDto.getTopics() == null || tournamentDto.getTopics().isEmpty())
            throw new TutorException(ErrorMessage.NO_TOPICS);

        for (TopicDto topicDto : tournamentDto.getTopics()) {
            Topic topic = topicRepository.findTopicByName(courseId, topicDto.getName());
            if (topic == null)
                throw new TutorException(ErrorMessage.TOPIC_NOT_FOUND_NAME, topicDto.getName());
            tournament.addTopic(topic);
            topic.addTournament(tournament);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void enrollTournament (String username, Integer tournamentId) {

        LocalDateTime date = LocalDateTime.now();

        if (username == null)
            throw new TutorException(ErrorMessage.USERNAME_EMPTY);

        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new TutorException(ErrorMessage.USERNAME_NOT_FOUND, username);

        try {
            Tournament tournament = tournamentRepository.getOne(tournamentId);
            if (tournament.getEndDate().isAfter(date)) {
                user.addEnrolledTournament(tournament);
                tournament.addEnrollment(user);
            }
        }
        catch (Exception e) {
            throw new TutorException(ErrorMessage.TOURNAMENT_ID_NOT_FOUND);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listOpenTournaments() {
        LocalDateTime date = LocalDateTime.now().plusDays(0);

        return tournamentRepository.findAll().stream()
                .filter(tournament -> date.isBefore(tournament.getEndDate()))
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelTournament (String username, Integer tournamentId) {
        if (username == null)
            throw new TutorException(ErrorMessage.USERNAME_EMPTY);

        User user = userRepository.findByUsername(username);

        if (tournamentId == null)
            throw new TutorException(ErrorMessage.TOURNAMENT_ID_EMPTY);

        Tournament tournament = tournamentRepository.getOne(tournamentId);

        User creator = tournament.getCreator();

        if (user != creator)
            throw new TutorException(ErrorMessage.USER_USERNAME_NOT_CREATOR);

        LocalDateTime currentDate = LocalDateTime.now().plusDays(0);
        LocalDateTime beginDate = tournament.getBeginDate();

        if (currentDate.isAfter(beginDate))
            throw new TutorException(ErrorMessage.TOURNAMENT_HAPPENING_OR_ENDED);

        if (tournament.getCanceled())
            throw new TutorException(ErrorMessage.TOURNAMENT_ALREADY_CANCELED);

        tournament.setCanceled(true);
    }
}