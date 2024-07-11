package com.example.blind_assistance_project.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Toast;

import com.example.blind_assistance_project.R;

import java.util.ArrayList;

public class Feature_activity extends AppCompatActivity {

    CardView currency_card;
    CardView image_card;
    public static final int CAMERA_ACTION_CODE = 1;
    public static boolean Currency_Button_Status;
    public static boolean Caption_Button_Status;

    MediaPlayer currency_mb3;
    MediaPlayer image_mb3;

    MediaPlayer please_say_again_feature;
    MediaPlayer guids;

    private SpeechRecognizer speechRecognizer;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private static boolean guidFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        Currency_Button_Status = false;
        Caption_Button_Status = false;
        currency_card = findViewById(R.id.cardView3);
        image_card = findViewById(R.id.cardView2);
        currency_mb3 = MediaPlayer.create(Feature_activity.this, R.raw.currency_classification);
        image_mb3 = MediaPlayer.create(Feature_activity.this, R.raw.image_caption_generation);

        please_say_again_feature =MediaPlayer.create(Feature_activity.this,R.raw.please_say_again);
        guids =MediaPlayer.create(Feature_activity.this,R.raw.guids);

        currency_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                currency_mb3.start();
                return true;
            }
        });
        image_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                image_mb3.start();
                return true;
            }
        });
        currency_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Currency_Button_Status = true;
                Caption_Button_Status = false;
                make_action();
            }
        });
        image_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Caption_Button_Status = true;
                Currency_Button_Status = false;
                make_action();
            }
        });

        if(!guidFlag){
            guidFlag=true;
            guids.start();
            guids.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    initiateVoiceReg();
                }
            });
        }
        else{
            initiateVoiceReg();
        }





    }

    private void initiateVoiceReg(){
        // Request runtime permissions if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            initializeSpeechRecognizer();
        }
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

    public void make_action() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_ACTION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTION_CODE && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            if (Caption_Button_Status) {
                Intent intent = new Intent(Feature_activity.this, image_feature.class);
                intent.putExtra("imageBitmap", bitmap);
                startActivity(intent);
            } else if (Currency_Button_Status) {
                Intent intent = new Intent(Feature_activity.this, currency_feature.class);
                intent.putExtra("imageBitmap", bitmap);
                startActivity(intent);
            }
        }
    }

    private void processVoiceCommand(String command) {
        if (command.equalsIgnoreCase("currency classification")) {
            Currency_Button_Status = true;
            Caption_Button_Status = false;
            make_action();
        } else if (command.equalsIgnoreCase("image caption generation")) {
            Caption_Button_Status = true;
            Currency_Button_Status = false;
            make_action();
        } else {

            please_say_again_feature.start();
            Handler h1= new Handler();
            h1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    restartListening();
                }
            },1000);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSpeechRecognizer();
            } else {
                Toast.makeText(this, "Permission denied. Speech recognition will not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        startListening();
//    }


    @Override
    protected void onRestart() {
        super.onRestart();
        initializeSpeechRecognizer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        guids.stop();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
