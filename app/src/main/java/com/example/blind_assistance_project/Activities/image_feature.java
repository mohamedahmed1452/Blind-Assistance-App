package com.example.blind_assistance_project.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blind_assistance_project.R;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;

import java.util.ArrayList;
public class image_feature extends AppCompatActivity implements TextToSpeech.OnInitListener {

    ImageView image;
    TextView t;

    TextToSpeech textToSpeech;
    ProgressBar b;
    MediaPlayer please_say_again_image;

    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_feature);
        textToSpeech = new TextToSpeech(this, this);
        image = findViewById(R.id.image_caption_id);
        Bitmap bitmap = getIntent().getParcelableExtra("imageBitmap");
        image.setImageBitmap(bitmap);
        t = findViewById(R.id.caption_id);
        b=findViewById(R.id.progress_id);
        sendImageToAPI(bitmap);

        please_say_again_image =MediaPlayer.create(image_feature.this,R.raw.please_say_again);

        // Request runtime permissions if not granted

        Handler h1= new Handler();
        h1.postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeSpeechRecognizer();
            }
        },10000);

    }


    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    processVoiceCommand(matches.get(0));
                }
//                restartListening();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        startListening();
    }

    private void startListening() {
        if (speechRecognizer != null) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            speechRecognizer.startListening(intent);
        }
    }

    private void restartListening() {
        if (speechRecognizer != null) {
            speechRecognizer.cancel();
            startListening();
        }
    }

    private void processVoiceCommand(String command) {
        if (command.equalsIgnoreCase("back to main screen")) {
            Intent i =new Intent(image_feature.this, Feature_activity.class);
            startActivity(i);
        }else {
            please_say_again_image.start();
            Handler h1= new Handler();
            h1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    restartListening();
                }
            },1000);
        }
    }

    private void sendImageToAPI(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg", RequestBody.create(MediaType.parse("image/jpeg"), imageBytes))
                .build();

        Request request = new Request.Builder()
                .url("https://323c-45-241-2-143.ngrok-free.app/generate_caption")
                .post(requestBody)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.e("API_REQUEST", " "+e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String caption = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t.setText(caption);
                            Handler h= new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    b.setVisibility(View.INVISIBLE);
                                    readAloud();
                                }
                            },500);
                        }
                    });
                } else {
                    Log.e("API_REQUEST", "Failed to receive response from API: " + response.code());
                }
            }
        });
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language (e.g., Locale.US for US English)
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void readAloud() {
        String text = t.getText().toString();

        // Check if the TextToSpeech engine is initialized
        if (textToSpeech != null && !text.isEmpty()) {
            // Speak the text
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Toast.makeText(this, "Text or TextToSpeech engine not available", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected  void onDestroy() {
        super.onDestroy();
        // Shutdown TextToSpeech when activity is destroyed to release resources
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}