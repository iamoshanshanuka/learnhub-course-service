# LearnHub Course Service

Course catalogue (Spring Boot, MySQL on Cloud SQL).

## Student Information

- **Student Name:** M.W. Oshan Shanuka
- **Student Number:** 2301692025
- **Slack Handle:** oshan_shanuka
- **GCP Project ID:** learnhub-capstone

Final project of **ITS 2130 Enterprise Cloud Architecture** (Higher Diploma in Software Engineering, IJSE).

## Project Description

CRUD for courses: unique course code, title, description, duration in weeks and fee. Business rules are validated (fee and duration must be positive, duration is capped by the value `learnhub.course.max-duration-weeks` delivered by the Config Server). Data lives in the MySQL database `learnhub_course_db` on Cloud SQL (private IP).

## Technology Stack

- Java 25
- Spring Boot 4.0.7 and Spring Cloud 2025.1.2
- Spring Data JPA, Hibernate, MySQL (Cloud SQL)
- Spring Cloud Netflix Eureka Client, Spring Cloud Config Client, Bean Validation, Actuator
- PM2 (process manager on the VM: restarts on failure, starts again after a reboot)
- Google Compute Engine (IaaS), Cloud DNS, Cloud NAT, Cloud Load Balancing

## Setup / Getting Started

**Requirements:** JDK 25 and Maven 3.9+.

```bash
git clone https://github.com/iamoshanshanuka/learnhub-course-service.git
cd learnhub-course-service
mvn clean package
java -jar target/course-service.jar       # needs a local MySQL on localhost:3306 (root/root)
```

Runs on port **8082**. Every setting has a local default, so no environment variable is needed for a local run.

### Configuration (environment variables)

| Variable | Meaning | Default |
|---|---|---|
| `SERVER_PORT` | HTTP port | `8082` |
| `DB_URL` | JDBC URL | `jdbc:mysql://localhost:3306/learnhub_course_db?createDatabaseIfNotExist=true` |
| `DB_USERNAME / DB_PASSWORD` | Database login | `root / root` |
| `CONFIG_SERVER_URL` | Config Server address | `http://localhost:8888` |
| `EUREKA_SERVER_URL` | Eureka URL(s), comma separated | `http://localhost:8761/eureka/` |

### API

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/courses` | create a course |
| GET | `/api/courses`, `/api/courses/{id}` | list, read |
| PUT | `/api/courses/{id}` | update |
| DELETE | `/api/courses/{id}` | delete |

Errors return `{timestamp, status, error, message}` with 400 (validation), 404 or 409 (course code already used).

### Run under PM2 (like on the GCP VM)

```bash
sudo mkdir -p /opt/learnhub /var/log/pm2
sudo cp target/course-service.jar /opt/learnhub/course-service.jar
pm2 start ecosystem.config.js --update-env
pm2 save
pm2 startup systemd -u root --hp /root      # PM2 starts again after a reboot
```

`ecosystem.config.js` restarts the app when it crashes and writes logs to `/var/log/pm2/course-service-out.log` and `course-service-error.log`.

## Related repositories

- [`learnhub-backend-services`](https://github.com/iamoshanshanuka/learnhub-backend-services)
- [`learnhub-student-service`](https://github.com/iamoshanshanuka/learnhub-student-service)
- [`learnhub-course-service`](https://github.com/iamoshanshanuka/learnhub-course-service)
- [`learnhub-enrollment-service`](https://github.com/iamoshanshanuka/learnhub-enrollment-service)
