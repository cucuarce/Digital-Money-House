#🏦 Digital Money House – Plataforma de Billetera Virtual

Digital Money House es una plataforma de billetera virtual desarrollada bajo una 
arquitectura de **microservicios**, diseñada para ofrecer una gestión segura y escalable de cuentas, 
transacciones y autenticación de usuarios.

****************************************************************************************************

##📌 **Características**
✅ Arquitectura de Microservicios con **Spring Boot** y **Spring Cloud**  
✅ Autenticación y Autorización con **JWT**  
✅ Comunicación entre microservicios mediante **FeignClient**  
✅ Configuración centralizada con **Spring Cloud Config Server**  
✅ Descubrimiento de servicios con **Eureka**  
✅ Enrutamiento con **Spring Cloud Gateway**  
✅ Base de datos **MySQL** con persistencia en **Docker**  
✅ Monitoreo y trazabilidad con **Zipkin**

****************************************************************************************************

##🛠 Tecnologías Utilizadas
**Backend:** Java 17, Spring Boot 3, Spring Cloud
**Seguridad:** JWT, Spring Security
**Base de Datos:** MySQL
**Gestión de Configuración:** Spring Cloud Config Server
**Descubrimiento de Servicios:** Eureka Server
**API Gateway:** Spring Cloud Gateway
**Mensajería:** FeignClient para comunicación entre microservicios
**Trazabilidad:** Zipkin
**Contenedores:** Docker, Docker Compose

****************************************************************************************************

##🚀 Instalación y Configuración
1️⃣ **Prerrequisitos**
Antes de iniciar el proyecto, asegúrate de tener instalados:

- Java 17
- Maven
- Docker y Docker Compose

2️⃣ **Clonar el Repositorio**

1. git clone git@github.com:dhm-group/digital-money-house.git
2. cd digital-money-house

3️⃣ **Configuraciones**

- Agregar el user y password de la base de datos en el docker compose
- Si tienes que crear los schemas de la base de datos son dmh_users, dmh_accounts, dhm_cards, dhm_transactions.

4️⃣ **Construcción de los Microservicios**
Ejecuta el siguiente comando en la raíz del proyecto para construir los microservicios:

- mvn clean package

5️⃣ **Levantar los Contenedores con Docker**
Ejecuta:

- docker-compose up --build

📌 **Nota:** Si realizas cambios en el código, debes reconstruir la imagen del microservicio afectado:
Ejemplo con security-service:

- docker-compose build security-service
- docker-compose up security-service

****************************************************************************************************

##🏗 Microservicios Disponibles
Cada microservicio expone endpoints REST, que pueden ser accedidos a través del API Gateway.

📌 **1. API Gateway**
URL Base: http://localhost:8081
Ejemplo de endpoint: http://localhost:8081/users/auth/login

📌 **2. Security Service**
URL Base: http://localhost:8082
Endpoints:
POST /users/auth/register → Registrar usuario
POST /users/auth/login → Iniciar sesión
POST /users/auth/verify-email → Verificar usuario

📌 **3. Accounts Service**
URL Base: http://localhost:8083
Endpoints:
GET /accounts/api/{id} → Obtener cuenta por ID
POST /accounts/api → Crear nueva cuenta

📌 **4. Transactions Service**
URL Base: http://localhost:8084
Endpoints:
GET /transactions/api/{id} → Obtener transacción por ID
POST /transactions/api → Crear transacción

📌 **5. Cards Service**
URL Base: http://localhost:8085
Endpoints:
GET /cards/api/{id} → Obtener tarjeta por ID
POST /cards/api → Crear tarjeta

****************************************************************************************************

##📚 Documentación

  📄 http://localhost:8081/security-service/swagger-ui.html
  📄 http://localhost:8081/accounts-service/swagger-ui.html
  📄 http://localhost:8081/cards-service/swagger-ui.html
  📄 http://localhost:8081/transactions-service/swagger-ui.html

****************************************************************************************************

##📊 Monitoreo y Logs
Puedes monitorear la actividad del sistema usando:

- Eureka Dashboard: http://localhost:8761
- Zipkin UI: http://localhost:9411

Para ver los logs en tiempo real:

- docker logs -f api-gateway
- docker logs -f security-service

****************************************************************************************************

##🔧 Posibilidad de mejoras

- Implementar RabbitMQ para peticiones asincrónicas
- Implementar circuit breaker
- Implementar keycloak
- Separar servicio de seguridad del de usuarios

****************************************************************************************************

##📜 Licencia
Este proyecto está bajo la licencia MIT.

****************************************************************************************************

##👨‍💻 Equipo
Desarrollado con mucho amor y esfuerzo por el desarrollador Alberto Arce 🚀

Si tienes preguntas o sugerencias, contáctame en cucuarce@gmail.com.
