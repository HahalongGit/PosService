package com.lll.posserviceaidl.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.lll.posserviceaidl.AidlDeviceManager;
import com.lll.posserviceaidl.AidlScanCallback;
import com.lll.posserviceaidl.IPosQuickScanManager;
import com.lll.posserviceaidl.PosPrinterCallback;
import com.lll.posserviceaidl.PsoPrinterManager;
import com.lll.posserviceaidl.bean.CameraBeanZbar;
import com.lll.posserviceaidl.bean.DecodeResult;
import com.lll.posserviceaidl.bean.PosInfo;
import com.lll.posserviceaidl.bean.PrinterParams;
import com.lll.posserviceaidl.constant.Constant;

import java.util.HashMap;
import java.util.List;

/**
 * @author runningDigua
 * @date 2019/11/27
 */
public class PosPrinterService extends Service {

    private Handler mHandler;

    private final int MESSAGE_CODE = 10001;

    private static final String TAG = "PosPrinterService";

    private String posInfo = "我是Service--pos机器，我的型号是XZ20191120";

    /**
     * 设备功AIDL管理Binder（对其他Binder分发）
     */
    private AidlDeviceManagerImpl mAidlDeviceManager = new AidlDeviceManagerImpl();
    /**
     * 打印模块Binder
     */
    private PosPrinterBinder mPosPrinterBinder = new PosPrinterBinder();

    /**
     * pos扫码功能Binder
     */
    private PosScannQRBinder mPosScannQRBinder = new PosScannQRBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        Log.e(TAG, "onCreate-currentThread: " + Thread.currentThread().getName());
        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MESSAGE_CODE: {
                        if (msg.obj instanceof PosInfo) {
                            PosInfo posInfo = (PosInfo) msg.obj;
                            Toast.makeText(PosPrinterService.this, posInfo.getManufacturers(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind-currentThread: " + Thread.currentThread().getName());
        return mAidlDeviceManager;
    }

    /**
     * Aidl Device Manager
     */
    private class AidlDeviceManagerImpl extends AidlDeviceManager.Stub {

        @Override
        public IBinder getDevice(int deviceType) throws RemoteException {
            // 根据参数分发 Binder
            if (deviceType == Constant.DEVICE_TYPE.DEVICE_TYPE_PRINTERDEV) {
                return mPosPrinterBinder;
            } else if (deviceType == Constant.DEVICE_TYPE.DEVICE_TYPE_QUICKSCAN) {
                return mPosScannQRBinder;
            }
            return null;
        }

        @Override
        public void destoryDeivce(int deviceType) throws RemoteException {

        }

        @Override
        public String getVersion() throws RemoteException {

            return null;
        }
    }

    /**
     * 扫码Binder
     */
    private class PosScannQRBinder extends IPosQuickScanManager.Stub {

        @Override
        public int init(String packageName) throws RemoteException {
            //初始化扫码功能 返回状态
            return 0;
        }

        @Override
        public void cancelQRscan() throws RemoteException {

        }

        @Override
        public void switchCameraDisplayEffect(int cameraType, int mode) throws RemoteException {

        }

        @Override
        public DecodeResult decodeBarCode(int width, int height, byte[] data) throws RemoteException {
            Log.e(TAG, "decodeBarCode-thread: " + Thread.currentThread().getName());
            Log.e(TAG, "decodeBarCode: " + width + "--" + height);
            return null;
        }

        @Override
        public void scanQrCode(CameraBeanZbar cameraBeanZbar, AidlScanCallback cb) throws RemoteException {
            Log.e(TAG, "scanQrCode-Thread: " + Thread.currentThread().getName());
            if (cameraBeanZbar != null) {
                Log.e(TAG, "scanQrCode-CameraId: " + cameraBeanZbar.getCameraId());
                Log.e(TAG, "scanQrCode-Height: " + cameraBeanZbar.getHeight());
                Log.e(TAG, "scanQrCode-LightMode: " + cameraBeanZbar.getLightMode());
                Log.e(TAG, "scanQrCode-Time: " + cameraBeanZbar.getTime());
                Log.e(TAG, "scanQrCode-SpinDegree: " + cameraBeanZbar.getSpinDegree());
                HashMap<String, Object> externalMap = cameraBeanZbar.getExternalMap();
                boolean scanEffect = (boolean) externalMap.get("ScanEffect");
                boolean showPreview = (boolean) externalMap.get("ShowPreview");
                Log.e(TAG, "scanQrCode-scanEffect: " + scanEffect);
                Log.e(TAG, "scanQrCode-showPreview: " + showPreview);
                if (cb != null) {
                    cb.onCaptured("12423", 23144);
                }
            }
        }
    }

    /**
     * 打印机 Binder
     */
    private class PosPrinterBinder extends PsoPrinterManager.Stub {

        @Override
        public String queryPosInfo(String psoId) throws RemoteException {
            // Binder线程池
            Log.e(TAG, "queryPosInfo-currentThread: " + Thread.currentThread().getName());
            return posInfo + "--" + psoId;
        }

        @Override
        public void spitPaper(int distance) throws RemoteException {
            Log.e(TAG, "spitPaper: " + distance);
        }

        @Override
        public void setPostInfo(PosInfo posInfo) throws RemoteException {
            // Binder线程池
            Log.e(TAG, "setPostInfo-currentThread: " + Thread.currentThread().getName());
            if (posInfo != null) {
                Log.e(TAG, "setPostInfo: " + posInfo.getManufacturers());
                Message message = mHandler.obtainMessage();
                message.obj = posInfo;
                message.what = MESSAGE_CODE;
                mHandler.sendMessage(message);
                // bind线程池中

            }
        }

        @Override
        public int printData(List<PrinterParams> list) throws RemoteException {
            if (list != null) {
                for (PrinterParams params : list) {

                }
            }
            return 0;
        }

        @Override
        public void printOther(List<PrinterParams> list, PosPrinterCallback bc) throws RemoteException {
            // 打印其他
            if (bc != null) {
                if (list != null) {
                    PrinterParams params = list.get(0);
                    bc.onPrintSuccess(1000, params.getText());
                } else {
                    bc.onPrintFailed("回调-打印失败~~~");
                }
            }
        }
    }

}
