<template>
  <v-card class="table">
    <v-card-title>
      <span class="headline">Students Submitted Questions</span>
    </v-card-title>

    <v-data-table
      :headers="headers"
      :items="proposedQuestions"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >

      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.content, null)"
          @click="showProposedQuestionDialog(item)"
      /></template>

      <template v-slot:item.status="{ item }">
        <v-select
          v-model="item.status"
          :items="statusList"
          dense
          data-cy="questionStatus"
          @change="statusChange(item.id, item.status, item.justification)"
        >
          <template v-slot:selection="{ item }">
            <v-chip :color="getStatusColor(item)" small data-cy="questionStatus1">
              <span>{{ item }}</span>
            </v-chip>
          </template>
        </v-select>
      </template>

      <template v-slot:item.justification="{ item }">
        <v-textarea
          v-model="item.justification"
          dense
          data-cy="questionJustification"
        />
      </template>

      <template v-slot:item.image="{ item }">
        <v-file-input
          show-size
          dense
          small-chips
          @change="handleFileUpload($event, item)"
          accept="image/*"
        />
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showProposedQuestionDialog(item)"
              >visibility</v-icon
            >
          </template>
          <span>Show Question</span>
        </v-tooltip> 
      </template>

    </v-data-table>
    <show-question-dialog
      v-if="currentQuestion"
      v-model="questionDialog"
      :question="currentQuestion"
      v-on:close-show-question-dialog="onCloseShowProposedQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import Image from '@/models/management/Image';
import ShowProposedQuestionDialog from '@/views/teacher/studentquestions/ShowProposedQuestionDialog.vue';

@Component({
  components: {
    'show-question-dialog': ShowProposedQuestionDialog
  }
})
export default class StudentsQuestionsView extends Vue {
  proposedQuestions: ProposedQuestion[] = [];
  currentQuestion: ProposedQuestion | null = null;
  questionDialog: boolean = false;
  statusList = ['DEPENDENT', 'APPROVED', 'REJECTED'];
  currentStatus: string = 'DEPENDENT';
  currentJustification: string = '';

  headers: object = [
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Question', value: 'content', align: 'center' },
    { text: 'Status', value: 'status', align: 'center', sortable: false },
    { text: 'Justification', value: 'justification', align: 'center', sortable: false },
    { text: 'Image', value: 'image', align: 'center', sortable: false }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.proposedQuestions] = await Promise.all([ RemoteServices.getAllProposedQuestions() ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  statusChange(proposedQuestionId: number, status: string, justification: string) {
    if(justification){
      this.justificationChange(proposedQuestionId, status, justification)
    }
    else {
      this.currentStatus = status;
      this.setStatus(proposedQuestionId);
    }
  }

  justificationChange(proposedQuestionId: number,status: string, justification: string) {
    if ((status == 'APPROVED') || (status == 'REJECTED')){
      this.currentStatus = status;
      this.currentJustification = justification;
      this.setStatus(proposedQuestionId);
    }
  }

  async setStatus(proposedQuestionId: number) {
    try {
      await RemoteServices.changeStatusProposedQuestion(proposedQuestionId, this.currentStatus, this.currentJustification);
      let proposedQuestion = this.proposedQuestions.find(
        proposedQuestion => proposedQuestion.id === proposedQuestionId
      );
      if (proposedQuestion) {
        proposedQuestion.status = status;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  getStatusColor(status: string) {
    if (status === 'REJECTED') return 'red';
    else if (status === 'DEPENDENT') return 'orange';
    else return 'green';
  }

  async handleFileUpload(event: File, proposedQuestion: ProposedQuestion) {
    if (proposedQuestion.id) {
      try {
        const imageURL = await RemoteServices.uploadImageProposedQuestion(event, proposedQuestion.id);
        proposedQuestion.image = new Image();
        proposedQuestion.image.url = imageURL;
        confirm('Image ' + imageURL + ' was uploaded!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  showProposedQuestionDialog(question: ProposedQuestion) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  onCloseShowProposedQuestionDialog() {
    this.questionDialog = false;
  }
}

</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>