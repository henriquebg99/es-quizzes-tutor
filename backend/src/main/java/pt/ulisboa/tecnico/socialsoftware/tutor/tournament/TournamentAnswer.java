package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name="tournament_answers")
public class TournamentAnswer {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int selectedAnswer;

    public void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public TournamentAnswer(Tournament tournament, Question question, User user, int selectedAnswer) {
        this.tournament = tournament;
        this.question = question;
        this.user = user;
        this.selectedAnswer = selectedAnswer;
    }

    public Integer getId() {
        return id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Question getQuestion() {
        return question;
    }

    public User getUser() {
        return user;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }
}
