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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            List<Device01> last7Days = device01Dao.findLast7Days(deviceCode);
            List<String> voltageAList = new ArrayList<>();
            List<String> voltageBList = new ArrayList<>();
            List<String> voltageCList = new ArrayList<>();
            List<String> remainElecList = new ArrayList<>();
            List<String> boxTempList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
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
            map.put("voltageAList", voltageAList);
            map.put("voltageBList", voltageBList);
            map.put("voltageCList", voltageCList);
            map.put("remainElecList", remainElecList);
            map.put("boxTempList",boxTempList );
            map.put("timeList", timeList);
            return new ResultBean(200, "查询成功", map);
        }
        if (deviceType.equals("02")){//单相子机
            List<Device02> last7Days = device02Dao.findLast7Days(deviceCode);
            List<String> branchElecList = new ArrayList<>();
            List<String> branchTempList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            for (Device02 device02:
            last7Days) {
                branchElecList.add(device02.getBranchElec());
                branchTempList.add(device02.getBranchTemp());
                String format = simpleDateFormat.format(device02.getMeasureTime());
                timeList.add(format);
            }
            map.put("branchElecList", branchElecList);
            map.put("branchTempList", branchTempList);
            map.put("timeList", timeList);
            return new ResultBean(200, "查询成功", map);
        }
        if (deviceType.equals("03")){//三相子机
            List<Device03> last7Days = device03Dao.findLast7Days(deviceCode);
            List<String> branchElecAList = new ArrayList<>();
            List<String> branchElecBList = new ArrayList<>();
            List<String> branchElecCList = new ArrayList<>();
            List<String> branchTempAList = new ArrayList<>();
            List<String> branchTempBList = new ArrayList<>();
            List<String> branchTempCList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
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

}
