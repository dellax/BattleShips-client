package game;

import javafx.scene.Parent;

public class Ship extends Parent {
    public int type;
    public boolean vertical = true;

    private int health;

    public Ship(int type, boolean vertical) {
        this.type = type;
        this.vertical = vertical;
        health = type;
    }

    /**
     * Hit the ship
     */
    public void hit() {
        health--;
    }

    /**
     * Check if ship is alive
     * @return Boolean
     */
    public boolean isAlive() {
        return health > 0;
    }
}