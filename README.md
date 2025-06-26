# 🧠 Flashcards App

Una aplicación web de tarjetas de estudio (flashcards) con preguntas y respuestas para practicar y memorizar de forma interactiva.

---

## 🚀 Descripción

Este proyecto tiene como objetivo crear una aplicación de estudio con tarjetas dinámicas. El backend se desarrollará con Spring Boot, cargando inicialmente los datos desde un archivo JSON. El frontend estará construido con HTML, CSS y JavaScript, con animaciones y efectos para una experiencia de usuario fluida y atractiva.

---

## ⚙️ Tecnologías

- **Backend:** Java + Spring Boot
- **Frontend:** HTML, CSS, JavaScript
- **Base de datos:** JSON local (prototipo) / BD relacional (futuro)
- **Despliegue:** Pendiente

---

## 🛣️ Roadmap para Aplicación de Flashcards

### 🧱 FASE 1: Fundamentos y Prototipo Local (JSON + HTML/CSS/JS)

#### Objetivo: Tener un prototipo funcional simple sin backend real

1. **Diseñar estructura JSON** para tarjetas (id, pregunta, respuesta, categoría...).
2. **Cargar JSON localmente** desde JavaScript (sin API aún).
3. **Diseñar tarjeta básica** en HTML y CSS.
4. **Añadir animación de “flip”** al hacer clic.
5. **Mostrar una tarjeta al azar o en secuencia**.
6. **Agregar botón "Siguiente"** para pasar a la siguiente tarjeta.
7. **Diseño responsive básico** (funciona en móvil y PC).
8. **Modo noche/día** (switch de estilo CSS).
9. **Separar tarjetas por categoría o mazo** desde el frontend.
10. **Guardar progreso en LocalStorage** (ej: qué tarjetas ya viste hoy).

---

### 🔌 FASE 2: API Backend con Spring Boot + Carga desde JSON

#### Objetivo: Servir las tarjetas desde una API real

11. **Inicializar proyecto Spring Boot** con dependencias Web.
12. **Modelo de entidad `Card` y DTOs.**
13. **Cargar tarjetas desde archivo JSON al arrancar** (CommandLineRunner).
14. **Crear endpoints REST básicos**:

- `GET /cards`
- `GET /cards/{id}`
- `GET /decks/{deck}/cards`

15. **CORS config para permitir frontend.**
16. **Conectar frontend al backend** usando `fetch` o `axios`.
17. **Mostrar tarjetas dinámicamente desde la API.**

---

### 🗃️ FASE 3: Administración de tarjetas

#### Objetivo: Permitir añadir, editar y eliminar tarjetas

18. **Endpoints `POST /cards`, `PUT`, `DELETE`.**
19. **Validador de datos y manejo de errores.**
20. **Formulario web simple para crear/editar tarjetas.**
21. **Guardar tarjetas modificadas en memoria (por ahora).**
22. **Actualizar la vista al crear/modificar tarjeta.**

---

### 🧠 FASE 4: Lógica de estudio y aprendizaje

#### Objetivo: Que el usuario pueda practicar de forma útil

23. **Modo "estudio" con tarjetas aleatorias.**
24. **Modo "examen" con control de tiempo.**
25. **Seguimiento de puntuación/respuestas acertadas.**
26. **Estadísticas de progreso local.**
27. **Sistema de repetición espaciada simple (Spaced Repetition).**
28. **Guardar progreso de usuario en LocalStorage o sesión.**

---

### 🛢️ FASE 5: Persistencia con Base de Datos

#### Objetivo: Guardar datos reales (opcional pero recomendable)

29. **Agregar base de datos (H2 o PostgreSQL).**
30. **Repositorio con Spring Data JPA.**
31. **Migración de JSON a BD.**
32. **Agregar autenticación de usuario básica.**
33. **Guardar progreso por usuario en la base de datos.**

---

### 🧪 FASE 6: Pulido y extras

#### Objetivo: Mejorar experiencia, diseño y funciones

34. **Animaciones mejoradas (entrada/salida, swipe, drag).**
35. **Barajado de tarjetas.**
36. **Soporte para imágenes, audio, fórmulas en tarjetas.**
37. **Filtro por dificultad o tags.**
38. **Modo revisión diaria.**
39. **Diseño mejorado y responsive completo.**
40. **Panel de administración completo (crear, editar, borrar).**

---

### ☁️ FASE 7: Despliegue y escalado

#### Objetivo: Publicar la app y prepararla para crecer

41. **Deploy backend en Render/Heroku/VPS.**
42. **Deploy frontend en Vercel/Netlify.**
43. **Dominio propio y configuración HTTPS.**
44. **Soporte multilenguaje (si aplica).**
45. **Analíticas de uso (Google Analytics, Matomo...).**

---
