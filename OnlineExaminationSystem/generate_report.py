#!/usr/bin/env python3
"""
Generates PROJECT_REPORT.docx for "Online Examination System"
following the Chandigarh University BE project report format
(same format as the Library Management System report).
Team: Rishi Kant (24BCS12414), Ujjwal Gupta (24BCS12567),
      Satyam Kr Jha (24BCS12132)
Also draws the diagrams and screen figures with PIL.
"""
import os
from PIL import Image, ImageDraw, ImageFont
from docx import Document
from docx.shared import Pt, Cm, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.enum.section import WD_SECTION
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn
from docx.oxml import OxmlElement

BASE = os.path.dirname(os.path.abspath(__file__))
DIAGRAM1 = os.path.join(BASE, "diagram1_graphical_abstract.png")
DIAGRAM2 = os.path.join(BASE, "diagram2_module_diagram.png")
SHOT1 = os.path.join(BASE, "screenshots", "1-login.png")
SHOT2 = os.path.join(BASE, "screenshots", "2-exam.png")
SHOT3 = os.path.join(BASE, "screenshots", "3-result.png")
OUTPUT = os.path.join(BASE, "PROJECT_REPORT.docx")

FONT_DIR = "/usr/share/fonts/truetype/dejavu"
F_SANS = os.path.join(FONT_DIR, "DejaVuSans.ttf")
F_SANS_B = os.path.join(FONT_DIR, "DejaVuSans-Bold.ttf")

BLUE = (31, 78, 121)        # #1f4e79
NAVY = (25, 70, 130)
DARK = (0, 0, 0)
LIGHT_FILL = (255, 255, 255)
GREEN = (34, 139, 60)
RED = (200, 45, 45)
PURPLE = (120, 70, 160)
GREY_TXT = (90, 90, 90)
BORDER = (190, 200, 215)
LIGHT_BG = (240, 243, 247)


# ----------------------------------------------------------------------------
# Diagram helpers
# ----------------------------------------------------------------------------
def font(size, bold=False):
    return ImageFont.truetype(F_SANS_B if bold else F_SANS, size)


def draw_box(draw, xy, lines, fill=LIGHT_FILL, outline=BLUE, width=4):
    x0, y0, x1, y1 = xy
    draw.rectangle(xy, fill=fill, outline=outline, width=width)
    metrics = []
    for text, bold, size in lines:
        f = font(size, bold)
        bbox = draw.textbbox((0, 0), text, font=f)
        metrics.append((f, bbox, bbox[2] - bbox[0], bbox[3] - bbox[1]))
    total = sum(m[3] for m in metrics) + 8 * (len(lines) - 1)
    cy = y0 + (y1 - y0 - total) / 2
    for (text, bold, size), (f, bbox, tw, th) in zip(lines, metrics):
        draw.text((x0 + (x1 - x0 - tw) / 2 - bbox[0], cy - bbox[1]),
                  text, font=f, fill=DARK)
        cy += th + 8


def arrow(draw, x0, y0, x1, y1, width=5):
    import math
    draw.line([x0, y0, x1, y1], fill=DARK, width=width)
    dx, dy = x1 - x0, y1 - y0
    dist = math.hypot(dx, dy) or 1
    ux, uy = dx / dist, dy / dist
    px, py = -uy, ux
    hl, hw = 24, 11
    bx, by = x1 - ux * hl, y1 - uy * hl
    pts = [(x1, y1), (bx + px * hw, by + py * hw), (bx - px * hw, by - py * hw)]
    draw.polygon(pts, fill=DARK)


