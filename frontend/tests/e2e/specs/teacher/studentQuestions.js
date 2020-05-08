describe('Teacher walkthrough', () => {
	beforeEach(() => {
        cy.visit('/')
		cy.get('[data-cy="studentButton"]').click()
		cy.get('[data-cy="submit"]').click()
        cy.get('[data-cy="questionTitle"]').type("tlt")
    	cy.get('[data-cy="questionContent"]').type("qtn")
    	cy.get('[data-cy="optionContent1"]').type("opt1")
    	cy.get('[data-cy="optionContent2"]').type("opt2")
    	cy.get('[data-cy="optionContent3"]').type("opt3")
    	cy.get('[data-cy="optionContent4"]').type("opt4")
    	cy.contains('Option 1 Correct').click()
        cy.get('[data-cy="submitQuestion"]').click()
        cy.contains('Logout').click()

		cy.visit('/')
        cy.get('[data-cy="teacherButton"]').click()
        cy.contains('Management').click()
        cy.contains('Students Questions').click()
        
	})

	afterEach(() => {
		cy.contains('Logout').click()
	})

	it('login approve student question', () => {
        cy.get(':nth-child(1) > :nth-child(3) > .v-input > .v-input__control').click()
        cy.contains("APPROVED").click()
        cy.get('[data-cy="management"]').click()
        cy.get('[data-cy="questions"]').click()
        cy.get('[data-cy="searchText"]').type("tlt")
        cy.get('tbody > tr > :nth-child(1)').contains("tlt")
	});

	it('login reject student question with justification', () => {
		cy.get(':nth-child(2) > :nth-child(4) > .v-input > .v-input__control > .v-input__slot > .v-text-field__slot').type("just")
        cy.get(':nth-child(2) > :nth-child(3) > .v-input > .v-input__control').click()
        cy.contains("REJECTED").click()
    });

    it('login give justification to student question with Depedent Status', () => {
        cy.get(':nth-child(3) > :nth-child(4) > .v-input > .v-input__control > .v-input__slot > .v-text-field__slot').type("just")
        cy.get(':nth-child(3) > :nth-child(3) > .v-input > .v-input__control').click()
        cy.get('[data-cy="management"]').click()
        cy.get('[data-cy="questions"]').click()
        cy.get('[data-cy="management"]').click()
        cy.get('[data-cy="studentQuestions"]').click()
        cy.get('[data-cy="questionJustification"]').contains("just").should('not.exist')
        cy.get('[data-cy="management"]').click()
	});
});
  