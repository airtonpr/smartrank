package android.support.v4.os;

import android.os.*;

public interface ParcelableCompatCreatorCallbacks<T>
{
    T createFromParcel(Parcel p0, ClassLoader p1);
    
    T[] newArray(int p0);
}
