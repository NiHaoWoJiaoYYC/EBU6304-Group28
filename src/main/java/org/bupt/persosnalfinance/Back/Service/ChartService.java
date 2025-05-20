package org.bupt.persosnalfinance.Back.Service;

import org.bupt.persosnalfinance.dto.PlanDTO;
import org.jfree.data.general.DefaultPieDataset; // JFreeChart 数据集，需要泛型参数
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图表服务：生成饼图数据集
 */
public class ChartService {
    /**
     * 根据 PlanDTO 列表按类别累加 amount，生成饼图数据
     */
    public DefaultPieDataset<String> createPieDataset(List<PlanDTO> records) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Double> sumMap = new HashMap<>();
        for (PlanDTO r : records) {
            sumMap.merge(r.getCategory(), r.getAmount(), Double::sum);
        }
        if (sumMap.isEmpty()) {
            dataset.setValue("No Data", 1.0);
        } else {
            sumMap.forEach((category, amount) -> dataset.setValue(category, amount));
        }
        return dataset;
    }
}