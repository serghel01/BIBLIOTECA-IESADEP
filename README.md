
# Biblioteca IESADEP - Proyecto web (Frontend + Backend) - Versión mejorada

Este proyecto contiene:
- **Frontend**: páginas estáticas en Bootstrap 5 servidas desde Spring Boot (`backend/src/main/resources/static`).
- **Backend**: Spring Boot (Java 17, Maven) con integración a **Firebase Firestore**.
- **Autenticación**: login de administrador que devuelve **JWT** (variable de entorno `ADMIN_JWT_SECRET`).
- **Import/Export**: los endpoints soportan XLSX (Excel) y CSV (usando Apache POI).
- **Reportes**: generación de PDF con gráfico (JFreeChart + Apache PDFBox).
- **Dockerfile** incluido para construir la imagen del backend.

Variables de entorno importantes:
- `ADMIN_USER` - usuario administrador (ej: admin)
- `ADMIN_PASS` - contraseña del admin
- `ADMIN_JWT_SECRET` - clave secreta para JWT (debe ser larga)
- `FIREBASE_CREDENTIALS_PATH` - ruta al JSON del servicio de Firebase
- `PORT` - puerto (opcional)

Cómo probar localmente:
1. Configura las variables de entorno (especialmente `ADMIN_JWT_SECRET` y `FIREBASE_CREDENTIALS_PATH`).
2. Desde la carpeta `backend`:
```bash
mvn package
java -jar target/backend-0.0.2-SNAPSHOT.jar
```
3. Abre en el navegador: `http://localhost:8080/index.html`

Despliegue en Render:
- Construye la imagen con Docker (`docker build . -t iesadep-backend`) y empújala a tu registro; en Render configura los environment variables.

Notas:
- Esta versión es una **implementación funcional** del prototipo: endpoints reales para importar XLSX, generar PDF con gráficos y autenticación por JWT.
- Para producción se recomienda mejorar el manejo de errores, validaciones y seguridad (rotación de claves, HTTPS, etc.)

