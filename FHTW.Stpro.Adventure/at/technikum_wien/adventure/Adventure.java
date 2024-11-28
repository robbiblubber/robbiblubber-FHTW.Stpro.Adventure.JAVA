package at.technikum_wien.adventure;


import java.awt.*;
import java.util.Random;
import java.util.Scanner;



/** This class implements the adventure game. */
public final class Adventure
{
    /** Random number generator. */
    private final static Random rnd = new Random();

    /** Dungeon map. */
    private static Field[][] dungeon;

    /** Player object. */
    private final static Character player = new Character("Player", new Weapon("hands", 4), 200, 0, 0, true);;

    /** Player position. */
    private static Point pos;

    /** Indicates if the game is won. */
    private static boolean won = false;

    /** Player kills. */
    private static int kills = 0;

    /** Gift flags for Janitor, Wizard, and Adventurer. */
    private static final boolean[] giftAvailable = { true, true, true };



    /** Application entry pont. */
    public static void main(String[] args)
    {
        initDungeon();                                                          // initialize dungeon
        startStory();                                                           // show the initial story

        while((player.health > 0) && (!won))
        {                                                                       // main loop
            showEnv();
            action();
        }

        debrief();                                                              // show summary
    }


    /** Describes the environment, collects gold and applies bonus. */
    private static void showEnv()
    {
        Field f = dungeon[pos.x][pos.y];
        System.out.println();
        System.out.printf("You are in a %s.", f.env);                           // describe field

        switch(rnd.nextInt(20))
        {                                                                       // add some atmosphere
            case 1:
            case 2: System.out.println(" It is dark."); break;
            case 3: System.out.println(" This is a creepy place."); break;
            case 4: System.out.println(" You hear ghostly noises from the distance."); break;
            case 5: System.out.println(" This is a filthy place."); break;
            case 6: System.out.println(" There is an unnatural fog in here."); break;
            case 7: System.out.println(" Something is wrong here."); break;
            case 8: System.out.println(" You feel like something is watching you."); break;
            default: System.out.println();
        }

        if(f.hasWell)                                                           // heal if well is available
        {
            if(player.health < 230)
            {
                System.out.println("A magical effect around here has eased your pain.");
            }
            else { System.out.println("Some strange magic has made you stronger."); }

            player.health += (1 + rnd.nextInt(60));
            f.hasWell = false;
        }

        if(f.coins > 0)                                                         // take coins
        {
            System.out.printf("You found %d gold coins!\n", f.coins);
            player.coins += f.coins;
            f.coins = 0;
        }

        if(f.item != null)                                                      // show item
        {
            System.out.printf("There is a %s. %s\n", f.item.name, f.item.getInfo());
        }

        if(f.foe != null)                                                       // show npc
        {
            System.out.printf("You see a %s. The %s appears to be %s and %s.\n", f.foe.name, f.foe.name, f.foe.getState(), f.foe.getMindset());
        }

        if(f.isExit)                                                            // show exit
        {
            System.out.println("There seems to be a climbable ravine here. It seems, you have found a way out!");
        }

        if(player.health < 26)                                                  // show health state if bad
        {
            System.out.println("You are in critical condition.");
        }
        else if(player.health < 51)
        {
            System.out.println("You are severely wounded.");
        }
        else if(player.health < 101)
        {
            System.out.println("You are wounded.");
        }

    }


