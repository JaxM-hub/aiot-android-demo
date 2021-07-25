package com.linkkit.aiot_android_demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;
/*
//import com.aliyun.alink.devicesdk.app.DemoApplication;
//import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttPublishRequest;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttSubscribeRequest;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSubscribeListener;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.alink.linksdk.tools.log.IDGenerater;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcHandle;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSubscribeListener;

 */



import java.math.BigInteger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ActivityMain extends AppCompatActivity {

    private final String TAG = "AiotMqtt";
    /* 设备三元组信息

    */

    final private String PRODUCTKEY = "a1fiNLLlJ5o";
    final private String DEVICENAME = "phone";
    final private String DEVICESECRET = "89bd860db8319ae8f0a2e9141920ac87";
    /*
?ceshi
???

 final private String PRODUCTKEY = "a1OOBsgpcAd";
    final private String DEVICENAME = "test1";
    final private String DEVICESECRET = "eeaffa3c7e7fa616d3864a7944bc7873";
     */
    /* 自动Topic, 用于上报消息 */
    final private String PUB_TOPIC = "/" + PRODUCTKEY + "/" + DEVICENAME + "/user/update";
    /* 自动Topic, 用于接受消息 */
    final private String SUB_TOPIC = "/" + PRODUCTKEY + "/" + DEVICENAME + "/user/get";
    /*王国海看这里↓*/
    final private String WGH_PUB_TOPIC = "/sys/" + PRODUCTKEY + "/" + DEVICENAME + "thing/event/property/post";


    /* 阿里云Mqtt服务器域名 */
    final String host = "tcp://" + PRODUCTKEY + ".iot-as-mqtt.cn-shanghai.aliyuncs.com:443";
    private String clientId;
    private String userName;
    private String passWord;

    MqttAndroidClient mqttAndroidClient;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().hide();
        /* 获取Mqtt建连信息clientId, username, password */
        AiotMqttOption aiotMqttOption = new AiotMqttOption().getMqttOption(PRODUCTKEY, DEVICENAME, DEVICESECRET);
        if (aiotMqttOption == null) {
            Log.e(TAG, "device info error");
        } else {
            clientId = aiotMqttOption.getClientId();
            userName = aiotMqttOption.getUsername();
            passWord = aiotMqttOption.getPassword();
        }

        /*
        创建几个TextView的实例 方便显示温湿度数据
         */
        TextView WenDu = findViewById(R.id.WenDu);
        TextView ShiDu = findViewById(R.id.ShiDu);
        TextView JiaQuanNongDu = findViewById(R.id.JiaQuanNongDu);
        TextView GuangZhaoQiangDu = findViewById(R.id.GuangZhaoQiangDu);
        TextView QWenDu = findViewById(R.id.QWenDu);
        TextView QShiDu = findViewById(R.id.QShiDu);
        TextView QJiaQuanNongDu = findViewById(R.id.QJiaQuanNongDu);
        TextView QGuangZhaoQiangDu = findViewById(R.id.QGuangZhaoQiangDu);
        TextView LED =findViewById(R.id.LED);
        TextView MenSuo =findViewById(R.id.MenSuo);
        TextView PWM =findViewById(R.id.PWM);
        TextView QLED =findViewById(R.id.QLED);
        TextView QMenSuo =findViewById(R.id.QMenSuo);
        TextView QPWM =findViewById(R.id.QPWM);
        /* 创建MqttConnectOptions对象并配置username和password */
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(userName);
        mqttConnectOptions.setPassword(passWord.toCharArray());


        /* 创建MqttAndroidClient对象, 并设置回调接口 */
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), host, clientId);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i(TAG, "topic: " + topic + ", msg: " + new String(message.getPayload()));
                String data = new String( message.getPayload());
                String MyCommand = message.getId() + "\", \"version\":\"1.0\"" ;
                String Wrong =getSubString(data,"{\"id\":\"",",\"params\":");
                if(!MyCommand.equals(Wrong)){
                    String wendu =getSubString(data,"\"temperature\":","\"humidity\":");  //温度
                    wendu = getSubString(wendu,"\"value\":","}");
                    if (!wendu.equals("wrong")){
                        WenDu.setText("温度:"+wendu);
                    }

                    //Toast.makeText(getApplicationContext(), wendu, Toast.LENGTH_SHORT).show();
                    String shidu =getSubString(data,"\"humidity\":","\"LED\":");  //湿度
                    shidu = getSubString(shidu,"\"value\":","}");
                    if(!shidu.equals("wrong")){
                        ShiDu.setText("湿度:"+shidu);
                    }

                    //Toast.makeText(getApplicationContext(), shidu, Toast.LENGTH_SHORT).show();
                    String qitinongdu =getSubString(data,"\"MQ\":","\"Light\":");  //温度
                    qitinongdu = getSubString(qitinongdu,"\"value\":","}");
                    if(!qitinongdu.equals("wrong")){
                        JiaQuanNongDu.setText("气体浓度:"+qitinongdu);
                    }

                    String guangzhaoqiangdu =getSubString(data,"\"Light\":","\"Curtain\":");  //温度
                    guangzhaoqiangdu = getSubString(guangzhaoqiangdu,"\"value\":","}");
                    if (!guangzhaoqiangdu.equals("wrong"))GuangZhaoQiangDu.setText("光照强度:"+guangzhaoqiangdu);

                    String led = getSubString(data,"\"LED\":","\"ID\":");
                    led = getSubString(led,"\"value\":","}");
                    if(!led.equals("wrong")){
                        if(led.equals("1")){
                            LED.setText("状态:OPEN");
                        }else{
                            LED.setText("状态:CLOSE");
                        }
                    }
                    String mensuo =getSubString(data,"\"Lock\":","\"BING\":");
                    mensuo = getSubString(mensuo,"\"value\":","}");
                    if (!mensuo.equals("wrong")){
                        if (mensuo.equals("0")){
                            MenSuo.setText("状态:CLOSE");
                        }else{
                            MenSuo.setText("状态:OPEN");
                        }
                    }

                    String pwm =getSubString(data,"\"PWM1\":","\"Lock\":");
                    pwm=getSubString(pwm,"\"value\":","}");
                    if(!pwm.equals("wrong")){
                        PWM.setText("状态:"+pwm);
                    }

/*
ELSE
 */

                    String Qwendu =getSubString(data,"\"tempearture5\":","\"humidity5\":");  //温度
                    Qwendu = getSubString(Qwendu,"\"value\":","}");
                    if(!Qwendu.equals("wrong")){
                        QWenDu.setText("温度:"+Qwendu);
                    }

                    //Toast.makeText(getApplicationContext(), wendu, Toast.LENGTH_SHORT).show();
                    String Qshidu =getSubString(data,"\"humidity5\":","}");  //温度
                    Qshidu = getSubString(Qshidu,"\"value\":","}");
                    if (!Qshidu.equals("wrong")){
                        QShiDu.setText("湿度:"+Qshidu);
                    }

                    //Toast.makeText(getApplicationContext(), shidu, Toast.LENGTH_SHORT).show();
                    String Qqitinongdu =getSubString(data,"\"MQ5\"","\"Light5\":");  //温度
                    Qqitinongdu = getSubString(Qqitinongdu,"\"value\":","}");
                    if (!Qqitinongdu.equals("wrong")) {
                        QJiaQuanNongDu.setText("气体浓度:"+Qqitinongdu);
                    }
                    String Qguangzhaoqiangdu =getSubString(data,"\"Light5\":","\"LED5\":");  //温度
                    Qguangzhaoqiangdu = getSubString(Qguangzhaoqiangdu,"\"value\":","}");
                    if (!Qguangzhaoqiangdu.equals("wrong"))QGuangZhaoQiangDu.setText("光照强度:"+Qguangzhaoqiangdu);
                    String Qled = getSubString(data,"\"LED5\":","\"PWM5\":");
                    Qled = getSubString(Qled,"\"value\":","}");
                    if(!Qled.equals("wrong")){
                        if(Qled.equals("1")) QLED.setText("状态:OPEN");
                        else QLED.setText("状态:CLOSE");
                    }


                    String Qpwm =getSubString(data,"\"PWM5\":","\"tempearture5\":");
                    Qpwm=getSubString(Qpwm,"\"value\":","}");
                    if (!Qpwm.equals("wrong")) QPWM.setText("状态:" + Qpwm);

                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i(TAG, "msg delivered");
            }
        });

        /*
        另一类数据包
        {
	"deviceType": "None",
	"iotId": "aFlX045q46X3LXfLf1MY000000",
	"requestId": "1626486093",
	"checkFailedData": {},
	"productKey": "a1OKNu80FX5",
	"gmtCreate": 1626486093742,
	"deviceName": "raspberry",
	"items": {
		"MQ5": {
			"time": 1626486093739,
			"value": 301.0
		},
		"Light5": {
			"time": 1626486093739,
			"value": 781.0
		},
		"LED5": {
			"time": 1626486093739,
			"value": 1
		},
		"PWM5": {
			"time": 1626486093739,
			"value": 51
		},
		"tempearture5": {
			"time": 1626486093739,
			"value": 271.0
		},
		"humidity5": {
			"time": 1626486093739,
			"value": 591.0
		}
	}
}
         */














        /*
王国海 JSON数据包

{
	"deviceType": "None",
	"iotId": "aFlX045q46X3LXfLf1MY000000",
	"requestId": "1626486083",
	"checkFailedData": {},
	"productKey": "a1OKNu80FX5",
	"gmtCreate": 1626486083292,
	"deviceName": "raspberry",
	"items": {
		"MQ": {
			"time": 1626486083289,
			"value": 99.0
		},
		"Light": {
			"time": 1626486083289,
			"value": 99.0
		},
		"Curtain": {
			"time": 1626486083289,
			"value": 0.0
		},
		"temperature": {
			"time": 1626486083289,
			"value": 2.0
		},
		"humidity": {
			"time": 1626486083289,
			"value": 6.0
		},
		"LED": {
			"time": 1626486083289,
			"value": 0
		},
		"ID": {
			"time": 1626486083289,
			"value": "00000000"
		},
		"PWM1": {
			"time": 1626486083289,
			"value": 5
		},
		"Lock": {
			"time": 1626486083289,
			"value": 1
		},
		"BING": {
			"time": 1626486083289,
			"value": 0
		}
	}
}
         */
        /* Mqtt建连 */
        try {
            mqttAndroidClient.connect(mqttConnectOptions,null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "connect succeed");
                    Toast.makeText(getApplicationContext(), "connect succeed", Toast.LENGTH_SHORT).show();
                    subscribeTopic(SUB_TOPIC);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "connect failed");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }


        //subscribeTopic(SUB_TOPIC);
        /*editText指令发送逻辑*/
        TextView editText = findViewById(R.id.editText);
        Button pubButton = findViewById(R.id.button1);
        /* 通过按键发布消息 */
        pubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editText.getText().toString();
                Toast.makeText(getApplicationContext(), inputText, Toast.LENGTH_SHORT).show();
                //publishMessage("hello word");
                publishMessage(inputText);
                //PublishJsonMessage();

            }
        });
        /*调用其他两个activity*/
        Button imageButton1 = findViewById(R.id.ImageButton1);
        Button imageButton2 = findViewById(R.id.ImageButton2);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this,Scene.class);
                startActivity(intent);
            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this,RecycleViewTest.class);
                startActivity(intent);

            }
        });
        /*
        开关灯逻辑
         */
        Button kaideng = findViewById(R.id.KaiDeng);
        Button guandeng = findViewById(R.id.GuanDeng);
        kaideng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "已开灯", Toast.LENGTH_SHORT).show();
                publishMessage("{\"LED\":1}");
            }
        });
        guandeng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "已开卧室灯", Toast.LENGTH_SHORT).show();
                publishMessage("{\"LED5\":0}");
            }
        });
        /*
        移动窗帘逻辑
         */
        Button right = findViewById(R.id.ButtonRight);
        Button left = findViewById(R.id.ButtonLeft);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "窗帘向右移动", Toast.LENGTH_SHORT).show();
                publishMessage("{\"Right\":2}");
                progressBar.incrementProgressBy(2);
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "窗帘向左移动", Toast.LENGTH_SHORT).show();
                publishMessage("{\"Left\":2}");
                progressBar.incrementProgressBy(-2);
            }
        });
        /*
        帮助和版本信息
         */
        Button help =findViewById(R.id.BangZhu);
        Button version = findViewById(R.id.BanBen);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
                builder.setTitle("命令提示");
                builder.setMessage("开灯:{\"LED\":1}\n关灯:{\"LED\":0}");
                /*
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                 */
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();




            }
        });
        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
                builder.setTitle("版本信息");
                builder.setMessage("开发者:缪智强 1807020215\n开发版本号:4.1.2");
                /*
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                 */
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();




            }
        });

    }



    /**
     * 订阅特定的主题
     * @param topic mqtt主题
     */
    public void subscribeTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "subscribed succeed");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "subscribed failed");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向默认的主题/user/update发布消息
     * @param payload 消息载荷
     */
    public void publishMessage(String payload) {
        try {
            if (mqttAndroidClient.isConnected() == false) {
                mqttAndroidClient.connect();
            }

            MqttMessage message = new MqttMessage();
            //payload = "{\"id\":\"" + message.getId() + "\", \"version\":\"1.0\"" + ",\"params\":{\"LED\":1}}";
            payload = "{\"id\":\"" + message.getId() + "\", \"version\":\"1.0\"" + ",\"params\":"+payload+ "}";
            message.setPayload(payload.getBytes());
            message.setQos(0);
            mqttAndroidClient.publish(PUB_TOPIC, message,null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "publish succeed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "publish failed!");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }


    /**
     * 王国海给的JASON格式解包代码
     */

    /**
     * 取两个文本之间的文本值
     * @param text 源文本 比如：欲取全文本为 12345
     * @param left 文本前面
     * @param right 后面文本
     * @return 返回 String
     */
    public static String getSubString(String text, String left, String right) {
        String result = "";
        int zLen;
        if (left == null || left.isEmpty()) {
            zLen = 0;
        } else {
            zLen = text.indexOf(left);
            if (zLen > -1) {
                zLen += left.length();
            } else {
                zLen = 0;
                return "wrong";
            }
        }
        int yLen = text.indexOf(right, zLen);
        if (yLen < 0 || right == null || right.isEmpty()) {
            yLen = text.length();
        }
        result = text.substring(zLen, yLen);
        return result;
    }


    /**
     * MQTT建连选项类，输入设备三元组productKey, deviceName和deviceSecret, 生成Mqtt建连参数clientId，username和password.
     */
    class AiotMqttOption {
        private String username = "";
        private String password = "";
        private String clientId = "";

        public String getUsername() { return this.username;}
        public String getPassword() { return this.password;}
        public String getClientId() { return this.clientId;}

        /**
         * 获取Mqtt建连选项对象
         * @param productKey 产品秘钥
         * @param deviceName 设备名称
         * @param deviceSecret 设备机密
         * @return AiotMqttOption对象或者NULL
         */
        public AiotMqttOption getMqttOption(String productKey, String deviceName, String deviceSecret) {
            if (productKey == null || deviceName == null || deviceSecret == null) {
                return null;
            }

            try {
                String timestamp = Long.toString(System.currentTimeMillis());

                // clientId
                this.clientId = productKey + "." + deviceName + "|timestamp=" + timestamp +
                        ",_v=paho-android-1.0.0,securemode=2,signmethod=hmacsha256|";

                // userName
                this.username = deviceName + "&" + productKey;

                // password
                String macSrc = "clientId" + productKey + "." + deviceName + "deviceName" +
                        deviceName + "productKey" + productKey + "timestamp" + timestamp;
                String algorithm = "HmacSHA256";
                Mac mac = Mac.getInstance(algorithm);
                SecretKeySpec secretKeySpec = new SecretKeySpec(deviceSecret.getBytes(), algorithm);
                mac.init(secretKeySpec);
                byte[] macRes = mac.doFinal(macSrc.getBytes());
                password = String.format("%064x", new BigInteger(1, macRes));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return this;
        }
    }

}
/*

JSON格式

*/

