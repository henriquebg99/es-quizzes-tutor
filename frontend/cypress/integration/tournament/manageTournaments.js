describe('Tournament walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Tournaments').click()
    cy.contains('Logout').click()
  })

  it('create tournament', () => {
    cy.goToCreateTournaments()
    cy.createTournament()
  });

  it('show available tournaments', () => {
    cy.goToAvailableTournaments()
  });

  it('enroll in tournament', () => {
    cy.goToCreateTournaments()
    //cy.addTournament()
    cy.goToAvailableTournaments()
    cy.enrollTournaments()
  });

  it('cancel tournament', () => {
    cy.goToCreateTournaments()
    cy.addTournament()
    cy.goToCancelTournaments()
    cy.cancelTournament(5)
  });
/*
  it('cancel tournament twice', () => {
    cy.goToCreateTournaments()
    cy.addTournament()
    cy.goToCancelTournaments(5)
    cy.goToCancelTournaments(5)
    cy.closeErrorMessage()
  });

  it('cancel tournament if not creator', () => {
    cy.goToCreateTournaments()
    cy.addTournament()
    cy.goToCreateTournaments()
    cy.goToCancelTournaments(5)
    cy.closeErrorMessage()
  });*/
});
