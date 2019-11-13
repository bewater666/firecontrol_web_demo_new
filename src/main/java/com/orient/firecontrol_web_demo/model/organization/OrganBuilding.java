package com.orient.firecontrol_web_demo.model.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/24 10:45
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganBuilding {
    private Integer id;
    private Integer organ_id;
    private Integer building_id;
}
