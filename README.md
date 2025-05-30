# 🧠 Flashcards App

Aplicación web interactiva de tarjetas de estudio (flashcards) construida con HTML, CSS y JavaScript en el frontend, y una API REST en Java Spring Boot en el backend.

Permite practicar preguntas y respuestas de forma visual e intuitiva mediante una animación de giro y aplicar técnicas de aprendizaje espaciado.

---

## ✨ Características

* Tarjetas interactivas con animación de volteo (flip).
* Pregunta por un lado, respuesta por el otro.
* Navegación entre tarjetas (anterior / siguiente).
* Clasificación por temas y niveles de aprendizaje.
* Aprendizaje espaciado con sistema de revisión progresiva.
* Actualización del estado mediante botones "✅ Recordé" y "❌ Fallé".
* Persistencia completa en base de datos.

---

## 📁 Estructura del proyecto

```tree
flashcards-app/
├── frontend/
│   ├── index.html        # Página principal
│   ├── style.css         # Estilos y animación
│   ├── script.js         # Lógica del cliente
│   └── flashcards.json   # Datos iniciales para carga
│
├── backend/
│   ├── src/main/java/... # Código fuente Java Spring Boot
│   ├── src/main/resources/data/flashcards.json  # Datos de carga inicial
│   └── application.properties  # Configuración de la BD y app
└── data/flashcards-db.mv.db  # Base de datos persistente (H2)
```

---

## 🚀 Cómo usarlo

1. Clona el repositorio:

    ```bash
    git clone https://github.com/tu_usuario/flashcards-app.git
    ```

2. Abre el proyecto en VSCode.
3. Ejecuta el backend (`Spring Boot`) para activar la API REST:

   * Usa `./mvnw spring-boot:run` o desde tu IDE.
4. Abre el `index.html` con Live Server (recomendado).
5. Interactúa con las tarjetas:

   * Clic para girar y ver la respuesta.
   * Usa los botones para registrar si recordaste o fallaste.

---

## 📌 Tecnologías utilizadas

* Frontend: HTML5 + CSS3 (con transformaciones 3D) + JavaScript
* Backend: Spring Boot + Maven + H2 (modo archivo persistente)
* JSON: para datos iniciales

---

## 🔁 Aprendizaje espaciado

Cada tarjeta tiene un `nivel de memorización (0–4)` y una `fecha de próxima revisión`. Al pulsar:

* `✅ Recordé` → Aumenta el nivel y programa la próxima revisión según el nuevo nivel
* `❌ Fallé` → Reinicia el nivel a 0 y agenda revisión inmediata

| Nivel | Intervalo | Ejemplo próximo repaso |
| ----- | --------- | ---------------------- |
| 0     | inmediato | hoy                    |
| 1     | +1 día    | mañana                 |
| 2     | +3 días   | en 3 días              |
| 3     | +7 días   | en 1 semana            |
| 4     | +30 días  | en 1 mes               |

La información se guarda en base de datos y se conserva entre sesiones ✅

---

## 📄 Licencia

Este proyecto es de uso libre para fines educativos y personales.

---
