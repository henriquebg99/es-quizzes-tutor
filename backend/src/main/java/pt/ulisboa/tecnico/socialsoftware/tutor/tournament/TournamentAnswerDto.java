package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class TournamentAnswerDto implements Serializable {
    private int selected, questionId;

    public TournamentAnswerDto() {}

    public TournamentAnswerDto(TournamentAnswer answer) {
        this.selected = answer.getSelectedAnswer();
        this.questionId = answer.getQuestion().getId();
    }

    @Override
    public String toString() {
        return "TournamentAnswerDto{}";
    }

    public int getSelected() {
        return selected;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}