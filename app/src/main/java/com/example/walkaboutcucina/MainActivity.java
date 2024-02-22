package com.example.walkaboutcucina;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    //contenuti grafici
    public TextView listaGruppi;

    //conessione al server
    private final String SERVER_IP = "172.24.64.1";
    private final int SERVER_PORT = 151;


    //componenti
    private Button bottoneEliminazione;
    private Button bottoneInvioDati;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaGruppi = findViewById(R.id.infoGruppi);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




        //bottone invio dati
        bottoneInvioDati = findViewById(R.id.bottoneRichiestadati);
        bottoneInvioDati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();

                Toast.makeText(MainActivity.this, "Aggiornamento dati...", Toast.LENGTH_SHORT).show();

            }
        });


        //bottone richiesta eliminazione
        bottoneEliminazione = findViewById(R.id.bottoneEliminazione);
        bottoneEliminazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confermaDialog();

            }
        });

    }



    //apre un menù di conferma di eliminazione dati del server
    private void confermaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conferma");
        builder.setMessage("Accettando eliminerai tutti i dati che le guide hanno mandato oggi, una volta fatto non saranno più recuperabili.");
        builder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                richiestaEliminazione();

                dialog.dismiss(); // Chiude il dialog
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Chiude il dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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



    //richiede al server se ci sono nuovi dati
    protected void getData(){

        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        ) {

            dos.writeUTF("info");

            String response = dis.readUTF();

            if(response == null){

                listaGruppi.setText("Errore di connessione al server");
                return;

            }//if

            socket.close();

            listaGruppi.setText(sostituisciCarattere(response,'#','\n'));

            return;

        } catch (IOException e) {

            listaGruppi.setText("Impossibile ottenere i dati");

        }

        listaGruppi.setText("Errore di connessione al server");


    }


    //richiede l'eliminazione di tutti i dati delle guide dal server
    void richiestaEliminazione(){

        try(Socket server = new Socket(SERVER_IP,SERVER_PORT);
            DataOutputStream dos = new DataOutputStream(server.getOutputStream());
        ) {

            dos.writeUTF("del");

        }catch(IOException ignored){


        }


    }


}