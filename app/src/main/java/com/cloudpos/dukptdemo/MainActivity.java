package com.cloudpos.dukptdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cloudpos.AlgorithmConstants;
import com.cloudpos.DeviceException;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.pinpad.KeyInfo;
import com.cloudpos.pinpad.PINPadDevice;
import com.cloudpos.pinpad.PINPadOperationResult;
import com.cloudpos.pinpad.extend.PINPadExtendDevice;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvLog;
    private RadioGroup rioGr;
    private Button btnOpen, btnClose, btnCalcMac, btnCalcPin, btnEntryData;
    private PINPadExtendDevice device = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLog = (TextView) findViewById(R.id.tvLog);
        tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        rioGr = (RadioGroup) findViewById(R.id.rio_gr);
        btnOpen = (Button) findViewById(R.id.open);
        btnClose = (Button) findViewById(R.id.close);
        btnCalcMac = (Button) findViewById(R.id.calcMac);
        btnCalcPin = (Button) findViewById(R.id.calcPin);
        btnEntryData = (Button) findViewById(R.id.entryData);
        btnOpen.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnCalcMac.setOnClickListener(this);
        btnCalcPin.setOnClickListener(this);
        btnEntryData.setOnClickListener(this);

        if (device == null) {
            device = (PINPadExtendDevice) POSTerminal.getInstance(this)
                    .getDevice("cloudpos.device.pinpad");
        }
    }

    public void open() {
        try {
            device.open();
            LogHelper.infoAppendMsgForSuccess("open success", tvLog);
        } catch (DeviceException e) {
            e.printStackTrace();
            LogHelper.infoAppendMsgForFailed("open fail", tvLog);
        }
    }

    public void close() {
        try {
            device.close();
            LogHelper.infoAppendMsgForSuccess("close success", tvLog);
        } catch (DeviceException e) {
            e.printStackTrace();
            LogHelper.infoAppendMsgForFailed("close fail", tvLog);
        }
    }

    @Override
    public void onClick(View v) {
        int dukptMode = rioGr.getCheckedRadioButtonId();
        switch (v.getId()) {
            case R.id.calcMac:
                calculateMAC(dukptMode);
                break;
            case R.id.calcPin:
                waitForPinBlock(dukptMode);
                break;
            case R.id.entryData:
                encryptData(dukptMode);
                break;
            case R.id.open:
                open();
                break;
            case R.id.close:
                close();
                break;
        }
    }

    public void encryptData(int dukptMode) {
        KeyInfo keyInfo = null;
        if (dukptMode == R.id.rio_2004) {
            keyInfo = new KeyInfo(PINPadDevice.KEY_TYPE_TDUKPT, 0, AlgorithmConstants.ALG_3DES);
        } else if (dukptMode == R.id.rio_2009) {
            keyInfo = new KeyInfo(PINPadDevice.KEY_TYPE_TDUKPT_2009, 0, AlgorithmConstants.ALG_3DES);
        }

        try {
            byte[] plain = new byte[]{
                    0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38
            };
            byte[] cipher = device.encryptData(keyInfo, plain);
            LogHelper.infoAppendMsgForSuccess(" cipher data = " + StringUtility.byteArray2String(cipher), tvLog);
        } catch (DeviceException e) {
            e.printStackTrace();
            LogHelper.infoAppendMsgForFailed("encryptData fail," + e.getMessage(), tvLog);
        }
    }

    private byte[] dukptMAC = {0x34, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x39, 0x44, 0x39, 0x38, 0x37, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public void calculateMAC(int dukptMode) {
        KeyInfo keyInfo = null;
        if (dukptMode == R.id.rio_2004) {
            keyInfo = new KeyInfo(PINPadDevice.KEY_TYPE_TDUKPT, 0, AlgorithmConstants.ALG_3DES);
        } else if (dukptMode == R.id.rio_2009) {
            keyInfo = new KeyInfo(PINPadDevice.KEY_TYPE_TDUKPT_2009, 0, AlgorithmConstants.ALG_3DES);
        }
        try {
            byte[] mac = device.calculateMac(keyInfo, AlgorithmConstants.ALG_MAC_METHOD_X99, dukptMAC);
            LogHelper.infoAppendMsgForSuccess("mac data = " + StringUtility.byteArray2String(mac), tvLog);
        } catch (DeviceException e) {
            e.printStackTrace();
            LogHelper.infoAppendMsgForFailed("calculateMAC fail," + e.getMessage(), tvLog);
        }
    }

    private String dukptCardNumber = "4012345678909";

    public void waitForPinBlock(int dukptMode) {
        KeyInfo keyInfo = null;
        if (dukptMode == R.id.rio_2004) {
            keyInfo = new KeyInfo(PINPadDevice.KEY_TYPE_TDUKPT, 0, AlgorithmConstants.ALG_3DES);
        } else if (dukptMode == R.id.rio_2009) {
            keyInfo = new KeyInfo(PINPadDevice.KEY_TYPE_TDUKPT_2009,  0, AlgorithmConstants.ALG_3DES);
        }
        try {
            OperationResult operationResult = device.waitForPinBlock(keyInfo, dukptCardNumber, false,
                    TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                byte[] pinBlock = ((PINPadOperationResult) operationResult).getEncryptedPINBlock();
                LogHelper.infoAppendMsgForSuccess("PINBlock = " + StringUtility.byteArray2String(pinBlock), tvLog);
            } else {
                LogHelper.infoAppendMsgForFailed("waitForPinBlock fail,", tvLog);
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            LogHelper.infoAppendMsgForFailed("waitForPinBlock fail," + e.getMessage(), tvLog);
        }
    }

}
