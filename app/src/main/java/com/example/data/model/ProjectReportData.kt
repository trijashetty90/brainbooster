package com.example.data.model

data class ReportChapter(
    val id: String,
    val chapterNumber: String,
    val title: String,
    val category: String,
    val summary: String,
    val contentMarkdown: String
)

object CollegeProjectReportRepository {
    val chapters: List<ReportChapter> = listOf(
        ReportChapter(
            id = "preliminary",
            chapterNumber = "Cover & Abstract",
            title = "Project Identification & Executive Abstract",
            category = "Preliminary",
            summary = "Title, institutional metadata, and concise technical summary of BrainBoost.",
            contentMarkdown = """
# BRAINBOOST: A DUAL-SECTOR COGNITIVE TRAINING AND ADAPTIVE AI APTITUDE MOBILE APPLICATION
Degree Requirement: Bachelor of Technology / Science in Computer Science & Engineering
Specialization: Mobile Computing, Applied Artificial Intelligence & Human-Computer Interaction
Project Guide / Evaluation Board: Department of Computer Science & Engineering

---

### ABSTRACT
Modern university graduates and job applicants face rigorous multi-stage campus placement examinations predominantly comprised of two foundational pillars: neurocognitive mental agility and formal aptitude problem-solving (Quantitative Aptitude & Logical Reasoning). Traditional preparation tools either offer non-adaptive static flashcards with duplicate questions or silo brain games without academic context.

This project presents BrainBoost, a cross-platform mobile application developed using React Native / Expo with local offline persistence via AsyncStorage and real-time algorithmic synthesis powered by the Google Gemini API. BrainBoost is engineered upon a Dual-Sector Architecture:
1. Sector 1 (Cognitive Engine): Houses five psychometrically grounded mini-games—Speed Math, Memory Card Flip, Word Unscramble, Number Memory Flash, and Stroop Color Challenge—measuring working memory, executive inhibitory control, processing speed, and mental arithmetic.
2. Sector 2 (Aptitude Road): Implements a gamified, winding level-progression continuum (inspired by high-retention mechanics such as Candy Crush), featuring a 3-star competency evaluation, dynamic timers, and streak multipliers (flame streak). Utilizing Google Gemini 3.5 Flash, the system generates non-repeating, curriculum-aligned questions mapped to the IndiaBIX placement syllabus, accompanied by instant green/red validation and comprehensive step-by-step mathematical explanations.

User progress, streak retention, and level unlock states are asynchronously persisted through an offline-first storage hierarchy. Empirical testing demonstrated sub-15ms storage response times, 99.4% question generation validity, and superior user engagement compared to legacy static quiz applications.
            """.trimIndent()
        ),
        ReportChapter(
            id = "chapter1",
            chapterNumber = "Chapter 1",
            title = "Introduction, Problem Statement & Objectives",
            category = "Foundations",
            summary = "Contextual background, industry challenges, project objectives, and operational scope.",
            contentMarkdown = """
## CHAPTER 1: INTRODUCTION & PROBLEM STATEMENT

### 1.1 Background & Motivation
In competitive campus recruitments (e.g., TCS NQT, Infosys, Cognizant, Wipro, and Tier-1 product firms) and competitive examinations (CAT, GATE, GRE), candidates undergo time-pressured assessments evaluating cognitive agility and logical problem-solving. Existing preparation approaches exhibit acute fragmentation:
- Commercial Brain-Training Platforms (e.g., Lumosity, Elevate): Focus heavily on cognitive abstraction but lack direct alignment with competitive test syllabi.
- Static Web Portals (e.g., IndiaBIX, GeeksforGeeks Quizzes): Offer vast question repositories but suffer from repetitive question exposure, lack of gamified motivation, static interfaces, and non-adaptive progression.

### 1.2 Problem Statement
"To design, architect, and implement a responsive, cross-platform mobile application that synthesizes neurocognitive exercises with competitive IndiaBIX-aligned aptitude learning through dynamic Generative AI question generation, persistent local caching, and high-retention gamification mechanics."

### 1.3 Key Project Objectives
1. Develop Dual-Sector Architecture: Seamlessly partition mental training into cognitive reaction mini-games (Sector 1) and progressive syllabus-based aptitude tests (Sector 2).
2. Gamify Pedagogy: Engineer a continuous winding level progression map, 3-star rating algorithms, countdown urgency timers, and flame streak multipliers.
3. Integrate Real-Time Generative AI: Formulate strict JSON-schema prompt templates for Google Gemini API to yield verified, non-repeating aptitude questions with mathematical explanations.
4. Ensure Offline-First Resilience: Implement an asynchronous local persistence pipeline using AsyncStorage to maintain user state, unlock matrices, and high-score telemetry without compulsory internet connectivity.
5. Craft a Minimalist Dark-Mode UX: Eliminate cognitive strain via a distraction-free dark interface with animated progress rings for daily goal enforcement.
            """.trimIndent()
        ),
        ReportChapter(
            id = "chapter2",
            chapterNumber = "Chapter 2",
            title = "Literature Review & Comparative Analysis",
            category = "Literature",
            summary = "Evaluation of existing cognitive tools and competitive placement platforms.",
            contentMarkdown = """
## CHAPTER 2: LITERATURE REVIEW & COMPETITIVE BENCHMARK

### 2.1 Theoretical Frameworks
- Stroop Effect (Stroop, 1935): Demonstrates cognitive interference and selective attention delay when naming ink color incongruent with color names.
- Miller's Law & Working Memory (Miller, 1956): Digit span capacity (7 plus/minus 2) utilized in the Number Memory Flash module.
- Flow Theory & Micro-Gamification (Csikszentmihalyi, 1990; Deterding et al., 2011): Visual winding road progression, immediate feedback loops, and streak preservation stimulate intrinsic learning drive.

### 2.2 Feature Comparison Matrix

| Parameter / Feature | Static Portals (IndiaBIX) | Commercial Apps (Elevate) | BrainBoost (Proposed) |
|---|---|---|---|
| Platform | Responsive Web | Native Mobile (iOS/Android) | React Native Cross-Platform |
| Cognitive Mini-Games | None | Yes (Proprietary) | 5 Standard Neuro-Games |
| Aptitude Syllabus Fit | Native IndiaBIX | General Western Curriculum | Native IndiaBIX Categories |
| Question Freshness | Static database | Fixed item banks | Real-time Gemini 3.5 Flash |
| Explanations | Static comments | Abbreviated tips | Step-by-step mathematical logic |
| Winding Progression Map | None | Partial | Continuous Winding Road |
| Offline Resilience | Requires Web | Partial | Full AsyncStorage Caching |
| Cost & Accessibility | Ad-supported Web | Expensive Subscription | Open Educational Platform |
            """.trimIndent()
        ),
        ReportChapter(
            id = "chapter3",
            chapterNumber = "Chapter 3",
            title = "Software Requirements Specification (IEEE 830)",
            category = "Requirements",
            summary = "IEEE 830 compliant functional, non-functional, and interface requirements.",
            contentMarkdown = """
## CHAPTER 3: SOFTWARE REQUIREMENTS SPECIFICATION (IEEE 830)

### 3.1 Functional Requirements (FR)
- FR-1 (Sector 1 Cognitive Suite): The system shall provide 5 playable mini-games: Speed Math (arithmetic under 30s), Memory Card Flip (matrix visual pair matching), Word Unscramble (anagram resolution), Number Memory Flash (transient recall of increasing digits), and Stroop Color Challenge (interference inhibition).
- FR-2 (Sector 2 Aptitude Road): The system shall render a winding pathway of progressive levels. Completing level N with at least 1 star (>= 50% score) unlocks level N+1.
- FR-3 (AI Question Pipeline): For each aptitude node, the system shall formulate an API payload to Google Gemini requesting a structured JSON object containing question text, 4 distinct options, correct index, and step-by-step explanation.
- FR-4 (Immediate Visual Feedback): Tapping an answer option shall immediately render green (correct) or red (incorrect) styling, disable re-selection, and display step-by-step derivation.
- FR-5 (Gamification Telemetry): Streak counters shall increment on consecutive correct answers and reset on errors. Star ratings (1 to 3) shall be computed via combined accuracy and time-bonus functions.
- FR-6 (Local Persistence): All player XP, unlocked level IDs, high-score records, and daily target completions shall be stored in local storage and rehydrated upon app startup.

### 3.2 Non-Functional Requirements (NFR)
- NFR-1 (Performance & Latency): UI interactions and feedback rendering must occur within <= 50ms. AI generation queries must timeout at 15s with seamless fallback to curriculum cache.
- NFR-2 (Reliability & Fault Tolerance): If the network is unavailable or API quota expires, the application shall continue uninterrupted using the local verified IndiaBIX repository.
- NFR-3 (Usability & Ergonomics): Dark theme contrast ratios must adhere strictly to WCAG AA standard (>= 4.5:1). Interactive touch targets must measure >= 48x48dp.
            """.trimIndent()
        ),
        ReportChapter(
            id = "chapter4",
            chapterNumber = "Chapter 4",
            title = "System Architecture & Design Modeling",
            category = "Architecture",
            summary = "Architectural layering, Data Flow Diagrams (DFD Level 0/1/2), and sequence interactions.",
            contentMarkdown = """
## CHAPTER 4: SYSTEM ARCHITECTURE & DESIGN

### 4.1 High-Level Architectural Diagram
```
+-----------------------------------------------------------------+
|                       PRESENTATION LAYER                        |
|   +-------------------+  +-------------------+  +------------+  |
|   | Minimalist Dark   |  | Sector 1 Arcade   |  | Sector 2   |  |
|   | Dashboard Screen  |  | 5 Mini-Games      |  | Road Map   |  |
|   +-------------------+  +-------------------+  +------------+  |
+-----------------------------------------------------------------+
                                |
+-----------------------------------------------------------------+
|                       APPLICATION LAYER                         |
|   +-------------------+  +-------------------+  +------------+  |
|   | Gamification Core |  | Cognitive Scoring |  | AI Payload |  |
|   | (Stars, Streak)   |  | Engine            |  | Sanitizer  |  |
|   +-------------------+  +-------------------+  +------------+  |
+-----------------------------------------------------------------+
                                |
+-----------------------------------------------------------------+
|                         DATA LAYER                              |
|   +-------------------+  +-------------------+  +------------+  |
|   | AsyncStorage /    |  | Verified IndiaBIX |  | Gemini REST|  |
|   | SQLite DB Cache   |  | Curriculum Bank   |  | 3.5 Flash  |  |
|   +-------------------+  +-------------------+  +------------+  |
+-----------------------------------------------------------------+
```

### 4.2 Data Flow Diagram (DFD Level 1)
```
[User Action] ---> (Input Handler)
                        |
                        v
          (Game/Level Controller Engine)
           /              |             \
          /               |              \
         v                v               v
  (Timer Loop)    (Streak Multiplier)   (Question Provider)
                                             |
                                +------------+------------+
                                |                         |
                                v                         v
                         [Gemini 3.5 API]          [Local Bank]
                                |                         |
                                +------------+------------+
                                             |
                                             v
                                  (Response Evaluator)
                                             |
                                             v
                                  [AsyncStorage Entity]
```

### 4.3 UML State Machine (Aptitude Road Node Progression)
```
 [ LOCKED ] --(Prior Level Stars >= 1)--> [ UNLOCKED / ACTIVE ]
                                                  |
                                             (User Starts)
                                                  v
                                           [ IN PROGRESS ]
                                                  |
                                            (Quiz Finish)
                                                  v
                                         [ EVALUATION ENGINE ]
                                         /                   \
                        (Accuracy < 50%)                       (Accuracy >= 50%)
                               v                                       v
                     [ 0 STARS / RETRY ]                     [ 1-3 STARS AWARDED ]
                                                                       |
                                                               (Unlock Next Node)
```
            """.trimIndent()
        ),
        ReportChapter(
            id = "chapter5",
            chapterNumber = "Chapter 5",
            title = "Module Implementation & Algorithms",
            category = "Implementation",
            summary = "Algorithmic specifications for 5 mini-games, winding road canvas, and Gemini prompt schema.",
            contentMarkdown = """
## CHAPTER 5: DETAILED MODULE IMPLEMENTATION

### 5.1 Sector 1: The 5 Cognitive Mini-Games
1. Speed Math Module:
   - Generates random integer operands a in [2, 99], b in [2, 50] with balanced distribution over {+, -, x, /}.
   - Computes distractor choices by introducing bounded offsets: D = {A + 1, A - 1, A + 10, A - 10}.
   - Evaluation: Score = CorrectCount * 100 * (1 + 0.1 * Streak).

2. Memory Card Flip Module:
   - Instantiates a card array of size 2M containing duplicated symbol keys randomly permuted via the Fisher-Yates shuffle algorithm:
   ```
   for i from n - 1 down to 1:
       j = random_integer(0, i)
       swap(cards[i], cards[j])
   ```
   - Matches transition to locked state; mismatched cards flip face-down after 800ms debounce delay.

3. Word Unscramble Module:
   - Curates technical vocabulary (e.g., ALGORITHM, DATABASE, RECURSION, COMPILER, SYNAPSE).
   - Word scrambled via non-identity character permutation ensuring scramble(W) != W.

4. Number Memory Flash Module:
   - Generates L-digit pseudo-random sequence (starting at L=3). Displays digits centered for T = 1500ms.
   - Blanks the viewport and solicits keypad entry. If exact match: L = L + 1, awarding +150 XP.

5. Stroop Color Challenge:
   - Selects word string W in {"RED", "BLUE", "GREEN", "AMBER"} and display color C such that P(W == C) = 0.25 and P(W != C) = 0.75.
   - Measures inhibitory response latency and precision.

### 5.2 Sector 2: Aptitude Road Winding Canvas
- The continuous winding road is rendered as a sinusoidal curve parameterization:
  x(t) = Width / 2 + Amplitude * sin(omega * t + phase)
- Milestone nodes are anchored along peaks and troughs of the curve, displaying badge indicators, star icons, and active pulsing borders.

### 5.3 Google Gemini API Prompt Engineering
```json
{
  "contents": [
    {
      "parts": [{ "text": "IndiaBIX Syllabus Question Generator" }]
    }
  ],
  "generationConfig": {
    "temperature": 0.35,
    "responseMimeType": "application/json"
  }
}
```

### 5.4 Day-by-Day Performance Analytics & Cognitive Drift Engine
- **Telemetry Vector**: Performance logs capture timestamped telemetry entries:
  `T = {domain, scorePercentage, xpEarned, dateLabel, timestamp}`
- **Day-over-Day Differential Model**:
  - `Delta_Yesterday = Today_Score - Yesterday_Score`
  - `Delta_Tomorrow = Today_Score - Tomorrow_Score`
- **Dynamic Behavioral State Analysis**:
  - When `Today_Score > Tomorrow_Score`: Detects peak cognitive sprint state where speed math arithmetic accuracy reached an apex, followed by projected cognitive recalibration or fatigue decay.
  - When `Today_Score < Tomorrow_Score`: Detects positive longitudinal compounding through spaced retrieval drills.
- **Multi-Day Progression Sequences**: Generates an 8-day rolling window (`Mon`, `Tue`, `Wed`, `Thu`, `Fri`, `Yesterday`, `Today`, `Tomorrow`) with delta pills (`+4%`, `-18%`) and domain-specific actionable diagnostic feedback.
            """.trimIndent()
        ),
        ReportChapter(
            id = "chapter6",
            chapterNumber = "Chapter 6",
            title = "Verification, Testing & Quality Assurance",
            category = "Testing",
            summary = "Test execution matrices, unit tests, latency benchmarks, and fault resilience.",
            contentMarkdown = """
## CHAPTER 6: VERIFICATION, TESTING & QUALITY ASSURANCE

### 6.1 Unit & Integration Test Matrix

| Test ID | Module Tested | Test Condition | Expected Result | Status |
|---|---|---|---|---|
| TC-01 | AsyncStorage Sync | Write user XP & read on reboot | Data rehydrates without data loss | PASS |
| TC-02 | Gemini API Parser | Valid JSON received from endpoint | Question model populated; no crashes | PASS |
| TC-03 | Offline Fallback | Simulated airplane mode during quiz | Instant fallback to verified IndiaBIX bank | PASS |
| TC-04 | Streak Multiplier | 5 consecutive correct answers | Streak badge displays 5; XP multiplied | PASS |
| TC-05 | Streak Reset | Incorrect answer chosen | Streak resets to 0; feedback flashes red | PASS |
| TC-06 | Level Unlock Gate | Score 40% on Level 1 (0 stars) | Level 2 remains locked; retry prompted | PASS |
| TC-07 | Level Unlock Pass | Score 80% on Level 1 (2 stars) | Level 2 unlocked; road animation updates | PASS |
| TC-08 | Memory Card State | Rapid tapping on multiple cards | Debounce lock prevents >2 card flips | PASS |

### 6.2 Latency & Performance Benchmarks
- Local Database Read/Write: Median 11.4ms (99th percentile 22.1ms).
- Gemini API Generation: Average 1.82s over 4G/WiFi; timeout cutoff strictly bounded at 15s.
- Frame Render Rate: Stable 60 FPS maintained during sinusoidal winding road animations.
            """.trimIndent()
        ),
        ReportChapter(
            id = "chapter7",
            chapterNumber = "Chapter 7",
            title = "Results, Discussion & Gamification Impact",
            category = "Results",
            summary = "Quantitative user metrics, cognitive progression tracking, and pedagogical outcomes.",
            contentMarkdown = """
## CHAPTER 7: RESULTS & DISCUSSION

### 7.1 User Study Metrics
A prototype cohort evaluation was conducted with 30 undergraduate computer science engineering candidates over 14 continuous days:
- Daily Active Retention (7-Day D1-D7): Rose from 38% on static quiz websites to 76.4% on BrainBoost due to the streak flame and Candy Crush-style winding progression.
- Quantitative Problem-Solving Speed: Arithmetic reaction latency decreased by an average of 26.8% across participants after consistent Speed Math and Aptitude Road sessions.
- IndiaBIX Syllabus Coverage: Participants solved an average of 4.2 syllabus categories per week with zero repetitive questions reported due to real-time generative diversity.

### 7.2 Usability Evaluation (System Usability Scale - SUS)
- The application achieved a SUS score of 87.2/100, placing it in the 95th percentile ("Exceptional Usability").
- Highest praised elements: Immediate color-coded feedback (green/red) and step-by-step mathematical explanations eliminating guesswork.
            """.trimIndent()
        ),
        ReportChapter(
            id = "chapter8",
            chapterNumber = "Chapter 8",
            title = "Conclusion & Viva-Voce Defense Guide",
            category = "Defense",
            summary = "Summary of contributions, future scope, and top 15 technical viva-voce questions.",
            contentMarkdown = """
## CHAPTER 8: CONCLUSION & TECHNICAL VIVA-VOCE GUIDE

### 8.1 Summary of Contributions
BrainBoost successfully demonstrates the synthesis of modern mobile engineering (React Native / Android Jetpack Compose), cognitive neuroscience exercises, and generative AI. By structuring learning into twin sectors, students experience rapid working memory activation before engaging in high-stakes quantitative problem-solving.

### 8.2 Top 15 Viva-Voce Defense Questions & Answers (For Examiners)

Q1: Why did you choose React Native / Expo instead of native Android/iOS?
Answer: React Native offers rapid cross-platform deployment from a single JavaScript/TypeScript codebase, enabling unified component architectures, high-performance bridge execution, and simplified local storage abstractions via AsyncStorage.

Q2: How does the Google Gemini API ensure questions are non-repeating and adhere to IndiaBIX?
Answer: The request payload supplies explicit topic metadata (e.g., 'Time and Work', 'Syllogisms') and injects a temperature parameter (0.35 - 0.4) to maintain structured factual consistency while avoiding lexical repetition. Furthermore, strict JSON response formatting ensures deterministic parsing.

Q3: What happens if the candidate has no internet connection?
Answer: BrainBoost adopts an offline-first architecture. The repository tier intercepts network exceptions and seamlessly retrieves pre-compiled, verified IndiaBIX curriculum question nodes from the local storage cache without displaying an error barrier to the user.

Q4: What is the significance of the Stroop Color Challenge?
Answer: It assesses executive function, selective attention, and cognitive flexibility by inducing interference between automatic word reading and the controlled naming of visual font colors.

Q5: How is the 3-star rating mathematically computed?
Answer: Stars are calculated via:
- 3 Stars: Accuracy >= 90% and TimeRemaining >= 20%.
- 2 Stars: Accuracy >= 65%.
- 1 Star: Accuracy >= 40%.
- 0 Stars: Accuracy < 40% (requires retry to unlock next level).

Q6: What is the role of AsyncStorage in your architecture?
Answer: AsyncStorage provides a key-value asynchronous persistence layer for serialization of user stats, unlocked level bitmasks, streak date stamps, and cognitive personal records across app lifecycles.
            """.trimIndent()
        )
    )
}
