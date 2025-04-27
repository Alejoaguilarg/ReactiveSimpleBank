ReactiveSimpleBank

Descripción

ReactiveSimpleBank es una aplicación bancaria reactiva desarrollada con Spring WebFlux y R2DBC que demuestra la implementación de un sistema financiero utilizando programación reactiva. Esta aplicación permite gestionar cuentas, realizar transacciones y consultar historiales de forma no bloqueante y con alta concurrencia.

Tecnologías Utilizadas

Java 17

Spring Boot 3.4.3

Spring WebFlux (Programación reactiva)

Spring Data R2DBC (Acceso a datos reactivo)

MySQL con R2DBC Driver

Gradle

Estructura del Proyecto

reactive-simple-bank
├── src/main/java/...                    # Código fuente Java
├── src/main/resources
│   ├── application.yml                  # Configuración base
│   ├── application-dev.yml              # Perfil local/desarrollo
│   ├── application-test.yml             # Perfil de pruebas
│   ├── application-prod.yml             # Perfil de producción (AWS)
│   └── banner.txt                       # Banner de arranque
├── build.gradle                         # Configuración Gradle
└── README.md                            # Documentación (este archivo)

Requisitos Previos

JDK 17 o superior

MySQL Server (local) o instancia RDS en AWS

AWS CLI configurada (aws configure)

Variables de entorno configuradas:

SPRING_R2DBC_URL: URL de conexión a la base de datos (R2DBC)

SPRING_R2DBC_USERNAME: Usuario de la base de datos

SPRING_R2DBC_PASSWORD: Contraseña de la base de datos

SERVER_PORT: Puerto del servidor (por defecto: 8080)

Configuración Local

Clonar el repositorio:

git clone <repo-url> reactive-simple-bank
cd reactive-simple-bank

Crear y configurar una base MySQL local.

Copiar application-dev.yml y ajustar SPRING_R2DBC_URL, usuario y contraseña.

Ejecutar la aplicación:

./gradlew bootRun --args='--spring.profiles.active=dev'

Verificar:

curl http://localhost:8080/actuator/health

Debe devolver { "status": "UP" }.

Despliegue en AWS

A continuación un resumen de la infraestructura y pasos realizados para desplegar la app en AWS:

1. Base de Datos - RDS MySQL

Creación de instancia RDS MySQL sin acceso público.

Configuración de security groups y subredes privadas.

2. Gestión de Credenciales - IAM & SSM

Creación de roles IAM:

ec2-s3-read-role (lectura de S3)

reactive-bank-role (lectura de parámetros en SSM)

Uso de AWS SSM Parameter Store para almacenar URL, usuario y contraseña de RDS.

3. Artefacto y S3

Build de jar con Gradle:

./gradlew clean bootJar

Subida a S3:

aws s3 cp build/libs/*.jar s3://reactive-simple-bank-artifacts/jars/reactive-simple-bank.jar

4. Instancias EC2 & AMI

Lanzamiento de EC2 Amazon Linux 2023 con perfil reactive-bank-role.

User Data para instalar Java 17, Docker, descargar jar y configurar service systemd.

Creación de AMI personalizada desde la instancia configurada.

Definición de Launch Template usando la AMI, key pair y security groups.

5. Load Balancer & Target Group

Creación de Application Load Balancer (ALB) público con listener HTTP:80.

Target Group en puerto 8080, health check en /actuator/health.

Security group ALB permite HTTP:80 desde Internet.

Security group instancias permite TCP:8080 desde ALB.

6. Auto Scaling Group (ASG)

ASG mínimo=1, deseado=2, máximo=4.

## 📂 Estructura del Proyecto