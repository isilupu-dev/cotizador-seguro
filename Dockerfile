# Imagen base oficial de Maven para construir la app
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar los archivos necesarios para la compilación
COPY pom.xml .
COPY src ./src

# Compila el proyecto, ejecuta las pruebas unitarias y genera el archivo JAR
RUN mvn clean verify

# Imagen de JDK para ejecutar la aplicación Spring Boot
FROM eclipse-temurin:17-jdk-jammy

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR desde la imagen anterior
COPY --from=build /app/target/cotizador-seguro-0.0.1-SNAPSHOT.jar app.jar

# Puerto en el que escucha la aplicación
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]