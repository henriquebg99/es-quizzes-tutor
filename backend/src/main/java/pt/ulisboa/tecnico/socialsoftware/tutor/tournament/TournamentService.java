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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private TournamentAnswerRepository tournamentAnswerRepository;

    @Autowired
    private QuestionRepository questionRepository;

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
        LocalDateTime[] dates = checkDates(tournamentDto, formatter);
        Tournament tournament = new Tournament (user, tournamentDto, courseExecution);

        tournament.setBeginDate(dates[0]);
        tournament.setEndDate(dates[1]);
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

    private LocalDateTime[] checkDates(TournamentDto tournamentDto, DateTimeFormatter formatter) {
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

        LocalDateTime[] dates = new LocalDateTime[2];
        dates[0] = beginDate;
        dates[1] = endDate;
        return dates;
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
        if(tournamentId == null)
            throw  new TutorException(ErrorMessage.TOURNAMENT_ID_EMPTY);

        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new TutorException(ErrorMessage.USERNAME_NOT_FOUND, username);

        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament == null) {
            throw new TutorException(ErrorMessage.TOURNAMENT_ID_NOT_FOUND);
        }

        CourseExecution course_execution= tournament.getCourseExecution();
        if (!course_execution.getUsers().contains(user)) {
            throw new TutorException(ErrorMessage.USER_NOT_IN_COURSE_EXECUTION);
        }

        if (tournament.getEndDate().isAfter(date) && !tournament.getCanceled()) {
            user.addEnrolledTournament(tournament);
            tournament.addEnrollment(user);
        }
        if (tournament.getEndDate().isBefore(date)){
            throw new TutorException(ErrorMessage.TOURNAMENT_ENDED);
        }
        if (tournament.getCanceled()) {
            throw new TutorException(ErrorMessage.TOURNAMENT_ALREADY_CANCELED);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listOpenTournaments(int courseExecutionId) {
        LocalDateTime date = LocalDateTime.now().plusDays(0);

        return tournamentRepository.findTournaments(courseExecutionId).stream()
                .filter(tournament -> date.isBefore(tournament.getEndDate()))
                .filter(tournament -> !tournament.getCanceled())
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listClosedTournaments(int courseExecutionId) {
        LocalDateTime date = LocalDateTime.now().plusDays(0);

        return tournamentRepository.findTournaments(courseExecutionId).stream()
                .filter(tournament -> date.isAfter(tournament.getEndDate()))
                .filter(tournament -> !tournament.getCanceled())
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listParticipationTournaments(String username, int courseExecutionId) {
        //lista torneios que estão a decorrer e em que o utilizador está inscrito
        LocalDateTime date = LocalDateTime.now().plusDays(0);

        if (username == null)
            throw new TutorException(ErrorMessage.USERNAME_EMPTY);

        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new TutorException(ErrorMessage.USERNAME_NOT_FOUND, username);

        return tournamentRepository.findTournaments(courseExecutionId).stream()
                .filter(tournament -> date.isBefore(tournament.getEndDate()))
                .filter(tournament -> date.isAfter(tournament.getBeginDate()))
                .filter(tournament -> !tournament.getCanceled())
                .filter(tournament -> tournament.userIsEnrolled(user.getId()))
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament (String username, Integer tournamentId) {

        if (username == null)
            throw new TutorException(ErrorMessage.USERNAME_EMPTY);
        if (tournamentId == null)
            throw new TutorException(ErrorMessage.TOURNAMENT_ID_EMPTY);

        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new TutorException(ErrorMessage.USERNAME_NOT_FOUND, username);

        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament == null)
            throw new TutorException(ErrorMessage.TOURNAMENT_ID_NOT_FOUND);

        User creator = tournament.getCreator();
        if (user.getId() != creator.getId())
            throw new TutorException(ErrorMessage.USER_USERNAME_NOT_CREATOR);

        LocalDateTime currentDate = LocalDateTime.now().plusDays(0);
        LocalDateTime beginDate = tournament.getBeginDate();
        LocalDateTime endDate = tournament.getEndDate();

        if (currentDate.isAfter(beginDate)) {
            if (currentDate.isBefore(endDate)) {
                throw new TutorException(ErrorMessage.TOURNAMENT_HAPPENING);
            } else {
                throw new TutorException(ErrorMessage.TOURNAMENT_ENDED);
            }
        }

        if (tournament.getCanceled())
            throw new TutorException(ErrorMessage.TOURNAMENT_ALREADY_CANCELED);

        tournament.setCanceled(true);

        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> listQuestions(String username, int tournamentId) {
        User user = getAndCheckUser(username);
        Tournament tournament = getAndCheckTournament(tournamentId);

        return tournament.listQuestions(user);
    }

    private User getAndCheckUser(String username) {
        // check username
        if (username == null)
            throw new TutorException(ErrorMessage.USERNAME_EMPTY);

        // find user
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new TutorException(ErrorMessage.USERNAME_NOT_FOUND);
        return user;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentAnswerDto> listAnswers(String username, int tournamentId) {
        User user = getAndCheckUser(username);
        Tournament tournament = getAndCheckTournament(tournamentId);

        return tournament.listAnswers(user);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void submitAnswer(String username, int tournamentId, TournamentAnswerDto answerDto) {
        User user = getAndCheckUser(username);
        Tournament tournament = getAndCheckTournament(tournamentId);

        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND));

        tournament.addTournamentAnswer(question, user, answerDto.getSelected());
    }

    private Tournament getAndCheckTournament(int tournamentId) {
        return tournamentRepository.findById(tournamentId).orElseThrow(
                () -> new TutorException(ErrorMessage.TOURNAMENT_ID_NOT_FOUND));
    }
}