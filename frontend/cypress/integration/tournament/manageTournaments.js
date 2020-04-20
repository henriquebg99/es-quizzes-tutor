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
    //cy.addTournament()
    cy.goToCreatedTournaments()
    //cy.cancelTournament()
  });
});
