package uk.co.cloudhunter.streamdeckjava;

/**
 * A class representing a change in the key state.
 */
public class StreamDeckKeyState
{
    private boolean[] keyState;
    private boolean[] keyChanged;
    private boolean invalid;

    StreamDeckKeyState(byte[] rawData)
    {
        if (rawData[0] == 0)
            invalid = true;
        keyState = new boolean[rawData.length - 1];
        keyChanged = new boolean[rawData.length - 1];
        for (int i = 1; i < 16; i++)
        {
            keyState[i - 1] = rawData[i] == 1;
        }
    }

    /**
     * A function to check if a key has been pressed.
     * @param key The ID of the key
     * @return Whether the key has been pressed
     */
    public boolean keyPressed(int key)
    {
        return keyState[key];
    }

    /**
     * A function to check if a key has changed - should be used in conjunction with keyPressed
     * @param key The ID of the key
     * @return Whether the key has changed from its previous state
     */
    public boolean keyChanged(int key)
    {
        return keyChanged[key];
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("StreamDeckKeyState[");
        for (int i = 0; i < keyState.length; i++)
        {
            builder.append(i).append(":").append(keyPressed(i) ? "down" : "up");
            if (i != keyState.length - 1)
                builder.append(",");
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof StreamDeckKeyState))
            return false;

        StreamDeckKeyState state = (StreamDeckKeyState) obj;
        for (int i = 0; i < keyState.length; i++)
        {
            if (state.keyPressed(i) != this.keyPressed(i))
            {
                return false;
            }
        }
        return true;
    }

    boolean isInvalid()
    {
        return invalid;
    }

    void setChanged(int changed)
    {
        keyChanged[changed] = true;
    }
}
