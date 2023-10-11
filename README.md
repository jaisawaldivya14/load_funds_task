# Velocity Limits Spring Boot Project

This is a simple Spring Boot project that demonstrates velocity checks

## Getting Started

To get started with this project, follow these steps:

1. Clone the repository.
2. Install the required dependencies.
3. Run the application using `mvn spring-boot:run`.

## Usage
- Use `mvn spring-boot:run` to run the load_funds_task, or you can directly run LoadFundsTaskApplication main class 
- Svc runs on port:8080, you can configure server.port to choose a different port
- Use rest api to execute the task. Postman collection for the same is attached in the repo
  ```
  curl --location 'http://localhost:8080/api/v1/load-funds' \
  --header 'Content-Type: application/json' \
  --data '{
  "executed_by": "Admin"
  }'
- Fund loads req has been read from input.txt in resources dir
- Go to resources dir  `cd src/main/resources`
- Run `vimdiff expected_output.txt actual_output.txt -c 'set diffopt+=iwhite'` cmd to compare the diff between expected output and actual output files
- actual_output.txt is generated by the service in resources dir

## Configuration
- You can configure the application by modifying the `application.properties` file.
- Svc runs on port:8080, you can configure server.port in`application.properties` to choose a different port
