# 🧠 Flashcards App

Aplicación web interactiva de tarjetas de estudio (flashcards) construida con HTML, CSS, JavaScript y Spring Boot como backend.

Permite practicar preguntas y respuestas de forma visual e intuitiva mediante una animación de giro. Ideal para repasar conocimientos o preparar exámenes.

---

## ✨ Características

### Frontend

* Tarjetas interactivas con animación de volteo (flip).
* Pregunta por un lado, respuesta por el otro.
* Navegación entre tarjetas (anterior / siguiente).
* Selector de tema visual con cambio de paleta (modo claro/oscuro).
* Conexión a backend REST para cargar y guardar tarjetas.

### Backend

* API REST construida con Spring Boot.
* Tarjetas con atributos: `question`, `answer`, `topic`, `status` (3 niveles de aprendizaje).
* Endpoints para CRUD básico, filtro por tema y estado.
* Manejo de errores con `@ControllerAdvice` y respuestas personalizadas.
* Carga de datos iniciales con `@PostConstruct`.

---

## 📁 Estructura del proyecto

```
flashcards-app/
├── backend/               # Proyecto Spring Boot
│   ├── src/
│   ├── pom.xml
│   └── ...
├── frontend/              # Interfaz HTML, CSS y JS
│   ├── index.html
│   ├── script.js
│   └── style.css
├── .gitignore
└── README.md
```

---

## 🚀 Cómo usarlo

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu_usuario/flashcards-app.git
cd flashcards-app
```

### 2. Ejecutar el backend (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080/api/flashcards/`

### 3. Ejecutar el frontend

* Abre `frontend/index.html` con Live Server o directamente desde el navegador
* Las tarjetas se cargarán automáticamente desde el backend

---

## 💡 Endpoints disponibles

* `GET /api/flashcards/` → Obtener todas las tarjetas
* `GET /api/flashcards/{id}` → Obtener tarjeta por ID (404 si no existe)
* `GET /api/flashcards?topic=XYZ` → Filtrar por tema
* `GET /api/flashcards?status=APRENDIDA` → Filtrar por nivel de aprendizaje
* `GET /api/flashcards?topic=XYZ&status=MEDIO` → Filtro combinado
* `POST /api/flashcards/` → Crear nueva tarjeta
* `POST /api/flashcards/{id}/status?status=MEDIO` → Actualizar estado
* `DELETE /api/flashcards/{id}` → Eliminar tarjeta

---

## 📅 Tecnologías utilizadas

* Frontend: HTML5, CSS3, JavaScript (vanilla)
* Backend: Java 17, Spring Boot, Maven
* Base de datos: H2 en memoria
* Herramientas: Postman, Live Server (VSCode)

---

## 🔖 Próximas mejoras

* Panel de administración para crear/editar tarjetas desde el frontend
* Aprendizaje espaciado automatizado
* Exportar tarjetas (JSON, CSV)
* Registro de usuario y tarjetas personalizadas

---

## 📄 Licencia

Este proyecto es de uso libre para fines educativos y perso
