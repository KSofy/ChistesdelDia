# ChistesdelDia
GEMINI
Prompt Utilizados:
PARTE DEL FUNCIONAMIENTO.
# 🤖 Instrucción para el Agente de IA

**Genera una aplicación Android en Java (usando Android Studio) llamada "Chistes del Día" que cumpla con los siguientes requisitos:**

---

## 📱 App: Chistes del Día (con JokeAPI)

### 🎯 Objetivo
Crear una app que muestre **un chiste nuevo cada día** usando la API pública **[JokeAPI](https://v2.jokeapi.dev/)**. El mismo chiste debe mostrarse durante todo el día, incluso si la app se abre varias veces.

### 🧩 Requisitos funcionales
- Al abrir la app, verificar la fecha actual (`yyyy-MM-dd`).
- Si ya hay un chiste guardado para hoy → mostrarlo.
- Si es un día nuevo → hacer una llamada a `https://v2.jokeapi.dev/joke/Any?lang=en&type=single,twopart`.
- Guardar el chiste y la fecha en `SharedPreferences`.
- Mostrar el chiste en un `TextView` grande.
- Incluir botones para **Copiar** y **Compartir** el chiste.
- Si falla la red, mostrar un chiste predeterminado y notificar al usuario.

### 🛠️ Tecnologías
- Lenguaje: **Java**
- HTTP client: **Volley**
- Almacenamiento: **SharedPreferences**
- Permisos: `android.permission.INTERNET`

### 📁 Archivos esperados
1. `AndroidManifest.xml` (con permiso de internet)
2. `activity_main.xml` (UI simple)
3. `MainActivity.java` (lógica completa: fecha, red, guardado, UI)

### 💡 Notas importantes
- Manejar ambos tipos de chiste: `single` (texto directo) y `twopart` (`setup` + `delivery`).
- Formatear el chiste de dos partes como:  
  `"¿Por qué...?\n\n¡Porque...!"`
- No hacer más de una llamada por día.
- Código limpio, comentado y listo para compilar en Android Studio.

Si hay una duda pregúntame antes de codificar.

PARTE VISUAL:

# 🤖 Instrucción 
- Mejorar la parte visual y la experiencia para el usuario.

Por favor, mejora **solo la parte visual y de experiencia de usuario** (UI/UX) manteniendo toda la lógica funcional intacta.

---

## 🎨 Requisitos de la nueva interfaz

### 1. **Estilo visual**
- Usa un **tema colorido y alegre**: fondo amarillo claro (`#FFF9C4`) o degradé suave (amarillo → naranja claro).
- Texto en color oscuro o negro para buen contraste.
- Añade un **emoji de risa** (😂, 🤣 o 😆) en la barra de título o como decoración.
- Bordes redondeados en botones y tarjetas.

### 2. **Tipografía**
- Usa una fuente redondeada y amigable (como `sans-serif-medium` o `Comic Neue` si se incluye).
- El chiste debe mostrarse en un `TextView` grande, centrado, con buen espaciado.

### 3. **Animaciones y microinteracciones**
- Al cargar un chiste nuevo, haz una **animación sutil de fade-in**.
- Al presionar "Copiar", muestra un pequeño **toast con emoji**: "¡Copiado! 😄".
- Al compartir, usa el ícono estándar pero con fondo animado (opcional).

### 4. **Elementos gráficos**
- Agrega un **ícono de micrófono o teatro** (🎭) arriba del chiste.
- Si el chiste es de dos partes, muestra un **"..." con animación de puntos suspensivos** antes de la entrega (ej: con un `TextView` que cambia cada segundo: `.`, `..`, `...`).

### 5. **Botones**
- Botones con fondo blanco, borde colorido y sombra suave.
- Texto de botones:  
  - 📋 **¡Copia el chiste!**  
  - ↗️ **¡Compártelo!**

---

## 📁 Archivos a modificar
- `activity_main.xml`: rediseñar completamente con los elementos visuales descritos.
- `MainActivity.java`: agregar animaciones simples (fade-in), toasts con emojis y lógica visual para chistes de dos partes.
- (Opcional) `colors.xml`, `themes.xml`: definir una paleta de colores divertida.

> ⚠️ **Importante**: No cambies la lógica de red, JokeAPI, ni SharedPreferences. Solo mejora la UI/UX.

---

Por favor, genera el código XML y Java actualizado listo para reemplazar en mi proyecto existente.

Hubo algún problema? No, no hubo ningún problema.

COPILOT

En copilot si hubo problema con el idioma.



Por favor, genera el código XML y Java actualizado listo para reemplazar en mi proyecto existente.
Hubo algún problema? No, no hubo ningún problema.
