package com.example.blind_assistance_project.Activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blind_assistance_project.R;
import com.example.blind_assistance_project.ml.MyModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;


import android.media.MediaPlayer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;

import java.util.ArrayList;
public class currency_feature extends AppCompatActivity implements TextToSpeech.OnInitListener{
    ImageView image ;
    TextView currency_text;
    TextToSpeech textToSpeech;

    ProgressBar b;
    MediaPlayer please_say_again_currency;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_feature);
        textToSpeech = new TextToSpeech(this, this);
        image = findViewById(R.id.image_currency_id);
        Bitmap bitmap = getIntent().getParcelableExtra("imageBitmap");
        image.setImageBitmap(bitmap);
        currency_text = findViewById(R.id.generated_currency_id);
        b=findViewById(R.id.currency_progress_id);

        please_say_again_currency =MediaPlayer.create(currency_feature.this,R.raw.please_say_again);

        Bitmap img = Bitmap.createScaledBitmap(bitmap , 224,224,true);
        img = img.copy(Bitmap.Config.ARGB_8888, true);
        try {
            MyModel model = MyModel.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(img);
            ByteBuffer byteBuffer = tensorImage.getBuffer();


            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            MyModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            int myindex = assignLabels(outputFeature0.getFloatArray());

            if(myindex==0 || myindex==1){
                currency_text.setText("this Currency is 100 Pounds");
            }
            else if(myindex==2 || myindex==3){
                currency_text.setText("this Currency is 10 Pounds");
            }
            else if(myindex== 4|| myindex==5){
                currency_text.setText("this Currency is 200 Pounds");
            }
            else if(myindex==6 || myindex==7){
                currency_text.setText("this Currency is 20 Pounds");
            }
            else if(myindex==8 || myindex==9){
                currency_text.setText("this Currency is 50 Pounds");
            }
            else if(myindex==10 || myindex==11){
                currency_text.setText("this Currency is 5 Pounds");
            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
        // Call readAloud to speak the currency text
        Handler h= new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                b.setVisibility(View.INVISIBLE);
                readAloud();
            }
        },800);

        // Request runtime permissions if not granted

        Handler h1= new Handler();
        h1.postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeSpeechRecognizer();
            }
        },5000);


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
            Intent i =new Intent(currency_feature.this, Feature_activity.class);
            startActivity(i);
        }else {
            please_say_again_currency.start();
            Handler h1= new Handler();
            h1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    restartListening();
                }
            },1000);
        }
    }

    public static int assignLabels(float[] predictions) {
        // Find the index of the maximum probability
        int maxIndex = 0;
        double maxValue = predictions[0];
        for (int i = 1; i < predictions.length; i++) {
            if (predictions[i] > maxValue) {
                maxValue = predictions[i];
                maxIndex = i;
            }
        }
        // In case of softmax, the index of the maximum probability corresponds to the predicted class
        return maxIndex;
    }

    @Override
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
        String text = currency_text.getText().toString();

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