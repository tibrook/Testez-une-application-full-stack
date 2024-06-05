describe('User Information', () => {
    beforeEach(() => {
        cy.visit('/login');

        cy.intercept('POST', '/api/auth/login', {
          body: {
            id: 1,
            username: 'userName',
            firstName: 'firstName',
            lastName: 'lastName',
            email: 'user@example.com',
            admin: false
          }
        }).as('login');

        cy.get('input[formControlName=email]').type("yoga@studio.com");
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);    
        cy.url().should('include', '/sessions');
    });

    it('should display user information correctly', () => {
        cy.intercept('GET', '/api/user/1', {
            body: {
                id: 1,
                firstName: 'firstName',
                lastName: 'lastName',
                email: 'user@example.com',
                admin: false,
                createdAt: '2024-05-29T14:24:33',
                updatedAt: '2024-05-29T15:20:22',
            }
        }).as('getUserInfo');

        cy.get('span.link').contains('Account').click();
        cy.wait('@getUserInfo');

        cy.get('p').contains('Name: firstName LASTNAME');
        cy.get('p').contains('Email: user@example.com');
        cy.get('p').contains('Create at: May 29, 2024');
        cy.get('p').contains('Last update: May 29, 2024');
    });

    it('should delete user account successfully', () => {
        cy.intercept('GET', '/api/user/1', {
            body: {
                id: 1,
                firstName: 'firstName',
                lastName: 'lastName',
                email: 'user@example.com',
                admin: false,
                createdAt: '2024-05-29T14:24:33',
                updatedAt: '2024-05-29T15:20:22',
            }
        }).as('getUserInfo');

        cy.intercept('DELETE', '/api/user/1', {
            statusCode: 200,
            body: {}
        }).as('deleteUser');

        cy.get('span.link').contains('Account').click();
        cy.wait('@getUserInfo');

        cy.get('button').contains('Detail').click();
        cy.wait('@deleteUser');

        cy.url().should('include', '/');
        cy.get('.mat-snack-bar-container').should('contain', 'Your account has been deleted !');
    });
});
