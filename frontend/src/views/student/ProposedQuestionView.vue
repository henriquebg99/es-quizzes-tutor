<template>
  <v-card class="table">
    <v-card-title>
      <span class="headline">Submit Question</span>
      <v-spacer />
			<v-btn color="primary" dark>Upload Image
				<v-file-input
          @change="handleFileUpload($event, item)"
          accept="image/*"
        />
			</v-btn>
      <v-btn color="primary" dark @click="createProposedQuestion()">Submit</v-btn>
    </v-card-title>
		<v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field v-model="proposedQuestion.title" label="Question Title" />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="proposedQuestion.content"
                label="Question"
              ></v-textarea>
            </v-flex>
            <v-flex
              xs24
              sm12
              md12
              v-for="index in proposedQuestion.options.length"
              :key="index"
            >
              <v-switch
                v-model="proposedQuestion.options[index - 1].correct"
                class="ma-4"
                label="Correct"
              />
              <v-textarea
                outline
                rows="10"
                v-model="proposedQuestion.options[index - 1].content"
                label="Option"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

  </v-card>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import Image from '@/models/management/Image';
import Option from '@/models/management/Option';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ProposedQuestionView extends Vue {

  proposedQuestion: ProposedQuestion = new ProposedQuestion();

    headers: object = [
    { text: 'Qestion Title', value: 'title', align: 'center' },
    { text: 'Question', value: 'content', align: 'left' },
    {
      text: 'Image',
      value: 'image',
      align: 'center',
      sortable: false
    },
  ];

  async createProposedQuestion() {
    try {
      await RemoteServices.createProposedQuestion(this.proposedQuestion);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    alert('Question Submited.');
    
    this.proposedQuestion = new ProposedQuestion();
	}
	
	convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
	}
	
	async handleFileUpload(event: File, proposedQuestion: ProposedQuestion) {
    if (proposedQuestion.id) {
      try {
        const imageURL = await RemoteServices.uploadImage(event, proposedQuestion.id);
        proposedQuestion.image = new Image();
        proposedQuestion.image.url = imageURL;
        confirm('Image ' + imageURL + ' was uploaded!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }


}
</script>

<style lang="scss" scoped></style>