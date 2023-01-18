package com.example.improparking_projet.communication;


public class Message {
    private String contenu;
    private String emmeteur;
    private String recepteur;
    private TypeMessage type;

    /*
     * Constructeur
     * @param message : le message à envoyer
     */
    public Message(String message){
        String[] messageDecoupe = message.split("\\|");
        this.emmeteur = messageDecoupe[0];
        this.recepteur = messageDecoupe[1];
        this.type = TypeMessage.valueOf(messageDecoupe[2]);
        this.contenu = messageDecoupe[3];
    }

    /*
     * Constructeur
     * @param emmeteur : l'emmeteur du message
     * @param recepteur : le recepteur du message
     * @param type : le type du message
     * @param contenu : le contenu du message
     */
    public Message(String emmeteur, String recepteur, TypeMessage type, String contenu){
        this.emmeteur = emmeteur;
        this.recepteur = recepteur;
        this.type = type;
        this.contenu = contenu;
    }

    /**
     * @return la chaine formatée du message
     */

    public String toString(){
        return this.emmeteur + "|" + this.recepteur + "|" + this.type + "|" + this.contenu;
    }

    /**
     * @return le contenu du message
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * @return l'emmeteur du message
     */
    public String getEmmeteur() {
        return emmeteur;
    }

    /**
     * @return le recepteur du message
     */
    public String getRecepteur() {
        return recepteur;
    }

    /**
     * @return le type du message
     */
    public TypeMessage getType() {
        return type;
    }
}
