package at.technikum_wien.adventure;



/** This class represents a character. */
public class Character
{
    /** Creates a new instance of this class.
     * @param name Character name.
     * @param weapon Character weapon.
     * @param health Character health.
     * @param coins Coins.
     * @param aggressionLevel Aggression level.
     * @param dropsWeapon Weapon drop flag. */
    public Character(String name, Weapon weapon, int health, int coins, int aggressionLevel, boolean dropsWeapon)
    {
        this.name = name;
        this.weapon = weapon;
        this.health = health;
        this.coins = coins;
        this.aggressionLevel = aggressionLevel;
        this.dropsWeapon = dropsWeapon;
    }

    /** Character name. */
    String name;

    /** Equipped weapon. */
    public Weapon weapon;

    /** Coins owned. */
    public int coins;

    /** Character health points. */
    public int health;

    /** Indicates if the character has a chance to drop a weapon. */
    boolean dropsWeapon;

    /** Character aggression level. */
    public int aggressionLevel;


    /** Gets the character's mindset.
     * @return Returns information about the character's mindset. */
    public String getMindset()
    {
        if(aggressionLevel < 3) return "calm";
        if(aggressionLevel < 6) return "dangerous";
        if(aggressionLevel < 9) return "fierce";
        return "furious";
    }

    /** Gets the character's state.
     * @return Returns information about the character's physical state. */
    public String getState()
    {
        if(health < 15) return "dying";
        if(health < 25) return "seriously wounded";
        if(health < 50) return "in bad shape";
        return "sound";
    }
}