def make_diagram1():
    W, H = 1760, 780
    img = Image.new("RGB", (W, H), "white")
    d = ImageDraw.Draw(img)
    d.text((40, 30), "System Flow of the Online Examination System",
           font=font(38, True), fill=DARK)

    boxes = [
        ("Student", None),
        ("Login Screen", None),
        ("Exam Screen", ("(Timer + Question", "Palette)")),
        ("Result Screen", ("(Score + Answer", "Review)")),
        ("Records File", ("(results.csv)",)),
    ]
    bw, bh, y0 = 288, 134, 122
    gap = 52
    xs = []
    x = 36
    for _ in boxes:
        xs.append(x)
        x += bw + gap
    for (title, sub), bx in zip(boxes, xs):
        lines = [(title, True, 32)]
        if isinstance(sub, tuple):
            for s in sub:
                lines.append((s, False, 24))
        elif sub:
            lines.append((sub, False, 24))
        draw_box(d, (bx, y0, bx + bw, y0 + bh), lines)
    for i in range(len(boxes) - 1):
        arrow(d, xs[i] + bw, y0 + bh // 2, xs[i + 1] - 4, y0 + bh // 2)

    # note under exam screen: question bank feeds it
    qb_y = y0 + bh + 150
    qb_x = xs[2]
    draw_box(d, (qb_x, qb_y, qb_x + bw, qb_y + 110),
             [("Question Bank", True, 30), ("(30 per subject)", False, 24)])
    arrow(d, qb_x + bw // 2, qb_y, qb_x + bw // 2, y0 + bh + 4)
    note = "15 questions are randomly selected for every exam"
    f = font(27)
    bb = d.textbbox((0, 0), note, font=f)
    d.text(((W - (bb[2] - bb[0])) / 2, qb_y + 150), note, font=f, fill=DARK)
    img.save(DIAGRAM1)


def make_diagram2():
    W, H = 1500, 900
    img = Image.new("RGB", (W, H), "white")
    d = ImageDraw.Draw(img)
    d.text((40, 26), "Module Diagram of the Online Examination System",
           font=font(36, True), fill=DARK)

    # centre box
    cx0, cy0, cx1, cy1 = 500, 110, 1000, 230
    draw_box(d, (cx0, cy0, cx1, cy1),
             [("Online Examination", True, 34), ("System", True, 34)])
    ccx, ccy = (cx0 + cx1) // 2, (cy0 + cy1) // 2

    modules = [
        ("Login &\nInstructions", 90, 380),
        ("Question\nBank", 400, 380),
        ("Exam Engine\n(Timer + Palette)", 710, 380),
        ("Result &\nReview", 1020, 380),
        ("Records\n(CSV File)", 565, 640),
    ]
    bw, bh = 280, 130
    for name, mx, my in modules:
        lines = [(t, True, 29) for t in name.split("\n")]
        draw_box(d, (mx, my, mx + bw, my + bh), lines)
        mcx, mcy = mx + bw // 2, my + bh // 2
        # line from centre box edge to module top/centre
        arrow(d, ccx, cy1 if mcy > ccy else cy0, mcx, my if mcy > ccy else my + bh)
    img.save(DIAGRAM2)


# ----------------------------------------------------------------------------
# Screen figures (drawn to match the actual application screens)
# ----------------------------------------------------------------------------
def _centered(d, cx, y, text, f, fill=DARK):
    bb = d.textbbox((0, 0), text, font=f)
    d.text((cx - (bb[2] - bb[0]) / 2 - bb[0], y), text, font=f, fill=fill)


def make_shot_login():
    W, H = 1120, 860
    img = Image.new("RGB", (W, H), "white")
    d = ImageDraw.Draw(img)
    # window border + title bar
    d.rectangle([0, 0, W - 1, H - 1], outline=(120, 120, 120), width=3)
    d.rectangle([0, 0, W, 44], fill=(225, 228, 232))
    d.text((18, 10), "Online Examination System", font=font(22), fill=DARK)
    # header
    d.rectangle([0, 44, W, 190], fill=NAVY)
    d.text((34, 66), "ONLINE EXAMINATION SYSTEM", font=font(40, True), fill=(255, 255, 255))
    d.text((36, 128), "Chandigarh University", font=font(24), fill=(200, 220, 245))
    # form
    fx, lx = 330, 120
    rows = [("Student Name:", "Rishi Kant"), ("Roll Number:", "24BCS12414")]
    y = 280
    for label, val in rows:
        d.text((lx, y + 8), label, font=font(27), fill=DARK)
        d.rectangle([fx, y, fx + 560, y + 56], outline=BORDER, width=2)
        d.text((fx + 14, y + 10), val, font=font(26), fill=DARK)
        y += 96
    d.text((lx, y + 8), "Select Subject:", font=font(27), fill=DARK)
    d.rectangle([fx, y, fx + 560, y + 56], outline=BORDER, width=2)
    d.text((fx + 14, y + 10), "Java", font=font(26), fill=DARK)
    d.text((fx + 520, y + 8), "v", font=font(26, True), fill=GREY_TXT)
    y += 100
    info = "Exam: 15 questions  |  Time: 15 minutes  |  No negative marking"
    _centered(d, W // 2, y, info, font(24), fill=GREY_TXT)
    # buttons
    y += 90
    d.rectangle([300, y, 560, y + 62], fill=GREEN)
    _centered(d, 430, y + 14, "Start Exam", font(27, True), fill=(255, 255, 255))
    d.rectangle([580, y, 860, y + 62], outline=BORDER, width=2)
    _centered(d, 720, y + 14, "View Past Results", font(27), fill=DARK)
    os.makedirs(os.path.dirname(SHOT1), exist_ok=True)
    img.save(SHOT1)


def make_shot_exam():
    W, H = 1500, 920
    img = Image.new("RGB", (W, H), LIGHT_BG)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, W - 1, H - 1], outline=(120, 120, 120), width=3)
    d.rectangle([0, 0, W, 46], fill=(225, 228, 232))
    d.text((18, 11), "Online Examination - Java", font=font(22), fill=DARK)
    # top bar
    d.rectangle([0, 46, W, 168], fill=NAVY)
    d.text((26, 62), "ONLINE EXAMINATION  -  JAVA", font=font(31, True), fill=(255, 255, 255))
    d.text((26, 112), "Candidate: Rishi Kant    |    Roll No: 24BCS12414    |    "
                      "Total Questions: 15    |    Max Marks: 15",
           font=font(23), fill=(200, 220, 245))
    # timer box
    d.rectangle([1250, 58, 1474, 156], fill=(13, 42, 74))
    _centered(d, 1362, 66, "TIME LEFT", font(19, True), fill=(170, 200, 235))
    _centered(d, 1362, 94, "13:47", font(46, True), fill=(255, 255, 255))
    # question title
    d.text((30, 190), "Question 3 of 15        |        Marks: 1        |        Negative Marks: 0",
           font=font(27, True), fill=NAVY)
    # white card
    d.rectangle([26, 244, 1130, 800], fill=(255, 255, 255), outline=BORDER, width=2)
    d.text((56, 272), "Which collection does NOT allow duplicate elements?",
           font=font(31, True), fill=(20, 20, 20))
    opts = ["A.   ArrayList", "B.   LinkedList", "C.   HashSet", "D.   Vector"]
    oy = 356
    for i, o in enumerate(opts):
        sel = (i == 2)
        d.rectangle([56, oy, 1100, oy + 88],
                    fill=(225, 240, 255) if sel else (255, 255, 255),
                    outline=NAVY if sel else BORDER, width=3 if sel else 2)
        # radio circle
        cx, cy = 92, oy + 44
        d.ellipse([cx - 13, cy - 13, cx + 13, cy + 13], outline=(60, 60, 60), width=3)
        if sel:
            d.ellipse([cx - 6, cy - 6, cx + 6, cy + 6], fill=NAVY)
        d.text((126, oy + 24), o, font=font(28), fill=(20, 20, 20))
        oy += 106
    # buttons row
    by = 824
    btns = [("<< Previous", (90, 100, 115), 170), ("Clear Response", (90, 100, 115), 215),
            ("Mark for Review", PURPLE, 225), ("Save & Next >>", GREEN, 200),
            ("SUBMIT EXAM", RED, 195)]
    bx = 26
    for text, col, wdt in btns:
        d.rectangle([bx, by, bx + wdt, by + 58], fill=col)
        _centered(d, bx + wdt // 2, by + 13, text, font(24, True), fill=(255, 255, 255))
        bx += wdt + 18
    # palette
    px0, py0 = 1160, 244
    d.rectangle([px0, py0, 1474, 800], fill=(255, 255, 255), outline=BORDER, width=2)
    d.text((px0 + 18, py0 + 14), "Question Palette", font=font(24, True), fill=NAVY)
    cols = {0: GREEN, 1: GREEN, 2: NAVY, 3: RED, 4: (228, 228, 228), 5: PURPLE}
    cell, gap = 62, 12
    for i in range(15):
        r, c = divmod(i, 4)
        x = px0 + 20 + c * (cell + gap)
        y = py0 + 64 + r * (cell + gap)
        fill = cols.get(i, (228, 228, 228))
        d.rectangle([x, y, x + cell, y + cell], fill=fill,
                    outline=NAVY if i == 2 else (150, 150, 150), width=3 if i == 2 else 1)
        _centered(d, x + cell // 2, y + 15, str(i + 1), font(25, True),
                  fill=(255, 255, 255) if fill != (228, 228, 228) else DARK)
    ly = py0 + 64 + 4 * (cell + gap) + 22
    for col, txt in [(GREEN, "Answered"), (RED, "Not Answered"),
                     (PURPLE, "Marked for Review"), ((150, 150, 150), "Not Visited")]:
        d.rectangle([px0 + 20, ly + 4, px0 + 42, ly + 26], fill=col)
        d.text((px0 + 54, ly), txt, font=font(22), fill=DARK)
        ly += 38
    d.text((px0 + 20, ly + 6), "Answered: 2  |  Marked: 1", font=font(22, True), fill=DARK)
    img.save(SHOT2)


def make_shot_result():
    W, H = 1500, 920
    img = Image.new("RGB", (W, H), "white")
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, W - 1, H - 1], outline=(120, 120, 120), width=3)
    d.rectangle([0, 0, W, 46], fill=(225, 228, 232))
    d.text((18, 11), "Exam Result", font=font(22), fill=DARK)
    # summary
    d.rectangle([0, 46, W, 196], fill=NAVY)
    d.text((28, 66), "Rishi Kant  (24BCS12414)   -   Java", font=font(31, True), fill=(255, 255, 255))
    d.text((28, 122), "Score: 11 / 15   |   Percentage: 73.3%   |   Grade: B+   |   "
                      "Attempted: 13   |   Wrong: 2   |   Time Used: 11:26   |   PASS",
           font=font(24), fill=(210, 228, 250))
    # table header
    tx = [28, 120, 700, 1010, 1300]
    ty = 226
    d.rectangle([28, ty, 1472, ty + 52], fill=(225, 228, 232), outline=BORDER, width=2)
    for x, htxt in zip(tx, ["Q.No", "Question", "Your Answer", "Correct Answer", "Result"]):
        d.text((x + 8, ty + 11), htxt, font=font(24, True), fill=DARK)
    rows = [
        ("1", "Who developed the Java language?", "B. James Gosling", "B. James Gosling", "Correct", GREEN),
        ("2", "What is the full form of JVM?", "A. Java Virtual Machine", "A. Java Virtual Machine", "Correct", GREEN),
        ("3", "Which collection does NOT allow...", "C. HashSet", "C. HashSet", "Correct", GREEN),
        ("4", "Which keyword is used to inherit...", "A. implements", "B. extends", "Wrong", RED),
        ("5", "What is the default value of an int?", "(Not attempted)", "A. 0", "Skipped", GREY_TXT),
        ("6", "Which method is the entry point...?", "C. main()", "C. main()", "Correct", GREEN),
    ]
    ry = ty + 52
    for qno, ques, yours, right, res, col in rows:
        d.rectangle([28, ry, 1472, ry + 62], outline=BORDER, width=1)
        d.text((tx[0] + 8, ry + 15), qno, font=font(23), fill=DARK)
        d.text((tx[1] + 8, ry + 15), ques, font=font(23), fill=DARK)
        d.text((tx[2] + 8, ry + 15), yours, font=font(23), fill=DARK)
        d.text((tx[3] + 8, ry + 15), right, font=font(23), fill=DARK)
        d.text((tx[4] + 8, ry + 15), res, font=font(23, True), fill=col)
        ry += 62
    d.text((28, ry + 18), "... (all 15 questions are shown in the review table)",
           font=font(23), fill=GREY_TXT)
    # buttons
    by = H - 96
    d.rectangle([560, by, 770, by + 56], outline=BORDER, width=2)
    _centered(d, 665, by + 12, "Back to Home", font(25), fill=DARK)
    d.rectangle([790, by, 940, by + 56], outline=BORDER, width=2)
    _centered(d, 865, by + 12, "Exit", font(25), fill=DARK)
    img.save(SHOT3)


