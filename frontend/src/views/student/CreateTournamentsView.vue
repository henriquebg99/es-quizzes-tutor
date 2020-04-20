<template>
  <v-card class="table">
    <v-card-title>
      <span>Create Tournament</span>
      <v-spacer />
      <v-btn
        color="primary"
        data-cy="createButton"
        dark
        @click="createTournament()"
        >Create</v-btn
      >
    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="Begin Date"
            format="yyyy-MM-dd HH:mm"
            v-model="beginDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
            data-cy="beginPicker"
          >
          </v-datetime-picker>
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="End Date"
            format="yyyy-MM-dd HH:mm"
            v-model="endDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
            data-cy="endPicker"
          >
          </v-datetime-picker>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-container>
            <p class="pl-0">Number of Questions</p>
            <v-btn-toggle
              v-model="tournament.numberOfQuestions"
              mandatory
              class="button-group"
            >
              <v-btn text value="5" data-cy="fiveNumber">5</v-btn>
              <v-btn text value="10" data-cy="tenNumber">10</v-btn>
              <v-btn text value="20" data-cy="twentyNumber">20</v-btn>
            </v-btn-toggle>
          </v-container>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-data-table
            v-model="tournament.topics"
            :headers="headers"
            :items="topics"
            :search="search"
            :single-select="false"
            item-key="name"
            show-select
            class="elevation-1"
            :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
            data-cy="topicsTable"
          >
            <template v-slot:top>
              <span>Select Topics</span>
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field v-model="search" label="Search" class="mx-4" />
                </v-col>
              </v-row>
            </template>
          </v-data-table>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { Tournament } from '@/models/management/Tournament';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class CreateTournamentsView extends Vue {
  tournament: Tournament = new Tournament();
  beginDate: Date = new Date();
  endDate: Date = new Date();
  topics: Topic[] = [];
  search: string = '';
  headers: object = [
    { text: 'Topic', value: 'name', align: 'left', width: '70%' },
    {
      text: 'Number of questions',
      value: 'numberOfQuestions',
      align: 'left',
      width: '20%'
    }
  ];

  async created() {
    await this.init();
  }

  private async init() {
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
      this.beginDate.setHours(this.beginDate.getHours() + 1);
      this.beginDate.setMinutes(0);
      this.endDate.setHours(this.endDate.getHours() + 1);
      this.endDate.setMinutes(0);
      this.endDate.setDate(this.endDate.getDate() + 1);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  static formatDate(date: Date): string {
    let month: string =
      date.getMonth() < 9
        ? '0' + (date.getMonth() + 1)
        : (date.getMonth() + 1).toString();
    let day: string =
      date.getDate() < 10 ? '0' + date.getDate() : date.getDate().toString();
    let hours: string =
      date.getHours() < 10 ? '0' + date.getHours() : date.getHours().toString();
    let minutes: string =
      date.getMinutes() < 10
        ? '0' + date.getMinutes()
        : date.getMinutes().toString();
    return (
      date.getFullYear() + '-' + month + '-' + day + ' ' + hours + ':' + minutes
    );
  }

  async validateInput(): Promise<boolean> {
    if (this.tournament.topics.length == 0) {
      await this.$store.dispatch('error', 'Error: No topics selected.');
      return false;
    }

    let now: Date = new Date();
    if (this.beginDate < now) {
      await this.$store.dispatch('error', 'Error: Begin date has passed.');
      return false;
    }

    if (this.endDate < now) {
      await this.$store.dispatch('error', 'Error: End date has passed.');
      return false;
    }

    if (this.beginDate >= this.endDate) {
      await this.$store.dispatch(
        'error',
        'Error: The end date is not after the begin date.'
      );
      return false;
    }
    return true;
  }

  async createTournament() {
    if (!(await this.validateInput())) return;

    this.tournament.beginDate = CreateTournamentsView.formatDate(
      this.beginDate
    );
    this.tournament.endDate = CreateTournamentsView.formatDate(this.endDate);

    try {
      await RemoteServices.createTournament(this.tournament);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }

    this.$router.push('availableTournaments');
  }
}
</script>
