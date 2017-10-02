package uk.co.cloudhunter.streamdeckjava;

import org.hid4java.HidDevice;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class representing an instance of a Stream Deck.
 */
public class StreamDeck implements IStreamDeck
{
    private HidDevice deckDevice;
    private final int numOfKeys = 15;
    private final int iconSize = 72;
    private final int rawBitmapDataLength = iconSize * iconSize * 3;
    private final Object disposeLock = new Object();
    private boolean disposed = false;
    private static final int pagePacketSize = 8191;
    private static final int numFirstPagePixels = 2583;
    private static final int numSecondPagePixels = 2601;
    private ArrayList<IStreamDeckListener> listeners = new ArrayList<IStreamDeckListener>();
    private Thread keyListenTask = null;
    private boolean isListening = true;

    private static final byte[] headerTemplatePage1 = new byte[] {
        0x02, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x42, 0x4d, (byte) 0xf6, 0x3c, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x28, 0x00,
        0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x48, 0x00,
        0x00, 0x00, 0x01, 0x00, 0x18, 0x00, 0x00, 0x00,
        0x00, 0x00, (byte) 0xc0, 0x3c, 0x00, 0x00, (byte) 0xc4, 0x0e,
        0x00, 0x00, (byte) 0xc4, 0x0e, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    private static final byte[] headerTemplatePage2 = new byte[] {
        0x02, 0x01, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    StreamDeck(HidDevice device, boolean open)
    {
        deckDevice = device;
        if (open)
            prepare();
    }

    StreamDeck(HidDevice device)
    {
        this(device, false);
    }

    /**
     * Prepares the Stream Deck for use - only required if using the path to multiple Stream Decks.
     */
    public void prepare()
    {
        if (!deckDevice.isOpen())
            deckDevice.open();
    }

    /**
     * Returns the number of keys. There is only one Stream Deck released currently, but implemented in case future Stream Decks are released.
     * @return
     */
    @Override
    public int getNumberOfKeys()
    {
        return numOfKeys;
    }

    /**
     * Sets the brightness of the Elgato's LCD display.
     * @param percent The percent.
     */
    @Override
    public void setBrightness(byte percent)
    {
        if (percent > 100) throw new IllegalArgumentException("Percent must not be over 100!");
        byte[] buffer = new byte[] { 0x55, (byte)0xaa, (byte)0xd1, 0x01, 0x64, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        buffer[4] = percent;
        deckDevice.sendFeatureReport(buffer, (byte)0x05);
    }

    /**
     * Instructs the Elgato Stream Deck to display the Elgato logo.
     */
    @Override
    public void showLogo()
    {
        deckDevice.sendFeatureReport(new byte[] { 0x63 }, (byte)0x0B);
    }

    /**
     * Closes device and instructs it to go back to the logo. Not strictly necessary, but nice to have!
     */
    @Override
    public void dispose()
    {
        synchronized (disposeLock)
        {
            if (disposed) return;
            disposed = true;
        }

        if (deckDevice == null) return;

        showLogo();

        deckDevice.close();
        deckDevice = null;
    }

    /**
     * Sets a key to a solid colour. Takes three bytes as the colour.
     * @param id The ID of the key
     * @param r Red
     * @param g Green
     * @param b Blue
     */
    @Override
    public void setKeyColour(int id, byte r, byte g, byte b)
    {
        byte[] rgb = {b, g, r};
        byte[] img = new byte[15552];
        for (int i = 0; i < img.length; i++)
            img[i] = rgb[i % 3];

        setKeyBitmap(id, img);
    }

    /**
     * Sets a key to a solid colour. Takes an int - can be specified as 0xRRGGBB
     * @param id The ID of the key
     * @param colour The colour passed as an int
     */
    @Override
    public void setKeyColour(int id, int colour)
    {
        byte b = (byte) (colour & 0xFF);
        byte g = (byte) ((colour >> 8) & 0xFF);
        byte r = (byte) ((colour >> 16) & 0xFF);
        setKeyColour(id, r, g, b);
    }

    /**
     * Sets a BufferedImage as a key image. Inputted BufferedImage _MUST_ be 72x72.
     * @param id The ID of the key
     * @param image The BufferedImage to set.
     * @throws IOException
     */
    @Override
    public void setKeyBitmap(int id, BufferedImage image) throws IOException
    {
        if (image.getWidth() > 72 || image.getHeight() > 72)
            throw new IOException("Image is greater than 72 x 72");
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] result = new byte[height * width * 3];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = new Color(image.getRGB(col, row));

                int i = ((row + 1) * height * 3) - ((col) * 3) - 3;

                result[i] = (byte) color.getBlue();
                result[i + 1] = (byte) color.getGreen();
                result[i + 2] = (byte) color.getRed();
            }
        }

        setKeyBitmap(id, result);
    }

