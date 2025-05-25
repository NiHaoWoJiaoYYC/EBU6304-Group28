package org.bupt.persosnalfinance.Back.Service;

import org.bupt.persosnalfinance.dto.PlanDTO;
import org.jfree.data.general.DefaultPieDataset; // JFreeChart dataset, requires generic parameters
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Charting services: generation of pie chart data sets
 */
public class ChartService {
    /**
     * 
     * Accumulate the amount by category from the PlanDTO list to generate pie chart data.
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