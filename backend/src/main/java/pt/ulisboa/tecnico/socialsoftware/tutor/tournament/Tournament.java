package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "begin_date")
    private LocalDateTime beginDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    public Tournament() {
    }
}
