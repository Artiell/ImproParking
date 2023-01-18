package com.example.improparking_projet.GestionMap;

import com.example.improparking_projet.voiture.Voiture;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Classe qui gère l'affichage des voitures sur la map
 */
public class VoitureManager {
    private final SpriteVoiture[] listeVoitures; // Liste de sprite voiture

    /**
     * Constructeur de la classe
     */
    public VoitureManager()
    {
        listeVoitures = new SpriteVoiture[12];
        remplissageListe();
    }

    /**
     * Méthode qui permet de remplir la liste de sprite voiture
     */
    public void remplissageListe()
    {
        try
        {
            listeVoitures[0] = new SpriteVoiture();
            listeVoitures[0].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/GrisBas.png"))),null))) ;

            listeVoitures[1] = new SpriteVoiture();
            listeVoitures[1].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/GrisDroit.png"))),null))) ;

            listeVoitures[2] = new SpriteVoiture();
            listeVoitures[2].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/GrisGauche.png"))),null))) ;

            listeVoitures[3] = new SpriteVoiture();
            listeVoitures[3].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/GrisHaut.png"))),null))) ;

            listeVoitures[4] = new SpriteVoiture();
            listeVoitures[4].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/OrangeBas.png"))),null))) ;

            listeVoitures[5] = new SpriteVoiture();
            listeVoitures[5].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/OrangeDroit.png"))),null))) ;

            listeVoitures[6] = new SpriteVoiture();
            listeVoitures[6].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/OrangeGauche.png"))),null))) ;

            listeVoitures[7] = new SpriteVoiture();
            listeVoitures[7].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/OrangeHaut.png"))),null))) ;

            listeVoitures[8] = new SpriteVoiture();
            listeVoitures[8].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/VertBas.png"))),null))) ;

            listeVoitures[9] = new SpriteVoiture();
            listeVoitures[9].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/VertDroit.png"))),null))) ;

            listeVoitures[10] = new SpriteVoiture();
            listeVoitures[10].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/VertGauche.png"))),null))) ;

            listeVoitures[11] = new SpriteVoiture();
            listeVoitures[11].setImageView(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/map/SpriteVoiture/VertHaut.png"))),null))) ;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Méthode qui permet de dessiner les voitures sur la map
     * @param gc GraphicsContext
     * @param voitureCree Voiture à dessiner
     */
   public void dessinerSpawnVoiture(GraphicsContext gc, Voiture voitureCree)
   {
       int x = 0,y = 0; // Coordonnées de la voiture sur la map

       switch (voitureCree.getCoord().getLigne()) {

           case 4 :
               x = 20; y = 66;
           break;

           case 1 :
               x = 610; y = 20;
           break;

           case 17 :
               x = 820; y = 272;
           break;
       }

       SpriteVoiture sprite = listeVoitures[voitureCree.getCouleur()*4+1]; // Récupération du sprite de la voiture
       voitureCree.setSprite(sprite); // On définit le sprite de la voiture
       gc.drawImage(sprite.getImageView().getImage(), x,y,48,32); // On dessine la voiture sur la map
   }

    /**
     * Méthode qui permet de clean la map de voitures
     * @param gc GraphicsContext
     */
    public void cleanMap(GraphicsContext gc, Canvas c){
        gc.clearRect(0,0,c.getWidth(),c.getHeight());
    }

    /**
     * Méthode qui permet de déplacer les voitures sur la map
     * @param gc GraphicsContext
     * @param voitureCree Voiture à dessiner
     */
    public void deplacerVoiture(GraphicsContext gc, Voiture voitureCree){
        // Coordonnées de la voiture sur la map
        int x = (voitureCree.getCoord().getColonne())*16;
        int y = (voitureCree.getCoord().getLigne())*16;

        // Selon la direction de la voiture, on dessine le sprite correspondant
        SpriteVoiture sprite = new SpriteVoiture();
        int longueurVoiture = 0;
        if(voitureCree.getDernierMouvement() != null)
        {
            switch (voitureCree.getDernierMouvement()) {
                case BAS :
                    sprite = listeVoitures[voitureCree.getCouleur()*4];
                    longueurVoiture = 32;
                    break;

                case DROITE :
                    sprite = listeVoitures[voitureCree.getCouleur()*4+1];
                    longueurVoiture = 48;
                    break;

                case GAUCHE :
                    sprite = listeVoitures[voitureCree.getCouleur()*4+2];
                    longueurVoiture = 48;
                    break;

                case HAUT :
                    sprite = listeVoitures[voitureCree.getCouleur()*4+3];
                    longueurVoiture = 32;
                    break;
            }
            gc.drawImage(sprite.getImageView().getImage(), x,y,longueurVoiture,32); // On dessine la voiture sur la map
        }
    }
}
