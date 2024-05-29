describe('Session Creation', () => {
    beforeEach(() => {
        cy.visit('/login');

        cy.intercept('POST', '/api/auth/login', {
          body: {
            id: 1,
            username: 'userName',
            firstName: 'firstName',
            lastName: 'lastName',
            admin: true
          },
        }).as('login');

        cy.intercept('GET', '/api/session', {
          id: 1,
          name: 'TEST session',
          date: '2024-05-30T13:27:22.000+00:00',
          teacher_id: 1,
          description: 'TEST session',
          users: [2],
          createdAt: '2024-05-29T14:24:33',
          updatedAt: '2024-05-29T15:20:22',
        }).as('getSessions');

        cy.intercept('GET', '/api/teacher', {
          body: [
            { id: 1, firstName: 'John', lastName: 'Doe' },
            { id: 2, firstName: 'Jane', lastName: 'Smith' }
          ]
        }).as('getTeachers');
    
        cy.get('input[formControlName=email]').type("yoga@studio.com");
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
        cy.url().should('include', '/sessions');
    });
  
    it('should create a session successfully', () => {
        cy.get('button[mat-raised-button] span').contains('Create').click();

        cy.intercept('POST', '/api/session', {
            statusCode: 201,
            body: {
            id: 1,
            name: 'Yoga Session',
            date: '2023-06-01',
            teacher_id: 1,
            description: 'A relaxing yoga session.',
            users: [],
            createdAt: '2023-05-01',
            updatedAt: '2023-05-01'
            }
        }).as('createSession');
    
        cy.get('input[formControlName=name]').type('Yoga Session');
        cy.get('input[formControlName=date]').type('2023-06-01');
        cy.get('mat-select[formControlName=teacher_id]').click().get('mat-option').contains('John Doe').click();
        cy.get('textarea[formControlName=description]').type('A relaxing yoga session.');
    
        cy.get('button[type=submit]').click();
    
        cy.wait('@createSession');
    
        cy.url().should('include', '/sessions');
        cy.get('.mat-snack-bar-container').should('contain', 'Session created !');
    });
});
