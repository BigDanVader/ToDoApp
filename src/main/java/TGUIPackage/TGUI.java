package TGUIPackage;

import java.util.Scanner;

public class TGUI {

    private Scanner in;
    
    public TGUI(){
        
    }

    public char getCharSelection(){
        in = new Scanner(System.in);
        String input = in.nextLine();
        System.out.println();
        return Character.toUpperCase(input.charAt(0));
    }

    public int getNumSelection(){
        //Since arrays start at 0 and our list starts at 1,
        //We subtract 1 from the user input to get the 
        //correct ToDoBean from Arraylist
        int sel = in.nextInt() - 1;
        System.out.println();
        return sel;
    }

    public String getStringSelection(){
        String update = in.nextLine();
        System.out.println();
        return update;
    }

    public void close(){
        if (in != null)
            in.close();
    }
}
