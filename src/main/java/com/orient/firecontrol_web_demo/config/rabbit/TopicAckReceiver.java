package com.orient.firecontrol_web_demo.config.rabbit;

import com.orient.firecontrol_web_demo.dao.alarm.AlarmDao;
import com.orient.firecontrol_web_demo.dao.alarm.AlarmEnum;
import com.orient.firecontrol_web_demo.dao.device.Device01Dao;
import com.orient.firecontrol_web_demo.dao.device.Device02Dao;
import com.orient.firecontrol_web_demo.dao.device.Device03Dao;
import com.orient.firecontrol_web_demo.dao.device.DeviceInfoDao;
import com.orient.firecontrol_web_demo.dao.organization.BuildingDao;
import com.orient.firecontrol_web_demo.dao.organization.OrganDao;
import com.orient.firecontrol_web_demo.model.alarm.AlarmInfo;
import com.orient.firecontrol_web_demo.model.device.Device01;
import com.orient.firecontrol_web_demo.model.device.Device02;
import com.orient.firecontrol_web_demo.model.device.Device03;
import com.orient.firecontrol_web_demo.model.device.DeviceInfo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/11 16:50
 * @func 消息的消费者 并对消息的接收与否做出确认 若消费者程序出现问题 消息会混滚回队列
 */
@Component
@Slf4j
public class TopicAckReceiver implements ChannelAwareMessageListener {


