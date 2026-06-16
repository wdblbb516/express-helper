package com.example.stationservice.service;

import com.example.stationservice.dto.DormBuilding;
import com.example.stationservice.dto.Station;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StationService {

    private final List<Station> stations = new ArrayList<>();
    private final List<DormBuilding> dormBuildings = new ArrayList<>();

    public StationService() {
        // 初始化3个快递站点
        stations.add(new Station("菜鸟驿站-东门站", "东门商业街A101", "13800138001"));
        stations.add(new Station("顺丰速运-中心站", "校园中心广场B区", "13800138002"));
        stations.add(new Station("京东物流-西门站", "西门学生公寓楼下", "13800138003"));

        // 初始化1-26栋宿舍楼
        for (int i = 1; i <= 26; i++) {
            dormBuildings.add(new DormBuilding("学生公寓" + i + "栋", String.valueOf(i)));
        }
    }

    public List<Station> getAllStations() {
        return stations;
    }

    public List<DormBuilding> getAllDormBuildings() {
        return dormBuildings;
    }

    public Station getStationByName(String name) {
        return stations.stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public DormBuilding getDormByNumber(String number) {
        return dormBuildings.stream()
                .filter(d -> d.getNumber().equals(number))
                .findFirst()
                .orElse(null);
    }
}