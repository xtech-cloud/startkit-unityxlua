package cloud.xtech.actor;

import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CaptureListener extends FileObserver {
    public  CaptureListener(String _path){
        super(_path);
    }

    @Override
    public void onEvent(int _event, String _path) {
        switch (_event) {
            case FileObserver.CLOSE_WRITE:
                Log.i("ActorPlugin", _path + " created");
                postCapture(_path);
                break;
        }
    }

    private void postCapture(String _path){
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/Screenshots/" + _path;
        File file = new File(filepath);
        try {
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            ActorData.latestCapture = new byte[length];
            fis.read(ActorData.latestCapture);
            fis.close();
            file.delete();
        } catch (FileNotFoundException e) {
            Log.e("ActorPlugin", e.getMessage());
        } catch (IOException e) {
            Log.e("ActorPlugin", e.getMessage());
        }
        Log.i("ActorPlugin", "ready to post capture, size is " + ActorData.latestCapture.length);
    }
}
