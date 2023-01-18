package com.example.improparking_projet.GestionMap;
import javafx.scene.image.ImageView;

public class SpriteVoiture {
    private ImageView imageView;

    /**
     * @return l'image du sprite
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Définit l'image du sprite
     * @param imageView l'image du sprite
     */
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
