package org.bupt.persosnalfinance.Back.Alarm;

import org.bupt.persosnalfinance.dto.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @PostMapping("/check")
    @GetMapping("/data")
    public BudgetResponse checkOverspending(@RequestBody User user, @RequestParam double threshold) {
        // 从 User 对象中获取数据
        double[] lastQuarterAvg = user.getLastQuarterAvg();
        double[] thisQuarter = user.getThisQuarter();


        String[] categories = {"Food", "Housing/Rent", "Daily Necessities", "Transportation",
                "Entertainment", "Shopping", "Healthcare", "Education",
                "Childcare", "Gifts", "Savings", "Others"};

        // 创建 BudgetResponse 对象来存储结果
        BudgetResponse response = new BudgetResponse();

        // 遍历所有分类数据进行超支判断
        for (int i = 0; i < categories.length; i++) {
            double diff = thisQuarter[i] - lastQuarterAvg[i];
            double percentage = diff / lastQuarterAvg[i];

            if (percentage > threshold) {
                response.addAlert(categories[i] + " 超支了 " + (percentage * 100) + "%");
            } else {
                response.addAlert(categories[i] + " 正常");
            }
        }

        // 将花费数据一起返回
        response.setLastQuarterAvg(lastQuarterAvg);
        response.setThisQuarter(thisQuarter);

        return response;
    }
}