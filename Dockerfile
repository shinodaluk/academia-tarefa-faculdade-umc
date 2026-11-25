# Etapa 1: compila as classes e empacota o .war (sem NetBeans nem Ant)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY lib/javax.servlet-api-4.0.1.jar lib/
COPY src/java src/java
COPY web web
RUN mkdir -p war/WEB-INF/classes war/WEB-INF/lib \
 && cp -r web/. war/ \
 && cp src/java/docs/jar/*.jar war/WEB-INF/lib/ \
 && find src/java -name '*.java' > fontes.txt \
 && javac --release 21 -encoding UTF-8 -proc:none \
      -cp "lib/javax.servlet-api-4.0.1.jar:src/java/docs/jar/*" \
      -d war/WEB-INF/classes @fontes.txt \
 && jar -cf WebApplication1.war -C war .

# Etapa 2: só o Tomcat e o .war; o JDK e os fontes ficam para trás
FROM tomcat:9.0-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/WebApplication1.war /usr/local/tomcat/webapps/WebApplication1.war
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=5 \
  CMD curl -fsS http://localhost:8080/WebApplication1/api/planos > /dev/null || exit 1
