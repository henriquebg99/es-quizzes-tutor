<template>
  <v-card class="table">
    <v-card-title>
      <span class="headline">Submit Question</span>
      <v-spacer />
			<v-btn color="primary" dark>Upload Image
				<v-file-input
          @change="handleFileUpload($event, item)"
          accept="image/*"
					data-cy="upImage"
        />
			</v-btn>
      <v-btn color="primary" dark @click="createProposedQuestion()" data-cy="submitQuestion">Submit</v-btn>
    </v-card-title>
		<v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field v-model="proposedQuestion.title" label="Question Title" data-cy="questionTitle" />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="proposedQuestion.content"
                label="Question"
								data-cy="questionContent"
              ></v-textarea>
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-switch
                v-model="proposedQuestion.options[0].correct"
                class="ma-4"
                label="Option 1 Correct"
              />
              <v-textarea
                outline
                rows="10"
                v-model="proposedQuestion.options[0].content"
                label="Option 1"
								data-cy="optionContent1"
              ></v-textarea>
            </v-flex>
						<v-flex xs24 sm12 md12>
              <v-switch
                v-model="proposedQuestion.options[1].correct"
                class="ma-4"
                label="Option 2 Correct"
              />
              <v-textarea
                outline
                rows="10"
                v-model="proposedQuestion.options[1].content"
                label="Option 2"
								data-cy="optionContent2"
              ></v-textarea>
            </v-flex>
						<v-flex xs24 sm12 md12>
              <v-switch
                v-model="proposedQuestion.options[2].correct"
                class="ma-4"
                label="Option 3 Correct"
              />
              <v-textarea
                outline
                rows="10"
                v-model="proposedQuestion.options[2].content"
                label="Option 3"
								data-cy="optionContent3"
              ></v-textarea>
            </v-flex>
						<v-flex xs24 sm12 md12>
              <v-switch
                v-model="proposedQuestion.options[3].correct"
                class="ma-4"
                label="Option 4 Correct"
              />
              <v-textarea
                outline
                rows="10"
                v-model="proposedQuestion.options[3].content"
                label="Option 4"
								data-cy="optionContent4"
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