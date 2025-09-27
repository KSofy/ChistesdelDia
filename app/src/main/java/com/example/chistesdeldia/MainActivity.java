package com.example.chistesdeldia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ChistesDelDiaPrefs";
    private static final String KEY_JOKE = "joke";
    private static final String KEY_DATE = "date";
    private static final String DEFAULT_JOKE = "No se pudo cargar el chiste. ¡Inténtalo de nuevo más tarde!";

    private TextView jokeTextView;
    private TextView jokeDeliveryIndicatorTextView; // TextView para "..."
    private Button copyButton;
    private Button shareButton;
    private SharedPreferences sharedPreferences;

    private Handler deliveryIndicatorHandler = new Handler(Looper.getMainLooper());
    private Runnable deliveryIndicatorRunnable;
    private int dotCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        jokeTextView = findViewById(R.id.jokeTextView);
        jokeDeliveryIndicatorTextView = findViewById(R.id.jokeDeliveryIndicatorTextView); // Inicializa el nuevo TextView
        copyButton = findViewById(R.id.copyButton);
        shareButton = findViewById(R.id.shareButton);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        loadJoke();

        copyButton.setOnClickListener(v -> copyJokeToClipboard());
        shareButton.setOnClickListener(v -> shareJoke());
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void loadJoke() {
        String currentDate = getCurrentDate();
        String savedDate = sharedPreferences.getString(KEY_DATE, null);
        String savedJoke = sharedPreferences.getString(KEY_JOKE, null);

        if (currentDate.equals(savedDate) && savedJoke != null) {
            displayJoke(savedJoke, false); // No es un chiste nuevo, no animar como twopart
        } else {
            fetchNewJoke(currentDate);
        }
    }

    private void fetchNewJoke(final String currentDate) {
        if (!isNetworkAvailable()) {
            displayJoke(DEFAULT_JOKE, false);
            Toast.makeText(this, "Error de red. Mostrando chiste predeterminado.", Toast.LENGTH_LONG).show();
            return;
        }

        // Mostrar "Cargando..." antes de la llamada a la API
        jokeTextView.setText("Cargando chiste...");
        jokeDeliveryIndicatorTextView.setVisibility(View.GONE); // Ocultar indicador si estaba visible

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://v2.jokeapi.dev/joke/Any?lang=es&type=single,twopart";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String joke;
                        boolean isTwoPart = false;

                        if (jsonObject.has("joke")) {
                            joke = jsonObject.getString("joke");
                        } else if (jsonObject.has("setup") && jsonObject.has("delivery")) {
                            String setup = jsonObject.getString("setup");
                            String delivery = jsonObject.getString("delivery");
                            joke = setup + "\n\n" + delivery;
                            isTwoPart = true;
                        } else {
                            joke = DEFAULT_JOKE;
                            Toast.makeText(MainActivity.this, "Formato de chiste no reconocido.", Toast.LENGTH_SHORT).show();
                        }
                        saveJoke(currentDate, joke);
                        displayJoke(joke, isTwoPart); // Pasar si es de dos partes
                    } catch (JSONException e) {
                        e.printStackTrace();
                        displayJoke(DEFAULT_JOKE, false);
                        Toast.makeText(MainActivity.this, "Error al procesar el chiste.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    displayJoke(DEFAULT_JOKE, false);
                    Toast.makeText(MainActivity.this, "Error al obtener el chiste de la API.", Toast.LENGTH_LONG).show();
                });
        queue.add(stringRequest);
    }

    private void saveJoke(String date, String joke) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DATE, date);
        editor.putString(KEY_JOKE, joke);
        editor.apply();
    }

    /**
     * Muestra el chiste en el TextView con animación y manejo de chistes de dos partes.
     * @param joke El chiste a mostrar.
     * @param isTwoPart Indica si el chiste es de tipo "twopart".
     */
    private void displayJoke(String joke, boolean isTwoPart) {
        // Detener animación anterior del indicador de "..."
        deliveryIndicatorHandler.removeCallbacks(deliveryIndicatorRunnable);
        jokeDeliveryIndicatorTextView.setVisibility(View.GONE);

        if (isTwoPart && joke.contains("\n\n")) {
            final String[] parts = joke.split("\n\n", 2);
            jokeTextView.setText(parts[0]); // Mostrar solo el "setup" inicialmente
            animateFadeIn(jokeTextView);

            // Iniciar animación de "..."
            jokeDeliveryIndicatorTextView.setText(".");
            jokeDeliveryIndicatorTextView.setVisibility(View.VISIBLE);
            dotCount = 1;
            deliveryIndicatorRunnable = new Runnable() {
                @Override
                public void run() {
                    dotCount++;
                    if (dotCount > 3) dotCount = 1;
                    String dots = new String(new char[dotCount]).replace("\0", ".");
                    jokeDeliveryIndicatorTextView.setText(dots);
                    deliveryIndicatorHandler.postDelayed(this, 700); // Cambiar cada 0.7 segundos
                }
            };
            deliveryIndicatorHandler.postDelayed(deliveryIndicatorRunnable, 700);

            // Mostrar la "delivery" después de un retraso
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                deliveryIndicatorHandler.removeCallbacks(deliveryIndicatorRunnable); // Detener "..."
                jokeDeliveryIndicatorTextView.setVisibility(View.GONE);
                jokeTextView.setText(joke); // Mostrar chiste completo
                animateFadeIn(jokeTextView); // Podrías tener una animación diferente para la "delivery"
            }, 4000); // Retraso para mostrar la "delivery" (ej. 4 segundos)

        } else {
            jokeTextView.setText(joke);
            animateFadeIn(jokeTextView);
        }
    }

    /**
     * Aplica una animación de fade-in al TextView del chiste.
     */
    private void animateFadeIn(View view) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(800); // Duración de la animación en milisegundos
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setAlpha(0f); // Asegurarse de que esté invisible al inicio
            }
        });
        fadeIn.start();
    }

    private void copyJokeToClipboard() {
        String jokeToCopy = jokeTextView.getText().toString();
        // Asegurarse de no copiar si es el estado inicial "Cargando chiste..."
        if (!jokeToCopy.isEmpty() && !jokeToCopy.equals("Cargando chiste...") && !jokeToCopy.equals(DEFAULT_JOKE.split("\n\n")[0])) { // Evitar copiar solo el setup
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Chiste Copiado", jokeToCopy);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "¡Copiado! 😄", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay chiste válido para copiar", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareJoke() {
        String jokeToShare = jokeTextView.getText().toString();
        if (!jokeToShare.isEmpty() && !jokeToShare.equals(DEFAULT_JOKE) && !jokeToShare.equals("Cargando chiste...") && !jokeToShare.equals(DEFAULT_JOKE.split("\n\n")[0])) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, jokeToShare);
            startActivity(Intent.createChooser(shareIntent, "Compartir chiste vía"));
        } else {
            Toast.makeText(this, "No hay chiste válido para compartir.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener el handler si la actividad se destruye para evitar memory leaks
        deliveryIndicatorHandler.removeCallbacks(deliveryIndicatorRunnable);
    }
}
