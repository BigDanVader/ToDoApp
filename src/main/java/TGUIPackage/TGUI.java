package TGUIPackage;

import java.util.Scanner;

/**
 * The {@code TGUI} class provides console input to the calling method using this object with included error checking.
 * It is expected that the calling method will determine whether the input is valid in the context of its own parameters
 * 
 * @author Dan Luoma
 */
public class TGUI {

    private Scanner in;
    
    public TGUI(){
        
    }

    /**
     * This gets a {@code char} from the user, checks that it is a valid char, and returns the user input.
     * It is expected that a user will not be allowed to return a blank entry.
     * 
     * @return a {@code char} representing the user input
     */
    public char getCharSelection(){
        in = new Scanner(System.in);
        String input = in.nextLine();
        System.out.println();
        if (input.length() == 0)
            //User has entered a blank selection. Passing '?' as a sentinal
            //to let calling program know there was an input error
            return '?';

        return Character.toUpperCase(input.charAt(0));
    }

    /**
     * This gets an {@code int} from the user, checks that it is a valid int, and returns the user input.
     * It is expected that a user will not be allowed to return a blank entry
     * 
     * @return a {@code int} representing the user input
     */
    public int getNumSelection(){
        in = new Scanner(System.in);
        String input = in.nextLine();
        System.out.println();
        if (input.length() == 0)
            //User has entered a blank selection. Passing -1 as a sentinal
            //to let calling program know there was an input error
            return -1;
        
        try {
            int sel = Integer.parseInt(input);
            return sel;
        } catch (NumberFormatException e) {
            // User has entered a non-int value. Passing -1 as a sentinal
            //to let calling program know there was an input error
            return -1;
        }
    }

    /**
     * This gets a {@code String} from the user and returns the user input.
     * This method does allow for null input.
     * 
     * @return a {@code String} representing the user input
     */
    public String getStringSelection(){
        in  = new Scanner(System.in);
        String update = in.nextLine();
        System.out.println();
        return update;
    }

    /**
     * This closes the Scanner object used by this class in preperation of ending the program.
     */
    public void close(){
        if (in != null)
            in.close();
    }
}
