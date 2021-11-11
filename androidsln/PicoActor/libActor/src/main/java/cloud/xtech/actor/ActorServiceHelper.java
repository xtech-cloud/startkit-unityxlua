package cloud.xtech.actor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


// 参考PicoVR ToBService 实现

public class ActorServiceHelper {
    private static ActorServiceHelper instance;
    private IActorService serviceBinder;
    private Context mContext;

    private BindCallBack mCallBack;

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("ActorPlugin", "ActorServiceHelper.onServiceConnected ");
            serviceBinder = IActorService.Stub.asInterface(service);
            if (serviceBinder == null) {
                Log.e("ActorPlugin", "ActorServiceHelper.onServiceConnected: fail");
            } else {
                try {
                    Log.d("ActorPlugin", "ActorServiceHelper.serviceBinder linkToDeath");
                    service.linkToDeath(mDeathRecipient, 0);
                } catch (RemoteException var4) {
                    var4.printStackTrace();
                }

                if (mCallBack != null) {
                    mCallBack.bindCallBack(true);
                }

                Log.e("ActorPlugin", "ActorServiceHelper.onServiceConnected: success");
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d("ActorPlugin", "ActorServiceHelper.onServiceDisconnected: ");
            if (mCallBack != null) {
                mCallBack.bindCallBack(false);
            }
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        public void binderDied() {
            Log.e("ActorPlugin", "ActorServiceHelper.binderDied ");
            if (mCallBack != null) {
                mCallBack.bindCallBack(false);
            }

            if (serviceBinder != null) {
                serviceBinder.asBinder().unlinkToDeath(mDeathRecipient, 0);
                bindTobService(mContext);
            }
        }
    };

    public static ActorServiceHelper getInstance() {
        if (instance == null) {
            instance = new ActorServiceHelper();
        }
        return instance;
    }

    public void setBindCallBack(ActorServiceHelper.BindCallBack callBack) {
        this.mCallBack = callBack;
    }

    private ActorServiceHelper() {
    }

    public void bindTobService(Context context) {
        Log.d("ActorPlugin", "ActorServiceHelper.bindTobService ");
        mContext = context;
        Intent intent = new Intent(context, ActorService.class);
        context.bindService(intent, this.conn, Context.BIND_AUTO_CREATE);
    }

    public void unBindTobService(Context context) {
        context.unbindService(this.conn);
    }

    public IActorService getServiceBinder() {
        if (this.serviceBinder == null) {
            Log.e("ActorPlugin", "ActorServiceHelper.getServiceBinder: 请先绑定ActorService");
        } else if (!this.serviceBinder.asBinder().isBinderAlive() || !this.serviceBinder.asBinder().pingBinder()) {
            Log.e("ActorPlugin", "ActorServiceHelper.getServiceBinder: 与ActorService连接断开，需要重新绑定");
            this.bindTobService(this.mContext);
        }

        return this.serviceBinder;
    }

    public interface BindCallBack {
        void bindCallBack(Boolean _status);
    }
}


