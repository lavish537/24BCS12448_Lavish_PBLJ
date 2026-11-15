# Project Synopsis — Online Examination System

**Student:** Rishi Kant (24BCS12414), Chandigarh University

## Problem
Manual exams take a lot of time to conduct and check. Marks are
calculated by hand, results come late, and students get no instant
feedback on their answers.

## Solution
A desktop Online Examination System in Java (AWT/Swing) where a
student logs in, gives a timed MCQ exam for a chosen subject and
gets the result immediately with a complete answer review.

## Modules
1. **Login & Instructions** — name, roll number, subject selection
2. **Exam Engine** — 15-minute countdown timer with auto-submit,
   question palette (answered / not answered / marked / not visited),
   Save & Next, Previous, Clear Response, Mark for Review
3. **Question Bank** — 3 subjects (Java, Aptitude, GK), bank of 20 questions each, 15 randomly picked per exam
4. **Result & Review** — score, percentage, grade, PASS/FAIL, time used,
   and a table comparing the student's answer with the correct answer
5. **Result Records** — every result is stored in a CSV file;
   a Past Results screen shows all submissions

## Technology
- Language: Java
- GUI: AWT / Swing (layouts, events, frames, tables)
- Storage: CSV file
- Tools: JDK 21, VS Code

## Conclusion
The system makes conducting small tests fast and error-free:
no manual checking, instant results, and a clear review that
helps the student learn from mistakes.