# ----------------------------------------------------------------------------
# Document helpers (same as the Library Management System report)
# ----------------------------------------------------------------------------
def set_cell_borders_none(table):
    tbl = table._tbl
    tblPr = tbl.tblPr
    borders = OxmlElement("w:tblBorders")
    for edge in ("top", "left", "bottom", "right", "insideH", "insideV"):
        el = OxmlElement("w:%s" % edge)
        el.set(qn("w:val"), "nil")
        el.set(qn("w:sz"), "0")
        el.set(qn("w:space"), "0")
        el.set(qn("w:color"), "auto")
        borders.append(el)
    tblPr.append(borders)


def add_para(doc, text="", size=12, bold=False, italic=False, align="justify",
             underline=False, space_after=6, space_before=0, font_name="Times New Roman"):
    p = doc.add_paragraph()
    p.alignment = {"justify": WD_ALIGN_PARAGRAPH.JUSTIFY,
                   "center": WD_ALIGN_PARAGRAPH.CENTER,
                   "left": WD_ALIGN_PARAGRAPH.LEFT,
                   "right": WD_ALIGN_PARAGRAPH.RIGHT}[align]
    pf = p.paragraph_format
    pf.space_after = Pt(space_after)
    pf.space_before = Pt(space_before)
    pf.line_spacing = 1.15
    if text:
        r = p.add_run(text)
        r.font.name = font_name
        r.font.size = Pt(size)
        r.bold = bold
        r.italic = italic
        r.underline = underline
    return p


def chapter_heading(doc, text, size=16, underline=False):
    return add_para(doc, text, size=size, bold=True, align="center",
                    underline=underline, space_before=6, space_after=12)


def section_heading(doc, text, size=13):
    return add_para(doc, text, size=size, bold=True, align="left",
                    space_before=10, space_after=4)


def body(doc, text):
    return add_para(doc, text, size=12, align="justify", space_after=6)


