package org.bupt.persosnalfinance.Back.Service;

import org.bupt.persosnalfinance.dto.PlanDTO;
import org.bupt.persosnalfinance.Util.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支出记录业务服务：提供数据管理接口，持久化到 JSON 文件，实现记忆性
 */
public class PlanService {
    // 内存缓存，启动时从持久化加载
    private List<PlanDTO> records;

    public PlanService() {
        // 从 DataStore 读取之前保存的记录
        this.records = new ArrayList<>(DataStore.loadPlans());
    }

    /**
     * 新增记录并持久化
     */
    public void addRecord(PlanDTO dto) {
        records.add(dto);
        DataStore.savePlans(records);
    }

    /**
     * 根据记录 ID 删除并持久化
     */
    public void removeRecord(int planId) {
        records.removeIf(p -> p.getId() == planId);
        DataStore.savePlans(records);
    }

    /**
     * 获取所有记录的副本
     */
    public List<PlanDTO> getAllRecords() {
        return new ArrayList<>(records);
    }

    /**
     * 按假期 ID 过滤并返回记录列表
     */
    public List<PlanDTO> getAllRecordsForHoliday(Integer holidayId) {
        return getAllRecords().stream()
                              .filter(p -> holidayId.equals(p.getHolidayId()))
                              .collect(Collectors.toList());
    }
}
