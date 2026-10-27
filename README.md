# BHOJON.FOOD (Android Application)

> *"Ghar Jaisa Swad — Freshly Cooked, Daily"*  
> Authentic Bengali food ordering, meal subscriptions, and restaurant workforce management.

## Overview

BHOJON.FOOD is a native Android application built with **Kotlin** and **Jetpack Compose (Material Design 3)**. It ports and enhances the Bhojon Food Hub restaurant platform, supporting three role-based workspaces in one cohesive experience:

1. **Customer Table (গ্রাহক টেবিল)**:
   - **Daily Rotating Menu**: Filter dishes across **Morning Tiffin** (9:00 AM, ₹25), **Lunch Thali** (1:30 PM, Regular ₹40, Special ₹50, Egg ₹70, Katla Fish ₹110), and **Dinner** (9:00 PM, ₹40–₹50).
   - **Item Details**: Bengali script names, ingredient highlights, dietary badges (Pure Veg vs Egg/Non-Veg), and serving times.
   - **Meal Basket & Checkout**: Quantity stepper, customer name, mobile, delivery address, special cooking notes, meal slot selector, and payment choices ("Pay on Delivery", "UPI Online").
   - **Weekly & Monthly Subscriptions**: Browse and subscribe to meal plans (Weekly 14 meals, Monthly 30 meals, Evening Dinner) with delivery tracking.
   - **Live Order Tracking**: 5-step visual tracking progress bar (Pending → Confirmed → Preparing → Out for Delivery → Delivered) with live kitchen timestamps and updates.

2. **Kitchen Team Desk (রান্নাঘর টিম / Employee)**:
   - **Staff Profile**: Displays employee ID (e.g. `EMP-001`), job title (Head Chef), mobile, and monthly salary.
   - **Daily Shift Attendance**: One-tap **Check In** and **Check Out** with real-time shift timestamps, working hours calculation, and shift notes.
   - **Assigned Kitchen Duties**: Interactive checklist for food prep and office tiffin dispatches.
   - **Kitchen Order Fulfillment**: Real-time kitchen order queue with one-tap status advancement (Accept → Start Cooking → Mark Ready for Delivery → Mark Handed Over).
   - **Leave Requests**: Submit casual, sick, or emergency leave requests with date pickers and view approval status.

3. **Restaurant Desk (রেস্তোরাঁ কন্ট্রোল / Admin & Manager)**:
   - **Live KPIs**: Today's Revenue, Total Orders, Pending Kitchen Items, Staff on Duty.
   - **Order Management**: Search and filter orders by status, view line items, customer details, and advance/cancel orders.
   - **Menu Management**: Add new dishes with Bengali titles, categories, prices, serving times, and toggle dish availability on/off or delete items.
   - **Staff & Workforce**: Employee directory, daily attendance log, and review/approve/reject pending leave applications.
   - **Subscription Management**: Monitor active customer meal plans and pause/resume subscriptions.
   - **Reports & Analytics**: Meal slot breakdown, revenue summaries, and spreadsheet CSV export.

## Architecture & Technology Stack

- **Platform**: Android SDK 36 (Android 15+)
- **Language**: Kotlin 2.2 with JVM 21
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Design Aesthetic**: Rustic Bengali culinary palette (Deep Forest Green `#28583D`, Terracotta `#B75F42`, Mustard Gold `#D5A34D`, and Warm Cream `#FAF7EE`)
- **State Management**: Kotlin Coroutines & `StateFlow` reactive data repository (`BhojonRepository`)
- **Custom Adaptive Icon**: Center thali graphic with layered Material You adaptive icon support
