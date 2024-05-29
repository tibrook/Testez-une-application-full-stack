describe('Not Found Page', () => {
    it('should display the not found page for an unknown route', () => {
      cy.visit('/unknown-page', { failOnStatusCode: false });
      cy.url().should('include', '/404');
      cy.contains('h1', 'Page not found !');
    });
  });
  