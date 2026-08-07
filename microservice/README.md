http://localhost:8080/get-hello

Response:
`
{
  "serviceA": "hello A"
}
`

Docker build:
```
mvn clean package
docker rmi -f skais-microservice-example:latest
docker build -t skais-microservice-example .
```

