package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "conclusion_date")
    private LocalDateTime conclusionDate;

    public Tournament() {
    }
}
