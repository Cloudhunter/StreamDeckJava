package uk.co.cloudhunter.streamdeckjava;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;

import java.util.ArrayList;

public class StreamDeckJava
{

    private static final Integer VENDOR_ID = 0x0FD9;
    private static final Integer PRODUCT_ID = 0x0060;
    public static final String SERIAL_NUMBER = null;

    /**
     * Returns the first Stream Deck found. Will be ready to use as will already have been prepared!
     * @return
     */
    public static IStreamDeck getFirstStreamDeck()
    {
        HidServices hidServices = HidManager.getHidServices();

        hidServices.start();

        HidDevice hidDevice = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, SERIAL_NUMBER);
        return hidDevice == null ? null : new StreamDeck(hidDevice);
    }

    /**
     * Gets a list of all the Stream Decks on the system. Untested with multiple devices, as I'm not a millionaire and only have one!
     * You must call StreamDeck.prepare() in order to prepare it to start receiving commands.
     * @return
     */
    public static ArrayList<IStreamDeck> getAllStreamDecks()
    {
        HidServices hidServices = HidManager.getHidServices();

        hidServices.start();

        ArrayList<IStreamDeck> ret = new ArrayList<IStreamDeck>();

        for(HidDevice device : hidServices.getAttachedHidDevices())
        {
            if (device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID)
            {
                ret.add(new StreamDeck(device, false));
            }
        }

        return ret;
    }
}
