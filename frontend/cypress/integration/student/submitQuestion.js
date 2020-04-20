describe('Student walkthrough', () => {
	beforeEach(() => {
		cy.demoStudentLogin()
	})

	afterEach(() => {
		cy.contains('Logout').click()
	})

	it('login submit a question', () => {
		cy.get('[data-cy="questionTitle"]').type("tlt")
    	cy.get('[data-cy="questionContent"]').type("qtn")
    	cy.get('[data-cy="optionContent1"]').type("opt1")
    	cy.get('[data-cy="optionContent2"]').type("opt2")
    	cy.get('[data-cy="optionContent3"]').type("opt3")
    	cy.get('[data-cy="optionContent4"]').type("opt4")
    	cy.contains('Option 1 Correct').click()
		cy.get('[data-cy="submitQuestion"]').click()
	});

	it('login submit a question without options', () => {
		cy.get('[data-cy="questionTitle"]').type("tlt")
    	cy.get('[data-cy="questionContent"]').type("qtn")
		cy.get('[data-cy="submitQuestion"]').click()
		cy.closeErrorMessage()
	});

	it('login submit a question without title/content', () => {
		cy.get('[data-cy="optionContent1"]').type("opt1")
    	cy.get('[data-cy="optionContent2"]').type("opt2")
    	cy.get('[data-cy="optionContent3"]').type("opt3")
    	cy.get('[data-cy="optionContent4"]').type("opt4")
    	cy.contains('Option 1 Correct').click()
		cy.get('[data-cy="submitQuestion"]').click()
		cy.closeErrorMessage()
	});

	it('login submit a question without a correct option', () => {
		cy.get('[data-cy="questionTitle"]').type("tlt")
		cy.get('[data-cy="questionContent"]').type("qtn")
		cy.get('[data-cy="optionContent1"]').type("opt1")
		cy.get('[data-cy="optionContent2"]').type("opt2")
		cy.get('[data-cy="optionContent3"]').type("opt3")
		cy.get('[data-cy="optionContent4"]').type("opt4")
		cy.get('[data-cy="submitQuestion"]').click()
		cy.closeErrorMessage()
	});

	it('login submit a question with 2 correct option', () => {
		cy.get('[data-cy="questionTitle"]').type("tlt")
    	cy.get('[data-cy="questionContent"]').type("qtn")
		cy.get('[data-cy="optionContent1"]').type("opt1")
    	cy.get('[data-cy="optionContent2"]').type("opt2")
    	cy.get('[data-cy="optionContent3"]').type("opt3")
		cy.get('[data-cy="optionContent4"]').type("opt4")
		cy.contains('Option 1 Correct').click()
		cy.contains('Option 2 Correct').click()
		cy.get('[data-cy="submitQuestion"]').click()
		cy.closeErrorMessage()
	});

	it('login submit a question with a image', () => {
		cy.get('[data-cy="questionTitle"]').type("tlt")
    	cy.get('[data-cy="questionContent"]').type("qtn")
    	cy.get('[data-cy="optionContent1"]').type("opt1")
    	cy.get('[data-cy="optionContent2"]').type("opt2")
    	cy.get('[data-cy="optionContent3"]').type("opt3")
    	cy.get('[data-cy="optionContent4"]').type("opt4")
		cy.contains('Option 1 Correct').click()
		cy.get('[data-cy="upImage"]').fixture("./question.jpg")
		cy.get('[data-cy="submitQuestion"]').click()
	});

});
  