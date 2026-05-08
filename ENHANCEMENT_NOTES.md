# SkillBuilders — Enhancement Notes

## What Was Enhanced

### 🎨 UI/UX — Deep Purple & Gold Design System
- **sb-global.css** — Complete design system: CSS variables, typography (Playfair Display + DM Sans), responsive header/footer, card system, buttons, form inputs, badges
- **sb-global.js** — Toast notifications, loading spinners, skeleton loaders, session error handler
- All pages rebuilt with consistent Deep Purple & Gold branding

### Pages Enhanced
| Page | Enhancements |
|---|---|
| `index.html` | Hero section, animated stat counters, marquee strip, features grid, gradient course cards, CTA banner |
| `user-login.html` | Split-panel auth layout with visual side, form validation |
| `user-signup.html` | Split-panel layout, benefit highlights |
| `user-page.html` | Dashboard grid with gradient cards, real enrollment counts |
| `user-profile.html` | Sidebar with stats, organized sections, photo upload |
| `user-index.html` | Logged-in feed, continue learning, explore, streak widget |
| `show-course-preview.html` | Dark hero, sticky purchase card, tabbed content, gradient card thumbnail |
| `instructor-page.html` | Full sidebar layout, analytics dashboard, bar chart, earnings progress bars |
| `admin-handle.html` | Admin sidebar navigation, stats overview, gradient pending course cards |
| `about-us.html` | Brand story, mission visual, values grid, contact section |

### 🖼️ Image Placeholder Replacement
All `<img>` thumbnails replaced with **CSS gradient cards** using `.sb-gradient-card` + color variants:
- `sb-gc-purple`, `sb-gc-gold`, `sb-gc-indigo`, `sb-gc-rose`, `sb-gc-teal`, `sb-gc-violet`, `sb-gc-amber`, `sb-gc-slate`
- Each shows a relevant emoji icon + zero broken image spaces

### ✨ New UX Features
- **Toast Notifications** — `sbToast(message, type)` — success/error/info popups with animations
- **Loading Spinner** — `sbShowLoading()` / `sbHideLoading()` overlay
- **Skeleton Screens** — `sb-skeleton` CSS class with shimmer animation for all async content
- **Hero Animated Banner** — Stat counters, marquee ticker on landing page
- **Page enter animations** — `sb-animate`, `sb-animate-d1..d4` CSS classes

### 🔒 Backend Enhancements
| File | Enhancement |
|---|---|
| `PasswordUtil.java` | SHA-256 + salt hashing; legacy plain-text fallback for migration |
| `InputValidator.java` | Email/name/password validation; HTML sanitization against XSS |
| `SessionManager.java` | Centralized session create/read/invalidate; 30-min timeout; JSON error on expired |
| `InstructorAnalyticsDAO.java` | Total students, total earnings, per-course revenue, view counts, enrollment trend |
| `FetchInstructorAnalytics.java` | New servlet: `POST /fetchinstructoranalytics` returns all analytics as JSON |
| `TrackCourseView.java` | New servlet: `POST /trackcourseview?courseid=X` increments view counter |
| `LogoutUser.java` | New servlet: `POST /logoutuser` invalidates session cleanly |
| `UserAuthenticationDAO.java` | Upgraded with PasswordUtil + InputValidator |
| `InstructorAuthenticationDAO.java` | Upgraded with PasswordUtil + InputValidator |

### 🗄️ SQL Schema (`sql/enhanced-schema-additions.sql`)
Run after `init.sql`:
- `courses.view_count INT DEFAULT 0`
- `courses.approved_at TIMESTAMP`
- `usercourses.enrolled_at TIMESTAMP` (for enrollment trend queries)
- `usercourses.progress INT`
- Extended user fields (gender, phone, grade, stream, country, city, DOB, profile)
- `certificates`, `transactions`, `testresult`, `userinterestedstream` tables (IF NOT EXISTS)
- Performance indexes on courses, usercourses, transactions

## Setup Instructions
1. Run `sql/init.sql` first (original schema)
2. Run `sql/enhanced-schema-additions.sql` (new columns + tables)
3. Build with Maven: `mvn clean package`
4. Deploy `target/skillbuilders.war` to Tomcat
5. The new analytics endpoint is available at `POST /fetchinstructoranalytics`
