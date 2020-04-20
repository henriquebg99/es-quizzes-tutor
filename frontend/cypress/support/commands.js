// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />
Cypress.Commands.add('demoAdminLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="adminButton"]').click()
    cy.contains('Administration').click()
    cy.contains('Manage Courses').click()
})

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/')
    cy.contains('student').click()
})

Cypress.Commands.add('goToAvailableTournaments', () => {
    cy.contains('Tournaments').click()
    cy.contains('Available').click()
})

Cypress.Commands.add('goToCreateTournaments', () => {
    cy.contains('Tournaments').click()
    cy.contains('Create').click()
})

Cypress.Commands.add('goToCreatedTournaments', () => {
    cy.contains('Tournaments').click()
    cy.contains('Created').click()
})

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="Name"]').type(name)
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})



Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
    cy.contains('Error')
        .parent()
        .find('button')
        .click()
})

Cypress.Commands.add('deleteCourseExecution', (acronym) => {
    cy.contains(acronym)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="deleteCourse"]')
        .click()
})

Cypress.Commands.add('createFromCourseExecution', (name, acronym, academicTerm) => {
    cy.contains(name)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="createFromCourse"]')
        .click()
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('createTournament', () => {
    // select day 29
    cy.get('#input-69').click();
    cy.get(':nth-child(5) > :nth-child(4) > .v-btn > .v-btn__content').click()
    cy.get('.green--text > .v-btn__content').click()

    //select day 30
    cy.get('#input-74').click();
    cy.get('.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__text > .v-tabs > .v-window > .v-window__container > .v-window-item > .v-picker > .v-picker__body > :nth-child(1) > .v-date-picker-table > table > tbody > :nth-child(5) > :nth-child(5) > .v-btn > .v-btn__content').click()
    cy.get('.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__actions > .green--text > .v-btn__content').click()

    // 10 questions
    cy.get('[data-cy="tenNumber"]').click();

    // select topic
    cy.get(':nth-child(2) > .text-start > .v-data-table__checkbox > .v-icon').click();

    // create it
    cy.get('[data-cy="createButton"]').click()
})