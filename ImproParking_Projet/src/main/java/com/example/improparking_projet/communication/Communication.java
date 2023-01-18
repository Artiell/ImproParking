package com.example.improparking_projet.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Communication {

    private int SERVER_PORT;//Port de connection du serveur
    private Socket socket;
    private BufferedReader fluxEntrant;
    private PrintWriter fluxSortant;

    /**
     * Constructeur de la classe Communication
     */
    public Communication(int srvPort){
        this.SERVER_PORT = srvPort;

        try { this.connexion(); } catch (IOException e)
        {
            System.out.println("Echec de la connexion au serveur");
            e.printStackTrace();
        }

        try { this.creationFlux();  } catch (IOException e) {
            System.out.println("Echec de l'initialisation des flux");
            e.printStackTrace();
        }

    }

    /**
     * Connecte le client au serveur
     * @throws IOException en cas d'erreur de connexion
     */
    public void connexion() throws IOException {
        //Création du socket entre client et serveur
        this.socket = new Socket("localhost", SERVER_PORT);
    }


    /**
     * Crée les deux gestionnaires de flux
     * @throws IOException en cas d'erreur de connexion
     */
    public void creationFlux() throws IOException {
        //Création du gestionnaire de flux entrant
        InputStreamReader iSReader = new InputStreamReader(this.socket.getInputStream());
        this.fluxEntrant = new BufferedReader(iSReader);
        //Création du gestionnaire de flux sortant
        this.fluxSortant = new PrintWriter(this.socket.getOutputStream(),true);
    }

    /**
     * Envoie le message donné au serveur et l'affiche dans la console
     * @param message le message à envoyer
     */
    public void envoyerMessage(Message message) {
        String envoi = message.toString();
        System.out.println(">> "+envoi);
        this.fluxSortant.println(envoi);
    }


    /**
     * Écoute et renvoie un message du serveur (en l'affichant aussi dans la console)
     * @throws java.io.IOException en cas d'erreur de connexion
     */
    public Message recevoirMessage() throws IOException {
        String recu = this.fluxEntrant.readLine();
        System.out.println("<< "+recu);
        Message message = new Message(recu);
        return message;
    }
}
