package at.technikum_wien.adventure;

import java.util.Random;



/** This class represents a field in the dungeon. */
public class Field
{
    /** Creates a new instance of this class. */
    public Field()
    {
        switch(new Random().nextInt(8))
        {
            case 0: env = "dark dungeon"; break;
            case 1: env = "rocky pit"; break;
            case 2: env = "hall"; break;
            case 3: env = "dragon's pit"; break;
            case 4: env = "clearing"; break;
            case 5: env = "underground river"; break;
            case 6: env = "dirty dungeon"; break;
            default: env = "dungeon";
        }
    }

    /** Environment text. */
    String env;

    /** Foe character if present. */
    Character foe = null;

    /** Item found in this field if any. */
    Weapon item = null;

    /** Indicates if a player is healed when entering this field. */
    boolean hasWell = false;

    /** Coins found in this field. */
    int coins = 0;

    /** Indicates if the field is the exit field (goal). */
    boolean isExit = false;
}
