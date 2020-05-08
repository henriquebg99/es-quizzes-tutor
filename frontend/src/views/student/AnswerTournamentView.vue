<template>
  <div style="height: 100%">
    <v-row>
      <v-btn-toggle
        v-model="currentQuestionIndex"
        mandatory
        class="button-group"
      >
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
          v-if="questions.length > 0 && questions[currentQuestionIndex] != undefined"
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
  options: Option[] = [];
  headers: object = [
    { text: 'Option', value: 'content', align: 'left', width: '70%' }
  ];

  public static tournament: Tournament;
  public static setTournament(tournament: Tournament): void {
    AnswerTournamentView.tournament = tournament;
  }

  async created() {
    alert(AnswerTournamentView.tournament);
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

    alert(
      'Objects received: ' + this.questions.length + ' ' + this.answers.length
    );

    alert(this.questions[0]);
    alert(this.questions[0].options);
    await this.$store.dispatch('clearLoading');
  }

  increaseOrder(): void {}

  decreaseOrder(): void {}

  changeOrder(newOrder: number): void {}

  confirmAnswer() {}

  async changeAnswer(optionId: number) {}

  async endQuiz() {}

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
  change(index: number) {
    this.currentQuestionIndex = index - 1;
    alert(this.questions[this.currentQuestionIndex]);
  }
}
</script>
