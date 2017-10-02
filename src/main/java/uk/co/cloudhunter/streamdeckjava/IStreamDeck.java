package uk.co.cloudhunter.streamdeckjava;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * An interface to a stream deck.
 */
public interface IStreamDeck
{
    /**
     * Prepares the Stream Deck for use - only required if using the path to multiple Stream Decks.
     */
    void prepare();

    /**
     * Returns the number of keys. There is only one Stream Deck released currently, but implemented in case future Stream Decks are released.
     * @return
     */
    int getNumberOfKeys();

    /**
     * Sets the brightness of the Elgato's LCD display.
     * @param percent The percent.
     */
    void setBrightness(byte percent);

    /**
     * Instructs the Elgato Stream Deck to display the Elgato logo.
     */
    void showLogo();

    /**
     * Closes device and instructs it to go back to the logo. Not strictly necessary, but nice to have!
     */
    void dispose();

    /**
     * Sets a key to a solid colour. Takes three bytes as the colour.
     * @param id The ID of the key
     * @param r Red
     * @param g Green
     * @param b Blue
     */
    void setKeyColour(int id, byte r, byte g, byte b);

    /**
     * Sets a key to a solid colour. Takes an int - can be specified as 0xRRGGBB
     * @param id The ID of the key
     * @param colour The colour passed as an int
     */
    void setKeyColour(int id, int colour);

    /**
     * Sets a BufferedImage as a key image. Inputted BufferedImage _MUST_ be 72x72.
     * @param id The ID of the key
     * @param image The BufferedImage to set.
     * @throws IOException
     */
    void setKeyBitmap(int id, BufferedImage image) throws IOException;

    /**
     * Sets a byte array to a key. Inputted data _MUST_ be 72x72.
     * @param id The ID of the key
     * @param bmdata A byte array containing the bitmap. _MUST_ be 72x72.
     */
    void setKeyBitmap(int id, byte[] bmdata);

    /**
     * Use this to register a key listener. Listener task doesn't listen unless at least one listener is registered.
     * @param listener
     */
    void registerKeyListener(IStreamDeckListener listener);

    /**
     * Use this to remove a previously registered key listener
     * @param listener A listener you want to unregister
     */
    void unRegisterKeyListener(IStreamDeckListener listener);
}
