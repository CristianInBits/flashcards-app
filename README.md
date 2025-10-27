# 🧠 Flashcards Backend

**Flashcards App** es una API moderna construida con **Spring Boot 3**, **Java 21** y **PostgreSQL**, diseñada para gestionar mazos (*decks*) y tarjetas (*cards*) de estudio.  
Este backend forma parte del ecosistema completo de Flashcards-App (backend + frontend).

---

## ⚙️ Tecnologías principales

| Categoría | Tecnología |
|------------|-------------|
| ☕ Backend | Spring Boot 3 (Java 21) |
| 🐘 Base de datos | PostgreSQL 16 + Flyway |
| 🧩 ORM | Spring Data JPA / Hibernate |
| 📦 Contenedores | Docker & Docker Compose |
| 🧰 Build Tool | Gradle Kotlin DSL |
| ✅ Validación | Jakarta Validation |
| 🛡️ Manejo de errores | `@RestControllerAdvice` global (API friendly) |

---

## 🧱 Arquitectura

La aplicación sigue una estructura modular y clara por dominios:

```bash
flashcards-app/
└── backend/
├── card/       ← tarjetas (Card)
│   ├── domain/   entidad JPA
│   ├── dto/      request/response
│   ├── repo/     repositorio JPA
│   ├── service/  lógica de negocio
│   └── api/      controladores REST
│
├── deck/       ← mazos (Deck)
│   ├── domain/
│   ├── dto/
│   ├── repo/
│   ├── service/
│   └── api/
│
├── common/     ← utilidades compartidas
│   ├── api/      controladores globales (Health)
│   ├── error/    manejadores de excepciones
│   └── web/      helpers genéricos (paginación)
│
└── resources/
    ├── application.yml
    └── db/migration/  ← scripts Flyway (V001 Deck, V002 Card)
````

---

## 📚 Estado actual del proyecto

✅ **Módulo Deck**

- CRUD completo con validaciones, paginación y orden.
- Entidad `Deck` con timestamps automáticos (`createdAt`).
- Migración inicial `V001__create_deck.sql`.

✅ **Módulo Card**

- CRUD completo asociado a cada mazo.
- Búsqueda por texto y etiquetas.
- Migración `V002__create_card.sql`.

✅ **Infraestructura**

- PostgreSQL gestionado por Docker Compose.
- Migraciones automáticas con Flyway.
- Configuración limpia en `application.yml`.

---

## 🚀 Cómo ejecutar

1. **Levantar la base de datos**

   ```bash
   docker compose up -d
   ```

2. **Iniciar el backend**

   ```bash
   ./gradlew bootRun
   ```

3. **Probar endpoints**

   - Health check: `GET http://localhost:8080/api/health`
   - Decks API: `/api/decks`
   - Cards API: `/api/decks/{deckId}/cards`

---

## 🧩 Próximos pasos

- 📆 Sistema de revisiones (spaced repetition).
- 📊 Registro de sesiones y estadísticas.
- 🔐 Autenticación JWT y usuarios.
- 🌍 Despliegue en entorno cloud.

---

> 💬 Proyecto desarrollado por **CristianInBits**
> Código limpio, modular y documentado para crecer fácilmente 🚀
