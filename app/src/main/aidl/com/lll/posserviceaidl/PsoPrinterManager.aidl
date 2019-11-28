// PsoPrinterManager.aidl
package com.lll.posserviceaidl;
import com.lll.posserviceaidl.bean.PosInfo;
import com.lll.posserviceaidl.bean.PrinterParams;
import com.lll.posserviceaidl.PosPrinterCallback;
// Declare any non-default types here with import statements

interface PsoPrinterManager {

      // 查询pos信息
        String queryPosInfo(String psoId);

        // 走纸
        void spitPaper(int distance);

        // 设置posInfo
        void setPostInfo(in PosInfo posInfo);

        // 打印数据
        int printData(in List<PrinterParams> list);

        /**
         * 打印其他
         *
         */
        void printOther(in List<PrinterParams> list,PosPrinterCallback bc);

}
