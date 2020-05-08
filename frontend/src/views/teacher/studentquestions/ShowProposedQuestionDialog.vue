<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ proposedQuestion.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-question :proposedQuestion="proposedQuestion" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('dialog')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import ShowProposedQuestion from '@/views/teacher/studentquestions/ShowProposedQuestion.vue';

@Component({
  components: {
    'show-proposed-question': ShowProposedQuestion
  }
})
export default class ShowProposedQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ProposedQuestion, required: true }) readonly proposedQuestion!: ProposedQuestion;
}
</script>
