import uk.co.cloudhunter.streamdeckjava.IStreamDeckListener;
import uk.co.cloudhunter.streamdeckjava.IStreamDeck;
import uk.co.cloudhunter.streamdeckjava.StreamDeckJava;
import uk.co.cloudhunter.streamdeckjava.StreamDeckKeyState;

import java.util.Random;

/**
 * An example demonstrating key handling and setting random colours based on key presses
 */
public class Example3
{
    public static void main(String[] args) {
        final IStreamDeck deck = StreamDeckJava.getFirstStreamDeck();
        final Random random = new Random();

        // Register a listener to key changes
        deck.registerKeyListener(new IStreamDeckListener()
        {
            // Called when any key is changed
            @Override
            public void keyStateChanged(StreamDeckKeyState state)
            {
                for(int i = 0; i < deck.getNumberOfKeys(); i++)
                {
                    // Get if key is changed, and if it is pressed
                    if (state.keyChanged(i) && state.keyPressed(i))
                    {
                        // Set a random colour on the key that has been pressed
                        deck.setKeyColour(i, (byte)random.nextInt(255), (byte)random.nextInt(255), (byte)random.nextInt(255));

                    }
                }
            }
        });

        while (true)
        {
            try
            {
                Thread.sleep(20000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                return;
            }
        }
    }
}
