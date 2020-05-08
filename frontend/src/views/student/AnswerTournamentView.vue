<template>
  <div style="height: 100%">
    <v-card>
      <v-row>
        <v-btn color="primary" data-cy="endButton" dark @click="exit()"
          >End</v-btn
        >
      </v-row>
      <v-row>
        <v-btn-toggle mandatory class="button-group">
          <v-btn
            v-for="index in questions.length"
            :key="index"
            value="index"
            @click="change(index)"
            >{{ index }}</v-btn
          >
        </v-btn-toggle>
      </v-row>
      <v-row>
        <v-col>
          <v-data-table
            v-if="questions.length > 0"
            v-model="selectedOptions[currentQuestionIndex]"
            :items="questions[currentQuestionIndex].options"
            :single-select="true"
            :headers="headers"
            show-select
            class="elevation-1"
          >
            <template v-slot:top>
              <span>{{ questions[currentQuestionIndex].content }}</span>
            </template>
          </v-data-table>
        </v-col>
      </v-row>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/management/Tournament';
import Question from '@/models/management/Question';
import { TournamentAnswer } from '@/models/management/TournamentAnswer';
import Option from '@/models/management/Option';

@Component
export default class AnswerTournamentView extends Vue {
  questions: Question[] = [];
  answers: TournamentAnswer[] = [];
  currentQuestionIndex: number = 0;
  selectedOptions: Option[][] = [];
  headers: object = [
    { text: 'Option', value: 'content', align: 'left', width: '70%' }
  ];

  public static tournament: Tournament;
  public static setTournament(tournament: Tournament): void {
    AnswerTournamentView.tournament = tournament;
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.answers = await RemoteServices.listAnswers(
        AnswerTournamentView.tournament.id
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    try {
      this.questions = await RemoteServices.listQuestions(
        AnswerTournamentView.tournament.id
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }

    // populate
    for (let i = 0; i < this.questions.length; i++) {
      this.selectedOptions.push([]);
    }

    // let's fill with old answers
    this.fillWithOldAnswers();

    await this.$store.dispatch('clearLoading');
  }

  searchAnswer(questionId: number): TournamentAnswer | null {
    for (let answer of this.answers) {
      if (answer.questionId == questionId) return answer;
    }
    return null;
  }

  searchOption(optionId: number): number | null {
    let options = this.questions[this.currentQuestionIndex].options;
    for (let optionIndex = 0; optionIndex < options.length; optionIndex++) {
      if (options[optionIndex].id == optionId) return optionIndex;
    }
    return null;
  }

  async change(index: number) {
    await this.submitAnswer();
    this.currentQuestionIndex = index - 1;
  }

  async submitAnswer() {
    let selectedToThisQuestion: Option[] = this.selectedOptions[
      this.currentQuestionIndex
    ];
    // check if the user answered
    if (selectedToThisQuestion.length == 1) {
      if (selectedToThisQuestion[0].id != null) {
        let index: number | null = this.searchOption(
          selectedToThisQuestion[0].id
        );

        if (index != null) {
          // build the answer
          let answer = this.makeAnswer(
            this.questions[this.currentQuestionIndex].id as number,
            index
          );

          // request
          await this.doSubmit(answer);
        }
      }
    }
  }

  makeAnswer(questionId: number, selected: number): TournamentAnswer {
    let answer: TournamentAnswer = new TournamentAnswer();
    answer.questionId = questionId;
    answer.selected = selected;

    return answer;
  }

  async doSubmit(answer: TournamentAnswer) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.submitTournamentAnswer(
        AnswerTournamentView.tournament.id,
        answer
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }

    await this.$store.dispatch('clearLoading');
  }

  fillWithOldAnswers() {
    for (let i = 0; i < this.questions.length; i++) {
      let answer: TournamentAnswer | null = this.searchAnswer(
        this.questions[i].id as number
      );
      if (answer != null)
        this.selectedOptions[i].push(
          this.questions[i].options[answer.selected]
        );
    }
  }

  async exit() {
    await this.submitAnswer();
    await this.$router.push('availableTournaments');
  }
}
</script>