def bullet(doc, text, bold_prefix=None):
    p = doc.add_paragraph(style="List Bullet")
    p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    p.paragraph_format.space_after = Pt(3)
    p.paragraph_format.line_spacing = 1.15
    if bold_prefix:
        r = p.add_run(bold_prefix)
        r.bold = True
        r.font.name = "Times New Roman"
        r.font.size = Pt(12)
    r = p.add_run(text)
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    return p


def set_cell(cell, text, bold=False, size=11, align="left"):
    cell.text = ""
    p = cell.paragraphs[0]
    p.alignment = {"left": WD_ALIGN_PARAGRAPH.LEFT,
                   "center": WD_ALIGN_PARAGRAPH.CENTER}[align]
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.line_spacing = 1.0
    r = p.add_run(text)
    r.font.name = "Times New Roman"
    r.font.size = Pt(size)
    r.bold = bold


def add_data_table(doc, headers, rows, widths=None):
    table = doc.add_table(rows=1, cols=len(headers))
    table.style = "Table Grid"
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    for i, h in enumerate(headers):
        set_cell(table.rows[0].cells[i], h, bold=True, size=11, align="center")
    for row in rows:
        cells = table.add_row().cells
        for i, val in enumerate(row):
            set_cell(cells[i], val, size=11)
    if widths:
        for i, w in enumerate(widths):
            for row in table.rows:
                row.cells[i].width = Cm(w)
    return table


# ----------------------------------------------------------------------------
# Document construction
# ----------------------------------------------------------------------------
TEAM = [("Rishi Kant", "24BCS12414"), ("Ujjwal Gupta", "24BCS12567"),
        ("Satyam Kr Jha", "24BCS12132")]
TITLE = "Online Examination System"


