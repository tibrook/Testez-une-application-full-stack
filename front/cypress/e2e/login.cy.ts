describe('Login spec', () => {
  beforeEach(() => {
    cy.visit('/login')
  })
  it('Login successfull', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')
  })
  it('Login unsuccessful', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401, 
      body: {
        message: 'Bad credentials'
      },
    }).as('loginFailure')

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("wrong@user.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.wait('@loginFailure')

    cy.get('.error').should('be.visible').and('contain', 'An error occurred')
  })
  it('Toggle password visibility', () => {
    // Vérification de la visibilité initiale du mot de passe (caché)
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'password')

    // Changement de la visibilité du mot de passe
    cy.get('button[aria-label="Hide password"]').click()
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'text')

    // Retour à la visibilité cachée
    cy.get('button[aria-label="Hide password"]').click()
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'password')
  })
  it('Login form validation', () => {
    // Vérification que le bouton de soumission est désactivé lorsque le formulaire est vide
    cy.get('button[type=submit]').should('be.disabled')

    // Remplissage d'un champ incorrect et vérification du bouton de soumission
    cy.get('input[formControlName=email]').type("invalid-email")
    cy.get('input[formControlName=password]').type("short")
    cy.get('button[type=submit]').should('be.disabled')

    // Correction des champs et vérification du bouton de soumission
    cy.get('input[formControlName=email]').clear().type("valid@example.com")
    cy.get('input[formControlName=password]').clear().type("validpassword")
    cy.get('button[type=submit]').should('not.be.disabled')
  })
});