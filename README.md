# 🏦 ReactiveSimpleBank [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-green.svg)
![Java](https://img.shields.io/badge/Java-17-red.svg)
![Gradle](https://img.shields.io/badge/Gradle-8.4-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)

Aplicación bancaria reactiva desarrollada con **Spring WebFlux** y **R2DBC** que implementa un sistema financiero no bloqueante.

## 🚀 Tecnologías Clave
- **Java 17** - Lenguaje base
- **Spring Boot 3.4.3—**Framework principal
- **Spring WebFlux** - Programación reactiva
- **R2DBC** - Conexión reactiva a MySQL
- **Gradle** - Gestión de dependencias
- **AWS** (RDS, EC2, ALB, ASG) - Infraestructura cloud

## 📂 Estructura del Proyecto
```tree
reactive-simple-bank/
├── src/main/java/.../           
│   ├── config/                  # Configuraciones Spring
│   ├── controller/              # Endpoints reactivos
│   ├── model/                   # Entidades de negocio
│   ├── repository/              # Repositorios R2DBC
│   └── service/                 # Lógica reactiva
├── src/main/resources/
│   ├── application.yml          # Config base
│   ├── application-*.yml        # Perfiles específicos
│   └── banner.txt               # Arte ASCII al iniciar
└── build.gradle                 # Configuración Gradle

```
⚙️ Configuración Rápida
📋 Requisitos Previos
☑️ JDK 17+

☑️ MySQL 8+ (local o RDS)

☑️ AWS CLI configurada

☑️ Variables de entorno:

env
SPRING_R2DBC_URL=r2dbc:mysql://...
SPRING_R2DBC_USERNAME=user
SPRING_R2DBC_PASSWORD=secret
SERVER_PORT=8080
🖥️ Desarrollo Local
bash
# 1. Clonar repositorio
git clone https://github.com/tu-usuario/reactive-simple-bank.git
cd reactive-simple-bank

# 2. Configurar base de datos
mysql -u root -p < scripts/init_db.sql

# 3. Ejecutar con perfil dev
./gradlew bootRun --args='--spring.profiles.active=dev'

# 4. Verificar salud
curl http://localhost:8080/actuator/health
☁️ Despliegue en AWS
<details> <summary>🔍 Infraestructura Detallada</summary>
1. 🗄️ Base de Datos (RDS MySQL)
Tipo: Instancia t3.micro

Red: Subredes privadas

Seguridad: SG solo permite 3306 desde EC2

2. 🔐 IAM & SSM
yaml
Roles:
  - EC2-S3-Read: Acceso a bucket de artifacts
  - ReactiveBank-Role: Lectura de SSM Parameters

SSM Parameters:
  - /bank/prod/rds-url
  - /bank/prod/rds-user
  - /bank/prod/rds-password
3. 📦 Artefactos (S3)
bash
# Build y subida a S3
./gradlew clean bootJar
aws s3 cp build/libs/*.jar s3://reactive-simple-bank-artifacts/jars/
4. 🖥️ Configuración EC2
bash
#!/bin/bash
# User Data para EC2
yum install -y java-17-amazon-corretto docker
systemctl start docker

aws s3 cp s3://reactive-simple-bank-artifacts/jars/reactive-simple-bank.jar /app/
java -jar /app/reactive-simple-bank.jar --spring.profiles.active=prod
5. ⚖️ Load Balancer (ALB)
Tipo: Application Load Balancer

Listeners: HTTP:80 → TG:8080

Health Check: /actuator/health

Security Groups:

ALB: Permite 80 desde 0.0.0.0/0

Instancias: Permite 8080 desde ALB

6. 📈 Auto Scaling Group
yaml
Capacity:
  Min: 1
  Desired: 2
  Max: 4

Scaling Policies:
  - CPU > 70% por 5min → +1 instancia
  - CPU < 30% por 15min → -1 instancia
</details>
📚 Documentación Adicional
Guía Spring WebFlux

R2DBC con MySQL

AWS Auto Scaling Best Practices

✨ Contribuciones bienvenidas!
📥 Crea un Issue o Pull Request siguiendo el Código de Conducta.
