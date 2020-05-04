package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TournamentAnswerRepository extends JpaRepository<TournamentAnswer, Integer> {

    @Query(value = "SELECT * FROM tournament_answers t, WHERE t.tournament_id = :tournamentId AND t.user_id = :userId", nativeQuery = true)
    List<TournamentAnswer> findTournamentAnswers(int tournamentId, int userId);
}
