# Etapa 1: Compilação da aplicação
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copia os arquivos do Maven
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Permite executar o Maven Wrapper no Linux
RUN chmod +x mvnw

# Baixa as dependências
RUN ./mvnw dependency:go-offline -B

# Copia o código-fonte
COPY src/ src/

# Compila e gera o JAR
RUN ./mvnw clean package -DskipTests -B

# Etapa 2: Execução da aplicação
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o JAR gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Porta utilizada pela aplicação
EXPOSE 8080

# Inicializa o Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
