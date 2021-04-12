package gamefiles.items;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Item {
    private int id;
    private String name;
    private String description;
    private int quantity;
    private boolean active;
    private ImageView imageView;
    private Image image;
    private final int height = 50;
    private final int width = 50;

    public Item(int id, String name, String description, int quantity, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.active = active;
        updateImageView("sprites/itemAssets/placeholder.png");
    }

    /**
     * Abstract function that goes in AnimationTimer to dictate what happens
     * due to item presence.
     * @param currentNanoTime same long as AnimationTimer
     */
    public abstract void effect(long currentNanoTime);

    public void updateImageView(String spritePath) {
        this.imageView = new ImageView(spritePath);
        imageView.setFitWidth(this.width);
        imageView.setFitHeight(this.height);
        image = imageView.getImage();
    }

    public void addQuantity(int value) {
        this.quantity += value;
    }

    public void subtractQuantity(int value) {
        addQuantity(-value);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void toggleActive() {
        if (this.active) {
            this.active = false;
        } else {
            this.active = true;
        }
    }

    public void setActive(boolean bool) {
        this.active = bool;
    }

    public boolean isActive() {
        return this.active;
    }

    public Image getImageView() {
        return this.image;
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
}
