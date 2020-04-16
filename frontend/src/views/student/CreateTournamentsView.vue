<template>
  <v-card class="table">
    <v-card-title>
      <span>Create Tournament</span>
      <v-spacer />
      <v-btn color="primary" dark @click="createTournament()">Create</v-btn>
    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="Begin Date"
            format="yyyy-MM-dd HH:mm"
            v-model="tournament.beginDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
          >
          </v-datetime-picker>
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="End Date"
            format="yyyy-MM-dd HH:mm"
            v-model="tournament.endDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
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
              <v-btn text value="5">5</v-btn>
              <v-btn text value="10">10</v-btn>
              <v-btn text value="20">20</v-btn>
            </v-btn-toggle>
          </v-container>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-data-table
            v-model="this.tournament.topics"
            :headers="headers"
            :items="topics"
            :search="search"
            :single-select="false"
            item-key="name"
            show-select
            class="elevation-1"
            :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
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
  topics: Topic[] | null = null;
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
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');

    /*let t = new Topic();
    t.numberOfQuestions = 9;
    t.name = 'MMA';
    this.topics = [t];*/
  }

  async createTournament() {
    try {
      await RemoteServices.createTournament(this.tournament);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
