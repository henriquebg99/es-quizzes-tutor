import Topic from '@/models/management/Topic';

export class Tournament {
  id!: number;
  beginDate!: string;
  endDate!: string;
  topics!: Topic[];
  numberOfQuestions!: number;

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.beginDate = jsonObj.beginDate;
      this.endDate = jsonObj.endDate;
      this.numberOfQuestions = jsonObj.numberOfQuestions;

      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map(
          (topic: Topic) => new Topic(topic)
        );
      }
    }
  }
}