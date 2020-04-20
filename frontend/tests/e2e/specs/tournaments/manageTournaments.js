describe('Tournament walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Tournaments').click()
    cy.contains('Logout').click()
  })

  it('create tournament succesfully', () => {
    cy.goToCreateTournaments()
    cy.createTournament()
  });

  it('create tournament without topics', () => {
    cy.goToCreateTournaments()

    // select day 29
    cy.get('#input-69').click();
    cy.get(':nth-child(3) > .v-btn__content > .v-icon').click()
    cy.get('.tab-transition-enter-active > tbody > :nth-child(5) > :nth-child(4) > .v-btn > .v-btn__content').click()
    cy.get('.green--text > .v-btn__content').click()

    // select day 30
    cy.get('#input-74').click();
    cy.get('.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__text > .v-tabs > .v-window > .v-window__container > .v-window-item > .v-picker > .v-picker__body > :nth-child(1) > .v-date-picker-header > :nth-child(3) > .v-btn__content > .v-icon').click()
    cy.get('.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__text > .v-tabs > .v-window > .v-window__container > .v-window-item > .v-picker > .v-picker__body > :nth-child(1) > .v-date-picker-table > table > tbody > :nth-child(6) > td > .v-btn > .v-btn__content').click()
    cy.get('.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__actions > .green--text > .v-btn__content').click()

    // 10 questions
    cy.get('[data-cy="tenNumber"]').click();

    // create it
    cy.get('[data-cy="createButton"]').click()

    //expect error
    cy.closeErrorMessage();
  });

  it('create tournament incorrect dates', () => {
    cy.goToCreateTournaments()

    // select day 29
    cy.get('#input-69').click();
    cy.get(':nth-child(3) > .v-btn__content > .v-icon').click()
    cy.get('.tab-transition-enter-active > tbody > :nth-child(5) > :nth-child(4) > .v-btn > .v-btn__content').click()
    cy.get('.green--text > .v-btn__content').click()

    // 10 questions
    cy.get('[data-cy="tenNumber"]').click();

    // select topic
    cy.get(':nth-child(2) > .text-start > .v-data-table__checkbox > .v-icon').click();

    // create it
    cy.get('[data-cy="createButton"]').click()

    //expect error
    cy.closeErrorMessage();
  });

  it('show available tournaments', () => {
    cy.goToAvailableTournaments()
  });

  it('enroll in tournament', () => {
    cy.goToCreateTournaments()
    cy.createTournament()
    cy.goToAvailableTournaments()
    cy.enrollTournaments()
  });

  it('enroll twice in a tournament', () => {
    cy.goToCreateTournaments()
    cy.createTournament()
    cy.goToAvailableTournaments()
    cy.enrollTournaments()
    cy.enrollTournaments()
    cy.closeErrorMessage()
  });

  it('cancel tournament', () => {
    cy.goToCreateTournaments()
    cy.createTournament()
    cy.goToCancelTournaments()
    cy.cancelTournament()
  });

  it('cancel tournament twice', () => {
    cy.goToCreateTournaments()
    cy.createTournament()
    cy.goToCancelTournaments()
    cy.cancelTournament()
    cy.cancelTournament()
    cy.closeErrorMessage()
  });
});
