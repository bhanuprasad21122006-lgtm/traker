# Simplify App Design

The user wants a simpler design, as the current one is perceived as having too many elements (cluttered). I will refactor the UI to be more minimalist, focused, and breathable.

## Proposed Changes

### [Component Name] UI Simplification

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/bhanu/StudioProjects/traker/app/src/main/java/com/example/ui/screens/DashboardScreen.kt)
*   **Unified Header:** Combine the greeting, date, and progress ring into a single minimalist header.
*   **Remove Clutter:** Remove the "Productivity Insight" card and the large "Weekly Activity" chart.
*   **Streamlined Sections:** Use simple text headers instead of complex card containers for categories.
*   **Prioritize Tasks:** Display the task list more prominently with more whitespace.

#### [MODIFY] [TaskCard.kt](file:///C:/Users/bhanu/StudioProjects/traker/app/src/main/java/com/example/ui/components/TaskCard.kt)
*   **Minimalist Design:** Remove the heavy background colors for badges. Use subtle text and simple icons.
*   **Clean Layout:** Align elements better to reduce visual noise.
*   **Simplistic Checkbox:** Use a cleaner, less "bubbly" checkbox design.

#### [MODIFY] [TasksScreen.kt](file:///C:/Users/bhanu/StudioProjects/traker/app/src/main/java/com/example/ui/screens/TasksScreen.kt)
*   **Combined Filters:** Use a single horizontal scroll for all filters or a more compact chip design.
*   **Lightweight Search:** Use a simpler search bar style.

#### [MODIFY] [TaskTrackerApp.kt](file:///C:/Users/bhanu/StudioProjects/traker/app/src/main/java/com/example/ui/TaskTrackerApp.kt)
*   **Cleaner Top Bar:** Simplify the top app bar to just show the essential information.

## Verification Plan

### Automated Tests
*   Run existing unit tests to ensure no logic was broken: `./gradlew :app:testDebugUnitTest`

### Manual Verification
*   Render Compose Previews for the updated screens to verify the new "simple" aesthetic.
*   Check for improved readability and reduced visual load.
