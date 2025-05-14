terraform {
  // aws 라이브러리 불러옴
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

# AWS 설정
provider "aws" {
  region = var.region
}

# VPC 설정
resource "aws_vpc" "team05-vpc" {
  cidr_block = "10.0.0.0/16"

  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.prefix}-vpc"
  }
}

# 퍼블릭 Subnet 설정
resource "aws_subnet" "team05-subnet_app" {
  vpc_id                  = aws_vpc.team05-vpc.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "${var.region}a"
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.prefix}-subnet-1"
  }
}

# 프라이빗 Subnet 설정
resource "aws_subnet" "team05-subnet_db" {
  vpc_id                  = aws_vpc.team05-vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "${var.region}b"
  map_public_ip_on_launch = false

  tags = {
    Name = "${var.prefix}-subnet-2"
  }
}

# Elastic IP (퍼블릭 IP) 생성 - 프라이빗 서브넷에서 외부로 가기위함
resource "aws_eip" "team05-eip" {
  domain = "vpc"
  tags = {
    Name = "${var.prefix}-eip"
  }
}

# NAT 게이트웨이 생성
resource "aws_nat_gateway" "team05-nat" {
  allocation_id = aws_eip.team05-eip.id
  subnet_id     = aws_subnet.team05-subnet_app.id #퍼블릭 서브넷
  tags = {
    Name = "${var.prefix}-nat"
  }
}

# 인터넷 게이트웨이 생성
resource "aws_internet_gateway" "team05-igw" {
  vpc_id = aws_vpc.team05-vpc.id

  tags = {
    Name = "${var.prefix}-igw"
  }
}

# 퍼블릭 라우팅 테이블
resource "aws_route_table" "team05-rt" {
  vpc_id = aws_vpc.team05-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.team05-igw.id
  }

  tags = {
    Name = "${var.prefix}-rt"
  }
}

# 프라이빗 라우팅 테이블
resource "aws_route_table" "team05-private-rt" {
  vpc_id = aws_vpc.team05-vpc.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.team05-nat.id
  }

  tags = {
    Name = "${var.prefix}-private-rt"
  }
}
# 퍼블릭 라우팅 테이블 연결
resource "aws_route_table_association" "team05-app-association" {
  subnet_id      = aws_subnet.team05-subnet_app.id
  route_table_id = aws_route_table.team05-rt.id
}

# 프라이빗 라우팅 테이블 연결
resource "aws_route_table_association" "team05-db-association" {
  subnet_id      = aws_subnet.team05-subnet_db.id
  route_table_id = aws_route_table.team05-private-rt.id
}

resource "aws_security_group" "team05-sg" {
  name = "${var.prefix}-sg"

  ingress {
    from_port = 0
    to_port   = 0
    protocol  = "all"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "all"
    cidr_blocks = ["0.0.0.0/0"]
  }

  vpc_id = aws_vpc.team05-vpc.id

  tags = {
    Name = "${var.prefix}-sg"
  }
}

# EC2 역할 생성
resource "aws_iam_role" "team05-ec2-role" {
  name = "${var.prefix}-ec2-role"

  # 이 역할에 대한 신뢰 정책 설정. EC2 서비스가 이 역할을 가정할 수 있도록 설정
  assume_role_policy = <<EOF
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Sid": "",
        "Action": "sts:AssumeRole",
        "Principal": {
            "Service": "ec2.amazonaws.com"
        },
        "Effect": "Allow"
      }
    ]
  }
  EOF
}

# EC2 역할에 AmazonS3FullAccess 정책을 부착
resource "aws_iam_role_policy_attachment" "s3_full_access" {
  role       = aws_iam_role.team05-ec2-role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}

# EC2 역할에 AmazonEC2RoleforSSM 정책을 부착
resource "aws_iam_role_policy_attachment" "ec2_ssm" {
  role       = aws_iam_role.team05-ec2-role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2RoleforSSM"
}

# IAM 인스턴스 프로파일 생성
resource "aws_iam_instance_profile" "team05-instance-profile" {
  name = "${var.prefix}-instance-profile"
  role = aws_iam_role.team05-ec2-role.name
}

locals {
  ec2_user_mysql = <<-END_OF_FILE
#!/bin/bash
# 가상 메모리 4GB 설정
sudo dd if=/dev/zero of=/swapfile bs=128M count=32
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo sh -c 'echo "/swapfile swap swap defaults 0 0" >> /etc/fstab'

# 도커 설치 및 실행/활성화
yum install docker -y
systemctl enable docker
systemctl start docker

# 도커 네트워크 생성
docker network create common

# mysql 설치
docker run -d \
  --name mysql_1 \
  --restart unless-stopped \
  -v /dockerProjects/mysql_1/volumes/var/lib/mysql:/var/lib/mysql \
  -v /dockerProjects/mysql_1/volumes/etc/mysql/conf.d:/etc/mysql/conf.d \
  --network common \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=${var.password_1} \
  -e TZ=Asia/Seoul \
  mysql:latest

# MySQL 컨테이너가 준비될 때까지 대기
echo "MySQL이 기동될 때까지 대기 중..."
until docker exec mysql_1 mysql -uroot -p${var.password_1} -e "SELECT 1" &> /dev/null; do
  echo "MySQL이 아직 준비되지 않음. 5초 후 재시도..."
  sleep 5
done
echo "MySQL이 준비됨. 초기화 스크립트 실행 중..."

docker exec mysql_1 mysql -uroot -p${var.password_1} -e "
CREATE USER 'team05'@'%' IDENTIFIED WITH caching_sha2_password BY '${var.password_1}';

GRANT ALL PRIVILEGES ON *.* TO 'team05'@'%';

CREATE DATABASE tung_db;

FLUSH PRIVILEGES;
"

END_OF_FILE
}

