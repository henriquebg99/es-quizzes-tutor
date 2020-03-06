package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateTournamentTest extends Specification {
    public static final String NAME = "topic1"

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    TopicRepository topicRepository

    def formatter
    def beginDate
    def endDate
    def topics
    def emptyTopics

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        beginDate = LocalDateTime.now().plusDays(1)
        endDate = LocalDateTime.now().plusDays(2)

        //topic respoitory; id vs name? save? relations between topic and topicDto

        def topic = new Topic()
        topicRepository.save(topic)

        def topicDto = new TopicDto()
        topicDto.setName(NAME)

        topics = new ArrayList()
        topics.add(topicDto)

        emptyTopics = new ArrayList()
    }

    def 'create a tournament' () {
        given: 'a valid tournament'
        def tournament = new TournamentDto ()
        tournament.setBeginDate(beginDate.format(formatter))
        expect: false
    }

    def 'create a tournament with begin date empty' () {
        //exception expected
        expect: true
    }

    def 'create a tournament with end date empty' () {
        //exception expected
        expect: true
    }

    def 'create a tournament with a negative number of questions' () {
        //exception expected
        expect: true
    }

    def 'create a tournament with 0 questions' () {
        //exception expected
        expect: true
    }

    def 'create a tournament without topics' () {
        //exception expected
        expect: true
    }

    def 'create a tournament with not existing topics' () {
        //exception expected
        expect: true
    }

    def 'create a tournament with beginDate after endDate' () {
        expect:true
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}

