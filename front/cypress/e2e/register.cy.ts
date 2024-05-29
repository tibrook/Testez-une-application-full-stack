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

  it('Shows validation errors', () => {
    // Check initial form state
    cy.get('button[type=submit]').should('be.disabled');

    // Trigger validation errors
    cy.get('input[formControlName=firstName]').focus().blur();
    cy.get('mat-error').should('contain', 'First name is required');

    cy.get('input[formControlName=lastName]').focus().blur();
    cy.get('mat-error').should('contain', 'Last name is required');

    cy.get('input[formControlName=email]').focus().blur();
    cy.get('mat-error').should('contain', 'Email is required');

    cy.get('input[formControlName=password]').focus().blur();
    cy.get('mat-error').should('contain', 'Password is required');
  });

  it('Form validation', () => {
    // Fill in invalid values and check validation
    cy.get('input[formControlName=firstName]').type('Jo').blur();
    cy.get('mat-error').should('contain', 'First name must be at least 3 characters long');

    cy.get('input[formControlName=lastName]').type('Do').blur();
    cy.get('mat-error').should('contain', 'Last name must be at least 3 characters long');

    cy.get('input[formControlName=email]').type('invalid-email').blur();
    cy.get('mat-error').should('contain', 'Invalid email address');

    cy.get('input[formControlName=password]').type('12').blur();
    cy.get('mat-error').should('contain', 'Password must be at least 3 characters long');

    // Fill in valid values and check that submit button is enabled
    cy.get('input[formControlName=firstName]').clear().type('John');
    cy.get('input[formControlName=lastName]').clear().type('Doe');
    cy.get('input[formControlName=email]').clear().type('john.doe@example.com');
    cy.get('input[formControlName=password]').clear().type('test!1234');

    cy.get('button[type=submit]').should('not.be.disabled');
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