def build():
    make_diagram1()
    make_diagram2()
    # screenshots in screenshots/ are real captures of the running
    # application (taken with GuiTest) - they are used as they are.

    doc = Document()
    sec = doc.sections[0]
    sec.page_width = Cm(21.0)
    sec.page_height = Cm(29.7)
    sec.top_margin = Cm(2.54)
    sec.bottom_margin = Cm(2.54)
    sec.left_margin = Cm(2.54)
    sec.right_margin = Cm(2.54)

    style = doc.styles["Normal"]
    style.font.name = "Times New Roman"
    style.font.size = Pt(12)

    # ---------------------------- TITLE PAGE ----------------------------
    add_para(doc, "", space_after=6)
    _logo_p = doc.add_paragraph()
    _logo_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    _logo_p.add_run().add_picture(os.path.join(BASE, "cu_logo.png"), width=Cm(6.8))
    add_para(doc, "", space_after=10)
    add_para(doc, TITLE, size=26, bold=True, align="center", space_after=18)
    add_para(doc, "A PROJECT REPORT", size=16, bold=True, align="center", space_after=24)
    add_para(doc, "Submitted by", size=14, align="center", space_after=8)
    for name, uid in TEAM:
        add_para(doc, "%s \u2013 %s" % (name, uid), size=14, bold=True,
                 align="center", space_after=4)
    add_para(doc, "", space_after=18)
    add_para(doc, "in partial fulfillment for the award of the degree of",
             size=13, align="center", space_after=8)
    add_para(doc, "Bachelor of Engineering", size=16, bold=True, align="center", space_after=2)
    add_para(doc, "IN", size=14, bold=True, align="center", space_after=2)
    add_para(doc, "Computer Science & Engineering", size=16, bold=True,
             align="center", space_after=30)
    add_para(doc, "Chandigarh University", size=18, bold=True, align="center", space_after=8)
    add_para(doc, "November 2026", size=14, align="center", space_after=0)
    doc.add_page_break()

    # ------------------------- BONAFIDE CERTIFICATE -------------------------
    chapter_heading(doc, "BONAFIDE CERTIFICATE", size=16, underline=True)
    add_para(doc, "", space_after=12)
    _parts = ["\u201C%s (%s)\u201D" % (n, u) for n, u in TEAM]
    names_text = ", ".join(_parts[:-1]) + " and " + _parts[-1]
    body(doc, "Certified that this project report \u201COnline Examination System\u201D "
              "is the Bonafide work of %s who carried out the project work under "
              "my/our supervision." % names_text)
    add_para(doc, "", space_after=36)

    sig1 = doc.add_table(rows=1, cols=2)
    set_cell_borders_none(sig1)
    c = sig1.rows[0].cells
    set_cell(c[0], "SIGNATURE", size=12)
    set_cell(c[1], "SIGNATURE", size=12)
    row = sig1.add_row().cells
    set_cell(row[0], "", size=12)
    set_cell(row[1], "", size=12)
    row = sig1.add_row().cells
    set_cell(row[0], "_______________", size=12)
    set_cell(row[1], "_______________", size=12)
    row = sig1.add_row().cells
    set_cell(row[0], "SUPERVISOR", bold=True, size=12)
    set_cell(row[1], "HEAD OF DEPARTMENT", bold=True, size=12)

    add_para(doc, "", space_after=24)
    body(doc, "Submitted for the project viva-voce examination held on _______________ .")
    add_para(doc, "", space_after=36)

    sig2 = doc.add_table(rows=1, cols=2)
    set_cell_borders_none(sig2)
    c = sig2.rows[0].cells
    set_cell(c[0], "SIGNATURE", size=12)
    set_cell(c[1], "SIGNATURE", size=12)
    row = sig2.add_row().cells
    set_cell(row[0], "", size=12)
    set_cell(row[1], "", size=12)
    row = sig2.add_row().cells
    set_cell(row[0], "_______________", size=12)
    set_cell(row[1], "_______________", size=12)
    row = sig2.add_row().cells
    set_cell(row[0], "INTERNAL EXAMINER", bold=True, size=12)
    set_cell(row[1], "EXTERNAL EXAMINER", bold=True, size=12)
    doc.add_page_break()

    # ------------------------------- ABSTRACT -------------------------------
    chapter_heading(doc, "ABSTRACT", size=16, underline=True)
    body(doc, "In colleges and coaching institutes, class tests and practice examinations "
              "are still mostly conducted on paper. The teacher has to print the question "
              "papers, collect the answer sheets, check every sheet by hand and then "
              "prepare the results. This complete work takes many days, mistakes in "
              "totalling are common, and by the time the result comes, the student has "
              "mostly forgotten the questions, so the review of answers loses its value. "
              "This project, Online Examination System, is a desktop application "
              "developed in Java using AWT and Swing to conduct objective examinations "
              "on a computer. The student enters his name and roll number, selects a "
              "subject and reads the instructions. The system then prepares a paper of "
              "15 questions selected randomly from a question bank of 30 questions of "
              "that subject, so that every student receives a different paper. The exam "
              "screen follows the pattern of real computer based tests: a countdown "
              "timer of 15 minutes with automatic submission at the end of time, and a "
              "question palette which shows the state of every question in colours - "
              "answered, not answered, marked for review and not visited. On submission, "
              "the result is shown immediately with the score, percentage, grade and "
              "the complete answer review of every question, and the result is also "
              "saved in a file for the teacher. Three subjects - Java, Aptitude and "
              "General Knowledge - are provided in the system at present.")
    add_para(doc, "", space_after=12)

    # -------------------------- GRAPHICAL ABSTRACT --------------------------
    chapter_heading(doc, "GRAPHICAL ABSTRACT", size=16, underline=True)
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run().add_picture(DIAGRAM1, width=Cm(15.8))
    add_para(doc, "Figure: Graphical Abstract of the Online Examination System",
             size=11, italic=True, align="center", space_after=6)
    doc.add_page_break()

    # ----------------------------- ABBREVIATIONS -----------------------------
    chapter_heading(doc, "ABBREVIATIONS/SYMBOLS", size=16, underline=True)
    abbr = [
        ("AWT", "Abstract Window Toolkit"),
        ("CBT", "Computer Based Test"),
        ("CSV", "Comma Separated Values"),
        ("GUI", "Graphical User Interface"),
        ("IDE", "Integrated Development Environment"),
        ("JDK", "Java Development Kit"),
        ("JRE", "Java Runtime Environment"),
        ("JVM", "Java Virtual Machine"),
        ("MCQ", "Multiple Choice Question"),
        ("OOP", "Object Oriented Programming"),
        ("UI", "User Interface"),
    ]
    at = doc.add_table(rows=0, cols=2)
    set_cell_borders_none(at)
    for short, full in abbr:
        row = at.add_row().cells
        set_cell(row[0], short, bold=True, size=12)
        set_cell(row[1], full, size=12)
        row[0].width = Cm(3.5)
        row[1].width = Cm(12.0)
    doc.add_page_break()

    # ------------------------------ CHAPTER 1 ------------------------------
    chapter_heading(doc, "CHAPTER 1. INTRODUCTION")
    section_heading(doc, "1.1 Identification of Client/Need")
    body(doc, "The client for this project is a college teacher, a coaching institute or "
              "a training department which conducts objective tests of students again "
              "and again - class tests, unit tests, mock tests for placements and "
              "practice examinations. For every test, the teacher needs a question "
              "paper within the syllabus, a fixed time limit which is followed strictly "
              "by every student, checking of all the answer sheets, and the results of "
              "the whole class in a proper record. The students also need their result "
              "immediately, together with the correct answers, so that they can learn "
              "from their mistakes while the questions are still fresh in mind. This "
              "need of the client is the starting point of the present project.")

    section_heading(doc, "1.2 Identification of Problem")
    body(doc, "From the study of the paper based examination system, the following "
              "problems were identified:")
    bullet(doc, "The teacher checks every answer sheet by hand, so preparing the result "
                "of even one class takes many days.",
           bold_prefix="Slow checking and results: ")
    bullet(doc, "Adding the marks of many questions and students by hand leads to "
                "totalling mistakes, and a single mistake changes the result of a "
                "student.",
           bold_prefix="Mistakes in totalling: ")
    bullet(doc, "On paper, the time limit cannot be enforced equally on everyone. Some "
                "students keep writing after the time is over.",
           bold_prefix="No strict time control: ")
    bullet(doc, "Every student receives the same paper in the same order, so copying "
                "from the nearby student is easy.",
           bold_prefix="Same paper for all: ")
    bullet(doc, "After the result, the student gets only the marks. He does not come to "
                "know which answers were wrong and what the correct answers were.",
           bold_prefix="No answer review: ")
    bullet(doc, "The answer sheets and result registers of past examinations are "
                "difficult to keep and even more difficult to search later.",
           bold_prefix="Records on paper: ")

    section_heading(doc, "1.3 Identification of Tasks")
    body(doc, "To solve the above problem, the following tasks were identified for this "
              "project:")
    tasks = [
        "Study the existing paper based and computer based examination systems and note their good and weak points.",
        "Prepare a question bank of objective questions for the selected subjects.",
        "Design the screens of the system: login, instructions, examination and result.",
        "Implement the examination engine with the countdown timer, automatic submission and the question palette.",
        "Implement the marking logic, the grade calculation and the answer review.",
        "Save every result in a file so that the records of all the students are kept properly.",
        "Test the complete system, including the cases of time finishing and questions left unattempted.",
        "Prepare the documentation and the project report.",
    ]
    for t in tasks:
        bullet(doc, t)

    section_heading(doc, "1.4 Timeline")
    body(doc, "The project work was completed in the following phases. In the first phase, "
              "the existing examination systems were studied and the requirements were "
              "listed. In the second phase, the design of the system was prepared, which "
              "includes the question bank structure, the state model of the question "
              "palette and the layout of the screens. In the third phase, the actual "
              "implementation was done module by module, first the question bank and "
              "marking logic, and then the screens. In the fourth phase, every flow of "
              "the system was tested and the mistakes found were corrected. In the last "
              "phase, the documentation and this report were prepared.")

    section_heading(doc, "1.5 Organization of the Report")
    body(doc, "This report is organised in five chapters. Chapter 1 gives the introduction "
              "of the project. It identifies the client and his need, the problems of the "
              "present system, the tasks taken up in the project and the timeline of the "
              "work. Chapter 2 presents the literature review and background study. It "
              "describes how the reported problem has developed with time, studies the "
              "existing solutions, and then states the problem definition and the goals "
              "and objectives of the project. Chapter 3 describes the design flow and "
              "process of the system. It explains the selection of features, the design "
              "constraints, the design approach that was selected and the implementation "
              "plan, and it presents the working prototype of the system. Chapter 4 gives "
              "the results and validation of the project, with the output of the tests "
              "that were performed on the system. Chapter 5 presents the conclusion of "
              "the work and the improvements that can be taken up in the future.")
    doc.add_page_break()

    # ------------------------------ CHAPTER 2 ------------------------------
    chapter_heading(doc, "CHAPTER 2. LITERATURE REVIEW/BACKGROUND STUDY")
    section_heading(doc, "2.1 Timeline of the Reported Problem")
    body(doc, "The method of conducting examinations has changed with time. In the "
              "beginning, all examinations were written examinations on paper, and every "
              "answer sheet was checked completely by hand. With the coming of objective "
              "type questions, OMR sheets came into use, in which the student fills a "
              "circle for every answer and a scanner machine reads the sheets. This made "
              "checking fast, but the OMR sheets and the scanner are costly, and the "
              "student still gets no review of his answers. In the next stage, computer "
              "based tests started, in which the complete examination is given on a "
              "computer - the question appears on the screen, the answer is clicked, a "
              "timer runs on the screen, and the result is prepared by the computer "
              "itself. At present, all the big examinations of the country, such as JEE, "
              "NEET and SSC, are conducted in this computer based manner only, with a "
              "question palette on the screen. The present project belongs to this "
              "latest stage and follows the same pattern on a small scale.")

    section_heading(doc, "2.2 Existing Solutions")
    bullet(doc, "The oldest method. Questions are printed on paper and answers are "
                "written or ticked on paper or on a separate sheet. It needs no "
                "computer, but checking is slow and completely manual.",
           bold_prefix="Paper based examination: ")
    bullet(doc, "The student fills circles on a special sheet, which is read by a "
                "scanner. Checking becomes fast, but the sheets and the machine are "
                "costly, a filled mistake cannot be corrected, and no answer review is "
                "given to the student.",
           bold_prefix="OMR based examination: ")
    bullet(doc, "Small quiz programs made for a single computer, in which the questions "
                "come one by one and the score is shown at the end. These are simple, "
                "but most of them have no timer discipline, no question palette and no "
                "record of the results.",
           bold_prefix="Simple desktop quiz software: ")
    bullet(doc, "Complete computer based test platforms used for national level "
                "examinations. These are powerful and secure, but they need servers, a "
                "network of many computers and technical staff, so a single teacher or "
                "a small institute cannot use them for a class test.",
           bold_prefix="Full CBT platforms: ")

    section_heading(doc, "2.3 Bibliometric Analysis")
    body(doc, "The existing solutions were compared on the basis of their features and "
              "drawbacks. The comparison is given in Table 2.1.")
    add_data_table(
        doc,
        ["Solution Type", "Features", "Drawbacks"],
        [
            ["Paper Based Exam",
             "No computer required; familiar to every student and teacher",
             "Checking is fully manual and slow; totalling mistakes; no strict time control; no answer review"],
            ["OMR Based Exam",
             "Fast machine checking of objective answers; useful for very large numbers of students",
             "Costly sheets and scanner; filled answers cannot be changed; no answer review; machine errors possible"],
            ["Simple Quiz Software",
             "Runs on one computer; instant score; no paper cost",
             "No timer discipline or auto-submit; no question palette; same paper for all; results are not recorded"],
            ["Online Examination System (Proposed)",
             "Random paper for every student; strict timer with auto-submit; question palette; instant result with answer review; results recorded in a file",
             "Works on one computer at a time in this version; question bank is limited to the entered questions"],
        ],
        widths=[3.6, 6.0, 6.2])
    add_para(doc, "Table 2.1: Comparison of Existing Solutions", size=11,
             italic=True, align="center", space_after=8)

    section_heading(doc, "2.4 Review Summary")
    body(doc, "From the review it is clear that paper and OMR based systems either check "
              "slowly or cost too much, simple quiz programs lack the discipline of a "
              "real examination, and full CBT platforms are too heavy for a class test. "
              "There is a clear need for a light desktop system which follows the real "
              "CBT pattern - timer, palette, random paper and instant reviewed result - "
              "and runs on a single computer without any server.")

    section_heading(doc, "2.5 Problem Definition")
    body(doc, "To design and implement a desktop online examination system in Java which "
              "conducts a timed objective examination with a randomly selected paper, a "
              "question palette, automatic submission at the end of time, and an "
              "immediate result with a complete answer review, while recording every "
              "result for the teacher.")

    section_heading(doc, "2.6 Goals/Objectives")
    body(doc, "The goals and objectives of the project are as follows:")
    objectives = [
        "To conduct the examination on the computer with a strict countdown timer and automatic submission.",
        "To give every student a different paper by random selection of questions from the question bank.",
        "To provide a question palette showing the state of every question - answered, not answered, marked for review and not visited.",
        "To show the result immediately after submission, with score, percentage, grade and pass or fail status.",
        "To give a complete answer review, so that the student can compare his answer with the correct answer for every question.",
        "To record the result of every student in a file for the teacher.",
    ]
    for o in objectives:
        bullet(doc, o)
    doc.add_page_break()

    # ------------------------------ CHAPTER 3 ------------------------------
    chapter_heading(doc, "CHAPTER 3. DESIGN FLOW/PROCESS")
    section_heading(doc, "3.1 Evaluation & Selection of Specifications/Features")
    body(doc, "Keeping the needs of the client in view, the following features were "
              "evaluated and finally selected for the system:")
    features = [
        "Student login with name and roll number, and selection of the subject before the exam starts.",
        "An instructions screen shown before the examination, so that the rules are clear to the student.",
        "A question bank of 30 questions for each subject (Java, Aptitude and General Knowledge), out of which 15 questions are selected randomly for every exam, so that every student gets a different paper.",
        "A countdown timer of 15 minutes on the exam screen, with a warning in the last minute and automatic submission when the time is over.",
        "A question palette, like the real computer based tests, which shows the state of every question in colours and allows the student to jump to any question.",
        "The facilities of Save and Next, Previous, Clear Response and Mark for Review during the exam.",
        "Immediate result on submission, with score, percentage, grade, pass or fail status and the time used.",
        "A complete answer review, in which the answer of the student is shown with the correct answer for every question.",
        "Recording of every result in a CSV file, with a Past Results screen for the teacher.",
    ]
    for f in features:
        bullet(doc, f)

    section_heading(doc, "3.2 Design Constraints")
    body(doc, "The design of the system was carried out under the following constraints:")
    constraints = [
        "The system is a desktop application. It should run on a single computer without any server and without an internet connection.",
        "Only Java and its own libraries should be used, so that no external library or paid tool is required.",
        "No database is used in this version. The question bank is kept in the program and the results are kept in a CSV file, which is sufficient for the scope of the course.",
        "Only one student gives the examination at a time on one computer.",
        "The screens should be clear and large enough to be read comfortably, because the student has to read questions on the screen for the full exam time.",
    ]
    for ctext in constraints:
        bullet(doc, ctext)

    section_heading(doc, "3.3 Analysis of Features and Finalization Subject to Constraints")
    body(doc, "The final list of features was prepared by balancing three things: the "
              "usefulness of the feature for conducting a real examination, the "
              "discipline of a computer based test, and the time available for "
              "implementation. The features of the real CBT pattern - the timer, the "
              "palette and the review - were given first priority, because they are "
              "exactly the things which are missing in simple quiz programs. One "
              "important design decision taken at this stage is that the marks are not "
              "kept or updated during the exam. During the exam, only the selected "
              "answer of every question is stored. The score, percentage and grade are "
              "calculated once, at the time of submission, from these stored answers. "
              "Because of this, there is no chance of the marks going wrong in the "
              "middle of the exam, and the same stored answers are used again for the "
              "answer review.")

    section_heading(doc, "3.4 Design Flow")
    body(doc, "Two approaches were considered for the design of the system. In the first "
              "approach, a web based design, the examination runs in a browser and a "
              "server prepares the questions and checks the answers. This approach "
              "allows many students at the same time, but it needs a server, a network "
              "and a database, and it cannot run at all without them. In the second "
              "approach, a desktop design, the complete examination runs in one "
              "application on a single computer: the question bank, the timer, the "
              "checking and the records are all inside the application itself. The "
              "second approach was selected for this project, because the system has to "
              "run in a classroom or laboratory where a server cannot be depended upon, "
              "and the complete examination works even without the internet.")
    body(doc, "The flow of one examination in the selected design is as follows. The "
              "student enters his name and roll number and selects the subject. The "
              "system shuffles the question bank of that subject and selects 15 "
              "questions. The exam screen opens with the timer running. Every answer "
              "selected by the student is stored in an answer array, and the state of "
              "every question is maintained for the palette. On submission - by the "
              "student or automatically by the timer - the stored answers are compared "
              "with the correct answers, the result is calculated and shown with the "
              "answer review, and the same result is written in the records file.")

    section_heading(doc, "3.5 Design Selection")
    body(doc, "On the basis of the above flow, the following selections were made:")
    bullet(doc, "as the programming language, because it is platform independent, fully "
                "object oriented, and its AWT and Swing libraries provide everything "
                "needed for the screens, the timer and the tables.",
           bold_prefix="Java ")
    bullet(doc, "for making the user interface. AWT provides the layouts, events, fonts "
                "and colours, and Swing provides the windows, buttons, radio buttons "
                "and tables used on the screens.",
           bold_prefix="AWT and Swing ")
    bullet(doc, "for keeping the records of results. A CSV file can be opened in any "
                "spreadsheet program, so the teacher can see and print the results "
                "easily, and no database setup is needed.",
           bold_prefix="CSV file ")
    bullet(doc, "for development, with the application finally packed as a runnable JAR "
                "file, so that it starts with a double click and does not need to be "
                "compiled again.",
           bold_prefix="JDK ")

    section_heading(doc, "3.6 Implementation Plan/Methodology")
    body(doc, "The system was implemented module by module. The working of each module "
              "is described below.")
    bullet(doc, "The student enters his name and roll number and selects the subject. "
                "If any field is empty, a warning is shown and the exam does not start. "
                "On pressing Start Exam, the instructions of the examination are shown, "
                "and the exam opens only after the student accepts them.",
           bold_prefix="Login and instructions module: ")
    bullet(doc, "This module keeps 30 objective questions of each subject, with four "
                "options and the correct answer of every question. The questions are "
                "kept in one separate class, so a teacher can change or add questions "
                "at one place only.",
           bold_prefix="Question bank module: ")
    bullet(doc, "This is the main module of the system. It prepares the paper by "
                "shuffling the question bank and selecting 15 questions. It shows one "
                "question at a time with its four options, stores the selected answer, "
                "and provides Save and Next, Previous, Clear Response and Mark for "
                "Review. The question palette shows the state of every question in "
                "colours - green for answered, red for seen but not answered, purple "
                "for marked for review and grey for not visited - and the student can "
                "jump to any question by clicking its number. The live count of every "
                "state is also shown below the palette.",
           bold_prefix="Exam engine module: ")
    bullet(doc, "A timer runs on the exam screen and counts down from 15 minutes. In "
                "the last minute, the timer turns red and a warning is shown. When the "
                "time becomes zero, the examination is submitted automatically with "
                "whatever answers are stored at that time.",
           bold_prefix="Timer module: ")
    bullet(doc, "On submission, the stored answers are compared with the correct "
                "answers. One mark is given for every correct answer and there is no "
                "negative marking. The percentage and grade are calculated from the "
                "score - 90 percent and above is A+, and 40 percent is the passing "
                "boundary. The result screen shows the score, percentage, grade, "
                "attempted and wrong counts and the time used, together with a table "
                "in which every question is shown with the answer of the student, the "
                "correct answer, and whether it was correct, wrong or skipped.",
           bold_prefix="Result and review module: ")
    bullet(doc, "Every submitted result is appended in the file data/results.csv with "
                "the date and time, the name and roll number of the student, the "
                "subject, the score, the percentage, the grade and the time used. The "
                "Past Results screen reads this file and shows all the records in a "
                "table for the teacher.",
           bold_prefix="Records module: ")

    section_heading(doc, "3.7 Prototype")
    body(doc, "The prototype of the system is fully developed and working. It contains "
              "three main screens. The Login screen takes the name and roll number of "
              "the student and the subject of the exam, and shows the instructions "
              "before starting. The Exam screen shows the question with its options, "
              "the countdown timer and the question palette. The Result screen shows "
              "the complete result with the answer review table. The system contains "
              "question banks of three subjects - Java, Aptitude and General Knowledge "
              "- with 30 questions in each bank. The module diagram of the system is "
              "shown in Figure 3.1.")
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run().add_picture(DIAGRAM2, width=Cm(15.2))
    add_para(doc, "Figure 3.1: Module Diagram of the Online Examination System",
             size=11, italic=True, align="center", space_after=6)

    body(doc, "The screenshots of the working application are shown in Figures "
              "3.2 to 3.4 below.")
    _shots = [
        ("screenshots/1-login.png", "Figure 3.2: Login Screen"),
        ("screenshots/2-exam.png", "Figure 3.3: Examination Screen with Question Palette"),
        ("screenshots/3-result.png", "Figure 3.4: Result Screen with Answer Review"),
    ]
    for _rel, _cap in _shots:
        _p = doc.add_paragraph()
        _p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        _p.add_run().add_picture(os.path.join(BASE, _rel), width=Cm(15.2))
        add_para(doc, _cap, size=11, italic=True, align="center", space_after=10)
    doc.add_page_break()

    # ------------------------------ CHAPTER 4 ------------------------------
    chapter_heading(doc, "CHAPTER 4. RESULTS & VALIDATION", underline=True)
    body(doc, "The complete system was tested after development. The main results "
              "obtained are given below:")
    results = [
        "The complete project compiled successfully and was packed as a runnable JAR file, which starts the application with a double click.",
        "On the login screen, leaving the name or roll number empty showed a warning and the exam did not start. With proper details, the instructions screen was shown and then the exam opened.",
        "Every exam received a different paper: the 15 questions were selected randomly from the bank of 30 questions of the subject, and their order was also different.",
        "The timer counted down correctly from 15:00. In the last minute it turned red with a warning, and on reaching zero the exam was submitted automatically.",
        "The question palette showed the correct state of every question at all times - the first question red (seen, not answered), answered questions green, a question marked for review purple, and the remaining questions grey. The live counts below the palette matched the palette.",
        "In a test attempt of the Java paper with 11 correct answers, 2 wrong answers and 2 questions left unattempted, the result was calculated exactly: Score 11/15, Percentage 73.3%, Grade B+, Attempted 13, Wrong 2, PASS.",
        "The answer review table showed all 15 questions with the answer of the student, the correct answer and the correct/wrong/skipped status of each question.",
        "After submission, the result was recorded in data/results.csv and the same record was seen in the Past Results screen.",
        "The question bank and marking logic were also verified separately through a console test for all three subjects, and the score, percentage and grade were found correct in every case.",
    ]
    for rtext in results:
        bullet(doc, rtext)
    add_para(doc, "", space_after=6)
    body(doc, "The summary of the test cases is given in Table 4.1.")
    add_data_table(
        doc,
        ["Test Case", "Expected Result", "Obtained Result", "Result"],
        [
            ["Login Validation", "Empty name or roll number is rejected with a warning; proper details open the instructions",
             "Warning shown on empty fields; instructions opened on proper details", "Pass"],
            ["Random Paper", "15 questions are selected randomly from the bank of 30, in a different order every time",
             "Different paper and order obtained in every attempt", "Pass"],
            ["Timer & Auto-Submit", "Timer counts down from 15:00; warning in the last minute; exam submits itself at zero",
             "Countdown, last-minute warning and automatic submission all worked", "Pass"],
            ["Question Palette", "Every question shows its correct state in colour - answered, not answered, marked, not visited",
             "All states and live counts shown correctly during the exam", "Pass"],
            ["Scoring & Grade", "11 correct, 2 wrong, 2 skipped should give 11/15, 73.3%, Grade B+, PASS",
             "Score 11/15, Percentage 73.3%, Grade B+, PASS obtained", "Pass"],
            ["Result Record", "Every submitted result is saved in the records file and visible to the teacher",
             "Result saved in data/results.csv and shown in Past Results", "Pass"],
        ],
        widths=[3.0, 5.4, 5.4, 1.6])
    add_para(doc, "Table 4.1: Test Cases and Results", size=11, italic=True,
             align="center", space_after=8)
    doc.add_page_break()

    # ------------------------------ CHAPTER 5 ------------------------------
    chapter_heading(doc, "CHAPTER 5. CONCLUSION & FUTURE IMPROVEMENTS")
    section_heading(doc, "5.1 Conclusion")
    body(doc, "In this project, an Online Examination System has been designed and "
              "implemented in Java using AWT and Swing. The system conducts the "
              "complete examination on the computer, in the same pattern as the real "
              "computer based tests. The student gets a randomly selected paper, so "
              "that no two students receive the same questions in the same order. The "
              "countdown timer enforces the time limit equally on everyone and "
              "submits the exam automatically when the time is over. The question "
              "palette keeps the student informed about every question at all times - "
              "which questions are answered, which are left, and which are marked for "
              "review. The checking is done by the system itself at the time of "
              "submission, so the mistakes of manual checking and totalling are "
              "removed completely. The student receives his result immediately, with "
              "the complete answer review, which is the most useful part for learning. "
              "The result of every student is recorded in a file for the teacher. All "
              "the objectives set for the project have been achieved and verified by "
              "testing.")

    section_heading(doc, "5.2 Future Improvements")
    body(doc, "The following improvements can be taken up in the future:")
    future = [
        "Keeping the question bank and the results in a database such as MySQL, so that questions can be managed easily and records remain safe.",
        "An admin panel inside the application, from which the teacher can add, edit and delete questions and subjects without touching the code.",
        "Facility of negative marking and different time limits for different subjects.",
        "Running the examination on many computers together through a network, with a separate login for every student.",
        "A result analysis for the teacher, showing the class average, the highest marks and the questions which most students answered wrongly.",
    ]
    for ftext in future:
        bullet(doc, ftext)

    doc.save(OUTPUT)
    print("Saved:", OUTPUT)


if __name__ == "__main__":
    build()
