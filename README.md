Adodas API
Adodas API es una aplicación Spring Boot para gestionar servicios, pedidos, clientes, artículos y reembolsos, con operaciones completas de CRUD, filtros dinámicos y validación.

Características principales
CRUD completo: Items, Customers, Orders, Services y Refunds.
Búsquedas avanzadas: Filtros dinámicos para cada entidad.
Gestión de excepciones: Manejo de errores 400, 404 y 500.
Consultas personalizadas: Operaciones JPQL y SQL nativas.
Logs: Registro de operaciones y errores.

Requisitos previos
Java 17 o superior.
Maven 3.8 o superior.
PostgreSQL u otra base de datos compatible.
Postman para pruebas.


Configuración
Clona el repositorio

git clone https://github.com/usuario/adodas_api.git
cd adodas_api

Configura la base de datos en application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/adodas_api
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

Ejecuta la aplicación:
mvn spring-boot:run

Principales Endpoints
CRUD General
Items: /items, /items/{id}, /items/search
Customers: /customers, /customers/{id}, /customers/search
Orders: /orders, /orders/{id}, /orders/search
Services: /services, /services/{id}, /services/search
Refunds: /refunds, /refunds/{id}, /refunds/search