    /**
     * Sets a byte array to a key. Inputted data _MUST_ be 72x72.
     * @param id The ID of the key
     * @param bmdata A byte array containing the bitmap. _MUST_ be 72x72.
     */
    @Override
    public void setKeyBitmap(int id, byte[] bmdata)
    {
        byte[] page1 = generatePage1(id, bmdata);
        byte[] page2 = generatePage2(id, bmdata);
        byte repID1 = page1[0];
        byte repID2 = page2[0];
        page1 = Arrays.copyOfRange(page1, 1, page1.length);
        page2 = Arrays.copyOfRange(page2, 1, page2.length);
        deckDevice.write(page1, page1.length, repID1);
        deckDevice.write(page2, page2.length, repID2);
    }

    /**
     * Use this to register a key listener. Listener task doesn't listen unless at least one listener is registered.
     * @param listener
     */
    @Override
    public void registerKeyListener(IStreamDeckListener listener)
    {
        synchronized (listeners)
        {
            listeners.add(listener);
            if (keyListenTask == null)
            {
                isListening = true;
                keyListenTask = new Thread(new KeyListenTask());
                keyListenTask.setName("StreamDeck key listener");
                keyListenTask.setDaemon(true);
                keyListenTask.start();
            }
        }
    }

    /**
     * Use this to remove a previously registered key listener
     * @param listener A listener you want to unregister
     */
    @Override
    public void unRegisterKeyListener(IStreamDeckListener listener)
    {
        synchronized (listeners)
        {
            listeners.remove(listener);
            if (listeners.isEmpty() && keyListenTask != null)
            {
                isListening = false;
                keyListenTask.setDaemon(false);
                keyListenTask = null;
            }
        }
    }

    private static byte[] generatePage1(int keyId, byte[] imgData)
    {
        byte[] p1 = new byte[pagePacketSize];
        System.arraycopy(headerTemplatePage1, 0, p1, 0, headerTemplatePage1.length);

        if (imgData != null)
            System.arraycopy(imgData, 0, p1, headerTemplatePage1.length, numFirstPagePixels * 3);

        p1[5] = (byte)(keyId + 1);
        return p1;
    }

    private static byte[] generatePage2(int keyId, byte[] imgData)
    {
        byte[] p2 = new byte[pagePacketSize];
        System.arraycopy(headerTemplatePage2, 0, p2, 0, headerTemplatePage2.length);

        if (imgData != null)
            System.arraycopy(imgData, numFirstPagePixels * 3, p2, headerTemplatePage2.length, numSecondPagePixels * 3);

        p2[5] = (byte)(keyId + 1);
        return p2;
    }

    private class KeyListenTask implements Runnable
    {
        private StreamDeckKeyState prevState;

        @Override
        public void run()
        {
            while (isListening)
            {
                byte[] data = new byte[16];
                deckDevice.read(data, 1000);
                synchronized (listeners)
                {
                    if (!listeners.isEmpty())
                    {
                        StreamDeckKeyState state = new StreamDeckKeyState(data);

                        if (!state.equals(prevState))
                            if (!state.isInvalid())
                            {
                                for(int i = 0; i < StreamDeck.this.getNumberOfKeys(); i++)
                                {
                                    if (prevState == null ? state.keyPressed(i) : prevState.keyPressed(i) != state.keyPressed(i))
                                    {
                                        state.setChanged(i);
                                    }
                                }

                                for (IStreamDeckListener listener : listeners)
                                {
                                    listener.keyStateChanged(state);
                                }
                                prevState = state;
                            }
                    }
                }
            }
        }
    }
}
