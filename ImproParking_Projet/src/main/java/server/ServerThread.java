package server;

import com.example.improparking_projet.communication.Communication;
import com.example.improparking_projet.communication.Message;
import com.example.improparking_projet.communication.TypeMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class ServerThread extends Thread{

    private Socket socket;
    private String userName;

    private CommunicationServer comm;

    public void setClientsConnect(HashMap<String, Socket> clientsConnect) throws InterruptedException {
        this.clientsConnect = clientsConnect;
    }
    private HashMap<String, Socket> clientsConnect;


    public ServerThread(String usnm, HashMap<String, Socket> clientsConnect) throws IOException {

        //initialisation des attributs

        this.userName = usnm;
        this.clientsConnect = clientsConnect;
        this.socket = clientsConnect.get(this.userName);

        this.comm = new CommunicationServer(this.socket);



    }
    // méthode run qui va être appelé par le thread au moment de l'appel a la méthode start()
    @Override
    public void run() {
        try {

            Thread.sleep(2000);
           boolean run = true;

            System.out.println(userName + " is working");

            Message rcvdMessage = null;

            while (run) {
                //on reçoit le message du client
                rcvdMessage = this.comm.recevoirMessage();

                //en fonction du type le serveur va réagir différemment
                switch (rcvdMessage.getType()) {

                    case IdentificationServeur, MauvaisMessage:
                    break;

                    case DemandeListeParking:
                        //on envoie la liste des parkings au client qui demande
                        String listeParking = "";
                        for(String key : this.clientsConnect.keySet()){
                            if(key.startsWith("Parking")){
                                listeParking += key + "/";
                            }
                        }
                        if(listeParking.equals("")){
                            listeParking = "Aucun parking";
                        }
                        this.comm.envoyerMessage(new Message("Server",this.userName,TypeMessage.DemandeListeParking,listeParking));
                    break;

                    case ContreProposition, DerniereProposition, PlusDePlace, DemandePlace, PremierTarif, Acceptation, Refus,Desistement,DemandeCoordPark, Confirmation:

                        Socket socketRecepteur = clientsConnect.get(rcvdMessage.getRecepteur());
                        new PrintWriter(socketRecepteur.getOutputStream(), true).println(rcvdMessage.toString());

                    break;

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
