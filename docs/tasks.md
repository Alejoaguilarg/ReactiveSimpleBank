# ReactiveSimpleBank Improvement Tasks

This document contains a prioritized list of tasks to improve the ReactiveSimpleBank application. Each task is marked with a checkbox [ ] that can be checked off when completed.

## Code Quality and Consistency

[ ] Fix spelling issues: Rename "Costumer" to "Customer" throughout the codebase (models, services, repositories, controllers)
[ ] Fix package naming inconsistency: Rename "transaction/Service" to "transaction/service" to follow Java conventions
[ ] Fix typo in package name: Rename "costumer/respository" to "costumer/repository"
[ ] Standardize method modifiers in repository interfaces (remove unnecessary "public" modifiers)
[ ] Fix inconsistent parameter naming (e.g., "CostumerId" vs "costumerId" in Costumer constructor)
[ ] Fix typos in error messages (e.g., "There's no exits bank account" to "There's no existing bank account")
[ ] Remove hardcoded service names in log messages and exception handlers
[ ] Make exception handler methods public instead of private

## Architecture and Design

[ ] Complete API implementations for all controllers (add missing CRUD endpoints)
[ ] Add table names to @Table annotations in Transaction and Costumer entities
[ ] Create DTOs for all request/response objects to decouple API from domain models
[ ] Implement proper validation for all input data using Bean Validation
[ ] Use appropriate data types for monetary values (BigDecimal instead of Long)
[ ] Create an enum for transaction types instead of using String
[ ] Fix logical errors in service methods (e.g., update method in CostumerService)
[ ] Remove arbitrary limits (e.g., 100 transactions in TransactionService.getFullResume)
[ ] Implement pagination for endpoints that return collections
[ ] Add proper documentation (Javadoc, OpenAPI/Swagger)

## Error Handling

[ ] Use appropriate HTTP status codes in exception handlers
[ ] Add validation exception handling
[ ] Implement consistent error handling across all services
[ ] Add global error handling for reactive exceptions
[ ] Improve error messages to be more user-friendly and consistent

## Testing

[ ] Add unit tests for all controllers
[ ] Add unit tests for all services
[ ] Add unit tests for all repositories
[ ] Add integration tests for API endpoints
[ ] Add test coverage reporting and set minimum coverage thresholds
[ ] Implement test data factories for easier test setup
[ ] Add performance tests for critical endpoints

## Security

[ ] Implement authentication and authorization
[ ] Add input validation to prevent injection attacks
[ ] Implement rate limiting for API endpoints
[ ] Add security headers to API responses
[ ] Implement audit logging for sensitive operations
[ ] Review and secure database configuration

## DevOps and Configuration

[ ] Add profiles for different environments (dev, test, prod)
[ ] Configure logging properly
[ ] Add health check endpoints
[ ] Add metrics collection
[ ] Configure connection pool settings appropriately
[ ] Review SQL initialization mode (currently set to "always")
[ ] Add Docker configuration for containerization
[ ] Set up CI/CD pipeline

## Performance and Scalability

[ ] Optimize database queries
[ ] Add caching where appropriate
[ ] Implement connection pooling optimizations
[ ] Add database indexes for frequently queried fields
[ ] Implement reactive backpressure handling
[ ] Optimize reactive streams for better performance

## Documentation

[ ] Create comprehensive API documentation
[ ] Add README with setup and usage instructions
[ ] Document architecture and design decisions
[ ] Create database schema documentation
[ ] Add code style guidelines