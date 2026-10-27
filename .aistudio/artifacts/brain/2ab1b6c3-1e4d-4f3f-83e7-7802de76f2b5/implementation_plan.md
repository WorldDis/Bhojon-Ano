# Implementation Plan: Role-Based Authentication & Access Control

Implement role-specific authentication and registration workflows for **Bhojon.food** matching your exact requirements:
- **Customer**: Dedicated Sign In and Sign Up using Mobile Number and Password.
- **Employee**: Login with Registered Mobile Number and Password; registration restricted strictly to the Admin Desk.
- **Admin**: A single dedicated administrator account with fixed credentials:
  - **User ID**: `Administrator`
  - **Password**: `Admin1234`
- **Role Switching**: Authentication check triggered whenever switching workspaces.

---

## 1. Domain Models & Data Layer (`Models.kt` & `BhojonRepository.kt`)

1. **Customer User Model (`CustomerUser`)**:
   - `id: Int`, `fullName: String`, `mobile: String`, `password: String`, `deliveryAddress: String`.
   - Pre-seed with existing customers (e.g. Debashis Mukherjee, Priya Banerjee) for instant testing.
   - Methods:
     - `registerCustomer(fullName, mobile, password, address): Result<CustomerUser>`: Validates mobile uniqueness and creates customer account.
     - `loginCustomer(mobile, password): CustomerUser?`: Verifies credentials.

2. **Employee Registration & Authentication**:
   - Add `loginPassword: String` to `Employee` model.
   - Seed existing team members (Chef Subhashish Ghosh, Tapan Mondal, Anima Roy) with initial password `pass123`.
   - Public employee registration is blocked.
   - In Admin Desk `addEmployee(...)`, add a required `loginPassword` input so the administrator sets the staff password during hiring/onboarding.
   - Method:
     - `loginEmployee(mobileOrCode, password): Employee?`: Verifies registered staff mobile and password.

3. **Administrator Authentication**:
   - Single fixed credentials enforced in repository:
     - User ID: `Administrator`
     - Password: `Admin1234`
   - Method:
     - `loginAdmin(userId, password): Boolean`: Strictly returns true only when `userId.trim() == "Administrator"` and `password == "Admin1234"`.

4. **Active Session State**:
   - State flows:
     - `currentCustomerUser: StateFlow<CustomerUser?>`
     - `currentEmployeeUser: StateFlow<Employee?>`
     - `isAdminAuthenticated: StateFlow<Boolean>`
   - `logoutCurrentRole()` to allow signing out or switching accounts.

---

## 2. Authentication UI Dialogs & Flow (`AuthDialogs.kt`)

Create a unified, styled Material Design 3 authentication modal system with the Bhojon.food palette (`#173F2B` deep forest green, `#FAF5E9` warm cream, `#D5A343` mustard gold):

1. **Customer Auth Modal (`CustomerAuthDialog`)**:
   - Tab 1: **Sign In** (Mobile Number, Password, "Sign In" button).
   - Tab 2: **Sign Up** (Full Name, Mobile Number, Delivery Address, Password, "Create Account" button).
   - Seamless validation with inline error messages (e.g. invalid password, unregistered number).

2. **Employee Login Modal (`EmployeeLoginDialog`)**:
   - Registered Mobile Number input (or Employee Code).
   - Password input.
   - Information notice: *"Staff registration is managed by Admin. Please contact Manager Biplab Sen for employee credentials."*
   - "Sign In to Shift" button.

3. **Admin Login Modal (`AdminLoginDialog`)**:
   - User ID input (requires `Administrator`).
   - Password input (requires `Admin1234`).
   - Information badge: *"Protected Manager Desk • Single Administrator Access"*.
   - "Authorize & Enter" button.

---

## 3. Role Switcher & App Navigation Integration (`CommonUi.kt` & `BhojonApp.kt`)

1. **Role Switcher Integration**:
   - When tapping the Role Switcher button in `BhojonTopBar`:
     - Clicking **Customer Table**: If customer is not authenticated, presents `CustomerAuthDialog`. Upon success, sets role to `CUSTOMER`.
     - Clicking **Kitchen Team**: Presents `EmployeeLoginDialog`. Once authenticated, sets role to `EMPLOYEE` and loads that specific employee's duties and attendance shift.
     - Clicking **Restaurant Desk**: Presents `AdminLoginDialog`. Validates `Administrator` / `Admin1234` before granting access to `ADMIN`.
2. **Sign Out Action**:
   - Add explicit "Sign Out" option in the top bar role dropdown and inside customer/employee/admin screens so the user can easily re-authenticate or switch users.
3. **Admin Desk Employee Hiring Form**:
   - Update the "Add Staff" form in `AdminDeskScreen.kt` to include the `Login Password` field, ensuring the administrator can register new staff with their login credentials directly.

---

## 4. Verification Plan

1. **Customer Auth Verification**:
   - Test Sign Up with new customer credentials; verify instant login and profile population.
   - Test Sign In with existing mobile `+91 98315 22001` and registered password.
2. **Employee Auth Verification**:
   - Test login with Chef Subhashish's mobile `+91 98301 22345` and password `pass123`.
   - Verify non-staff numbers cannot log in.
   - In Admin Desk, add a new employee with password, then switch to Kitchen Team and verify login works with the newly created employee.
3. **Admin Auth Verification**:
   - Enter `Administrator` and `Admin1234` -> verify successful entrance to Restaurant Desk.
   - Enter incorrect credentials (e.g. `admin`, `123456`) -> verify access is rejected with clear feedback.
4. **Build & Compilation**:
   - Run `compile_applet` to confirm a 100% clean compilation.
