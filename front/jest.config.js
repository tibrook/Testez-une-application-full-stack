module.exports = {
  moduleNameMapper: {
    "@core/(.*)": "<rootDir>/src/app/core/$1",
  },
  preset: "jest-preset-angular",
  setupFilesAfterEnv: ["<rootDir>/setup-jest.ts"],
  bail: false,
  verbose: false,
  collectCoverage: true,
  coverageDirectory: "./coverage/jest",
  coverageReporters: ["html", "text", "lcov"],
  collectCoverageFrom: [
    "src/app/**/*.{ts,tsx}", // Inclure tous les fichiers TypeScript dans src/app
    "!src/app/**/*.interface.{ts,tsx}", // Exclure les fichiers d'interface
    "!src/app/**/*.spec.{ts,tsx}", // Exclure les fichiers de test
    "!**/node_modules/**", // Exclure le dossier node_modules
  ],
  testPathIgnorePatterns: ["<rootDir>/node_modules/"],
  coveragePathIgnorePatterns: ["<rootDir>/node_modules/"],
  coverageThreshold: {
    global: {
      statements: 80,
    },
  },
  roots: ["<rootDir>"],
  modulePaths: ["<rootDir>"],
  moduleDirectories: ["node_modules"],
};
