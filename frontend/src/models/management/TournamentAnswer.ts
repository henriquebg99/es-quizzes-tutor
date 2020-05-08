import Topic from '@/models/management/Topic';
import User from '@/models/user/User';
import axios from 'axios';
import Store from '@/store';

export class TournamentAnswer {
  questionId!: number;
  selected!: number;

  constructor(jsonObj?: TournamentAnswer) {
    if (jsonObj) {
      this.questionId = jsonObj.questionId;
      this.selected = jsonObj.selected;
    }
  }
}
