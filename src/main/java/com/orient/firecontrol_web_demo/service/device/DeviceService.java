package com.orient.firecontrol_web_demo.service.device;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.config.page.PageUtils;
import com.orient.firecontrol_web_demo.dao.device.Device01Dao;
import com.orient.firecontrol_web_demo.dao.device.Device02Dao;
import com.orient.firecontrol_web_demo.dao.device.Device03Dao;
import com.orient.firecontrol_web_demo.dao.device.DeviceInfoDao;
import com.orient.firecontrol_web_demo.dao.organization.BuildingDao;
import com.orient.firecontrol_web_demo.dao.organization.FloorDao;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.device.Device01;
import com.orient.firecontrol_web_demo.model.device.Device02;
import com.orient.firecontrol_web_demo.model.device.Device03;
import com.orient.firecontrol_web_demo.model.device.DeviceInfo;
import com.orient.firecontrol_web_demo.model.organization.BuildingInfo;
import com.orient.firecontrol_web_demo.model.organization.FloorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/28 11:43
 * @func
 */
@Service
public class DeviceService {
    @Autowired
    private DeviceInfoDao deviceInfoDao;
    @Autowired
    private BuildingDao buildingDao;
    @Autowired
    private FloorDao floorDao;
    @Autowired
    private Device01Dao device01Dao;
    @Autowired
    private Device02Dao device02Dao;
    @Autowired
    private Device03Dao device03Dao;





    /**
     * 新增设备 并绑定建筑及楼层
     * @param deviceInfo
     * @return
     */
    @Transactional
    public ResultBean addDevice(DeviceInfo deviceInfo){
        String buildCode = deviceInfo.getBuildCode();
        String deviceCode = deviceInfo.getDeviceCode();
        //其中buildCode 是前端可以根据上下文获得的 所以一般不会出错  并且是固定的
        if (!deviceCode.substring(0, 10).equals(buildCode)){
            throw new CustomException("新增失败,设备编号前10位和建筑物编号不一致");
        }
        DeviceInfo one = deviceInfoDao.findOne(deviceCode);
        if (one!=null){
            throw new CustomException( "新增设备失败,该设备已存在(deviceCode)");
        }
        BuildingInfo byBuildCode = buildingDao.findByBuildCode(buildCode);
        if (byBuildCode==null){
            throw new CustomException("新增设备失败,传入建筑物编码有误");
        }
        //设备类型 deviceType也不需要进行判定  用什么存什么就好
        if (deviceInfo.getDeviceType().equals("01")){
            deviceInfo.setTypeName("主机");
        }else if (deviceInfo.getDeviceType().equals("02")){
            deviceInfo.setTypeName("单相子机");
        }else if (deviceInfo.getDeviceType().equals("03")){
            deviceInfo.setTypeName("三相子机");
        }else {
            throw new CustomException("设备类型输入错误(01,02,03)");
        }
        Integer floorCode = deviceInfo.getFloorCode();
        FloorInfo floorInfo = floorDao.floorIsRight(deviceInfo.getBuildCode(), floorCode);
        if (floorInfo==null){
            throw new CustomException("该建筑物下不能存在"+floorCode+"楼");
        }
        int i = deviceInfoDao.addDevice(deviceInfo);
        if (i<=0){
            throw new CustomException("新增失败");
        }
        return new ResultBean(200, "新增成功", null);
    }

    /**
     * 根据建筑物id 获得建筑下的设备列表
     * @param buildingId
     * @return
     */
    public PageInfo<DeviceInfo> findByBuildId(PageUtils pageUtils, Integer buildingId){
        if (pageUtils.getPage()==null || pageUtils.getRows()==null){
            pageUtils.setPage(1);
            pageUtils.setRows(10);
        }
        BuildingInfo byId = buildingDao.findById(buildingId);
        if (byId==null){
            throw new CustomException("查询失败(输入的建筑物id不存在)");
        }
        String buildCode = byId.getBuildCode();
        PageHelper.startPage(pageUtils.getPage(), pageUtils.getRows());
        List<DeviceInfo> byBuildCode = deviceInfoDao.findByBuildCode(buildCode);
        PageInfo<DeviceInfo> pageInfo = new PageInfo<>(byBuildCode);
        return pageInfo;
    }


