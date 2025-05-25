# 🧾 EBU6304 - Group 28: Personal Finance Assistant

## 👤 Personal Contribution

**Name:** Yang Yuchen  
**QMUL ID:** 221166806  

### 📌 Main Contributions

- ✅ Implemented **core features** for user interaction and transaction management:
  - **Import CSV** — Load user transaction records into the system.
  - **Export CSV** — Save updated transactions back to a file.
  - **LLM Dialog Interface** — Enable intelligent Q&A via integrated Large Language Model (LLM).
    - **Frontend:** Implemented via `ObservationFrame.java`
    - **Backend:** Handled using `Transaction... .java` (For Record visualization)and `Chat... .java`(For LLM chat)
- 🏗️ Created and initialized the project repository structure for team collaboration.


**Name:** Jing Wenrui  
**QMUL ID:** 221167696  

### 📌 Main Contributions

- ✅In this project, I implemented the functions of **manual transaction entry** and **information management**. I designed and developed the **GUI** for users to input personal transaction information using **Java Swing**.
  - This includes **input fields** for transaction date, amount, type, object, and remarks, with proper **validation for data** (e.g., date format and non-negative amount).
- ✅I also implemented the **data persistence logic** by serializing and deserializing transaction records using **JSON format**.This ensures that all user inputs are saved locally and remain consistent across sessions. To support efficient data handling, I created the TransactionInformation data model, which maintains a static list of all transactions and provides methods for loading from and saving to a JSON file.
- ✅In addition, I developed the transaction **viewing interface**, allowing users to **browse, edit, and delete** existing records. The table view is **dynamically** updated based on user operations, and the data is sorted in chronological order. I implemented input validation and user confirmation dialogs during editing and deletion to ensure safe and accurate data manipulation.

**Name:** Dong Xuerui  
**QMUL ID:** 221167021  

### 📌 Main Contributions

- ✅Developed Homepage.java, which serves as the system's entry point, providing users the ability to:

  - Create a new empty account book.
  - Import transaction records via CSV and transition to the main dashboard.
- Implemented major interface logic in Dashboard.java, including:
  - UI layout and navigation buttons to core functions like CSV export, manual input, budget planning, transaction list, and etc. (functions themselves developed by others).
  - Implemented monthly income/expense summary display.
  - Created and integrated a pie chart visualization of monthly expenses by category.
  - Developed the refreshTable() mechanism to dynamically update the transaction list table.
- Created CombinedUIManager.java, a singleton controller to manage the main window instance and avoid duplication.
- Built FullBudgetPlannerManager.java, TransactionListManager.java, and other Manager classes to manage sub-windows’ display and lifecycle.
These components serve as UI launchers only; actual business logic is handled elsewhere.

**Name:** Liu XiaoKun  
**QMUL ID:** 221167342 

### 📌 Main Contributions

- ✅**Division of Labor**:
I was primarily responsible for developing the localization module. Specifically, I integrated the holiday feature with the budget management system to create a localized holiday budget tracking page.

Page UI Overview:

- **Top Section**: Holiday List

  - Displays statutory holidays fetched via API interfaces

  - Allows users to create custom personal holidays with customizable names and date ranges

  - Supports editing and deletion for all holiday entries

- **Bottom Section**: Split-Pane Layout
  - **[Left Panel]** Budget Planning

    - Enables creation of multiple holiday spending budgets

    - Supports adding/removing expense items (e.g., dining, transportation, accommodation)

    - Implements automatic persistence via localStorage to ensure long-term data retention

  - **[Right Panel]** Data Visualization

    - Generates dynamic pie charts reflecting budget allocations

    - Visually represents expense category proportions through color-coded segments

    - Supports filtering specific expense categories by clicking on chart legends
   

**Name:** Ye Tianyu  
**QMUL ID:** 221167342 

### 📌 Main Contributions

- AI-Powered Budget Generation
  - Implemented BudgetAIService to:
    - Build prompts from UserInfo
    - Call the AI API and parse its JSON response into a 12-category budget
    - Normalize allocations so they sum exactly to disposable income
- Real-Time Spending Analysis
  - Added getActualSpendingFromJson() to aggregate this month’s transactions by category
  - Rendered side-by-side comparison of actual vs. AI budget in SpendingTablePanel and JFreeChart
- Editable Monthly Budget UI
  - Created FullBudgetPlannerApp with “Generate Budget” and “Monthly Budget” buttons
  - Built CurrentMonthBudgetPanel to load, edit, and save current_budget.json, automatically re-scaling other categories on changes
- Clean Architecture & Persistence
  - Segregated layers:
   - Service (Back.Service)
   - DTO (dto)
   - UI (Front.AIBudgetPlanner)
Used Gson for JSON I/O and Swing for responsive desktop views
---

## 💻 Execution 
Run **src/main/java/org/bupt/persosnalfinance/Front/HomePage/HomePage.java** to start Frontend

### MVN Backend Execution

```bash
mvn spring-boot:run

