# Online Examination System

A desktop examination application made by me in **Java (AWT / Swing)**.
A student logs in with name and roll number, reads the instructions,
and gives a timed exam. At the end the result comes instantly with
marks, percentage, grade and a full answer review.

Developed by **Rishi Kant** — Chandigarh University

## Features

- Student login screen (name + roll number + subject)
- Instruction page before the exam starts
- 3 subjects — **Java, Aptitude, General Knowledge** (30 questions in each bank)
- Every exam randomly picks **15 questions** from the bank — a new paper each time
- **Big countdown timer** of 15 minutes — warning at the last minute,
  exam auto-submits when time ends
- **Question Palette** on the right side, like real online exams:
  - Green = Answered
  - Red = Seen but not answered
  - Purple = Marked for review
  - Grey = Not visited
- Buttons: Save & Next, Previous, Clear Response, Mark for Review
- Instant result: score, percentage, grade, PASS/FAIL, time used
- **Answer Review table** — your answer vs correct answer for every question
- Every result is saved in `data/results.csv`
- "View Past Results" screen for the teacher

## How to Run

You need Java (JDK 17 or above) installed.

### In VS Code (easiest for showing the code)
1. Open this folder in VS Code (**File → Open Folder**).
2. If VS Code asks, install the **Extension Pack for Java**
   (the project suggests it automatically through `.vscode/extensions.json`).
3. Open `src/com/exam/Main.java`.
4. Click the **Run** button above the `main` method, or press **F5**.
   The login screen opens. That is all — no commands needed.
   (The ready launch setting is already saved in `.vscode/launch.json`.)

### Without VS Code
**Windows:** double-click `run.bat` — it runs the ready-made
`OnlineExaminationSystem.jar`.
**Linux/Mac:** run `./run.sh`

Manual way:
```
javac -d out -sourcepath src src/com/exam/Main.java
java -cp out com.exam.Main
```

## Project Structure

```
src/com/exam/Main.java                  -> starts the application
src/com/exam/model/Question.java        -> one MCQ question
src/com/exam/model/ExamResult.java      -> result, percentage, grade
src/com/exam/data/QuestionBank.java     -> all 30 questions (3 subjects)
src/com/exam/ui/LoginFrame.java         -> login + instructions screen
src/com/exam/ui/ExamFrame.java          -> exam screen (timer + palette)
src/com/exam/ui/ResultFrame.java        -> result + answer review screen
src/com/exam/ui/ResultsFrame.java       -> past results (teacher view)
data/results.csv                        -> saved results (auto-created)
```

## Exam Rules

- 15 questions per exam (randomly picked from a bank of 30 per subject), 15 minutes total
- 1 mark per question, **no negative marking**
- 40% and above = PASS

## Future Improvements

- Questions from a database (MySQL) instead of code
- Negative marking and different time per subject
- Admin panel to add/edit questions from the app itself
