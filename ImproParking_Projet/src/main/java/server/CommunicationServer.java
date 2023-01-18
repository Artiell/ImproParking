package server;

import com.example.improparking_projet.communication.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationServer {

    private Socket socket;
    private BufferedReader fluxEntrant;
    private PrintWriter fluxSortant;

    /**
     * Constructeur de la classe Communication
     */

    public CommunicationServer(Socket socket) throws IOException {

        this.socket = socket;

        try { this.creationFlux();  } catch (IOException e) {
            System.out.println("Echec de l'initialisation des flux");
            e.printStackTrace();
        }

    }

    /**
     * Méthode qui crée les flux d'entrée et de sortie
     */
    public void creationFlux() throws IOException {
        //Création du gestionnaire de flux entrant
        InputStreamReader iSReader = new InputStreamReader(this.socket.getInputStream());
        this.fluxEntrant = new BufferedReader(iSReader);
        //Création du gestionnaire de flux sortant
        this.fluxSortant = new PrintWriter(this.socket.getOutputStream(),true);
    }


    /**
     * Méthode qui envoie un message au client
     * @param message Message à envoyer
     */
    public void envoyerMessage(Message message) {
        String envoi = message.toString();
        System.out.println(">> "+envoi);
        this.fluxSortant.println(envoi);
    }


    /**
     * Méthode qui reçoit un message du client
     * @return Message reçu
     */
    public Message recevoirMessage() throws IOException {
        String recu = this.fluxEntrant.readLine();
        System.out.println("<< "+recu);
        Message message = new Message(recu);
        return message;
    }
}
