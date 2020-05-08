describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('create a tournament and check stats', () => {
    cy.goToCreateTournaments();
    cy.createTournament();
    cy.goToStats();
    cy.checkTotalCreatedTournaments();
  });

  it('enroll in a tournament and check stats', () => {
    cy.goToCreateTournaments();
    cy.createTournament();
    cy.enrollTournaments();
    cy.goToStats();
    cy.checkTotalTournaments();
  });

  it('turn stats private', () => {
    cy.goToStats();
    cy.setStatsPrivacy();
  });

  it('turn stats private and public again', () => {
    cy.goToStats();
    cy.setStatsPrivacy();
    cy.setStatsPrivacy();
  });

  it('turn stats private and public again and private again', () => {
    cy.goToStats();
    cy.setStatsPrivacy();
    cy.setStatsPrivacy();
    cy.setStatsPrivacy();
  });


});
