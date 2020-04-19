import Topic from '@/models/management/Topic';
import User from '@/models/user/User';
import axios from 'axios';
import Store from '@/store';

export class Tournament {
  id!: number;
  beginDate!: string;
  endDate!: string;
  topics: Topic[] = [];
  enrollments: User[] = [];
  creator!: User;
  numberOfQuestions!: number;
  courseExecution!: string;
  enrolled: Boolean = false;
  constructor(jsonObj?: Tournament, user_id?: number) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.beginDate = jsonObj.beginDate;
      this.endDate = jsonObj.endDate;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.creator = new User(jsonObj.creator);
      console.log('criador ' + jsonObj.creator.username);

      if (jsonObj.topics)
        this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));

      if (jsonObj.enrollments) {
        this.enrollments = jsonObj.enrollments.map((user: User) => new User(user));
        for (var user of this.enrollments) {
          if (user.id == user_id) {
            this.enrolled = true;
          }
        }
      }
    }
  }
}
