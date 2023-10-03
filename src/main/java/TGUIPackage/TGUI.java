package TGUIPackage;

import java.util.InputMismatchException;
import java.util.Scanner;

public class TGUI {

    private Scanner in;
    
    public TGUI(){
        
    }

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
        } catch (InputMismatchException e) {
            // User has entered a non-int value. Passing -1 as a sentinal
            //to let calling program know there was an input error
            return -1;
        }
    }

    public String getStringSelection(){
        in  = new Scanner(System.in);
        String update = in.nextLine();
        System.out.println();
        return update;
    }

    public void close(){
        if (in != null)
            in.close();
    }
}
