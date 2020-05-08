describe('Tournament walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Tournaments').click();
    cy.contains('Logout').click();
  });

  it('create tournament succesfully', () => {
    cy.goToCreateTournaments();
    cy.createTournament();
  });

  it('create tournament without topics', () => {
    cy.goToCreateTournaments();

    cy.get('#input-77').click();
    cy.get(':nth-child(5) > :nth-child(6) > .v-btn > .v-btn__content').click();
    cy.get('.v-time-picker-clock__item--active > span').click();
    cy.get(
      '.fade-transition-enter-active > .v-time-picker-clock > .v-time-picker-clock__inner > [style="left: 50%; top: 100%;"] > span'
    ).click();
    cy.get('.green--text > .v-btn__content').click();

    // select day 30
    cy.get('#input-82').click();
    cy.get(
      '.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__text > .v-tabs > .v-window > .v-window__container > .v-window-item > .v-picker > .v-picker__body > :nth-child(1) > .v-date-picker-table > table > tbody > :nth-child(5) > :nth-child(7) > .v-btn > .v-btn__content'
    ).click();
    cy.get(
      '.v-window-item--active > .v-picker > .v-picker__body > .v-time-picker-clock__container > .v-time-picker-clock > .v-time-picker-clock__inner > .v-time-picker-clock__item--active > span'
    ).click();
    cy.get(
      '.fade-transition-enter-active > .v-time-picker-clock > .v-time-picker-clock__inner > [style="left: 50%; top: 100%;"] > span'
    ).click();
    cy.get(
      '.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__actions > .green--text > .v-btn__content'
    ).click();

    // 10 questions
    cy.get('[data-cy="tenNumber"]').click();

    // create it
    cy.get('[data-cy="createButton"]').click();

    //expect error
    cy.closeErrorMessage();
  });

  it('create tournament incorrect dates', () => {
    cy.goToCreateTournaments();

    cy.get('#input-77').click();
    cy.get(':nth-child(5) > :nth-child(6) > .v-btn > .v-btn__content').click();
    cy.get('.v-time-picker-clock__item--active > span').click();
    cy.get(
      '.fade-transition-enter-active > .v-time-picker-clock > .v-time-picker-clock__inner > [style="left: 50%; top: 100%;"] > span'
    ).click();
    cy.get('.green--text > .v-btn__content').click();

    // 10 questions
    cy.get('[data-cy="tenNumber"]').click();

    // select topic
    cy.get(
      ':nth-child(2) > .text-start > .v-data-table__checkbox > .v-icon'
    ).click();

    // create it
    cy.get('[data-cy="createButton"]').click();

    //expect error
    cy.closeErrorMessage();
  });

  it('show available tournaments', () => {
    cy.goToAvailableTournaments();
  });

  it('enroll in tournament', () => {
    cy.goToCreateTournaments();
    cy.createTournament();
    cy.enrollTournaments();
  });

  it('enroll twice in a tournament', () => {
    cy.goToCreateTournaments();
    cy.createTournament();
    cy.enrollTournaments();
    cy.enrollTournaments();
    cy.closeErrorMessage();
  });

  it('cancel tournament', () => {
    cy.goToCreateTournaments();
    cy.createTournament();
    cy.goToCancelTournaments();
    cy.cancelTournament();
  });

  it('cancel tournament twice', () => {
    cy.goToCreateTournaments();
    cy.createTournament();
    cy.goToCancelTournaments();
    cy.cancelTournament();
    cy.cancelTournament();
    cy.closeErrorMessage();
  });
});
