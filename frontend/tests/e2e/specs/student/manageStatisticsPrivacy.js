describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.goToStats();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('turn stats private', () => {
    cy.setStatsPrivacy();
  });

  it('turn stats private and public again', () => {
    cy.setStatsPrivacy();
    cy.setStatsPrivacy();
  });

  it('turn stats private and public again and private again', () => {
    cy.setStatsPrivacy();
    cy.setStatsPrivacy();
    cy.setStatsPrivacy();
  });
});
