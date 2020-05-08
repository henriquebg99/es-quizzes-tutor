<template>
  <v-container grid-list-md fluid>
    <v-layout column wrap>
      <ol>
        <li
          v-for="proposedQuestion in proposedQuestions"
          :key="proposedQuestion.sequence"
          class="text-left"
        >
          <show-proposed-question :proposedQuestion="proposedQuestion" />
        </li>
      </ol>
    </v-layout>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import ShowProposedQuestion from '@/views/teacher/studentquestions/ShowProposedQuestion.vue';

@Component({
  components: { 'show-proposed-question': ShowProposedQuestion }
})
export default class ShowProposedQuestionList extends Vue {
  @Prop({ type: Array, required: true }) readonly proposedQuestions!: ProposedQuestion[];
}
</script>