locals {
  ec2_user_base = <<-END_OF_FILE
#!/bin/bash
# 가상 메모리 4GB 설정
sudo dd if=/dev/zero of=/swapfile bs=128M count=32
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo sh -c 'echo "/swapfile swap swap defaults 0 0" >> /etc/fstab'

# 도커 설치 및 실행/활성화
yum install docker -y
systemctl enable docker
systemctl start docker

# 도커 네트워크 생성
docker network create common

# nginx 설치
docker run -d \
  --name npm_1 \
  --restart unless-stopped \
  --network common \
  -p 80:80 \
  -p 443:443 \
  -p 81:81 \
  -e TZ=Asia/Seoul \
  -v /dockerProjects/npm_1/volumes/data:/data \
  -v /dockerProjects/npm_1/volumes/etc/letsencrypt:/etc/letsencrypt \
  jc21/nginx-proxy-manager:latest

# ha proxy 설치
## 설정파일을 위한 디렉토리 생성
mkdir -p /dockerProjects/ha_proxy_1/volumes/usr/local/etc/haproxy/lua

cat << 'EOF' > /dockerProjects/ha_proxy_1/volumes/usr/local/etc/haproxy/lua/retry_on_502_504.lua
core.register_action("retry_on_502_504", { "http-res" }, function(txn)
  local status = txn.sf:status()
  if status == 502 or status == 504 then
    txn:Done()
  end
end)
EOF

## ha proxy 설정파일 생성
echo -e "
global
    lua-load /usr/local/etc/haproxy/lua/retry_on_502_504.lua

resolvers docker
    nameserver dns1 127.0.0.11:53
    resolve_retries       3
    timeout retry         1s
    hold valid            10s

defaults
    mode http
    timeout connect 5s
    timeout client 60s
    timeout server 60s

frontend http_front
    bind *:80
    acl host_app1 hdr_beg(host) -i api.devapi.store

    use_backend http_back_1 if host_app1

backend http_back_1
    balance roundrobin
    option httpchk GET /actuator/health
    default-server inter 2s rise 1 fall 1 init-addr last,libc,none resolvers docker
    option redispatch
    http-response lua.retry_on_502_504

    server app_server_1_1 app1_1:8080 check
    server app_server_1_2 app1_2:8080 check
" > /dockerProjects/ha_proxy_1/volumes/usr/local/etc/haproxy/haproxy.cfg

docker run \
  -d \
  --restart unless-stopped \
  --network common \
  -p 8090:80 \
  -v /dockerProjects/ha_proxy_1/volumes/usr/local/etc/haproxy:/usr/local/etc/haproxy \
  -e TZ=Asia/Seoul \
  --name ha_proxy_1 \
  haproxy

# redis 설치
docker run -d \
  --name=redis_1 \
  --restart unless-stopped \
  --network common \
  -p 6379:6379 \
  -e TZ=Asia/Seoul \
  redis --requirepass ${var.password_1}

# ec2 생성 시 깃허브 자동 로그인, 깃 액션 이미지를 가져오기 위함
echo "${var.github_access_token_1}" | docker login ghcr.io -u ${var.github_access_token_1_owner} --password-stdin

END_OF_FILE
}

data "aws_ami" "latest_amazon_linux" {
  most_recent = true
  owners = ["amazon"]

  filter {
    name = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }

  filter {
    name = "architecture"
    values = ["x86_64"]
  }

  filter {
    name = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name = "root-device-type"
    values = ["ebs"]
  }
}

# EC2 main 인스턴스 생성
resource "aws_instance" "team05-main" {
  # 사용할 AMI ID
  ami = data.aws_ami.latest_amazon_linux.id
  # EC2 인스턴스 유형
  instance_type = "t3.micro"
  # 사용할 서브넷 ID
  subnet_id = aws_subnet.team05-subnet_app.id
  # 적용할 보안 그룹 ID
  vpc_security_group_ids = [aws_security_group.team05-sg.id]
  # 퍼블릭 IP 연결 설정
  associate_public_ip_address = true

  # 인스턴스에 IAM 역할 연결
  iam_instance_profile = aws_iam_instance_profile.team05-instance-profile.name

  # 인스턴스에 태그 설정
  tags = {
    Name = "${var.prefix}-main"
  }

  # 루트 볼륨 설정
  root_block_device {
    volume_type = "gp3"
    volume_size = 12 # 볼륨 크기를 12GB로 설정
  }

  user_data = <<-EOF
${local.ec2_user_base}
EOF
}

# EC2 db 인스턴스 생성
resource "aws_instance" "team05-db" {
  # 사용할 AMI ID
  ami = data.aws_ami.latest_amazon_linux.id
  # EC2 인스턴스 유형
  instance_type = "t3.micro"
  # 사용할 서브넷 ID
  subnet_id = aws_subnet.team05-subnet_db.id
  # 적용할 보안 그룹 ID
  vpc_security_group_ids = [aws_security_group.team05-sg.id]
  # 퍼블릭 IP 연결 설정
  associate_public_ip_address = false

  # 인스턴스에 IAM 역할 연결
  iam_instance_profile = aws_iam_instance_profile.team05-instance-profile.name

  # 인스턴스에 태그 설정
  tags = {
    Name = "${var.prefix}-db"
  }

  # 루트 볼륨 설정
  root_block_device {
    volume_type = "gp3"
    volume_size = 24 # 볼륨 크기를 12GB로 설정
  }

  user_data = <<-EOF
${local.ec2_user_mysql}
EOF
}