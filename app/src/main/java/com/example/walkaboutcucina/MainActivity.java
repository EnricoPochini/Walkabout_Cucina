package com.example.walkaboutcucina;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    //contenuti grafici
    private TextView listaGruppi;


    //conessione al server
    private static final String SERVER_IP = "192.168.1.10";
    private static final int SERVER_PORT = 151;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaGruppi = findViewById(R.id.infoGruppi);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        GetDataFromServer getDataThread = new GetDataFromServer();
        getDataThread.start();


        Button bottoneInvioDati = findViewById(R.id.bottoneRichiestadati);
        bottoneInvioDati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetDataFromServer getDataThread = new GetDataFromServer();
                getDataThread.start();

                Toast.makeText(MainActivity.this, "Aggiornamento dati...", Toast.LENGTH_SHORT).show();

            }
        });

    }


    //classe che riceve e mostra i dati ricevuti dal server
    protected class GetDataFromServer extends Thread {

        public GetDataFromServer(){


        }//costruttore

        @Override
        public void run(){

            getData();

        }


        //richiede al server se ci sono nuovi dati
        @SuppressLint("SetTextI18n")
        protected void getData() {

            try (
                    Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {


                String response = in.readLine();

                if(response == null){

                    listaGruppi.setText("Errore di connessione al server");
                    return;

                }//if

                listaGruppi.setText(sostituisciCarattere(response,'#','\n'));

                return;

            } catch (IOException e) {

                listaGruppi.setText("Impossibile ottenere i dati");

            }

            listaGruppi.setText("Errore di connessione al server");


        }


        //riporta la stringa al formato scelto di visualizzazione
        public String sostituisciCarattere(String input, char daSostituire, char daInserire) {
            char[] chars = input.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == daSostituire) {
                    chars[i] = daInserire;
                }
            }
            return new String(chars);
        }

    }




}

