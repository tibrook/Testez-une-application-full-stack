describe('Register spec', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('Register successful', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
    }).as('register');

    cy.get('input[formControlName=firstName]').type("John");
    cy.get('input[formControlName=lastName]').type("Doe");
    cy.get('input[formControlName=email]').type("john.doe@example.com");
    cy.get('input[formControlName=password]').type("test!1234");

    cy.get('button[type=submit]').click();

    cy.wait('@register');

    cy.url().should('include', '/login');
  });

  it('Register with existing email', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400, 
      body: {
        message: 'Error: Email is already taken!'
      },
    }).as('register');

    cy.get('input[formControlName=firstName]').type("Jane");
    cy.get('input[formControlName=lastName]').type("Doe");
    cy.get('input[formControlName=email]').type("jane.doe@example.com");
    cy.get('input[formControlName=password]').type("test!1234");

    cy.get('button[type=submit]').click();

    cy.wait('@register');

    cy.get('.error').should('be.visible').and('contain', 'An error occurred');
  });
  it('Handles server errors gracefully', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 500,
      body: {
        message: 'Internal server error'
      },
    }).as('serverError');

    cy.get('input[formControlName=firstName]').type("John");
    cy.get('input[formControlName=lastName]').type("Doe");
    cy.get('input[formControlName=email]').type("john.doe@example.com");
    cy.get('input[formControlName=password]').type("test!1234");

    cy.get('button[type=submit]').click();

    cy.wait('@serverError');

    cy.get('.error').should('be.visible').and('contain', 'An error occurred');
  });
});