    /**
     * 查询某建筑物下某楼层下的设备信息
     * @param buildCode
     * @param floorCode
     * @return
     */
    public PageInfo<DeviceInfo> listByBuildCodeAndFloorCode(PageUtils pageUtils,String buildCode,Integer floorCode){
        if (pageUtils.getPage()==null || pageUtils.getRows()==null){
            pageUtils.setPage(1);
            pageUtils.setRows(10);
        }
        BuildingInfo byBuildCode = buildingDao.findByBuildCode(buildCode);
        if (byBuildCode==null){
            throw new CustomException("建筑物编号不存在");
        }
        FloorInfo floorInfo = floorDao.floorIsRight(buildCode, floorCode);
        if (floorInfo==null){
            throw new CustomException("该建筑物下暂无"+floorCode+"楼");
        }
        PageHelper.startPage(pageUtils.getPage(), pageUtils.getRows());
        List<DeviceInfo> byBuildCodeAndFloorCode = deviceInfoDao.findByBuildCodeAndFloorCode(buildCode, floorCode);
        if (byBuildCodeAndFloorCode.size()==0){
            throw new CustomException(floorCode+"楼无设备信息");
        }
        PageInfo<DeviceInfo> pageInfo = new PageInfo<>(byBuildCodeAndFloorCode);
        return pageInfo;

    }

    /**
     * 查询某个设备最新的一条监测数据
     * @param deviceCode    设备编号
     * @param deviceType    设备类型
     * 设备类型得传 因为每种设备检测的数据不一样  我数据库做了分类
     * 设备类型由前端  通过上下文获取
     * @return
     */
//    public ResultBean findNewMeasure(String deviceCode,String deviceType){
//        DeviceInfo one = deviceInfoDao.findOne(deviceCode);
//        if (one==null){
//            throw new CustomException("该设备编号不存在");
//        }
//        String deviceType1 = one.getDeviceType();
//        if (!deviceType1.equals(deviceType)){
//            throw new CustomException("设备类型不匹配");
//        }
//        if (deviceType.equals("01")){
//            List<Device01> device01s = device01Dao.listByDeviceCode(deviceCode);
//            if (device01s==null){
//                return new ResultBean(200, "该设备暂无检测数据", null);
//            }
//            //取该list的最后一条 最新的一条测试数据
//            return new ResultBean(200, "查询监测数据成功", device01s.get(device01s.size()-1));
//        }
//        if (deviceType.equals("02")){
//            List<Device02> device02s = device02Dao.listByDeviceCode(deviceCode);
//            if (device02s==null){
//                return new ResultBean(200, "该设备暂无检测数据", null);
//            }
//            return new ResultBean(200, "查询监测数据成功", device02s.get(device02s.size()-1));
//        }
//        if (deviceType.equals("03")){
//            List<Device03> device03s = device03Dao.listByDeviceCode(deviceCode);
//            if (device03s==null){
//                return new ResultBean(200, "该设备暂无检测数据", null);
//            }
//            return new ResultBean(200, "查询监测数据成功", device03s.get(device03s.size()-1));
//        }
//        return null;
//    }


    /**
     * 根据设备编号查询所有(历史)数据
     * 注意 这里根据id进行了倒序  所以第一条就是最新的监测数据
     * @param deviceCode    设备编号
     * @param deviceType    设备类型
     * @return
     */
    public PageInfo listAllMeasure(PageUtils pageUtils,String deviceCode,String deviceType){
        if (pageUtils.getPage()==null || pageUtils.getRows()==null){
            pageUtils.setPage(1);
            pageUtils.setRows(10);
        }
        DeviceInfo one = deviceInfoDao.findOne(deviceCode);
        if (one==null){
            throw new CustomException("该设备编号不存在");
        }
        String deviceType1 = one.getDeviceType();
        if (!deviceType1.equals(deviceType)){
            throw new CustomException("设备类型不匹配");
        }
        Map<String,Object> map = new HashMap();
        if (deviceType.equals("01")){
            PageHelper.startPage(pageUtils.getPage(), pageUtils.getRows());
            List<Device01> device01s = device01Dao.listByDeviceCode(deviceCode);
            PageInfo pageInfo = new PageInfo(device01s);
            return pageInfo;
        }
        if (deviceType.equals("02")){
            PageHelper.startPage(pageUtils.getPage(), pageUtils.getRows());
            List<Device02> device02s = device02Dao.listByDeviceCode(deviceCode);
            PageInfo pageInfo = new PageInfo(device02s);
            return pageInfo;
        }
        if (deviceType.equals("03")){
            PageHelper.startPage(pageUtils.getPage(), pageUtils.getRows());
            List<Device03> device03s = device03Dao.listByDeviceCode(deviceCode);
            PageInfo pageInfo = new PageInfo(device03s);
            return pageInfo;
        }
        return null;
    }

