package com.orient.firecontrol_web_demo.model.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/11/6 11:35
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class FloorDto extends FloorInfo {
    private Integer floorStatus;    //楼层状态
}
