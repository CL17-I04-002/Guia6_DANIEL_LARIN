package com.example.guia6_daniel_larin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guia6_daniel_larin.utils.AudioAsincrono;

public class MainActivity extends AppCompatActivity {

    private Button btnIniciar, btnReiniciar;
    private TextView txvActual, txvFinal;
    private AudioAsincrono audioAsincrono;
    private MediaPlayer reproductorMusica;

    private SeekBar seekbar;
    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvActual = findViewById(R.id.txvActual);
        txvFinal  = findViewById(R.id.txvFinal);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        //handler
        handler = new Handler();

        // SeekBar
        seekbar = (SeekBar) findViewById(R.id.seekBar);

        //MediaPlayer
        reproductorMusica = MediaPlayer.create(this, R.raw.nokia_tune);
        reproductorMusica.setAudioStreamType(AudioManager.STREAM_MUSIC);

        reproductorMusica.setOnPreparedListener(mp -> {
            seekbar.setMax(reproductorMusica.getDuration());// Valor Final
            playCycle();
            //reproductorMusica.start();
        });



        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            //hace un llamado a la perilla cuando se arrastra
            @Override
            public void onProgressChanged(SeekBar seekBar,
                                          int i/*progress*/,
                                          boolean b/*fromUser*/) {
                if(b){
                    progressChangedValue = i;
                    //Log.i("Tranverse Change: ", Integer.toString(i) );
                    reproductorMusica.seekTo(i);
                    seekbar.setProgress(i);
                    //mostrarPorcentaje.setText(String.valueOf(/*progress*/i)+" %");
                }
            }

            //hace un llamado  cuando se toca la perilla
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            //hace un llamado  cuando se detiene la perilla
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this,
                        "Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });


        btnIniciar.setOnClickListener(v -> {
            iniciar();
        });

        btnReiniciar.setOnClickListener(  v -> {
            /** TODO : PROGRAMAR EL REINICIAR **/
            reiniciar();
        });
    }

    public void playCycle(){
        // Valor Inicial
        seekbar.setProgress(reproductorMusica.getCurrentPosition() );

        if(reproductorMusica.isPlaying()){
            runnable = () -> playCycle();
            handler.postDelayed(runnable,1000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        audioAsincrono.pausarAudio();
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioAsincrono.pausarAudio();
    }

    private void iniciar() {
        ///Si el audio es null
        if ( audioAsincrono == null ) {
            audioAsincrono = new AudioAsincrono(MainActivity.this, txvActual, txvFinal);
            reproductorMusica.start();
            playCycle();
            audioAsincrono.execute();
            ///Se ejecuta por primera vez la canción, entonces el botón cambia a estado Pausar
            btnIniciar.setText("Pausar");
            Toast.makeText(this, "La canción inició", Toast.LENGTH_LONG).show();

        }
        else if(audioAsincrono == null && audioAsincrono.getStatus() == AsyncTask.Status.FINISHED){
            ///btnIniciar.setText("Reanudar");
            Toast.makeText(this, "La canción inició de nuevo", Toast.LENGTH_LONG).show();
        }
        ///Si el hilo ya terminó, se ejecuta otro hilo
        else if ( audioAsincrono.getStatus() == AsyncTask.Status.FINISHED ) {
            audioAsincrono = new AudioAsincrono(MainActivity.this, txvActual, txvFinal);
            reproductorMusica.start();
            playCycle();
            audioAsincrono.execute();
            ///Toda la canción ya terminó, así que la reproducimos de nuevo, entonces el botón cambia a estado Pausar
            btnIniciar.setText("Pausar");
            playCycle();
            Toast.makeText(this, "La canción inició de nuevo", Toast.LENGTH_LONG).show();
            ///Pausar el audio
            ///Si el audio está corriendo y no está pausado
        } else if (audioAsincrono.getStatus() == AsyncTask.Status.RUNNING && !audioAsincrono.esPause() ) { // En caso de que este corriendo y no este pausado; entonces se pausa.
            audioAsincrono.pausarAudio();
            Toast.makeText(this, "Se pausó la canción", Toast.LENGTH_LONG).show();
            btnIniciar.setText("Reanudar");

        } else { // En caso de que este pausado; entonces se debe reanudar
            audioAsincrono.reanudarAudio();
            btnIniciar.setText("Pausar");
            Toast.makeText(this, "La canción se reanudó", Toast.LENGTH_LONG).show();
            reproductorMusica.start();
            playCycle();
            audioAsincrono.reanudarAudio();
        }
    }
    private void reiniciar(){
        if(audioAsincrono.getStatus() == AsyncTask.Status.RUNNING){
             Toast.makeText(this, "Se reinició la canción", Toast.LENGTH_LONG).show();

            audioAsincrono.reiniciarAudio();
            btnIniciar.setText("Pausar");
            audioAsincrono = new AudioAsincrono(MainActivity.this, txvActual, txvFinal);
            reproductorMusica.reset();
            reproductorMusica.start();
            audioAsincrono.execute();
            playCycle();

        }
    }
}