    /**
     * 查询某个设备最近7天的监测数据
     * @param deviceCode
     * @param deviceType
     * @return
     */
    public ResultBean findLast7DaysMeasure(String deviceCode,String deviceType){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String,Object> map = new HashMap<>();
        if (deviceType.equals("01")){//主控设备
            long l = System.currentTimeMillis();
            List<Device01> last7Days = device01Dao.findLast7Days(deviceCode);
            long l1 = System.currentTimeMillis();
            System.out.println("执行查询消耗的时间==="+String.valueOf(l1-l));
            List<String> voltageAList = new ArrayList<>();
            List<String> voltageBList = new ArrayList<>();
            List<String> voltageCList = new ArrayList<>();
            List<String> remainElecList = new ArrayList<>();
            List<String> boxTempList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            long l2 = System.currentTimeMillis();
            for (Device01 device01:
            last7Days) {
                voltageAList.add(device01.getVoltageA());
                voltageBList.add(device01.getVoltageB());
                voltageCList.add(device01.getVoltageC());
                remainElecList.add(device01.getRemainElec());
                boxTempList.add(device01.getBoxTemp());
                String format = simpleDateFormat.format(device01.getMeasureTime());
                timeList.add(format);
            }
            long l3 = System.currentTimeMillis();
            System.out.println("执行for循环消耗的时间==="+String.valueOf(l3-l2));
            map.put("voltageAList", voltageAList);
            map.put("voltageBList", voltageBList);
            map.put("voltageCList", voltageCList);
            map.put("remainElecList", remainElecList);
            map.put("boxTempList",boxTempList );
            map.put("timeList", timeList);
            return new ResultBean(200, "查询成功", map);
        }
        if (deviceType.equals("02")){//单相子机
            long l = System.currentTimeMillis();
            List<Device02> last7Days = device02Dao.findLast7Days(deviceCode);
            long l1 = System.currentTimeMillis();
            System.out.println("执行查询消耗的时间==="+String.valueOf(l1-l));
            List<String> branchElecList = new ArrayList<>();
            List<String> branchTempList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            long l2 = System.currentTimeMillis();
            for (Device02 device02:
            last7Days) {
                branchElecList.add(device02.getBranchElec());
                branchTempList.add(device02.getBranchTemp());
                String format = simpleDateFormat.format(device02.getMeasureTime());
                timeList.add(format);
            }
            long l3 = System.currentTimeMillis();
            System.out.println("执行for循环消耗的时间==="+String.valueOf(l3-l2));
            map.put("branchElecList", branchElecList);
            map.put("branchTempList", branchTempList);
            map.put("timeList", timeList);
            return new ResultBean(200, "查询成功", map);
        }
        if (deviceType.equals("03")){//三相子机
            long l = System.currentTimeMillis();
            List<Device03> last7Days = device03Dao.findLast7Days(deviceCode);
            long l1 = System.currentTimeMillis();
            System.out.println("执行查询消耗的时间==="+String.valueOf(l1-l));
            List<String> branchElecAList = new ArrayList<>();
            List<String> branchElecBList = new ArrayList<>();
            List<String> branchElecCList = new ArrayList<>();
            List<String> branchTempAList = new ArrayList<>();
            List<String> branchTempBList = new ArrayList<>();
            List<String> branchTempCList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            long l2 = System.currentTimeMillis();
            for (Device03 device03:
            last7Days) {
                branchElecAList.add(device03.getBranchElecA());
                branchElecBList.add(device03.getBranchElecB());
                branchElecCList.add(device03.getBranchElecC());
                branchTempAList.add(device03.getBranchTempA());
                branchTempBList.add(device03.getBranchTempB());
                branchTempCList.add(device03.getBranchTempC());
                String format = simpleDateFormat.format(device03.getMeasureTime());
                timeList.add(format);
            }
            long l3 = System.currentTimeMillis();
            System.out.println("执行for循环消耗的时间==="+String.valueOf(l3-l2));
            map.put("branchElecAList", branchElecAList);
            map.put("branchElecBList", branchElecBList);
            map.put("branchElecCList", branchElecCList);
            map.put("branchTempAList", branchTempAList);
            map.put("branchTempBList", branchTempBList);
            map.put("branchTempCList", branchTempCList);
            map.put("timeList", timeList);
            return new ResultBean(200, "查询成功", map);
        }
        return null;
    }

