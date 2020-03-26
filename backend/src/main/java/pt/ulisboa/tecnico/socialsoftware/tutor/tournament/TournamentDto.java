package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TournamentDto implements Serializable {
    private Integer id;
    private String beginDate = null;
    private String endDate = null;
    private Set<TopicDto> topics = null;
    private int numberOfQuestions;
    private Boolean cancelState;
    private Set<UserDto> enrollements;

    public TournamentDto () {}

    public TournamentDto (Tournament tournament) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.topics = new HashSet<TopicDto>();
        this.id = tournament.getId();
        this.beginDate = tournament.getBeginDate().format(formatter);
        this.endDate = tournament.getEndDate().format(formatter);
        this.numberOfQuestions = tournament.getNumberOfQuestions();
        this.cancelState = tournament.getCanceled();

        for (Topic topic : tournament.getTopics()) {
            TopicDto topicDto = new TopicDto(topic);
            this.addTopic(topicDto);
        }

        for (User user: tournament.getEnrollments()) {
            UserDto userDto = new UserDto(user);
            this.addEnrollement(userDto);
        }
    }

    public Set<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(Set<TopicDto> topics) {
        this.topics = topics;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void addTopic (TopicDto topicDto) {this.topics.add(topicDto);}

    public void addEnrollement (UserDto userDto) {this.enrollements.add(userDto);}

    public Boolean getCancelState () {return this.cancelState;}

    public Set<UserDto> getEnrollements () {return this.enrollements;}

    @Override
    public String toString() {
        return "TournamentDto{}";
    }
}