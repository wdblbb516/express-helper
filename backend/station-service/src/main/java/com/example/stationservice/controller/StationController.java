package com.example.stationservice.controller;

import com.example.stationservice.dto.ApiResponse;
import com.example.stationservice.dto.DormBuilding;
import com.example.stationservice.dto.Station;
import com.example.stationservice.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@CrossOrigin(origins = "*")
@Tag(name = "站点管理", description = "快递站点和宿舍楼栋信息查询接口")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    @Operation(summary = "获取所有站点", description = "获取系统中所有的快递站点信息")
    public ResponseEntity<ApiResponse<List<Station>>> getAllStations() {
        List<Station> stations = stationService.getAllStations();
        return ResponseEntity.ok(ApiResponse.success(stations));
    }

    @GetMapping("/dorms")
    @Operation(summary = "获取所有楼栋", description = "获取系统中所有的宿舍楼栋信息")
    public ResponseEntity<ApiResponse<List<DormBuilding>>> getAllDormBuildings() {
        List<DormBuilding> dorms = stationService.getAllDormBuildings();
        return ResponseEntity.ok(ApiResponse.success(dorms));
    }

    @GetMapping("/search")
    @Operation(summary = "根据名称查询站点", description = "通过站点名称查询站点详细信息")
    public ResponseEntity<ApiResponse<Station>> getStationByName(
            @Parameter(description = "站点名称", required = true)
            @RequestParam String name) {
        Station station = stationService.getStationByName(name);
        if (station == null) {
            ApiResponse<Station> response = new ApiResponse<>();
            response.setCode(404);
            response.setMessage("站点不存在");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(ApiResponse.success(station));
    }

    @GetMapping("/dorms/{number}")
    @Operation(summary = "根据楼栋号查询", description = "通过楼栋号查询宿舍楼详细信息")
    public ResponseEntity<ApiResponse<DormBuilding>> getDormByNumber(
            @Parameter(description = "楼栋号", required = true)
            @PathVariable String number) {
        DormBuilding dorm = stationService.getDormByNumber(number);
        if (dorm == null) {
            ApiResponse<DormBuilding> response = new ApiResponse<>();
            response.setCode(404);
            response.setMessage("宿舍楼不存在");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(ApiResponse.success(dorm));
    }
}