    /**
     * 用于紫东园区 生命科创园插入假数据
     * @param deviceCode
     * @return
     */
    public ResultBean insertFakeData(String deviceCode){
        DeviceInfo one = deviceInfoDao.findOne(deviceCode);
        if (one.getDeviceType().equals("01")){//主控设备
            String voltageA = String.valueOf(new Random().nextDouble()*(220.00-215.00)+215.00).substring(0, 6);
            String voltageB = String.valueOf(new Random().nextDouble()*(220.00-215.00)+215.00).substring(0, 6);
            String voltageC = String.valueOf(new Random().nextDouble()*(220.00-215.00)+215.00).substring(0, 6);
            String remainElec = String.valueOf(new Random().nextDouble()*(0.18-0.15)+0.15).substring(0, 4);
            String boxTemp = String.valueOf(new Random().nextDouble()*(15.8-15.4)+15.4).substring(0, 4);
            Date date = new Date();
            Device01 device01 = new Device01();
            device01.setVoltageA(voltageA).setVoltageB(voltageB).setVoltageC(voltageC).setRemainElec(remainElec)
                    .setBoxTemp(boxTemp).setMeasureTime(date).setDeviceCode(deviceCode);
            device01Dao.insertDevice01Measure(device01);
            return new ResultBean(200, "主控设备假数据插入成功", null);
        }
        if (one.getDeviceType().equals("02")){//单相子机
            String branchElec = String.valueOf(new Random().nextDouble()*(0.29-0.25)+0.25).substring(0, 4);
            String branchTemp = String.valueOf(new Random().nextDouble()*(15.8-15.4)+15.4).substring(0, 4);
            Date date = new Date();
            Device02 device02 = new Device02();
            device02.setDeviceCode(deviceCode).setBranchTemp(branchTemp).setBranchElec(branchElec).setMeasureTime(date);
            device02Dao.insertDevice02Measure(device02);
            return new ResultBean(200, "单相子机设备假数据插入成功", null);
        }
        if (one.getDeviceType().equals("03")){//三相子机
            String branchElecA = String.valueOf(new Random().nextDouble()*(0.38-0.37)+0.37).substring(0, 4);
            String branchElecB = String.valueOf(new Random().nextDouble()*(0.28-0.27)+0.27).substring(0, 4);
            String branchElecC = String.valueOf(new Random().nextDouble()*(0.30-0.29)+0.29).substring(0, 4);
            String branchTempA = String.valueOf(new Random().nextDouble()*(15.8-15.4)+15.4).substring(0, 4);
            String branchTempB = String.valueOf(new Random().nextDouble()*(15.8-15.4)+15.4).substring(0, 4);
            String branchTempC = String.valueOf(new Random().nextDouble()*(15.8-15.4)+15.4).substring(0, 4);
            Date date = new Date();
            Device03 device03 = new Device03();
            device03.setDeviceCode(deviceCode).setBranchElecA(branchElecA).setBranchElecB(branchElecB).setBranchElecC(branchElecC)
                    .setBranchTempA(branchTempA).setBranchTempB(branchTempB).setBranchTempC(branchTempC).setMeasureTime(date);
            device03Dao.insertDevice03Measure(device03);
            return new ResultBean(200, "三相子机设备假数据插入成功", null);
        }

        return null;
    }


