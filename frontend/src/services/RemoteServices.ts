import axios from 'axios';
import Store from '@/store';
import Question from '@/models/management/Question';
import ProposedQuestion from '@/models/management/ProposedQuestion';
import { Quiz } from '@/models/management/Quiz';
import Course from '@/models/user/Course';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import StudentStats from '@/models/statement/StudentStats';
import StatementQuiz from '@/models/statement/StatementQuiz';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import Topic from '@/models/management/Topic';
import { Student } from '@/models/management/Student';
import Assessment from '@/models/management/Assessment';
import AuthDto from '@/models/user/AuthDto';
import StatementAnswer from '@/models/statement/StatementAnswer';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import { Tournament } from '@/models/management/Tournament';
import AnswerTournamentView from '@/views/student/AnswerTournamentView.vue';
import { TournamentAnswer } from '@/models/management/TournamentAnswer';

const httpClient = axios.create();
httpClient.defaults.timeout = 10000;
httpClient.defaults.baseURL = process.env.VUE_APP_ROOT_API;
httpClient.defaults.headers.post['Content-Type'] = 'application/json';
httpClient.interceptors.request.use(
  config => {
    if (!config.headers.Authorization) {
      const token = Store.getters.getToken;

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  error => Promise.reject(error)
);

export default class RemoteServices {
  static async fenixLogin(code: string): Promise<AuthDto> {
    return httpClient
      .get(`/auth/fenix?code=${code}`)
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoStudentLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/student')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoTeacherLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/teacher')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoAdminLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/admin')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getUserStats(): Promise<StudentStats> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/stats`
      )
      .then(response => {
        return new StudentStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setUserStatsPrivacy(privacy: number): Promise<StudentStats> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/stats/${privacy}`
      )
      .then(response => {
        return new StudentStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuestions(): Promise<Question[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/questions`)
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAllProposedQuestions(): Promise<ProposedQuestion[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/listproposedquestions/`
      )
      .then(response => {
        return response.data.map((proposedQuestion: any) => {
          return new ProposedQuestion(proposedQuestion);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportCourseQuestions(): Promise<Blob> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/export`,
        {
          responseType: 'blob'
        }
      )
      .then(response => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream'
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuestions(): Promise<Question[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/available`
      )
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createQuestion(question: Question): Promise<Question> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/`,
        question
      )
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestion(question: Question): Promise<Question> {
    return httpClient
      .put(`/questions/${question.id}`, question)
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuestion(questionId: number) {
    return httpClient.delete(`/questions/${questionId}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async setQuestionStatus(
    questionId: number,
    status: String
  ): Promise<Question> {
    return httpClient
      .post(`/questions/${questionId}/set-status`, status, {})
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static createProposedQuestion(
    proposedQuestion: ProposedQuestion
  ): Promise<ProposedQuestion> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/proposedquestions/`,
        proposedQuestion
      )
      .then(response => {
        return new ProposedQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static uploadImageProposedQuestion(
    file: File,
    proposedQuestionId: number
  ): Promise<string> {
    let formData = new FormData();
    formData.append('file', file);
    return httpClient
      .put(`/proposedquestions/${proposedQuestionId}/image`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then(response => {
        return response.data as string;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeStatusProposedQuestion(
    proposedQuestionId: number,
    status: string,
    justification: string
  ): Promise<ProposedQuestion> {
    let proposedQuestionData = new ProposedQuestion();
    proposedQuestionData.status = status;
    proposedQuestionData.justification = justification;
    return httpClient
      .post(
        `/proposedquestions/${proposedQuestionId}/status`,
        proposedQuestionData
      )
      .then(response => {
        return new ProposedQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async seeJustificationProposedQuestion(
    proposedQuestionId: number
  ): Promise<string> {
    return httpClient
      .get(`/proposedquestions/${proposedQuestionId}/status/justification`)
      .then(response => {
        return response.data as string;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async uploadImage(file: File, questionId: number): Promise<string> {
    let formData = new FormData();
    formData.append('file', file);
    return httpClient
      .put(`/questions/${questionId}/image`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then(response => {
        return response.data as string;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestionTopics(questionId: number, topics: Topic[]) {
    return httpClient.put(`/questions/${questionId}/topics`, topics);
  }

  static async getTopics(): Promise<Topic[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/topics`)
      .then(response => {
        return response.data.map((topic: any) => {
          return new Topic(topic);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuizzes(): Promise<StatementQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/available`
      )
      .then(response => {
        return response.data.map((statementQuiz: any) => {
          return new StatementQuiz(statementQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async generateStatementQuiz(params: object): Promise<StatementQuiz> {
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/generate`,
        params
      )
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getSolvedQuizzes(): Promise<SolvedQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/solved`
      )
      .then(response => {
        return response.data.map((solvedQuiz: any) => {
          return new SolvedQuiz(solvedQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizByQRCode(quizId: number): Promise<StatementQuiz> {
    return httpClient
      .get(`/quizzes/${quizId}/byqrcode`)
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportQuiz(quizId: number): Promise<Blob> {
    return httpClient
      .get(`/quizzes/${quizId}/export`, {
        responseType: 'blob'
      })
      .then(response => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream'
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async startQuiz(quizId: number) {
    return httpClient.get(`/quizzes/${quizId}/start`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async submitAnswer(quizId: number, answer: StatementAnswer) {
    return httpClient
      .post(`/quizzes/${quizId}/submit`, answer)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async concludeQuiz(
    quizId: number
  ): Promise<StatementCorrectAnswer[] | void> {
    return httpClient
      .get(`/quizzes/${quizId}/conclude`)
      .then(response => {
        if (response.data) {
          return response.data.map((answer: any) => {
            return new StatementCorrectAnswer(answer);
          });
        }
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/topics/`,
        topic
      )
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .put(`/topics/${topic.id}`, topic)
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteTopic(topic: Topic) {
    return httpClient.delete(`/topics/${topic.id}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async getNonGeneratedQuizzes(): Promise<Quiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/non-generated`
      )
      .then(response => {
        return response.data.map((quiz: any) => {
          return new Quiz(quiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuiz(quizId: number) {
    return httpClient.delete(`/quizzes/${quizId}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async getQuiz(quizId: number): Promise<Quiz> {
    return httpClient
      .get(`/quizzes/${quizId}`)
      .then(response => {
        return new Quiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizAnswers(quizId: number): Promise<QuizAnswers> {
    return httpClient
      .get(`/quizzes/${quizId}/answers`)
      .then(response => {
        return new QuizAnswers(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveQuiz(quiz: Quiz): Promise<Quiz> {
    if (quiz.id) {
      return httpClient
        .put(`/quizzes/${quiz.id}`, quiz)
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes`,
          quiz
        )
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async getCourseStudents(course: Course) {
    return httpClient
      .get(`/executions/${course.courseExecutionId}/students`)
      .then(response => {
        return response.data.map((student: any) => {
          return new Student(student);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAssessments(): Promise<Assessment[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`
      )
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableAssessments() {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments/available`
      )
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveAssessment(assessment: Assessment) {
    if (assessment.id) {
      return httpClient
        .put(`/assessments/${assessment.id}`, assessment)
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`,
          assessment
        )
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async deleteAssessment(assessmentId: number) {
    return httpClient
      .delete(`/assessments/${assessmentId}`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setAssessmentStatus(
    assessmentId: number,
    status: string
  ): Promise<Assessment> {
    return httpClient
      .post(`/assessments/${assessmentId}/set-status`, status, {
        headers: {
          'Content-Type': 'text/html'
        }
      })
      .then(response => {
        return new Assessment(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async activateCourse(course: Course): Promise<Course> {
    return httpClient
      .post('/courses', course)
      .then(response => {
        return new Course(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getCourses(): Promise<Course[]> {
    return httpClient
      .get('/admin/courses/executions')
      .then(response => {
        return response.data.map((course: any) => {
          return new Course(course);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createCourse(course: Course): Promise<Course> {
    return httpClient
      .post('/admin/courses/executions', course)
      .then(response => {
        return new Course(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteCourse(courseExecutionId: number | undefined) {
    return httpClient
      .delete('/admin/courses/executions/' + courseExecutionId)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportAll() {
    return httpClient
      .get('/admin/export', {
        responseType: 'blob'
      })
      .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        let dateTime = new Date();
        link.setAttribute(
          'download',
          `export-${dateTime.toLocaleString()}.zip`
        );
        document.body.appendChild(link);
        link.click();
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async errorMessage(error: any): Promise<string> {
    if (error.message === 'Network Error') {
      return 'Unable to connect to server';
    } else if (error.message.split(' ')[0] === 'timeout') {
      return 'Request timeout - Server took too long to respond';
    } else if (error.response) {
      return error.response.data.message;
    } else if (error.message === 'Request failed with status code 403') {
      await Store.dispatch('logout');
      return 'Unauthorized access or Expired token';
    } else {
      console.log(error);
      return 'Unknown Error - Contact admin';
    }
  }

  static createTournament(tournament: Tournament): Promise<Tournament> {
    return httpClient
      .post(
        '/student/course/executions/' +
          Store.getters.getCurrentCourse.courseExecutionId +
          '/tournaments/',
        tournament
      )
      .then(response => {
        return new Tournament(response.data, Store.getters.getUser.id);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static availableTournaments(): Promise<Tournament[]> {
    return httpClient
      .get(
        '/student/course/executions/' +
          Store.getters.getCurrentCourse.courseExecutionId +
          '/tournaments/'
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament, Store.getters.getUser.id);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static participationTournaments(): Promise<Tournament[]> {
    return httpClient
      .get(
        '/student/course/executions/' +
          Store.getters.getCurrentCourse.courseExecutionId +
          '/participationTournaments/'
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament, Store.getters.getUser.id);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static enrollTournament(id: number) {
    return httpClient
      .put(
        '/student/course/executions/' +
          Store.getters.getCurrentCourse.courseExecutionId +
          '/tournaments/' +
          id +
          '/enroll/'
      )
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static cancelTournament(tournamentId: number) {
    return httpClient
      .post(
        '/student/course/executions/' +
          Store.getters.getCurrentCourse.courseExecutionId +
          '/tournaments/' +
          tournamentId +
          '/cancel/'
      )
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static closedTournaments(): Promise<Tournament[]> {
    return httpClient
      .get(
        '/student/course/executions/' +
          Store.getters.getCurrentCourse.courseExecutionId +
          '/closedTournaments/'
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament, Store.getters.getUser.id);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async listQuestions(tournamentId: number): Promise<Question[]> {
    return httpClient
      .get(
        '/student/course/executions/' +
          Store.getters.getCurrentCourse.courseExecutionId +
          '/tournaments/' +
          tournamentId +
          '/questions'
      )
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async listAnswers(tournamentId: number): Promise<TournamentAnswer[]> {
    return httpClient
      .get(
        '/student/course/executions/' +
          Store.getters.getCurrentCourse.courseExecutionId +
          '/tournaments/' +
          tournamentId +
          '/answers'
      )
      .then(response => {
        return response.data.map((answer: any) => {
          return new TournamentAnswer(answer);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async submitTournamentAnswer(
    tournamentId: number,
    answer: TournamentAnswer
  ) {
    return httpClient
      .put(
        '/student/course/executions/' +
          Store.getters.getCurrentCourse.courseExecutionId +
          '/tournaments/' +
          tournamentId +
          '/submit',
        answer
      )
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }
}
