package com.example.guia6_daniel_larin.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.TextView;

import com.example.guia6_daniel_larin.R;

import java.util.concurrent.TimeUnit;

public class AudioAsincrono extends AsyncTask<Void, String, String> {

    Context context;
    TextView txvActual, txvFinal;

    /*
    La clase MediaPlayer se puede usar para controlar la reproducción de archivos y transmisiones de audio / video.

    MediaPlayer no es seguro para subprocesos. La creación y todo el acceso a las instancias de jugador
    deben estar en el mismo hilo. Si registra devoluciones de llamada , el hilo debe tener un Looper.
     */
    MediaPlayer reproductorMusica;

    ///Maneja el estado de las pausas
    boolean pause = false;
    ///Vigila si el proceso está en pausa o ejecutandose
    private String VIGILANTE = "vigilante";

    ///Constructor de clase
    ///Los parámetros son: el contexto que hace referencia a un Activity, el textView actual y el final
    public AudioAsincrono(Context context, TextView txvActual, TextView txvFinal) {
        this.context = context;
        this.txvActual = txvActual;
        this.txvFinal = txvFinal;
    }

    @Override
    protected String doInBackground(Void... voids) {

        ///Iniciamos el reproductor
        reproductorMusica.start();
        ///Mientras el reproductor este ejecutandose
        while( reproductorMusica.isPlaying() ) {
            esperaUnSegundo();
            ///Nos permite publicar el progreso de nuestra acción
            ///publishProgress método protegido de la clase AsyncTask
            publishProgress(tiempo(reproductorMusica.getCurrentPosition()));
            if ( pause){
                synchronized (VIGILANTE){
                    try{
                        /** Realiza una pausa en el hilo */
                        reproductorMusica.pause();
                        VIGILANTE.wait();
                    }catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    pause = false;
                    reproductorMusica.start();
                }
            }
        }
        return null;
    }


    private void esperaUnSegundo() {
        try{
            //Es parte de los util de android
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignore){}
    }

    /** Notifica al vigilante en todas sus llamadas con syncronized **/
    /**Cuando se reanuda el audio**/
    public void reanudarAudio() {
        synchronized (VIGILANTE){
            VIGILANTE.notify();
        }
    }

    public void pausarAudio() {
        pause = true;
    }

    ///Evalua el estado de la pausa
    public boolean esPause() {
        return pause;
    }

    private String tiempo(long tiempo) {
        long fin_min = TimeUnit.MILLISECONDS.toMinutes(tiempo);
        long fin_sec = TimeUnit.MILLISECONDS.toSeconds(tiempo) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tiempo));
        return  fin_min + ":" + fin_sec;

    }
    public String getTiempo(long tiempo){
        long inicio_min = TimeUnit.MILLISECONDS.toMinutes(tiempo);
        long inicio_sec = TimeUnit.MILLISECONDS.toSeconds(tiempo) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tiempo));
        return  inicio_min + ":" + inicio_sec;
    }

    ///Hace la pre ejecución
    @Override
    protected void onPreExecute() {
        reproductorMusica = MediaPlayer.create(this.context, R.raw.nokia_tune);
        ///Duración del audio
        long fin = reproductorMusica.getDuration();
        txvFinal.setText(tiempo(fin));
        super.onPreExecute();
    }

    ///Lo que lleva el contador actual
    @Override
    protected void onProgressUpdate(String... values) {
        txvActual.setText(values[0]);
        super.onProgressUpdate(values);
    }
    public void Reset(){
        reproductorMusica.reset();
    }

    public void reiniciarAudio() {
        synchronized (VIGILANTE){
            VIGILANTE.notify();

            reproductorMusica.reset();
        }
    }
    public void Reanudar(){
        reproductorMusica.start();
    }

    public void repetirCancion(){
        reproductorMusica = MediaPlayer.create(this.context, R.raw.nokia_tune);
        reproductorMusica.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
}
