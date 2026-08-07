## API:

`http://XXX/get-communication-hello`
```
[
  {
    "role": "A1",
    "timestamp": "2026-08-06T08:37:41.372949513"
  },
  {
    "response-get-hello": {
      "timestamp": "2026-08-06T08:37:41.494113557",
      "role": "B1"
    },
    "host": "http://service-b-svc:8080"
  },
  {
    "response-get-hello": {
      "timestamp": "2026-08-06T08:37:41.584582119",
      "role": "B1"
    },
    "host": "http://service-b-svc:8080"
  }
]
```

`http://XXX/get-hello`
```
{
  "timestamp": "2026-08-06T09:02:50.657740098",
  "role": "B1"
}
```

Docker build:
```
mvn clean package
docker rmi -f skais-microservice-example:latest
docker build -t skais-microservice-example .
```

