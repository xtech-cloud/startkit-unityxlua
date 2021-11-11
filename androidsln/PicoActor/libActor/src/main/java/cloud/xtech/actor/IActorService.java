package cloud.xtech.actor;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;

public interface IActorService extends IInterface
{
    abstract class Stub extends Binder implements IActorService {
        private static final String DESCRIPTOR = "cloud.xtech.actor.interfaces.IActorService";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IActorService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
                return (IActorService)(iin != null && iin instanceof IActorService ? (IActorService)iin : new IActorService.Stub.Proxy(obj));
            }
        }
        public IBinder asBinder() {
            return this;
        }

        private static class Proxy implements IActorService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }
        }

    }
}
