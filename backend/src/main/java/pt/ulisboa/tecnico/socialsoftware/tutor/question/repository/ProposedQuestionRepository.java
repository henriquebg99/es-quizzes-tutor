package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;

import java.util.List;

@Repository
@Transactional
public interface ProposedQuestionRepository extends JpaRepository<ProposedQuestion, Integer> {
    @Query(value = "SELECT * FROM proposed_questions pq WHERE pq.course_id = :courseId", nativeQuery = true)
    List<ProposedQuestion> findProposedQuestions(int courseId);

    @Query(value = "SELECT * FROM proposed_questions pq WHERE pq.key = :key", nativeQuery = true)
    ProposedQuestion findProposedQuestionByKey(Integer key);

    @Query(value = "SELECT MAX(key) FROM proposed_questions", nativeQuery = true)
    Integer getMaxProposedQuestionNumber();

}
