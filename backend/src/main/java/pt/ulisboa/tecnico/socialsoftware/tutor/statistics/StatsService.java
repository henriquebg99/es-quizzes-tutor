package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.*;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;

@Service
public class StatsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatsDto getStats(String username, int executionId) {
        if (username == null)
            throw new TutorException(ErrorMessage.USERNAME_EMPTY);

        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new TutorException(ErrorMessage.USERNAME_NOT_FOUND, username);

        StatsDto statsDto = new StatsDto();

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        if (!courseExecution.getUsers().contains(user)) {
            throw new TutorException(ErrorMessage.USER_NOT_IN_COURSE_EXECUTION);
        }

        int totalQuizzes = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(courseExecution))
                .count();

        int totalQuizzesAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(courseExecution))
                .map(QuizAnswer::getQuestionAnswers)
                .mapToLong(Collection::size)
                .sum();

        int uniqueQuestions = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(courseExecution))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getId)
                .distinct().count();

        int correctAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(courseExecution))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect).count();

        int uniqueCorrectAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(courseExecution))
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getId()))),
                        ArrayList::new)).stream()
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect)
                .count();

        int totalTournaments = (int) user.getEnrolledTournaments().stream().
                count();

        int totalCreatedTournaments = (int) courseExecution.getTournaments().stream()
                .filter(tournament -> tournament.isCreator(user))
                .count();

        Course course = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId)).getCourse();

        int totalAvailableQuestions = questionRepository.getAvailableQuestionsSize(course.getId());

        statsDto.setTotalQuizzes(totalQuizzes);
        statsDto.setTotalAnswers(totalQuizzesAnswers);
        statsDto.setTotalUniqueQuestions(uniqueQuestions);
        statsDto.setTotalAvailableQuestions(totalAvailableQuestions);
        statsDto.setTotalTournaments(totalTournaments);
        statsDto.setTotalCreatedTournaments(totalCreatedTournaments);

        if (totalQuizzesAnswers != 0) {
            statsDto.setCorrectAnswers(((float)correctAnswers)*100/totalQuizzesAnswers);
            statsDto.setImprovedCorrectAnswers(((float)uniqueCorrectAnswers)*100/uniqueQuestions);
        }

        return statsDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatsDto setPrivacy(String username, int executionId, Boolean privacy) {
        if (username == null)
            throw new TutorException(ErrorMessage.USERNAME_EMPTY);

        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new TutorException(ErrorMessage.USERNAME_NOT_FOUND, username);

        StatsDto statsDto = new StatsDto();

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        if (!courseExecution.getUsers().contains(user)) {
            throw new TutorException(ErrorMessage.USER_NOT_IN_COURSE_EXECUTION);
        }

        statsDto.setPrivacy(privacy);

        return statsDto;
    }
}
