# Technical Debt

## PASO Server
1. Tests are not executable

## Part List Service
1. Tests use outdated date (incomplete data)
2. Tests rely on embedded MongoDB and are not executable on Bamboo
   1. Is a "real" DB required during test execution?
   2. If a real DB is required, would TestContainers be a solution?

## Refactoring Ideas