    /** Gets and evaluates commands. */
    private static void action()
    {
        Scanner sc = new Scanner(System.in);

        System.out.print("> ");                                                 // get player command
        String cmd = sc.next().toLowerCase();

        if(cmd.equals("n") || cmd.equals("north") || cmd.equals("go n") || cmd.equals("go north"))
        {
            go(Direction.NORTH);
        }
        else if(cmd.equals("s") || cmd.equals("south") || cmd.equals("go s") || cmd.equals("go south"))
        {
            go(Direction.SOUTH);
        }
        else if(cmd.equals("e") || cmd.equals("east") || cmd.equals("go e") || cmd.equals("go east"))
        {
            go(Direction.EAST);
        }
        else if(cmd.equals("w") || cmd.equals("west") || cmd.equals("go w") || cmd.equals("go west"))
        {
            go(Direction.WEST);
        }
        else if(cmd.equals("l") || cmd.equals("look"))
        {
            look();
        }
        else if(cmd.equals("ln") || cmd.equals("l n") || cmd.equals("l north") || cmd.equals("look n") || cmd.equals("look north"))
        {
            look(Direction.NORTH);
        }
        else if(cmd.equals("ls") || cmd.equals("l s") || cmd.equals("l south") || cmd.equals("look s") || cmd.equals("look south"))
        {
            look(Direction.SOUTH);
        }
        else if(cmd.equals("le") || cmd.equals("l e") || cmd.equals("l east") || cmd.equals("look e") || cmd.equals("look east"))
        {
            look(Direction.EAST);
        }
        else if(cmd.equals("lw") || cmd.equals("l w") || cmd.equals("l west") || cmd.equals("look w") || cmd.equals("look west"))
        {
            look(Direction.WEST);
        }
        else if(cmd.equals("a") || cmd.equals("attack"))
        {
            attack();
        }
        else if(cmd.equals("k") || cmd.equals("talk"))
        {
            talk();
        }
        else if(cmd.equals("t") || cmd.equals("take"))
        {
            take();
        }
        else if(cmd.equals("f") || cmd.equals("flee"))
        {
            flee();
        }
        else if(cmd.equals("c") || cmd.equals("climb"))
        {
            climb();
        }
        else if(cmd.equals("i") || cmd.equals("inventory"))
        {
            showInventory();
        }
        else if(cmd.equals("?") || cmd.equals("h") || cmd.equals("help"))
        {
            showHelp();
            return;                                                             // Help doesn't count as an action
        }
        else
        {
            System.out.println("I don't understand that.");
            return;                                                             // Unknown commands don't count as an action
        }

        Character foe = dungeon[pos.x][pos.y].foe;
        if(foe != null)                                                         // if there is anyone else here, make npc move
        {
            if(1 + rnd.nextInt(10) < foe.aggressionLevel)
            {                                                                   // there is a chance for the npc to attack you
                System.out.printf("The %s attacks you with a %s!\n", foe.name, foe.weapon.name);

                if(rnd.nextInt(4000) < player.health)
                {                                                               // the chance of dodging an attack decreases with injuries
                    System.out.println("You were able to dodge the attack.");
                }
                else if(rnd.nextInt(20) == 4)
                {                                                               // critical hit
                    System.out.printf("The %s hit you really hard.\n", foe.weapon.name);
                    player.health -= (foe.weapon.attack * 2);
                }
                else
                {
                    System.out.println("You are hit.");
                    player.health -= foe.weapon.attack;
                }
            }
            else if(foe.aggressionLevel < 3)
            {                                                                   // just looking
                System.out.printf("The %s is looking at you.\n", foe.name);
            }
            else
            {                                                                   // growling
                System.out.printf("The %s is growling angrily.\n", foe.name);
            }
        }

        if(player.health < 1)                                                   // find out if player eventually died
        {
            System.out.println("You died.");
        }
    }


    /** Tries to go to a direction.
     * @param dir Direction. */
    private static void go(Direction dir)
    {
        Field f = dungeon[pos.x][pos.y];

        if(f.foe != null)
        {
            if(f.foe.aggressionLevel > rnd.nextInt(10))
            {                                                                   // npc has a chance to not let player go
                System.out.printf("The %s won't let you go.\n", f.foe.name);
                return;
            }
        }

        System.out.printf("You proceed to the %s.\n", parse(dir));
        pos = locationAt(dir);
    }


    /** Looks around. */
    private  static void look()
    {
        System.out.println("You see nothing else.");
    }


    /** Looks in a direction. */
    private static void look(Direction dir)
    {
        Point loc = locationAt(dir);
        Field tar = dungeon[loc.x][loc.y];

        System.out.printf("To the %s you see a %s.", parse(dir), tar.env);

        if(tar.hasWell && (rnd.nextInt(4) == 2))
        {                                                                       // there is a chance to detect a well
            System.out.print(" You perceive a strange glow.");
        }
        System.out.println();

        if((tar.foe != null) && (rnd.nextInt(7) == 2))
        {                                                                       // there is a chance to detect an npc
            System.out.printf("In the distance there appears to be a %s.\n", tar.foe.name);
        }
    }


    /** Let the player take an item. */
    private static void take()
    {
        Field f = dungeon[pos.x][pos.y];

        if(f.item == null)
        {
            System.out.println("There is nothing to take.");
            return;
        }

        if(f.item.attack < player.weapon.attack)
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("Your equipped weapon appears to be better.");
            System.out.print("Take it anyway (y[es]|N[o]? ");
            if(!sc.next().toLowerCase().startsWith("y"))
            {
                return;
            }
        }

