package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {
    ACCESS_DENIED("You do not have permission to view this resource"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),

    COURSE_NOT_FOUND("Course not found with name %s"),
    COURSE_NAME_IS_EMPTY("The course name is empty"),
    COURSE_TYPE_NOT_DEFINED("The course type is not defined"),
    COURSE_EXECUTION_ACRONYM_IS_EMPTY("The course execution acronym is empty"),
    COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY("The course execution academic term is empty"),
    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %s not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    AUTHENTICATION_ERROR("Authentication Error"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),

    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),
    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),
    FENIX_ERROR("Fenix Error"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),

    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS("Can not change correct option of answered question"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    QUIZ_NOT_CONSISTENT("Field %s of quiz is not consistent"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    NO_CORRECT_OPTION("Question does not have a correct option"),

    QUESTION_MISSING_DATA("Missing information for quiz"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS("Questions can only have 1 correct option"),

    QUESTION_NOT_FOUND("Question not found with id %d"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    CANNOT_OPEN_FILE("Cannot open file"),
    QUIZ_OPTION_MISMATCH("Quiz Question %d does not have option %d"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    TOPIC_NOT_FOUND_NAME("Topic not found with name"),
    USERS_IMPORT_ERROR("Error importing users: %s"),

    USER_NOT_FOUND("User not found with id %d"),
    END_DATE_IS_EMPTY("End date is empty."),
    BEGIN_DATE_IS_EMPTY("Begin date is empty."),
    INVALID_NUMBER_OF_QUESTIONS ("The number of questions is less than one."),
    NO_TOPICS ("No topics."),
    END_DATE_IS_NOT_AFTER_BEGIN_DATE ("The end date is not after the begin date."),

    USER_USERNAME_NOT_CREATOR("A tournament must be canceled by the user that created it."),
    TOURNAMENT_ID_NOT_FOUND("Tournament id not found"),
    TOURNAMENT_HAPPENING("Tournament is still happening"),
    TOURNAMENT_ENDED("Tournament has already ended"),
    TOURNAMENT_ID_EMPTY("Tournament id is empty"),
    USERNAME_EMPTY ("Username is empty"),
    TOURNAMENT_ALREADY_CANCELED("This tournament was already canceled"),
    BEGIN_DATE_HAS_PASSED("The tournament begin date has passed."),
    ALREADY_ENROLLED_IN_TOURNAMENT("The user is already enrolled in the tournament"),
    USER_NOT_IN_COURSE_EXECUTION("The course execution does not contain the user");
    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}