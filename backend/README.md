# Backend (Spring Boot)

## Requisitos

- **Java/JDK 21** (el `pom.xml` fija `java.version=21` y Spring Boot 4 requiere Java 21+).
- Acceso a Internet la primera vez (el wrapper descargará Maven).

## Compilar / tests (Windows PowerShell)

1. Instala un JDK 21 (por ejemplo Temurin 21).
2. Configura `JAVA_HOME` y añade el `bin` al `PATH` (en la sesión actual):

```powershell
$env:JAVA_HOME = "C:\\Program Files\\Eclipse Adoptium\\jdk-21.*"  # ajusta a tu ruta real
$env:Path = "$env:JAVA_HOME\\bin;$env:Path"
java -version
```

3. Ejecuta Maven mediante el wrapper:

```powershell
cd "c:\\Users\\franj\\IdeaProjects\\proyecto-secretaria\\backend"
.\\mvnw.cmd test
```

## Avisos públicos

Endpoints añadidos en `AvisoPublicoController`:

- `GET /avisos-publicos` → lista visibles y vigentes
- `GET /avisos-publicos/destacados` → lista destacados visibles y vigentes

Administración (requiere rol `ADMIN` o `SECRETARIA` por anotaciones `@PreAuthorize`):

- `GET /avisos-publicos/admin`
- `GET /avisos-publicos/admin/{id}`
- `POST /avisos-publicos`
- `PUT /avisos-publicos/{id}`
- `DELETE /avisos-publicos/{id}`

Cuando un aviso no existe, el servicio lanza `AvisoPublicoNoEncontradoException` y el `GlobalExceptionHandler` devuelve **HTTP 404**.

