package FullThrottle;

import org.jsfml.graphics.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;

public class Main {
    public static void main(String[] args){
        RenderWindow window = new RenderWindow(new VideoMode(800, 600), "Full Throttle");

        //Main loop
        while(window.isOpen()) {
            //Fill the window with red
            window.clear(Color.RED);

            //Display what was drawn (... the red color!)
            window.display();

            //Handle events
            for(Event event : window.pollEvents()) {
                if(event.type == Event.Type.CLOSED) {
                    //The user pressed the close button
                    window.close();
                }
            }
        }
    }
}
