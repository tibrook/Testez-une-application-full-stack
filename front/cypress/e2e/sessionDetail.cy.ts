
const TEST_SESSION = {
    id: 1,
    name: 'Yoga Session',
    date: '2023-06-01T13:27:22.000+00:00',
    teacher_id: 1,
    description: 'A relaxing yoga session.',
    users: [2],
    createdAt: '2024-01-13T14:24:33',
    updatedAt: '2024-01-26T09:20:22',
  };
const TEACHERS_LIST = [
  {
      id: 1,
      lastName: 'JOHN',
      firstName: 'Doe',
      createdAt: '2024-05-15T15:33:42',
      updatedAt: '2024-05-18T15:33:42',
  },
  {
      id: 2,
      lastName: 'Johny',
      firstName: 'Doette',
      createdAt: '2024-05-15T15:33:42',
      updatedAt: '2024-05-18T15:33:42',
  },
];
const SESSIONS_LIST = [TEST_SESSION];

describe('Session Detail Page', () => {
    beforeEach(() => {
      cy.intercept('GET', '/api/session', (req) => {
        req.reply(SESSIONS_LIST);
      });
      cy.intercept('GET', `/api/session/${TEST_SESSION.id}`, TEST_SESSION);

      cy.intercept('GET', `/api/teacher`, TEACHERS_LIST);
        cy.intercept(
        'GET',
        `/api/teacher/${TEACHERS_LIST[0].id}`,
        TEACHERS_LIST[0]
        );
      cy.login();

    });
  
    it('should display session details', () => {
      cy.get('button[mat-raised-button] span').contains('Detail').click();

      cy.contains('h1', 'Yoga Session');
      cy.contains('span', 'Doe JOHN');
      cy.contains('div', 'A relaxing yoga session.');
      cy.contains('span', '1 attendees');
    });
  
    it('should allow admin to delete session', () => {
   
      cy.get('button[mat-raised-button] span').contains('Detail').click();
      cy.intercept('DELETE', '/api/session/1', {
        statusCode: 200,
        body: {}
      }).as('deleteSession');
  
      cy.contains('span', 'Delete').should('be.visible').click();
      cy.wait('@deleteSession');
      cy.url().should('include', '/sessions');
      cy.get('.mat-snack-bar-container').should('contain', 'Session deleted !');
    });
  });
  