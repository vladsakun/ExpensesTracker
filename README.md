# Expenses tracker app 🫰

Gradle dependency graph:

![](/gradle/graphs/all_modules9.dot.png)

Engineering To-Do:

1) [ ] Playground
2) [x] Refactor modules
3) [x] api modules (data.api)
4) [ ] Support foldable devices
5) [ ] Export data via GDrive
6) [x] Baseline profiles
7) [x] Dynamic colors
8) [ ] Kotlin multiplatform migration (Ktor, Kotlin inject)
9) [ ] Migrate to Jetpack Compose Multiplatform

Features:

1) BUDGET!
2) Accounts grouping (drag and drop)
3) Add compound budget
4) Put transactions via voice input (ChatGPT). I say. 100$ restaurant, 200$ cafe and 30$ groceries. The app should add all the transactions
5) Shortruts for transactions (like in 1Money)
6) Expense Analytics: Advanced analytics features like trends, patterns, and forecasting help users gain deeper insights into their spending habits.
7) Interactive Reports: The app generates visually appealing reports with interactive graphs and charts, allowing users to analyze their spending patterns.
8) Scheduled transactions (payments, transfers, etc.)
9) Credits
10) Subscription Tracker: The app tracks recurring subscriptions, sends alerts before renewal, and provides insights on subscription spending.
11) Multi-Currency Support: Users can track expenses in different currencies and view exchange rates.
12) Expense Notes and Attachments: Users can add notes and attach files to expense entries for better documentation.
13) Goal Setting: The app allows users to set financial goals, tracks progress, and provides guidance on saving to achieve those goals.
14) Location-based Expense Tracking: Users can track expenses based on their current location, making it convenient for frequent travelers.
15) Receipt Scanning: Users can capture and scan receipts, which the app then extracts relevant details from for easy expense entry.
16) Shared expenses management (Users can create shared expenses groups) (Backend needed)
17) Intelligent Categorization: The app automatically categorizes expenses based on patterns and user input, saving time and effort.
18) Bill Reminders: The app sends reminders for upcoming bills, ensuring timely payments and avoiding late fees.
19) Budget Optimization: Using historical spending data, the app suggests optimized budgets for different expense categories, helping users save money. (
    ChatGPT)
20) Expense Comparison: Users can compare their expenses with others in their age group or location, providing valuable insights and benchmarks. (Backend
    needed)
21) Expense Tags and Labels: Users can tag expenses with customizable labels, enabling easy search and filtering.
22) Multiple Devices Sync: The app syncs seamlessly across devices, ensuring users have access to their expenses from anywhere.
23) Gamification Elements: The app incorporates gamification elements to make tracking expenses more engaging and rewarding.
24) Streaks
25) Security and Privacy: The app uses encryption, authentication, and offers biometric login options to ensure data privacy and security.
26) Offline mode
27) Voice input (ChatGPT)
28) Data import & export
29) Subcategories (extract to a separate category)
30) Wearable support (Add transactions)
31) Sign in with google, apple, facebook
32) Transactions map (Google maps locations)
33) Export data to excel or other formats
34) Pin code for app (biometrics)
35) Transactions period like 1Money instead of MoneyFlow
36) Last transactions block on Overview (work on Dashboard)
37) Put custom location PLACE!

!!!!
Handle case when deleting one of the transfer accounts. Convert the transaction into income/expense depending whether which account was deleted.

Articles to apply:
https://medium.com/codex/from-junior-to-senior-the-real-way-to-implement-clean-architecture-in-android-8514005e85e1

libs:

1) Jetpack compose (detailed) vs XML
2) Room vs SQLite
3) Material3
4) DataStore vs SharedPreferences
5) Hilt vs Dagger
6) Kotlin Coroutines vs RxJava
7) Compose destinations (less detailed) vs Compose navigation
8) WorkManager vs AlarmManager vs JobScheduler
9) Kotlinx serialization vs Gson vs Moshi
10) Retrofit vs Ktor