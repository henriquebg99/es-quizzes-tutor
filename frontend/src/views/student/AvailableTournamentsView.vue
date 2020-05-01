<template>
  <div class="container">
    <h2>Available Tournaments</h2>
    <ul>
      <span class="list-header">
        <div class="col">Number</div>
        <div class="col">Available since</div>
        <div class="col">Available until</div>
        <!-- shoulb be able to see the topics -->
        <div class="col">Enroll</div>
      </span>
      <li
        class="list-row"
        v-for="tournament in tournaments"
        :key="tournament.id"
        @click="enroll(tournament)"
      >
        <div class="col">
          {{ tournament.id }}
        </div>
        <div class="col">
          {{ tournament.beginDate }}
        </div>
        <div class="col">
          {{ tournament.endDate }}
        </div>
        <div class="col last-col">
          <i
            v-bind:class="{
              'fa-square': !tournament.enrolled,
              'fa-check-square': tournament.enrolled
            }"
            class="far enroll"
          ></i>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { Tournament } from '@/models/management/Tournament';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class AvailableTournamentsView extends Vue {
  tournaments: Tournament[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (
        await RemoteServices.availableTournaments()
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async enroll(tournament: Tournament) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.enrollTournament(tournament.id);
      tournament.enrolled = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li, span {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .enroll {
      font-size: 25px;
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>