        Weapon tmp = player.weapon.name.equals("hands") ? null : player.weapon;
        player.weapon = f.item;
        f.item = tmp;
    }


    /** Attacks a character. */
    private static void attack()
    {
        Character foe = dungeon[pos.x][pos.y].foe;

        if(foe == null)
        {                                                                       // nothing to attack around
            System.out.println("There is no one to attack.");
        }
        else
        {                                                                       // attack npc
            System.out.printf("You attack the %s with your %s.\n", foe.name, player.weapon.name);
            if(foe.aggressionLevel < 6)
            {                                                                   // increase aggression if peaceful
                foe.aggressionLevel = 6;
            }
            else if(foe.aggressionLevel < 10) { foe.aggressionLevel++; }

            if(rnd.nextInt(3000) < foe.health)
            {                                                                   // npc has dodge chance
                System.out.printf("The %s dodged the attack!\n", foe.name);
            }
            else if(rnd.nextInt(20) == 4)
            {                                                                   // critical hit
                System.out.println("You strike really hard!");
                foe.health -= (player.weapon.attack * 3);
            }
            else
            {
                System.out.println("You hit!");
                foe.health -= player.weapon.attack;
            }

            if(foe.health < 1)
            {                                                                   // npc dies
                kills++;
                Field f = dungeon[pos.x][pos.y];
                System.out.printf("You have slain the %s.\n", foe.name);
                f.foe = null;
                if(foe.dropsWeapon && (rnd.nextInt(3) == 2))
                {                                                               // has a chance to drop weapon
                    if((f.item == null) || (f.item.attack < foe.weapon.attack))
                    {                                                           // don't overwrite better weapon
                        System.out.printf("The enemy dropped a %s.\n", foe.weapon.name);
                        f.item = foe.weapon;
                    }
                }

                if(foe.coins > 0)
                {                                                               // get gold dropped
                    System.out.printf("You collect %d gold coins from the remains.", foe.coins);
                    player.coins += foe.coins;
                }

                if(player.health < 150) { player.health += 30; }                // regenerate a little
            }
        }
    }


    /** Talks to a character. */
    private static void talk()
    {
        Character foe = dungeon[pos.x][pos.y].foe;
        Scanner sc = new Scanner(System.in);

        if(foe == null)
        {                                                                       // no one to talk to
            System.out.println("It seems like you are talking to yourself.");
        }
        else if(((1 + rnd.nextInt(10)) > foe.aggressionLevel))
        {                                                                       // npc can be talked to
            if(foe.name.equals("Janitor"))
            {                                                                   // a friendly janitor
                System.out.println("The Janitor explains why it's so hard to keep this place tidy.");

                if(giftAvailable[0])
                {                                                               // player has not been given a broom yet
                    giftAvailable[0] = false;
                    Weapon broom = new Weapon("broom", 7);

                    if (broom.attack < player.weapon.attack)
                    {                                                           // already equipped better weapon
                        System.out.println("Then he offers to give you a broom. It appears to be worthless compared to your current weapon.");
                        System.out.print("Take it anyway (Y[es]|n[o]? ");
                        if (sc.next().toLowerCase().startsWith("y"))
                        {
                            dungeon[pos.x][pos.y].item = player.weapon;
                            player.weapon = broom;
                        }
                    }
                    else
                    {
                        System.out.println("He gives you a broom. You now have a better weapon equipped.");
                        dungeon[pos.x][pos.y].item = player.weapon;
                        player.weapon = broom;
                    }
                }
            }
            else if(foe.name.equals("Wizard"))
            {                                                                   // a friendly wizard
                System.out.println("The Wizard tells you about the deep secrets of the arcane ways.");

                if(giftAvailable[1])
                {                                                               // player has not been given a wizard's gift yet
                    giftAvailable[1] = false;

                    if(player.health < 200)
                    {
                        System.out.println("Then the Wizard agrees to heal your wounds. You are at full health again!");
                    }
                    else
                    {
                        Weapon wand = new Weapon("wand", 11);

                        if (wand.attack < player.weapon.attack)
                        {                                                       // already equipped better weapon
                            System.out.println("Then he offers you a magic wand. Your current weapon seems to exceed its powers.");
                            System.out.print("Take it anyway (Y[es]|n[o]? ");
                            if(sc.next().toLowerCase().startsWith("y"))
                            {
                                dungeon[pos.x][pos.y].item = player.weapon;
                                player.weapon = wand;
                            }
                        }
                    else
                    {
                            System.out.println("He gives you a magic wand. You now have a better weapon equipped.");
                            dungeon[pos.x][pos.y].item = player.weapon;
                            player.weapon = wand;
                        }
                    }
                }
            }
            else if(foe.name.equals("Adventurer"))
            {                                                                   // a friendly adventurer
                System.out.println("The Adventurer talks of his life of adventure.");

                if(giftAvailable[2])
                {                                                               // player has not been given a broom yet
                    giftAvailable[2] = false;
                    Weapon sword = new Weapon("broom", 26);

                    if(sword.attack < player.weapon.attack)
                    {                                                           // already equipped better weapon
                        System.out.println("Then he offers to give you a sword, but your equipped weapon is a somewhat better.");
                        System.out.print("Take it anyway (Y[es]|n[o]? ");
                        if(sc.next().toLowerCase().startsWith("y"))
                        {
                            dungeon[pos.x][pos.y].item = player.weapon;
                            player.weapon = sword;
                        }
                    }
                    else
                    {
                        System.out.println("The Adventurer gives you a fine sword. You now have a better weapon equipped.");
                        dungeon[pos.x][pos.y].item = player.weapon;
                        player.weapon = sword;
                    }
                }
            }
            else
            {
                System.out.printf("The %s looks at you as you talk.\n", foe.name);
            }
        }
        else
        {
            switch(rnd.nextInt(7))
            {
                case 1: System.out.printf("The %s won't listen.\n", foe.name); break;
                case 2: System.out.printf("The %s growls at you.\n", foe.name); break;
                case 3: System.out.printf("The %s looks menacing.\n", foe.name); break;
                default: System.out.printf("The %s doesn't seem to be interested in a conversation with you.\n", foe.name);
            }
        }

    }


    /** Tries to flee from the current field. */
    private static void flee()
    {
        Field f = dungeon[pos.x][pos.y];

        if(f.foe != null)
        {
            if(f.foe.aggressionLevel > rnd.nextInt(16))
            {                                                                   // npc has a chance to not let player go
                System.out.printf("You try to run away but the %s blocks your way!", f.foe.name);
                return;
            }
        }

        Direction dir;
        switch(rnd.nextInt(4))
        {
            case 1: dir = Direction.NORTH; break;
            case 2: dir = Direction.SOUTH; break;
            case 3: dir = Direction.EAST; break;
            default: dir = Direction.WEST; break;
        }
        System.out.println("You have run away.");
        pos = locationAt(dir);
    }


    /** Tries to climb out of the dungeon. */
    private static void climb()
    {
        if(dungeon[pos.x][pos.y].isExit)
        {
            System.out.println("You begin to climb up the ravine. After a few hours you see sunlight through the rock.");
            System.out.println("You have finally made it back.");
            won = true;
        }
        else
        {
            System.out.println("You can't climb here.");
        }
    }


    /** Shows the inventory. */
    private static void showInventory()
    {
        System.out.printf("Your current weapon is: %s.\n", player.weapon.name);
        System.out.printf("You have %d gold coins in your purse.\n", player.coins);
    }


    /** Gets the location to a direction.
     *  @param dir Direction.
     * @return Result coordinates. */
    private static Point locationAt(Direction dir)
    {
        int x = pos.x, y = pos.y;

        switch(dir)
        {
            case NORTH: y--; break;
            case SOUTH: y++; break;
            case EAST:  x++; break;
            case WEST:  x--; break;
        }
        if(x >= dungeon.length)
        {
            x = 0;
        }
        else if(x < 0) { x = dungeon.length - 1; }

        if(y >= dungeon[0].length)
        {
            y = 0;
        }
        else if(y < 0) { y = dungeon[0].length - 1; }

        return new Point(x, y);
    }


    /** Parses a direction.
     * @param dir Direction.
     * @return Direction text. */
    private static String parse(Direction dir)
    {
        switch(dir)
        {
            case NORTH: return "North";
            case SOUTH: return "South";
            case EAST:  return "East";
        }
        return "West";
    }


    /** Shows help. */
    private static void showHelp()
    {
        System.out.println("Valid commands:");
        System.out.println("   [Go ]N[orth]|S[outh]|E[ast]|W[est]");
        System.out.println("   L[ook][ N[orth]|S[outh]|E[ast]|W[est]]");
        System.out.println("   A[ttack]");
        System.out.println("   [Tal]k");
        System.out.println("   T[ake]");
        System.out.println("   F[lee]");
        System.out.println("   C[limb]");
        System.out.println("   I[nventory]");
        System.out.println("   H[elp]");
    }


    /** Shows the starting story. */
    private static void startStory()
    {
        System.out.println("A magical portal that suddenly appeared in your room has sent you to an underground dungeon.");
        System.out.println("You find yourself in an eerie environment. Whooshing shadows make you believe you're not alone.");
        System.out.println("There must be a way out! There must!");
        System.out.println("You decide to head out and find a way back to the surface.");
        System.out.println();
    }


    /** Prints a debrief summery for the player. */
    private static void debrief()
    {
        System.out.println("Your adventure is over.");
        System.out.printf("You have killed %d creatures and collected %d gold pieces.\n", kills, player.coins);
    }


    /** Initializes the dungeon. */
    private static void initDungeon()
    {
        dungeon = new Field[40][40];                                            // create the dungeon map

        for(int i = 0; i < dungeon.length; i++)
        {                                                                       // initialize dungeon map
            for(int k = 0; k < dungeon[0].length; k++) { dungeon[i][k] = new Field(); }
        }

        for(int i = 0; i < 63; i++)                                             // add monsters
        {
            Character m;
            int coins = rnd.nextInt(20);
            if(i < 12) { m = new Character("Ork", new Weapon("mace", 12), 85,  coins, 7, true); }
            else if(i < 18) { m = new Character("Ogre", new Weapon("giant club", 19), 140,  coins, 8, true); }
            else if(i < 30) { m = new Character("Werebadger", new Weapon("fang", 8), 50,  0, 6, false); }
            else if(i < 36) { m = new Character("Werewolf", new Weapon("fang", 11), 80,  0, 10, false); }
            else if(i < 41) { m = new Character("Spinder", new Weapon("fang", 8), 40,  0, 9, false); }
            else if(i < 46) { m = new Character("Goblin", new Weapon("dagger", 9), 40,  coins, 5, true); }
            else if(i < 52) { m = new Character("Goblin", new Weapon("dart", 7), 38,  coins, 7, true); }
            else if(i < 59) { m = new Character("Rogue", new Weapon("bodkin", 6), 42,  coins, 9, true); }
            else if(i == 59) { m = new Character("Wizard", new Weapon("staff", 10), 80,  0, 2, true); }
            else if(i == 60) { m = new Character("Janitor", new Weapon("broom", 7), 80,  0, 0, true); }
            else { m = new Character("Adventurer", new Weapon("sword", 30), 120,  coins, 1, true); }

            while(true)                                                         // places the monster
            {
                int x = rnd.nextInt(dungeon.length), y = rnd.nextInt(dungeon[0].length);
                if(dungeon[x][y].foe == null)
                {
                    dungeon[x][y].foe = m; break;
                }
            }
        }

        for(int i = 0; i < 60; i++)
        {
            dungeon[rnd.nextInt(dungeon.length)][rnd.nextInt(dungeon[0].length)].coins += rnd.nextInt(8);
        }

        for(int i = 0; i < 40; i++)                                             // add some wells
        {
            dungeon[rnd.nextInt(dungeon.length)][rnd.nextInt(dungeon[0].length)].hasWell = true;
        }

        for(int i = 0; i < 26; i++)                                              // add a few weapons
        {
            Weapon w = null;
            if(i < 7) { w = new Weapon("dagger", 11); }
            else if(i < 12) { w = new Weapon("knife", 10); }
            else if(i < 17) { w = new Weapon("axe", 13); }
            else if(i < 19) { new Weapon("pan", 7); }
            else { w = new Weapon("hammer", 16); }

            dungeon[rnd.nextInt(dungeon.length)][rnd.nextInt(dungeon[0].length)].item = w;
        }

        do
        {                                                                       // place player
            pos = new Point(rnd.nextInt(dungeon.length), rnd.nextInt(dungeon[0].length));
        }
        while(dungeon[pos.x][pos.y].foe != null);
    }
}
