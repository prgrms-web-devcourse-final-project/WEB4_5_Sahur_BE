# 1단계: React 앱 빌드
FROM node:18 AS build

WORKDIR /app

# package.json, package-lock.json을 복사하고 의존성 설치
COPY package*.json ./
RUN npm install

# 소스 코드 복사
COPY . ./
# React 앱을 빌드
RUN npm run build

# 2단계: Nginx로 서빙
FROM nginx:alpine

# Nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf

# React 앱 빌드된 파일을 Nginx의 웹 루트로 복사
COPY --from=build /app/build /usr/share/nginx/html

# 포트 80을 노출
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]