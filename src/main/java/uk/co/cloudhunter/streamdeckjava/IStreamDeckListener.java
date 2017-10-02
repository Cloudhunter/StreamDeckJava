package uk.co.cloudhunter.streamdeckjava;

/**
 * A class which is used to get called upon a change in the key state
 */
public interface IStreamDeckListener
{
    /**
     * A function which is called whenever the key state changes. This is called on the thread which is listening for keys - shouldn't take too long here!
     * @param state The current key state
     */
    void keyStateChanged(StreamDeckKeyState state);
}
