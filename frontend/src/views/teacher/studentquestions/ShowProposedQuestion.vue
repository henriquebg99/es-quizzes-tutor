<template>
  <div>
    <span v-html="convertMarkDown(proposedQuestion.content, proposedQuestion.image)" />
    <ul>
      <li v-for="option in proposedQuestion.options" :key="option.number">
        <span
          v-if="option.correct"
          v-html="convertMarkDown('**[★]** ', null)"
        />
        <span
          v-html="convertMarkDown(option.content, null)"
          v-bind:class="[option.correct ? 'font-weight-bold' : '']"
        />
      </li>
    </ul>
    <br />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import Image from '@/models/management/Image';

@Component
export default class ShowProposedQuestion extends Vue {
  @Prop({ type: ProposedQuestion, required: true }) readonly proposedQuestion!: ProposedQuestion;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
