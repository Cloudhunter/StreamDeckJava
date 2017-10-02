import uk.co.cloudhunter.streamdeckjava.IStreamDeck;
import uk.co.cloudhunter.streamdeckjava.StreamDeckJava;

import java.util.Random;

/**
 * An example to show setting random colours in a loop
 */
public class Example2
{
    public static void main(String[] args) {
        IStreamDeck deck = StreamDeckJava.getFirstStreamDeck();

        if (deck == null)
        {
            return;
        }

        Random random = new Random();


        while (true)
        {
            for(int i = 0; i < deck.getNumberOfKeys(); i++)
            {
                // Random colour setting!
                deck.setKeyColour(i, (byte)random.nextInt(255), (byte)random.nextInt(255), (byte)random.nextInt(255));
            }
        }
    }
}
