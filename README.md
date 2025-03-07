# Prometheus Alerts WebSocket Service

## Overview
This Spring Boot application integrates with Prometheus to fetch alerts from its `/alerts` REST endpoint and efficiently manage them using an expiring map. The system then pushes new **firing** status alerts to WebSocket clients connected to `ws://localhost:8203/events`.

## Features
- **Fetch alerts from Prometheus** via REST API.
- **Store alerts in an expiring map** based on their names.
- **Detect and forward new firing alerts** in real-time.
- **WebSocket support** for instant alert broadcasting.

## Technologies Used
- **Spring Boot** (REST and WebSocket support)
- **Prometheus** (Alerting source)
- **Expiring Map** (Efficient alert storage)
- **WebSockets** (Real-time event delivery)

## Architecture
1. **Prometheus Alerts Fetching**
   - Periodically queries `http://<prometheus-host>/alerts` to retrieve active alerts.
   
2. **Expiring Map for Alerts**
   - Alerts are stored using an expiring map with a predefined TTL.
   - Only **new firing alerts** are considered for processing.

3. **WebSocket Notification System**
   - Clients can connect to `ws://localhost:8203/events`.
   - New alerts are pushed instantly to all active WebSocket sessions.

## Setup & Installation
### Prerequisites
Ensure you have the following installed:
- **Java 17+**
- **Maven**
- **Prometheus instance running**

### Installation Steps
1. **Clone the repository:**
   ```sh
   git clone <repo-url>
   cd <project-directory>
   ```
2. **Configure Prometheus Endpoint** in `application.properties`:
   ```properties
   prometheus.url=http://localhost:9090
   ```
3. **Run the application:**
   ```sh
   mvn spring-boot:run
   ```

## API Usage
### WebSocket Connection
- **WebSocket URL:** `ws://localhost:8203/events`
- Clients receive a JSON payload whenever a new alert is fired.

Example WebSocket message:
```json
{
  "name": "HighCPUUsage",
  "state": "firing",
  "instance": "server-1",
  "health": "critical",
  "query": " prom-ql",
  "type: "type",
  "lastEvaluation: "lastEvaluation"
}
```

## Future Enhancements
- Implement **alert filtering** based on severity or labels.
- Add **authentication** for WebSocket clients.
- Store alerts in a persistent database.



