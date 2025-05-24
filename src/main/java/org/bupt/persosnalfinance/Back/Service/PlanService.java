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

        // 为历史遗留的无 ID 记录分配唯一 ID
        int maxId = records.stream()
                           .filter(p -> p.getId() != null)
                           .mapToInt(PlanDTO::getId)
                           .max()
                           .orElse(0);
        for (PlanDTO p : records) {
            if (p.getId() == null) {
                p.setId(++maxId);
            }
        }
        // 立即持久化一次，保证 plans.json 中都有合法 ID
        DataStore.savePlans(records);
    }

    /**
     * 新增记录并持久化，新增时给 DTO 分配不重复的 ID
     */
    public void addRecord(PlanDTO dto) {
        // 计算一个比当前所有 ID 最大值都大的新 ID
        int newId = records.stream()
                           .mapToInt(p -> p.getId() != null ? p.getId() : 0)
                           .max()
                           .orElse(0) + 1;
        dto.setId(newId);
        records.add(dto);
        DataStore.savePlans(records);
    }

    /**
     * 根据记录 ID 删除并持久化
     */
    public void removeRecord(int planId) {
        records.removeIf(p -> planId == p.getId());
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