    @Autowired
    private TextMessageConverter textMessageConverter;
    @Autowired
    private DeviceInfoDao deviceInfoDao;
    @Autowired
    private Device01Dao device01Dao;
    @Autowired
    private Device02Dao device02Dao;
    @Autowired
    private Device03Dao device03Dao;
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private BuildingDao buildingDao;
    @Autowired
    private OrganDao organDao;




    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //deliveryTag该消息的index。
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String msg = textMessageConverter.fromMessage(message).toString();
            if (msg.substring(24, 26).equals("ff")){//心跳
                System.out.println("收到心跳指令:==="+msg);
                log.info("建筑物id==="+msg.substring(10, 20));
                log.info("一共发送了"+msg.substring(27, 28)+"个数据包过来");
                String data = msg.substring(28, msg.length() - 2);
                System.out.println("当前设备监控箱编号==="+msg.substring(10, 20)+data.substring(0, 4));
                for (int i = 0; i <data.length(); ) {
                    String datai = data.substring(i, i+8 );
                    System.out.println("当前设备id==="+msg.substring(10, 20)+datai.substring(0, 6));
                    System.out.println("当前设备状态==="+((datai.substring(6).equals("01"))?"在线":"离线"));
                    String statusFF = ((datai.substring(6).equals("01"))?"在线":"离线");
                    String deviceCode = msg.substring(10, 20)+datai.substring(0, 6);
                    DeviceInfo deviceInfo = deviceInfoDao.findOne(deviceCode);
                    String status = deviceInfo.getStatus();
                    if (!status.equals(statusFF)){//当心跳包发过来的设备状态和原先数据库存的设备状态不一致时  需要改变数据库中设备状态
                        //更改设备状态
                        deviceInfoDao.updateDeviceStatus(statusFF, deviceCode);
                        log.warn(deviceInfo.getBuildName()+"中的"+deviceInfo.getDeviceName()+"状态发生改变,请留意");
                    }else {
                        log.info("设备状态未发生改变,不调用服务器");
                    }

                    i=i+8;
                }

            }
            if (msg.substring(24, 26).equals("40")){//事件值上送
                System.out.println("收到40事件值指令:==="+msg);
                log.info("建筑物id==="+msg.substring(10, 20));
                log.info("一共发送了"+msg.substring(27, 28)+"个数据包过来");
                String data = msg.substring(28, msg.length() - 2);
                for (int i = 0; i < data.length();) {
                    String datai = data.substring(i, i+24 );
                    System.out.println("当前监控箱编号==="+msg.substring(10, 20)+datai.substring(0, 4));
                    System.out.println("当前的设备id==="+msg.substring(10, 20)+datai.substring(0, 6));
                    System.out.println("当前设备类型==="+datai.substring(6, 8));
                    System.out.println("告警时间===20"+datai.substring(12, 14)+"-"+datai.substring(14, 16)
                            +"-"+datai.substring(16, 18)
                            +" "+datai.substring(18, 20)+":"+datai.substring(20, 22)+":"+datai.substring(22, 24));
                    System.out.println("告警码==="+datai.substring(8, 10));
                    String deviceType = datai.substring(7, 8);
                    String alarmCode = datai.substring(8, 10);
                    if (alarmCode.substring(0, 1).equals("0")){//告警码第一位是0 直接去掉
                        alarmCode = alarmCode.substring(1);
                    }
                    AlarmEnum[] values = AlarmEnum.values();
                    String alarmDetail="";
                    String alarmGrade = "";
                    for (int j = 0; j <values.length ; j++) {
                        if (values[j].getDeviceType().equals(deviceType)&&values[j].getAlarmCode().equals(alarmCode)){
                            alarmDetail = values[j].getAlarmDetail();
                            alarmGrade = values[j].getAlarmGrade();
                        }
                    }
                    System.out.println("当前告警信息==="+alarmDetail);
                    AlarmInfo alarmInfo = new AlarmInfo();
                    alarmInfo.setAlarmDetail(alarmDetail).setAlarmTime("20"+datai.substring(12, 14)+"-"+datai.substring(14, 16)
                            +"-"+datai.substring(16, 18)
                            +" "+datai.substring(18, 20)+":"+datai.substring(20, 22)+":"+datai.substring(22, 24))
                            .setDeviceCode(msg.substring(10, 20)+datai.substring(0, 6))
                            .setAlarmGrade(alarmGrade+"级告警").setIsHandler("未处理");
                    String buildCode = (msg.substring(10, 20)+datai.substring(0, 6)).substring(0, 10);
                    Integer buildId;
                    int organId;
                    if ((buildingDao.findByBuildCode(buildCode))==null){ //即传过来建筑物编号 在数据库中不存在  这时候设置organId= 0
                        //这里要注意 别数据库里organId = 0  感觉不理解
                        organId = 0;
                    }else{
                        buildId = buildingDao.findByBuildCode(buildCode).getId();
                        organId = organDao.findByBuildId(buildId);
                    }
                    alarmInfo.setOrganId(organId);
                    alarmDao.insert(alarmInfo);
                    i=i+24;
                }



            }
            if (msg.substring(24, 26).equals("41")||msg.substring(24, 26).equals("42")){//41测量值
                //eb90eb9002 3201130001 320042040100000135541053d0540000f3000100010326001c001e00000000000000010003021c000000010005020900000003
                System.out.println("收到41测量值指令:==="+msg);
                log.info("建筑物id==="+msg.substring(10, 20));
                log.info("一共发送了"+msg.substring(27, 28)+"个数据包过来");
                //取出msg里面的计数字符  注意这里倒过来 高字节在前
                String lengthData = msg.substring(22, 24)+msg.substring(20, 22);
                //将16进制数据 转成int数据 注意这里要乘以2 因为1=2个字符
                Integer len = Integer.parseInt(lengthData, 16)*2;
                //取出拼接进去的时间戳
                String measureTime = msg.substring(20+len,20+len+19);
                System.out.println("测量时间:"+measureTime);
                //这里不要留时间 把时间拿掉 去我们需要的测量数据即可 时间前面拿到就行了
                String data = msg.substring(28, msg.length() - 21);
                for (int i = 0; i <data.length();) {
                    String substring = data.substring(i + 7, i + 8);
                    if (substring.equals("1")) {
                        System.out.println("***主机设备,有5项数据");
                        //设备编号
                        String deviceCode = msg.substring(10, 20)+data.substring(i, i+6);
                        //A相电压
                        String voltageA1 = data.substring(i + 10, i + 12);
                        String voltageA2 = data.substring(i + 8, i + 10);
                        String vA = voltageA1 + voltageA2;
                        double voltageA = Integer.parseInt(vA, 16);
                        System.out.println("A相电压为===" + voltageA / 100 + "V");
                        //B相电压
                        String voltageB1 = data.substring(i + 14, i + 16);
                        String voltageB2 = data.substring(i + 12, i + 14);
                        String vB = voltageB1 + voltageB2;
                        double voltageB = Integer.parseInt(vB, 16);
                        System.out.println("B相电压为===" + voltageB / 100 + "V");
                        //C相电压
                        String voltageC1 = data.substring(i + 18, i + 20);
                        String voltageC2 = data.substring(i + 16, i + 18);
                        String vC = voltageC1 + voltageC2;
                        double voltageC = Integer.parseInt(vC, 16);
                        System.out.println("C相电压为===" + voltageC / 100 + "V");
                        //剩余电流
                        String remainElec1 = data.substring(i + 22, i + 24);
                        String remainElec2 = data.substring(i + 20, i + 22);
                        String E = remainElec1 + remainElec2;
                        double remainElec = Integer.parseInt(E, 16);
                        System.out.println("剩余电流为===" + remainElec / 10 + "mA");
                        //配电箱温度
                        String boxTemp1 = data.substring(i + 26, i + 28);
                        String boxTemp2 = data.substring(i + 24, i + 26);
                        String T = boxTemp1 + boxTemp2;
                        double boxTemp = Integer.parseInt(T, 16);
                        System.out.println("配电箱环境温度为===" + boxTemp / 10 + "℃");
                        Device01 device01 = new Device01();
                        device01.setVoltageA(voltageA / 100 + "V").setVoltageB(voltageB / 100 + "V").setVoltageC(voltageC / 100 + "V")
                                .setRemainElec(remainElec / 10 + "mA").setBoxTemp(boxTemp / 10 + "℃").setDeviceCode(deviceCode)
                                .setMeasureTime(measureTime);
                        device01Dao.insertDevice01Measure(device01);
                        i = i + 4 * 7;
                    }
                    if (substring.equals("2")) {
                        System.out.println("***单项子机设备,有2项数据");
                        //设备编号
                        String deviceCode = msg.substring(10, 20)+data.substring(i, i+6);
                        //支路电流
                        String branchElec1 = data.substring(i + 10, i + 12);
                        String branchElec2 = data.substring(i + 8, i + 10);
                        String E = branchElec1 + branchElec2;
                        double branchElec = Integer.parseInt(E, 16);
                        System.out.println("支路电流===" + branchElec / 100 + "mA");
                        //支路接头温度
                        String branchTemp1 = data.substring(i + 14, i + 16);
                        String branchTemp2 = data.substring(i + 12, i + 14);
                        String T = branchTemp1 + branchTemp2;
                        double branchTemp = Integer.parseInt(T, 16);
                        System.out.println("支路接头温度===" + branchTemp / 10 + "℃");
                        Device02 device02 = new Device02();
                        device02.setBranchElec(branchElec / 100 + "mA").setBranchTemp(branchTemp / 10 + "℃").setDeviceCode(deviceCode)
                                .setMeasureTime(measureTime);
                        device02Dao.insertDevice02Measure(device02);
                        i = i + 4 * 4;
                    }
                    if (substring.equals("3")) {
                        System.out.println("***三相子机设备,有6项数据");
                        //设备编号
                        String deviceCode = msg.substring(10, 20)+data.substring(i, i+6);
                        //支路 A 相电流
                        String branchElecA1 = data.substring(i + 10, i + 12);
                        String branchElecA2 = data.substring(i + 8, i + 10);
                        String AE = branchElecA1 + branchElecA2;
                        double branchElecA = Integer.parseInt(AE, 16);
                        System.out.println("支路A相电流===" + branchElecA / 100 + "mA");
                        //支路 B 相电流
                        String branchElecB1 = data.substring(i + 14, i + 16);
                        String branchElecB2 = data.substring(i + 12, i + 14);
                        String BE = branchElecB1 + branchElecB2;
                        double branchElecB = Integer.parseInt(BE, 16);
                        System.out.println("支路B相电流===" + branchElecB / 100 + "mA");
                        //支路 C 相电流
                        String branchElecC1 = data.substring(i + 18, i + 20);
                        String branchElecC2 = data.substring(i + 16, i + 18);
                        String CE = branchElecC1 + branchElecC2;
                        double branchElecC = Integer.parseInt(CE, 16);
                        System.out.println("支路C相电流===" + branchElecC / 100 + "mA");
                        //支路 A 相接头温度
                        String branchTempA1 = data.substring(i + 22, i + 24);
                        String branchTempA2 = data.substring(i + 20, i + 22);
                        String AT = branchTempA1 + branchTempA2;
                        double branchTempA = Integer.parseInt(AT, 16);
                        System.out.println("支路 A 相接头温度===" + branchTempA / 10 + "℃");
                        //支路 B 相接头温度
                        String branchTempB1 = data.substring(i + 26, i + 28);
                        String branchTempB2 = data.substring(i + 24, i + 26);
                        String BT = branchTempB1 + branchTempB2;
                        double branchTempB = Integer.parseInt(BT, 16);
                        System.out.println("支路 B 相接头温度===" + branchTempB / 10 + "℃");
                        //支路 C 相接头温度
                        String branchTempC1 = data.substring(i + 26, i + 28);
                        String branchTempC2 = data.substring(i + 24, i + 26);
                        String CT = branchTempC1 + branchTempC2;
                        double branchTempC = Integer.parseInt(CT, 16);
                        System.out.println("支路 C 相接头温度===" + branchTempC / 10 + "℃");
                        Device03 device03 = new Device03();
                        device03.setBranchElecA(branchElecA / 100 + "mA").setBranchElecB(branchElecB / 100 + "mA")
                                .setBranchElecC(branchElecC / 100 + "mA").setBranchTempA(branchTempA / 10 + "℃")
                                .setBranchTempB(branchTempB / 10 + "℃").setBranchTempC(branchTempC / 10 + "℃")
                                .setDeviceCode(deviceCode).setMeasureTime(measureTime);
                        device03Dao.insertDevice03Measure(device03);
                        i = i + 4 * 8;
                    }
                }
            }
            if (msg.substring(24, 26).equals("51")){//51控制结果上传
                //3201110100010002
                //eb90eb90023201110100 0800 51 01 0100 0201 03
                System.out.println("收到51控制结果指令:==="+msg);
                log.info("建筑物id==="+msg.substring(10, 20));
                log.info("一共发送了"+msg.substring(27, 28)+"个数据包过来");
                String data = msg.substring(28, msg.length() - 2);
                for (int i = 0; i < data.length();) {
                    String datai = data.substring(i, i+8 );
                    System.out.println("当前设备id==="+msg.substring(10, 20)+datai.substring(0, 6));
                    String result = datai.substring(6, 8);
                    if (result.equals("01")){
                        System.out.println("断开支路成功");
                        alarmDao.updateStatus("已处理", msg.substring(10, 20)+datai.substring(0, 6));
                    }
                    if (result.equals("81")){
                        System.out.println("断开支路失败");
                        alarmDao.updateStatus("处理失败", msg.substring(10, 20)+datai.substring(0, 6));
                    }
                    if (result.equals("02")){
                        System.out.println("支路合上成功");
                        alarmDao.updateStatus("已处理", msg.substring(10, 20)+datai.substring(0, 6));
                    }
                    if (result.equals("82")){
                        System.out.println("支路合上失败");
                        alarmDao.updateStatus("处理失败", msg.substring(10, 20)+datai.substring(0, 6));
                    }
                    i = i+8;
                }
            }
            if (msg.substring(24, 26).equals("60")){//对时指令
                System.out.println("收到60对时指令:==="+msg);
            }

            channel.basicAck(deliveryTag, true);//是否批量. true：将一次性拒绝所有小于deliveryTag的消息。
        } catch (Exception e) {
            channel.basicReject(deliveryTag, true);//为true会重新放回队列
            e.printStackTrace();
        }
    }
}
