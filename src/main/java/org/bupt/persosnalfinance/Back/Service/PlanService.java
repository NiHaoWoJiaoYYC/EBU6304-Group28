package org.bupt.persosnalfinance.Back.Service;

import org.bupt.persosnalfinance.dto.PlanDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * 支出记录业务服务：提供数据管理接口
 */
public class PlanService {
    private final List<PlanDTO> records = new ArrayList<>();

    public void addRecord(PlanDTO dto) {
        records.add(dto);
    }

    public void removeRecord(int index) {
        if (index >= 0 && index < records.size()) {
            records.remove(index);
        }
    }

    public List<PlanDTO> getAllRecords() {
        return new ArrayList<>(records);
    }
}