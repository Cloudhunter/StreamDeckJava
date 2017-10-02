import uk.co.cloudhunter.streamdeckjava.IStreamDeck;
import uk.co.cloudhunter.streamdeckjava.StreamDeckJava;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * An example showing writing an image to keys
 */
public class Example1
{
    public static void main(String[] args) {
        try
        {
            IStreamDeck deck = StreamDeckJava.getFirstStreamDeck();

            Rectangle[] tangles = new Rectangle[deck.getNumberOfKeys()];

            for (int i = 0; i < deck.getNumberOfKeys(); i++)
            {
                int sub = i <= 4 ? 4 : i <= 9 ? 9 : 14;
                tangles[i] = new Rectangle((72 * (sub - i)), (i / 5) * 72, 72, 72);
            }

            while (true)
            {
                try {
                    // Take screenshot
                    BufferedImage screenshot = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                    for(int i = 0; i < deck.getNumberOfKeys(); i++)
                    {
                        // Get part of screenshot corresponding to key
                        BufferedImage image = screenshot.getSubimage(tangles[i].x, tangles[i].y, tangles[i].width, tangles[i].height);
                        // Set the key bitmap
                        deck.setKeyBitmap(i, image);
                    }
                } catch (AWTException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
