import Topic from '@/models/management/Topic';
import User from '@/models/user/User';
import Store from '@/store';

export class Tournament {
  id!: number;
  beginDate!: string;
  endDate!: string;
  topics: Topic[] = [];
  enrollments: User[] = [];
  numberOfQuestions!: number;
  courseExecution!: string;
  enrolled: Boolean = false;
  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.beginDate = jsonObj.beginDate;
      this.endDate = jsonObj.endDate;
      this.numberOfQuestions = jsonObj.numberOfQuestions;

console.log('TORNEIO');

      if (jsonObj.topics)
        this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));

      if (!jsonObj.enrollments) {
      console.log('enrollments vazio');}
      if (jsonObj.enrollments) {
        this.enrollments = jsonObj.enrollments.map((user: User) => new User(user));
        console.log('student id' + Store.getters.getUser.id);
        for (var user of this.enrollments) {
          if (user.id == Store.getters.getUser.id) {
            console.log('user.id' + user.id);
            this.enrolled = true;
          }
        }
      }
    }
  }
}
