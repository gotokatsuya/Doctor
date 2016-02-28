// IIntentReceiveInterface.aidl
package com.goka.doctor;

// Declare any non-default types here with import statements
import com.goka.doctor.IIntentReceiveCallbackInterface;

interface IIntentReceiveInterface {
    oneway void registerCallback(IIntentReceiveCallbackInterface callback);
    oneway void unregisterCallback(IIntentReceiveCallbackInterface callback);
}
