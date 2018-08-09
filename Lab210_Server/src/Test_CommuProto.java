import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test_CommuProto {
    private static Date day = null;
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int SinUnic = 0, MulUnic = 1, SinMulc = 2, MulMulc = 3;
    private static final String[] IVparameter
            = new String[]{"0x17", "0x99", "0x6d", "0x09", "0x3d", "0x28", "0xdd", "0xb3", "0xba", "0x69", "0x5a", "0x2e", "0x6f", "0x58", "0x56", "0x2e"};
    private static String Key = "";// lumi通信协议密码，于米家App中查看

    public static void main(String args[]) {
        // UDP通信测试
        // 1) 接收器循环接受组播
//        new Proto_Receiver(MulMulc,9898).Run();
        // 2) 接收器单次接受组播
//        new Proto_Receiver(SinMulc,9898).Run();
        // 3) 接收器单次接受单播（配合发送器）
        // i) 发送器单次发送组播
//        new Proto_Sender(SinMulc).Run();
//        new Proto_Receiver(SinUnic, 4321).Run();
        // ii) 发送器单次发送单播
//        new Proto_Sender(SinUnic, "{\"cmd\":\"get_id_list\"}").Run();
//        new Proto_Receiver(SinUnic, 9898).Run();

        // 写设备测试 暨AES-CBC-128加密测试
        Proto_AES_CBC_128 Aes = new Proto_AES_CBC_128(IVparameter, Key);
        Proto_Receiver RecvHeartB = new Proto_Receiver(MulMulc, 9898);
        Proto_Sender SendWrite = new Proto_Sender(SinUnic);
        String Gateway_sid = "";
        String WriteKEY = "";
        Proto_Receiver RecvWriteAck = new Proto_Receiver(SinUnic, 9898);

        RecvHeartB.Run();
        JSONObject HeartBMsgRecv_json = new JSONObject().fromObject(RecvHeartB.GetRecvMsg());
        try {
            day = new Date();
            System.out.println(df.format(day) + " gateway_sid: " + HeartBMsgRecv_json.getString("sid"));
            System.out.println(df.format(day) + " token: " + HeartBMsgRecv_json.getString("token"));
            Gateway_sid = HeartBMsgRecv_json.getString("token");
            WriteKEY = Aes.Encrypt2Hex(HeartBMsgRecv_json.getString("token"));
            day = new Date();
            System.out.println(df.format(day) + " Gateway_sid: " + Gateway_sid);
            System.out.println(df.format(day) + " 加密得到的KEY: " + WriteKEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SendWrite.SetSendMsg("{\"cmd\":\"write\",\"model\":\"gateway\",\"sid\":\"" + Gateway_sid + "\",\"short_id\":0,\"data\":\"{\"rgb\":4278255360}\" },\"key\":\"" + WriteKEY + "\"");
        RecvWriteAck.Run();
        SendWrite.Run();
    }
}