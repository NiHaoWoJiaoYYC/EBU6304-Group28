/**
 * @version 1.0
 * @author Yang Yuchen
 * Function: Test CSV
 */

package Import_csv;

import javax.swing.*;
import java.awt.*;

/**
 * 导入 CSV 功能的“手动验收”演示程序
 * 运行后会展示 ImportCSVPanel，方便手动验证各个交互步骤。
 */
public class ImportCSVManualTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ImportCSVPanel Manual Acceptance Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // 实例化待测试面板
            ImportCSVPanel panel = new ImportCSVPanel();
            frame.add(panel, BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // 手动验收提示：
            // 1) 点击 Submit，无文件提示警告
            // 2) 点击 Upload CSV File，选择一个包含表头的 test.csv
            //    - 左侧列表应显示所有列名
            // 3) 不选列点击 Submit，应提示至少选择一列
            // 4) 选中部分列后 Submit，应弹出“Import successful”且行数正确
            // 5) 在调试器中或之后的逻辑里，通过 panel.getDataList() 和 panel.getTableModel()
            //    获取原始数据和表格模型，验证其内容
        });
    }
}
