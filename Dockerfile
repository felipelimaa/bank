# Realiza o download da imagem do gradle e realiza a instalação
FROM gradle:7.3.3-jdk11-alpine as build

# Aplica permissão para o grupo e usuário no determinado diretório
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle --no-daemon --console=plain test build

# Realiza o download da imagem do java
FROM adoptopenjdk/openjdk11-openj9:alpine

# Define variáveis de ambiente
ENV POSTGRESQL_URL="jdbc:postgresql://postgresql-server/bank" POSTGRESQL_USERNAME="bank" POSTGRESQL_PASSWORD="bank"

# Declarando que o processo não será executado como usuário ROOT
RUN addgroup -S bank && adduser -S bank -G bank && mkdir /app && chown bank.bank /app

# Declarando o usuário que executará os comandos a partir dos próximos passos
USER bank

# Realiza a cópia do jar
COPY --from=build /home/gradle/src/build/libs/*.jar /app/bank.jar

# Expõe a porta 8080
EXPOSE 8080

# Executa o comando para rodar o jar
CMD ["java", "-jar", "/app/bank.jar"]


