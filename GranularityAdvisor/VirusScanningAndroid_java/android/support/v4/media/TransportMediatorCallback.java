package android.support.v4.media;

import android.view.*;

interface TransportMediatorCallback
{
    long getPlaybackPosition();
    
    void handleAudioFocusChange(int p0);
    
    void handleKey(KeyEvent p0);
    
    void playbackPositionUpdate(long p0);
}
