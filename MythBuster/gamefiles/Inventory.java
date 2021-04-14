package gamefiles;

import controller.Controller;
import controller.GameLoop;
import gamefiles.characters.Player;
import gamefiles.items.Consumable;
import gamefiles.items.HealthPotion;
import gamefiles.items.Item;
import gamefiles.items.ItemDatabase;
import gamefiles.weapons.Bow;
import gamefiles.weapons.Weapon;
import gamefiles.weapons.WeaponDatabase;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Inventory {
    private static int INVENTORYROWSIZE = 5;
    private static int width = 1200;
    private static int height = 800;
    private static int numRows = 0;

    private static int hotbarSize = 0;
    private static int MAXHOTBARSIZE = 5;

    private static final ArrayList<Item> inventory = new ArrayList<>();
    private static Item[] hotbar = new Item[5];

    private static VBox inventoryRows = new VBox(20);
    private static HBox hotbarBox;
    private static Group weaponBox;
    private static Group board;
    private static Scene scene;

    private static final int UNSELECTED = -1;
    private static int selectedIndex = UNSELECTED;

    public static Scene getScene() {

        //For testing purposes.
        /*if (inventory.size() < 5) {
            addToInventory(ItemDatabase.getItem(0));
            addToInventory(ItemDatabase.getItem(1));
            addToInventory(WeaponDatabase.getWeapon(0));
            addToInventory(WeaponDatabase.getWeapon(1));
            addToInventory(WeaponDatabase.getWeapon(2));
        }*/

        //Pause the game when we go to inventory.
        Player player = Controller.getPlayer();
        GameLoop.stopAllAnimationTimers(player.getPlayerLogicTimer(),
                player.getPlayerHpUpdateTimer(), GameLoop.getMonsterLoop(),
                Controller.getControllerLoop(), player.getItemLoop());


        selectedIndex = UNSELECTED;
        board = new Group();
        scene = new Scene(board, width, height, Color.POWDERBLUE);

        //Display Text
        Text instructions = new Text(800, 300, "Click on a consumable and then press 1-5 to equip to hotbar.");
        Text instructions2 = new Text(800, 350,"Click on a weapon and press E to equip");
        Text instructions3 = new Text(800, 400,"Press I to go back.");


        board.getChildren().addAll(instructions, instructions2, instructions3);

        //Display inventory
        inventoryRows.getChildren().clear();
        for(int i = 0; i < inventory.size(); i++) {
            int row = i / INVENTORYROWSIZE;
            if (row >= numRows) {
                inventoryRows.getChildren().add(new HBox(10));
            }
            HBox currRow = (HBox)inventoryRows.getChildren().get(row);

            Group inventoryItemSpace = new Group();
            inventoryItemSpace.getChildren().add(new Rectangle(80,  80, Color.LIGHTYELLOW));
            currRow.getChildren().add(inventoryItemSpace);

            ImageView imageView = new ImageView(inventory.get(i).getImage());
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            final int j = i;
            inventoryItemSpace.setOnMouseClicked(mouseEvent -> {
                System.out.println(inventory.get(j).getDescription());
                selectedIndex = j;
            });
            inventoryItemSpace.getChildren().add(imageView);

        }
        inventoryRows.setPadding(new Insets(50, 50, 50, 200));
        board.getChildren().add(inventoryRows);


        //Display hotbar.
        hotbarBox = new HBox(50);
        hotbarBox.setTranslateX(700);
        hotbarBox.setTranslateY(700);
        for(int i = 0; i < MAXHOTBARSIZE; i++) {
            Group hotbarItemSpace = new Group();
            hotbarItemSpace.getChildren().add(new Rectangle(50,  50, Color.YELLOW));
            hotbarBox.getChildren().add(hotbarItemSpace);

            ImageView imageView = null;
            if(hotbar[i] != null) {

                imageView = new ImageView(hotbar[i].getImage());
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                hotbarItemSpace.getChildren().add(imageView);

            }


        }
        board.getChildren().add(hotbarBox);

        //Display weaponBox
        weaponBox = new Group();
        weaponBox.getChildren().add(new Rectangle(80, 80, Color.RED));
        weaponBox.setTranslateX(700);
        weaponBox.setTranslateY(500);
        ImageView imageView = new ImageView(Controller.getPlayer().getWeapon().getImage());
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        weaponBox.getChildren().add(imageView);
        board.getChildren().add(weaponBox);

        scene.setOnKeyReleased(
                e -> {
                    String code = e.getCode().toString();
                    System.out.println(selectedIndex);
                    if(code == "I") {

                        //Upon pressing I again, go back to game.
                        GameLoop.startAllAnimationTimers(player.getPlayerLogicTimer(),
                                player.getPlayerHpUpdateTimer(), GameLoop.getMonsterLoop(),
                                Controller.getControllerLoop(), player.getItemLoop());
                        Controller.goToGameScreen();
                        player.updateHotbarImages();
                    }
                    if(code == "DIGIT1" && selectedIndex != UNSELECTED) {
                        if(inventory.get(selectedIndex) instanceof Consumable) {
                            addToHotbar(selectedIndex, 0);
                        }
                    }
                    if(code == "DIGIT2" && selectedIndex != UNSELECTED) {
                        if(inventory.get(selectedIndex) instanceof Consumable) {
                            addToHotbar(selectedIndex, 1);
                            System.out.println("(For testing purposes) Trying to add!");
                        }
                    }
                    if(code == "DIGIT3" && selectedIndex != UNSELECTED) {
                        if(inventory.get(selectedIndex) instanceof Consumable) {
                            addToHotbar(selectedIndex, 2);
                        }
                    }
                    if(code == "DIGIT4" && selectedIndex != UNSELECTED) {
                        if(inventory.get(selectedIndex) instanceof Consumable) {
                            addToHotbar(selectedIndex, 3);
                        }
                    }
                    if(code == "DIGIT5" && selectedIndex != UNSELECTED) {
                        if(inventory.get(selectedIndex) instanceof Consumable) {
                            addToHotbar(selectedIndex, 4);
                        }
                    }
                    if (code == "E" && selectedIndex != UNSELECTED) {
                        if(inventory.get(selectedIndex) instanceof Weapon) {
                            Weapon prevWeapon = player.getWeapon();
                            player.setWeapon((Weapon)inventory.get(selectedIndex));
                            updateWeaponBox();
                            //Special logic for updating a bow.  Have to start/end animation group for arrows.
                            //Note: because of how weapon database is coded, there is only ever one instance of the bow.
                            if(prevWeapon instanceof Bow) {
                                ((Bow) prevWeapon).getArrowGroup().getChildren().clear();
                                GameLoop.stopAllAnimationTimers(((Bow) prevWeapon).getArrowTimer());
                            }
                            if(player.getWeapon() instanceof Bow) {
                                GameLoop.startAllAnimationTimers(((Bow) Controller.getPlayer().getWeapon()).arrowTimer());
                                System.out.println("ArrowTimer");
                            }

                            removeFromInventory(selectedIndex);
                            addToInventory(prevWeapon);
                        }
                    }
                });

        return scene;

    }

    public static void addToHotbar(int inventoryIndex, int hotbarIndex){
        ImageView imageView = new ImageView(inventory.get(inventoryIndex).getImage());
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        Group hotbarItemSpace = (Group) hotbarBox.getChildren().get(hotbarIndex);
        Item currItemInHotbar = hotbar[hotbarIndex];
        if(currItemInHotbar == null) {
            hotbar[hotbarIndex] = inventory.get(inventoryIndex);
            removeFromInventory(inventoryIndex);
            hotbarItemSpace.getChildren().add(imageView);
            hotbarSize++;
            refreshDisplays();
        } else {
            hotbar[hotbarIndex] = inventory.get(inventoryIndex);
            removeFromInventory(inventoryIndex);
            hotbarItemSpace.getChildren().clear();
            hotbarItemSpace.getChildren().add(new Rectangle(50, 50, Color.YELLOW));
            hotbarItemSpace.getChildren().add(imageView);
            addToInventory(currItemInHotbar);
            refreshDisplays();

        }
    }

    public static void updateWeaponBox() {
        weaponBox.getChildren().clear();
        weaponBox.getChildren().add(new Rectangle(80, 80, Color.RED));
        ImageView imageView = new ImageView(Controller.getPlayer().getWeapon().getImage());
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        weaponBox.getChildren().add(imageView);
    }


    public static void addToInventory(Item item) {

        inventory.add(item);
        refreshDisplays();
    }

    public static void removeFromInventory(int inventoryIndex) {
        inventory.remove(inventoryIndex);
        refreshDisplays();
    }

    public static void removeFromHotbar(int index) {
        hotbar[index] = null;
    }

    public static void clearInventory() {
        inventory.clear();
        refreshDisplays();
    }

    public static void refreshDisplays() {
        //Display inventory
        selectedIndex = UNSELECTED;
        inventoryRows.getChildren().clear();
        for(int i = 0; i < inventory.size(); i++) {
            int row = i / INVENTORYROWSIZE;
            if (row >= numRows) {
                inventoryRows.getChildren().add(new HBox(10));
            }
            HBox currRow = (HBox)inventoryRows.getChildren().get(row);

            Group inventoryItemSpace = new Group();
            inventoryItemSpace.getChildren().add(new Rectangle(80,  80, Color.LIGHTYELLOW));
            currRow.getChildren().add(inventoryItemSpace);

            ImageView imageView = new ImageView(inventory.get(i).getImage());
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            final int j = i;
            inventoryItemSpace.setOnMouseClicked(mouseEvent -> {
                System.out.println(inventory.get(j).getDescription());
                selectedIndex = j;
            });
            inventoryItemSpace.getChildren().add(imageView);

        }
        inventoryRows.setPadding(new Insets(50, 50, 50, 200));

    }

/*
    public void updateItems(ArrayList<Integer> toDelete, ArrayList<Item> toAdd) {
        boolean update = false;
        if (toDelete != null && toDelete.size() > 0) {
            update = true;
            for (int i = toDelete.size() - 1; i >= 0; i--) {
                inventor.remove(toDelete.remove(i).intValue());
            }
        }
        if (toAdd != null && toAdd.size() > 0) {
            update = true;
            for (Item item : toAdd) {
                items.add(item);
            }
        }

        if (update) {
            updateItemImages();
        }
    }

    public void updateItemImages() {
        ArrayList<ImageView> itemImages = new ArrayList<ImageView>(items.size());
        for (int j = 0; j < items.size(); j++) {
            ImageView imageView = new ImageView(items.get(j).getImageView());
            imageView.setFitWidth(items.get(j).getWidth());
            imageView.setFitHeight(items.get(j).getHeight());
            itemImages.add(imageView);
        }
        //inventoryDisplay.getChildren().setAll(itemImages);
    }*/

    public ArrayList<Item> getInventory() {
        return inventory;
    }
    public static Item[] getHotbar() {
        return hotbar;
    }
    public static int getHotbarSize() {
        return hotbarSize;
    }
    public static void setHotbarSize(int size) {
        hotbarSize = size;
    }

    public static int getMAXHOTBARSIZE() {
        return MAXHOTBARSIZE;
    }

}
