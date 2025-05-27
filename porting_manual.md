# 포팅매뉴얼

## 목차

1. [**개발환경**](#개발환경)
2. [**EC2 포트 번호**](#ec2-포트-번호)
3. [**환경변수**](#환경변수)

- [SpringBoot](#applicationyaml)
- [FastAPI](#flytoml)
- [Docker Compose](#docker-compose)
- [nginx.conf](#nginxconf)
- [redis.conf](#redisconf)

4. [**CI/CD 구축**](#cicd-구축)

- [Jenkins 설치 플러그인](#jenkins-설치-플러그인)
- [Jenkins Credentials](#jenkins-credentials)
- [Pipeline](#pipeline)
  - [SpringBoot Pipeline](#backendspring-boot-pipeline)

5. [**외부 서비스 API**](#외부-서비스-api)

## 개발환경

```
[Android]
agp = 8.5.2
kotlin = 2.1.0
coreKtx = 1.15.0
compileSdk = 35
minSdk = 26
targetSdk = 34
AndroidStudio = 2024.1.1

[Backend(SpringBoot)]
Java = jdk17
Spring Boot = 3.3.1
IntelliJ = 2023.1

[AI]
annotated-types==0.7.0
anyio==4.8.0
Bottleneck==1.4.2
click==8.1.8
colorama==0.4.6
exceptiongroup==1.2.2
fastapi==0.115.8
h11==0.14.0
idna==3.10
imbalanced-learn==0.12.3
joblib==1.4.2
mkl_fft==1.3.11
mkl_random==1.2.8
mkl-service==2.4.0
numexpr==2.10.1
numpy==1.26.4
pandas==2.2.3
pip==25.0
pydantic==2.10.6
pydantic_core==2.27.2
python-dateutil==2.9.0.post0
pytz==2024.1
scikit-learn==1.6.1
scipy==1.13.1
setuptools==72.1.0
six==1.16.0
sniffio==1.3.1
starlette==0.45.3
threadpoolctl==3.5.0
typing_extensions==4.12.2
tzdata==2023.3
uvicorn==0.34.0
wheel==0.45.1

[Database]
MySQL = 9.2.0

[Infra]
Ubuntu = 22.04.4 LTS
Jenkins = 2.479.3
Nginx = 1.27.3
Docker = 26.1.3
```

## EC2 포트 번호

| Skill       | Port   |
| ----------- | ------ |
| Jenkins     | 9090   |
| Nginx       | 80/443 |
| Spring Boot | 8080   |
| MySQL       | 3306   |
| Redis       | 6379   |

## 환경변수

### Spring Boot

#### application.yaml

```
# application.yaml
spring:
  jackson:
    time-zone: Asia/Seoul
  profiles:
    active: prod
  application:
    name: ttaenggrang
  datasource:
    url: jdbc:mysql://localhost:3307/ttgr?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&serverTimezone=Asia/Seoul
    username: [MySQL 데이터베이스 username]
    password: [MySQL 데이터베이스 password]
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        show_sql: true
        format_sql: false
        dialect: org.hibernate.dialect.MySQL8Dialect
        jdbc:
          time_zone: Asia/Seoul
    show-sql:
      true
  sql:
    init: 실행 시 init.sql 실행
  springdoc: # Swagger
    swagger-ui:
      groups-order: DESC
      tags-sorter: alpha
      operations-sorter: meth
      mode: always
    paths-to-match:
      - /api/**
    api-docs:
      path: /api/v3/api-docs
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 10MB
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
  data:
    redis:
      timeout: 6000

server:
  servlet:
    context-path: /api
    encoding:
      charset: UTF-8
      enabled: true
      force: true

api:
  openai_key: ${OPENAI_API_KEY}
```

```
# application-dev.yaml
spring:
  data:
    redis:
      host: localhost # local 환경
      port: 6379
      password: [Redis password]

scheduling:
  stock-market:
    open: "0 0 9 * * MON-FRI"  # 운영 환경에서는 09:00 개장
    close: "0 0 17 * * MON-FRI" # 운영 환경에서는 17:00 폐장
  weekly-report:
    generate: "0 0 17 ? * FRI"
  savings:
    auto-deposit: "0 0 0 * * *"  # 매일 자정(00:00)에 실행
```

```
# application-prod.yaml
spring:
  data:
    redis:
      host: redis # Docker Compose의 서비스명
      port: 6379
      password: [Redis password]
  jackson:
    time-zone: Asia/Seoul

scheduling:
  stock-market:
    open: "0 0 9 * * MON-FRI"  # 운영 환경에서는 09:00 개장
    close: "0 0 17 * * MON-FRI" # 운영 환경에서는 17:00 폐장
  weekly-report:
    generate: "0 0 17 ? * FRI"
  savings:
    auto-deposit: "0 0 0 * * *"  # 매일 자정(00:00)에 실행
```

### FastAPI fly.io 배포

#### fly.toml

```
app = 'ttaenggrang'
primary_region = 'nrt'

[http_service]
  internal_port = 8000
  force_https = false
  auto_stop_machines = 'stop'
  auto_start_machines = true
  min_machines_running = 1
  processes = ['app']

[[vm]]
  memory = '256mb'
  cpu_kind = 'shared'
  cpus = 1

```

### Docker Compose

#### docker-compose.yml

```
version: '3'
services:
  db:
    image: mysql
    container_name: ttgr-db
    environment:
      - MYSQL_DATABASE=[MySQL 데이터베이스]
      - MYSQL_ROOT_PASSWORD=[MySQL 데이터베이스 password]
    ports:
      - "3306:3306"
    networks:
      - app-network
    volumes:
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
      - mysql_data:/var/lib/mysql

  spring-boot-app:
    image: spring-boot-app
    container_name: ttgr-app
    ports:
      - "8080:8080"
    links:
      - db
    depends_on:
      - db # spring-boot-app
      - redis
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/ttgr?useSSL=false&useUnicode=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul
      - SPRING_DATASOURCE_USERNAME=[MySQL 데이터베이스 username]
      - SPRING_DATASOURCE_PASSWORD=[MySQL 데이터베이스 password]
      - TZ=Asia/Seoul
    env_file:
      - .env
    restart: always
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
    networks:
      - app-network

  nginx:
    image: nginx:latest
    container_name: nginx
    ports:
      - "80:80"
      - "443:443"
    command: "/bin/sh -c 'while :; do sleep 6h & wait $${!}; nginx -s reload; done & nginx -g \"daemon off;\"'"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./data/certbot/conf:/etc/letsencrypt
      - ./data/certbot/www:/var/www/certbot
    depends_on:
      - spring-boot-app
    networks:
      - app-network

  certbot:
      image: certbot/certbot
      volumes:
        - ./data/certbot/conf:/etc/letsencrypt
        - ./data/certbot/www:/var/www/certbot
      entrypoint: "/bin/sh -c 'trap exit TERM; while :; do certbot renew; sleep 12h & wait $${!}; done;'"

  redis:
    image: redis:latest
    container_name: redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
      - ./redis/redis.conf:/usr/local/etc/redis/redis.conf
    command: [ "redis-server", "/usr/local/etc/redis/redis.conf" ]
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  mysql_data:
  redis_data:
    driver: local
  jenkins_home:
```

### Nginx

#### nginx.conf

```
worker_processes auto;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    server {
        listen 443 ssl;
        server_name i12d107.p.ssafy.io;

        # SSL 설정 (Certbot에서 관리)
        ssl_certificate /etc/letsencrypt/live/i12d107.p.ssafy.io/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/i12d107.p.ssafy.io/privkey.pem;
        include /etc/letsencrypt/options-ssl-nginx.conf;
        ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

        # 프록시 설정
        location / {
            proxy_pass http://spring-boot-app:8080/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

	    # CORS 설정 추가
	    add_header 'Access-Control-Allow-Origin' '*';
            add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS, PUT, DELETE';
            add_header 'Access-Control-Allow-Headers' 'Origin, Content-Type, Accept, Authorization';

            # OPTIONS 요청 처리
            if ($request_method = 'OPTIONS') {
                add_header 'Access-Control-Max-Age' 1728000;
                add_header 'Content-Type' 'text/plain charset=UTF-8';
                add_header 'Content-Length' 0;
                return 204;
            }
        }
    }

    server {
        listen 80;
        server_name i12d107.p.ssafy.io www.i12d107.p.ssafy.io;

        location /.well-known/acme-challenge/ {
            root /var/www/certbot;
        }

        location ~ /\. {
            deny all;  # 숨김 파일(.env, .htaccess 등) 접근 차단
            return 404;
        }

        # HTTP 요청을 HTTPS로 리디렉션
        location / {
            return 301 https://$host$request_uri;
        }
    }
}
```

### Redis

#### redis.conf

```
appendonly yes
appendfilename "appendonly.aof"
save 900 1
save 300 10
requirepass [Redis password]
```

## CI/CD 구축

### Jenkins 설치 플러그인

1. Pipeline
2. Pipeline: Stage View
3. Pipeline: GitHub
4. Git Plugin
5. Git Parameter Plugin
6. GitLab Plugin
7. Docker Pipeline
8. Docker Commons
9. Credentials Binding Plugin
10. Gradle Plugin

### Jenkins Credentials

<img src="https://github.com/user-attachments/assets/6f7dea39-56d6-4ea1-b1ef-fd46136b1cca"/>

### Pipeline

#### Backend(Spring Boot) Pipeline

```
pipeline {
    agent any
    tools {
        gradle 'gradle'
    }
    environment {
        OPENAI_API_KEY = credentials('OPENAI_API_KEY')
        FIREBASE_KEY_JSON = credentials('FIREBASE_KEY_JSON')  // Jenkins Credentials에서 Firebase 키 가져오기
    }
    stages {
        stage('Git Checkout') {
            steps {
                git credentialsId: 'ssafy-gitlab',
                    branch: 'develop',
                    url: 'https://lab.ssafy.com/s12-webmobile4-sub1/S12P11D107.git'
            }
        }
        stage('Prepare Firebase Key') {
            steps {
                script {
                    // Firebase 서비스 계정 키 파일 생성
                    writeFile file: 'backend/src/main/resources/firebase/firebase_service_key.json', text: env.FIREBASE_KEY_JSON
                }
            }
        }
        stage('Check Workspace') {
            steps {
                sh 'pwd'    // 현재 디렉토리 출력
                sh 'ls -la' // 현재 디렉토리 내부 파일 목록 확인
                sh 'ls -la backend/src/main/resources/firebase/'  // firebase 디렉토리 내부 확인
            }
        }
        stage('Prepare .env File Securely') {
            steps {
                withCredentials([string(credentialsId: 'OPENAI_API_KEY', variable: 'OPENAI_API_KEY')]) {
                    script {
                        def envContent = String.format(
                            "OPENAI_API_KEY=%s\n",
                            OPENAI_API_KEY
                        )
                        writeFile file: './backend/.env', text: envContent
                        echo "✅ .env 파일 생성 완료 (script 블록 사용)"
                    }
                }
            }
        }
        stage('Build') {
            steps {
                dir('./backend') {
                    sh '''
                    chmod +x ./gradlew
                    ./gradlew clean build \
                    -DOPENAI_API_KEY="${OPENAI_API_KEY}" \
                    -Dspring.profiles.active=prod \
                    --no-build-cache -x check

                    echo "✅ 빌드 완료"
                    ls -la build/libs/
                    '''
                }
            }
        }
        stage('Deploy') {
        	steps {
            	sshagent(credentials: ['ssafy-ec2']) {
                    sh '''
                        echo "🚀 배포 시작..."

                        # 빌드된 JAR 파일을 EC2로 복사
                        scp -v -o StrictHostKeyChecking=no /var/jenkins_home/workspace/ttgr-pipeline/backend/build/libs/*.jar ubuntu@i12d107.p.ssafy.io:/home/ubuntu/S12P11D107/backend/build/libs

    		            # EC2 서버에서 Docker Compose 실행
    		            ssh -o StrictHostKeyChecking=no ubuntu@i12d107.p.ssafy.io <<EOF
    		            cd /home/ubuntu/S12P11D107/backend &&
    		            sudo docker-compose down &&
    		            sudo docker build --no-cache -t spring-boot-app . &&  # 캐시 무시하고 새로 빌드
                        sudo docker-compose up -d

    		            echo "✅ 배포 완료!"
		            '''
	        	}
	        }
        }
    }
}
```

## 외부 서비스 API

[OpenAI API](https://openai.com/index/openai-api/)

[Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging?hl=ko)
