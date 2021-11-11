package cloud.xtech.actor;

public interface UpgraderListener {
    //用于通知当前下载进度
    void onProgress(int progress);

    //用于通知下载成功
    void onSuccess(String _file);

    //用于通知下载失败
    void onFailed();

    //用于通知下载暂停
    void onPaused();

    //用于通知下载取消
    void onCanceled();
}
