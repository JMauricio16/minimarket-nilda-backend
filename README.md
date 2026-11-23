# Minimarket Nilda - Backend API REST

API REST desarrollada en **Java 21** y **Spring Boot** para la gestión de inventario, ventas, comprobantes y reportes del establecimiento **Minimarket Nilda**[cite: 1].

---

## Requisitos previos

Antes de ejecutar el proyecto, asegúrate de contar con lo siguiente:

* **Java JDK 21** configurado en las variables de entorno (`JAVA_HOME`)[cite: 1].
* **MySQL Server 8** instalado y en ejecución[cite: 1].
* ***Spring Boot 4.1.2**
---

## Base de datos

En la carpeta de **Base de datos** del proyecto encontrarás los recursos necesarios para preparar el entorno relacional en MySQL (`bd_minimarket_nilda`):

### Pasos para configurar la base de datos

1. Abre tu cliente de MySQL (por ejemplo, **MySQL Workbench** o consola) y asegúrate de que el servicio esté activo.
2. Crea la base de datos si el script no la crea automáticamente:
   ```sql
   CREATE DATABASE IF NOT EXISTS bd_minimarket_nilda CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Ejecuta el script de estructura (tablas y relaciones) sobre `bd_minimarket_nilda`.
4. Ejecuta el script de datos iniciales para contar con usuarios y categorías de prueba.

---

## Configuración del backend

Revisa el archivo `src/main/resources/application.properties` y verifica los parámetros de conexión a tu instancia local de MySQL:

```properties
spring.application.name=minimarket-nilda-api
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/bd_minimarket_nilda?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true
spring.datasource.username=tu_usuario_mysql
spring.datasource.password=tu_contraseña_mysql
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

---

## Ejecución del proyecto

1. Abre una terminal o consola en la raíz del proyecto (`minimarket-nilda-backend`).
2. Ejecuta la aplicación según tu sistema operativo:

   * **En Windows (`CMD` o `PowerShell`):**
     ```bat
     ./mvnw.cmd spring-boot:run
     ```

   * **En macOS / Linux (`Terminal`):**
     ```bash
     chmod +x mvnw
     ./mvnw spring-boot:run
     ```
3. Espera a que Spring Boot complete el arranque en el puerto `8080` y valide el mapeo JPA contra MySQL sin errores[cite: 1].
4. La API quedará disponible en:
   `http://localhost:8080/api`

---

