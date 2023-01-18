package server;

import com.example.improparking_projet.communication.Message;
import com.example.improparking_projet.communication.TypeMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class TCPserver {

    private ServerSocket serverSocket; // Socket du serveur
    private HashMap<String, Socket> clientsConnect; //Hash map qui contient les clients connectés au serveur avec leur nom d'utilisateur

    private ArrayList<ServerThread> threadList; //Liste des threads qui gèrent les clients connectés
    private BufferedReader inputReader;
    private PrintWriter outputWriter;

    private Semaphore mutex = new Semaphore(1);

    public TCPserver() throws Exception {


        try {

            // Initialisation du serveur sur le port donné en paramètre
            serverSocket = new ServerSocket(5000);
            System.out.println("Server Start...");

            // Initialisation de la hashmap qui contient les sockets des clients connectés et leur nom d'utilisateur
            this.clientsConnect = new HashMap<String,Socket>();

            // Initialisation de la liste des threads
            this.threadList = new ArrayList<ServerThread>();


            boolean isImatriCulreceived = false;
            Message rcvdMessage = null;

            //initialisation des flux d'entrée et de sortie et du socket de communication avec le client
            Socket socketAccept = null;


            //While true pour accepter plusieurs clients et qui va créer un thread pour chaque client
            while (true) {

                System.out.println("Waiting for a client...");
                // Accepte la connexion du client
                socketAccept = serverSocket.accept();
                System.out.println("Client found");

                this.inputReader = new BufferedReader(new InputStreamReader(socketAccept.getInputStream()));  //Initialisation du reader avec le socket correspondant au client accepté
                this.outputWriter = new PrintWriter(socketAccept.getOutputStream(), true); // Initialisation du writer avec le socket correspondant au client accepté


                // On force l'utilisateur à choisir un nom d'utilisateur
                while(!isImatriCulreceived){
                    this.outputWriter.println(new Message("Server","?",TypeMessage.IdentificationServeur,"Enter your client name :").toString());
                    rcvdMessage = new Message(this.inputReader.readLine());

                    if( (rcvdMessage.getType().equals(TypeMessage.IdentificationServeur)) && (rcvdMessage.getRecepteur().equals("server")) ){
                        isImatriCulreceived = true;
                    }
                }

                // On ajoute le client à la hashmap avec pour correspondance le nom d'utilisateur et le socket
                clientsConnect.put(rcvdMessage.getContenu(), socketAccept);
                System.out.println("Connected: username :"+ rcvdMessage.getContenu() + " socket :" + this.clientsConnect.get(rcvdMessage.getContenu()));

                // on crée un thread du serveur pour le client connecté avec en paramètre le socket pour que le thread puisse discuter avec le client et la liste des clients connectés pour pouvoir envoyer des dm
                ServerThread serverThread = new ServerThread(rcvdMessage.getContenu(),clientsConnect);
                // On ajoute le thread à la liste des threads
                threadList.add(serverThread);

                //pour chaque thread, on va venir actualiser la hashmap des clients connectés

                try {
                    mutex.acquire();
                    for(ServerThread thread : threadList){
                        thread.setClientsConnect(clientsConnect);
                    }
                } catch (InterruptedException e) {
                    System.out.println("ERROR LOCK MUTEX WHILE SETTING THE ");
                } finally {
                    mutex.release();
                }



                serverThread.start(); // On lance le thread
                rcvdMessage = null;
                isImatriCulreceived = false;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
