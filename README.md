# GymApp

Aplicación de escritorio para gestionar el gimnasio, desarrollada con Java, Swing, Spring Boot y PostgreSQL.

## Requisitos

- JDK 21 o superior y `JAVA_HOME` configurado.
- Acceso a la base PostgreSQL del gimnasio. Para crear las tablas, ejecuta `database/schema.sql` en una base nueva.
- Conexión a Internet para descargar las dependencias de Maven la primera vez.

## Ejecutar en Windows

Desde PowerShell:

```powershell
cd GymApp
.\mvnw.cmd spring-boot:run
```

También puedes abrir esta carpeta en tu editor como proyecto Maven y ejecutar `com.smartfit.gymApp.GymAppApplication`.

La configuración se encuentra en `src/main/resources/application.properties`. Puedes sustituir la conexión mediante las variables `GYMAPP_DB_URL`, `GYMAPP_DB_USER` y `GYMAPP_DB_PASSWORD`. La consulta de DNI utiliza `RENIEC_API_TOKEN`.

## Comprobar el código

```powershell
.\mvnw.cmd "-Dtest=*Test,!GymAppApplicationTests" test
.\mvnw.cmd spring-boot:run
```

Esta selección ejecuta las pruebas unitarias sin iniciar la aplicación ni conectarse a PostgreSQL. `GymAppApplicationTests` comprueba el contexto Spring y requiere la configuración de sus servicios.

## Estructura

- `src/main/java`: código de la aplicación.
- `src/main/resources`: configuración e imágenes.
- `src/test/java`: pruebas del código.
- `pom.xml`: dependencias y compilación.
- `.mvn`, `mvnw` y `mvnw.cmd`: Maven Wrapper.

Maven genera `target` al compilar; esa carpeta se excluye del repositorio.
