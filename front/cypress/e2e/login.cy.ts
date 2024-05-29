describe('Login spec', () => {
  beforeEach(() => {
    cy.visit('/login')
  });

  it('Login successful', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session');

    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);

    cy.url().should('include', '/sessions');
  });

  it('Login unsuccessful', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {
        message: 'Bad credentials'
      },
    }).as('loginFailure');

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session');

    cy.get('input[formControlName=email]').type("wrong@user.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);

    cy.wait('@loginFailure');

    cy.get('.error').should('be.visible').and('contain', 'An error occurred');
  });

  it('Toggle password visibility', () => {
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'password');

    cy.get('button[aria-label="Hide password"]').click();
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'text');

    cy.get('button[aria-label="Hide password"]').click();
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'password');
  });

  it('Login form validation', () => {
    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=email]').type("invalid-email");
    cy.get('input[formControlName=password]').type("short");
    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=email]').clear().type("valid@example.com");
    cy.get('input[formControlName=password]').clear().type("validpassword");
    cy.get('button[type=submit]').should('not.be.disabled');
  });

  it('Handles server errors gracefully', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 500,
      body: {
        message: 'Internal server error'
      },
    }).as('serverError');

    cy.get('input[formControlName=email]').type("server@error.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);

    cy.wait('@serverError');

    cy.get('.error').should('be.visible').and('contain', 'An error occurred');
  });
});
