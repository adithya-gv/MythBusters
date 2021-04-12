package gamefiles.items;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.scene.media.Media;

import java.io.File;

public abstract class Consumable extends Item {
    private long duration;
    private long durationTimer;
    private boolean consumed;
    private MediaPlayer soundEffect;

    public Consumable(int id, String name, String description, int quantity, boolean active, long duration) {
        super(id, name, description, quantity, active);
        this.duration = duration;
        this.durationTimer = 0;
        this.consumed = false;
        setSoundEffect("MythBuster/sounds/Consumable.wav");
    }

    public void use() {
        if (!this.isConsumed()) {
            durationTimer = this.getDuration();
            this.toggleConsumed();
        }
    }

    public boolean update() {
        if (this.durationTimer == 0) {
            this.toggleConsumed();
            this.toggleActive();
            this.subtractQuantity(1);
            return false; // ended; "no update"
        } else {
            durationTimer--;
            return true; // not ended; "updated"
        }
    }

    public void setSoundEffect(String soundPath) {
        //Instantiating Media class  
        Media media = new Media(new File(soundPath).toURI().toString());  
        //Instantiating MediaPlayer class   
        this.soundEffect = new MediaPlayer(media);
        this.soundEffect.setStopTime(Duration.seconds(2));
    }

    public long getDuration() {
        return this.duration;
    }

    public long getDurationTimer() {
        return this.durationTimer;
    }

    public void toggleConsumed() {
        if (this.consumed) {
            this.consumed = false;
        } else {
            //by setting this property to true, the audio will be played   
            this.soundEffect.seek(Duration.ZERO);
            this.soundEffect.play();  
            this.consumed = true;
        }
    }

    public boolean isConsumed() {
        return this.consumed;
    }
}