    /**
     * 查询某设备 最近7天 某一项数据最近7天的监控数据
     * 用于前端画折线图
     * @param deviceCode
     * @param button
     * @return
     */
    public ResultBean drawLast7DaysGraph(String deviceCode,String button){
        DeviceInfo one = deviceInfoDao.findOne(deviceCode);
        if (one==null){
            return new ResultBean(201, "该设备不存在", null);
        }
        if (one.getDeviceType().equals("01")){ //主控设备
            List<Object> last7DaysMeasureTime = device01Dao.findLast7DaysMeasureTime(deviceCode);
            Map<String,Object> map = new HashMap<>();
            //需要展示5组数据 配电箱环境温度 剩余电流 A相电压 B相电压 C相电压
            if (button.equals("配电箱环境温度")){
                List<Object> last7DaysBoxTemp = device01Dao.findLast7DaysBoxTemp(deviceCode);
                map.put("dataList", last7DaysBoxTemp);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询主控设备最近7天配电箱环境温度监测数据成功", map);
            }
            if (button.equals("剩余电流")){
                List<Object> last7DaysRemainElec = device01Dao.findLast7DaysRemainElec(deviceCode);
                map.put("dataList", last7DaysRemainElec);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询主控设备最近7天剩余电流监测数据成功", map);
            }
            if (button.equals("A相电压")){
                List<Object> last7DaysVoltageA = device01Dao.findLast7DaysVoltageA(deviceCode);
                map.put("dataList", last7DaysVoltageA);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询主控设备最近7天A相电压监测数据成功", map);
            }
            if (button.equals("B相电压")){
                List<Object> last7DaysVoltageB = device01Dao.findLast7DaysVoltageB(deviceCode);
                map.put("dataList", last7DaysVoltageB);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询主控设备最近7天B相电压监测数据成功", map);
            }
            if (button.equals("C相电压")){
                List<Object> last7DaysVoltageC = device01Dao.findLast7DaysVoltageC(deviceCode);
                map.put("dataList", last7DaysVoltageC);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询主控设备最近7天C相电压监测数据成功", map);
            }
        }

        if(one.getDeviceType().equals("02")){ //单相子机
            //需要展示2组数据  支路电流   支路接头温度
            List<Object> last7DaysMeasureTime = device02Dao.findLast7DaysMeasureTime(deviceCode);
            Map<String,Object> map = new HashMap<>();
            if (button.equals("支路电流")){
                List<Object> last7DaysBranchElec = device02Dao.findLast7DaysBranchElec(deviceCode);
                map.put("dataList", last7DaysBranchElec);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询单相子机最近7天支路电流监测数据成功", map);
            }
            if (button.equals("支路接头温度")){
                List<Object> last7DaysBranchTemp = device02Dao.findLast7DaysBranchTemp(deviceCode);
                map.put("dataList", last7DaysBranchTemp);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询单相子机最近7天支路接头温度监测数据成功", map);
            }
        }

        if (one.getDeviceType().equals("03")){  //三相子机
            //需要展示6组数据  支路A相电流  支路B相电流 支路C相电流  支路A相接头温度  支路B相接头温度  支路C相接头温度
            List<Object> last7DaysMeasureTime = device03Dao.findLast7DaysMeasureTime(deviceCode);
            Map<String,Object> map = new HashMap<>();
            if (button.equals("支路A相电流")){
                List<Object> last7DaysBranchElecA = device03Dao.findLast7DaysBranchElecA(deviceCode);
                map.put("dataList", last7DaysBranchElecA);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询三相子机最近7天支路A相电流监测数据成功", map);
            }
            if (button.equals("支路B相电流")){
                List<Object> last7DaysBranchElecB = device03Dao.findLast7DaysBranchElecB(deviceCode);
                map.put("dataList", last7DaysBranchElecB);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询三相子机最近7天支路B相电流监测数据成功", map);
            }
            if (button.equals("支路C相电流")){
                List<Object> last7DaysBranchElecC = device03Dao.findLast7DaysBranchElecC(deviceCode);
                map.put("dataList", last7DaysBranchElecC);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询三相子机最近7天支路C相电流监测数据成功", map);
            }
            if (button.equals("支路A相接头温度")){
                List<Object> last7DaysBranchTempA = device03Dao.findLast7DaysBranchTempA(deviceCode);
                map.put("dataList", last7DaysBranchTempA);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询三相子机最近7天支路A相接头温度监测数据成功", map);
            }
            if (button.equals("支路B相接头温度")){
                List<Object> last7DaysBranchTempB = device03Dao.findLast7DaysBranchTempB(deviceCode);
                map.put("dataList", last7DaysBranchTempB);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询三相子机最近7天支路B相接头温度监测数据成功", map);
            }
            if (button.equals("支路C相接头温度")){
                List<Object> last7DaysBranchTempC = device03Dao.findLast7DaysBranchTempC(deviceCode);
                map.put("dataList", last7DaysBranchTempC);
                map.put("timeList", last7DaysMeasureTime);
                return new ResultBean(200, "查询三相子机最近7天支支路C相接头温度监测数据成功", map);
            }
        }
        return new ResultBean(201, "查询失败,请检查参数");
    }

}
