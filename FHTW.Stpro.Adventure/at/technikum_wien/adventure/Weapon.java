package at.technikum_wien.adventure;



/** This class represents a weapon. */
public class Weapon
{
    /** Creates a new instance of this class.
     * @param name Weapon name.
     * @param attack Attack value. */
    public Weapon(String name, int attack)
    {
        this.name = name;
        this.attack = attack;
    }


    /** Weapon name. */
    public String name;

    /** Attack value. */
    public int attack;


    /** Gets information about the weapon.
     * @return Weapon description. */
    public String getInfo()
    {
        if(name.equals("hands")) return "You are not carrying any weapon.";

        if(attack < 10) return "It looks weak.";
        if(attack < 12) return "It doesn't seem to be too perficient.";
        if(attack < 16) return "It appears to be alright.";
        if(attack < 18) return "This is a good weapon.";
        return "It looks powerful.";
    }
}
