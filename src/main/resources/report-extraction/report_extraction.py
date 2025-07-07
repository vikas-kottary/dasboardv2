import os
import fitz  # PyMuPDF
import re
import json
from datetime import datetime

team_lookup = {
    "4Pay": {"teamId": 1, "managerId": 1, "client": "Ashish Garg", "opcoId": 1},
    "Air Riders": {"teamId": 2, "managerId": 2, "client": "Allegiant", "opcoId": 1},
    "ByteAlmighty": {"teamId": 3, "managerId": 3, "client": "Allegiant", "opcoId": 2},
    "CAT": {"teamId": 4, "managerId": 4, "client": "Allegiant", "opcoId": 1},
    "DevOps": {"teamId": 5, "managerId": 5, "client": "Allegiant", "opcoId": 3},
    "Incredibles": {"teamId": 6, "managerId": 6, "client": "Allegiant", "opcoId": 2},
    "Isengard": {"teamId": 7, "managerId": 7, "client": "Allegiant", "opcoId": 3},
    "Partywolves": {"teamId": 8, "managerId": 8, "client": "Allegiant", "opcoId": 2},
    "Phoenix": {"teamId": 9, "managerId": 9, "client": "Allegiant", "opcoId": 2},
    "AGT Release Automation": {"teamId": 10, "managerId": 10, "client": "Allegiant", "opcoId": 3},
    "Rocketeers": {"teamId": 11, "managerId": 11, "client": "Allegiant", "opcoId": 2},
    "Speedwagon": {"teamId": 12, "managerId": 12, "client": "Allegiant", "opcoId": 2},
    "SRE Dashboard": {"teamId": 13, "managerId": 13, "client": "Allegiant", "opcoId": 3},
    "Trio Cloud": {"teamId": 14, "managerId": 14, "client": "Trio Cloud", "opcoId": 1},
    "Tech Phantoms": {"teamId": 15, "managerId": 15, "client": "Allegiant", "opcoId": 1},
    "Vortex": {"teamId": 16, "managerId": 16, "client": "Allegiant", "opcoId": 1},
    "Zero Gravity": {"teamId": 17, "managerId": 17, "client": "Allegiant", "opcoId": 1},
    "FLSmidth Cement India LLP": {"teamId": 18, "managerId": 18, "client": "FLSmidth Cement India LLP", "opcoId": 1},
    "FLSmidth Pvt Ltd": {"teamId": 19, "managerId": 18, "client": "FLSmidth Pvt Ltd", "opcoId": 1},
    "Laufer": {"teamId": 20, "managerId": 19, "client": "Laufer Group International", "opcoId": 3},
    "PNW": {"teamId": 21, "managerId": 20, "client": "PNW", "opcoId": 4},
    "PrincessHouse": {"teamId": 22, "managerId": 21, "client": "PrincessHouse", "opcoId": 5},
    "Teesnap": {"teamId": 23, "managerId": 22, "client": "Teesnap", "opcoId": 4},
    "Wright Flood": {"teamId": 24, "managerId": 23, "client": "Wright Flood", "opcoId": 4}
}

rag_map = {"S": 1, "A": 2, "G": 2, "Y": 3, "R": 4}


def parse_dates_from_filename(filename):
    match = re.search(r'(\d{2})-([A-Z]{3})-(\d{2}) to (\d{2})-([A-Z]{3})-(\d{2})', filename)
    if match:
        start = datetime.strptime(match.group(1) + match.group(2) + match.group(3), "%d%b%y").date().isoformat()
        end = datetime.strptime(match.group(4) + match.group(5) + match.group(6), "%d%b%y").date().isoformat()
        return start, end
    return None, None

def get_rag_code(char):
    return 2 #rag_map.get(char.strip(), 2)  # Default to green if unknown

def extract_pdf_text(pdf_path):
    doc = fitz.open(pdf_path)
    return "\n".join([page.get_text() for page in doc])

def extract_between(text, start_label, end_label=None):
    start_idx = text.find(start_label)
    if start_idx == -1:
        return ""
    start_idx += len(start_label)
    if end_label:
        end_idx = text.find(end_label, start_idx)
        response =  text[start_idx:end_idx].strip() if end_idx != -1 else text[start_idx:].strip()
        return response
    res = text[start_idx:].strip()
    return res

def extract_between_strip(text, start_label, end_label=None):
    start_idx = text.find(start_label)
    if start_idx == -1:
        return ""
    start_idx += len(start_label)
    if end_label:
        end_idx = text.find(end_label, start_idx)
        response =  text[start_idx:end_idx].strip() if end_idx != -1 else text[start_idx:].strip()
        if "\n" in response:
            response = response.split("\n")[0].strip()
        return response
    res = text[start_idx:].strip()
    if "\n" in res:
        res = res.split("\n")[0].strip()
    return res

def parse_pdf(pdf_path):
    filename = os.path.basename(pdf_path)
    text = extract_pdf_text(pdf_path)

    # Team Identification
    team_name = extract_between(text, "Team Name", "Team Manager").strip()
    team_data = team_lookup.get(team_name)
    if not team_data:
        print(f"[WARN] Skipping unknown team '{team_name}' in file {filename}")
        return None

    start_date, end_date = parse_dates_from_filename(filename)
    rag_default = 2 #get_rag_code("A")  # Most sections are 'A' unless overridden

    # Milestones
    milestone_pattern = re.findall(r"(\d+)\s+(.*?)\s+(.*?)\s+(2025-\d{2}-\d{2})\s+([A-Z])", text)
    milestones = []
    for i, (seq, name, detail, date, status) in enumerate(milestone_pattern):
        if name.lower() != "none":
            milestones.append({
                "sequenceNo": int(seq),
                "projectName": name.strip(),
                "detail": detail.strip(),
                "milestoneDate": date.strip(),
                "ragStatusId": get_rag_code(status)
            })

    # Improvements
    improvements_block = extract_between(text, "Value Addition", "Non Adherence")
    improvements_lines = [line.strip() for line in improvements_block.splitlines() if line.strip()]
    improvements = []
    i = 1
    idx = 1  # skip the first line (usually 'A')
    while idx + 2 < len(improvements_lines):
        try:
            seq = int(improvements_lines[idx])
            area = improvements_lines[idx + 1]
            value_addition = improvements_lines[idx + 2]
            improvements.append({
                "sequenceNo": seq,
                "area": area,
                "valueAddition": value_addition,
                "ragStatusId": rag_default
            })
            idx += 3
        except Exception:
            break

    # Training
    training_block = extract_between(text, "Total Training Hours", "Track Billability*")
    training_lines = [line.strip() for line in training_block.splitlines() if line.strip()]
    if len(training_lines) > 1:
        training_details = training_lines[1]  # skip the first (usually 'A')
        total_hours = int(training_lines[2]) if len(training_lines) > 2 and training_lines[2].isdigit() else 0
    else:
        training_details = ""
        total_hours = 0

    # Billability
    bill_block = extract_between(text, "Overall Billability (%)", "Process")
    bill_lines = [line.strip() for line in bill_block.splitlines() if line.strip()]
    if len(bill_lines) > 5:
        # skip the first line (usually 'A')
        billed_resources = float(bill_lines[1])
        unbilled_resources = float(bill_lines[2])
        leaves_billed = float(bill_lines[3])
        leaves_unbilled = float(bill_lines[4])
        holidays = int(bill_lines[5])
        overall_billability_percent = float(bill_lines[6]) if len(bill_lines) > 6 else 0.0
    else:
        billed_resources = 0
        unbilled_resources = 0.0
        leaves_billed = 0.0
        leaves_unbilled = 0.0
        holidays = 0
        overall_billability_percent = 0.0

    # Timesheets
    timesheets_block = extract_between(text, "No Of Defaulters In ERP", "Innovation")
    timesheets_lines = [line.strip() for line in timesheets_block.splitlines() if line.strip()]
    client_defaulters = int(timesheets_lines[1]) if len(timesheets_lines) > 1 and timesheets_lines[1].isdigit() else 0
    erp_defaulters = int(timesheets_lines[2]) if len(timesheets_lines) > 2 and timesheets_lines[2].isdigit() else 0

    # Workload Visibility (Weeks)
    workload_visibility_value = extract_between(text, "Team Workload Visibility", "Adequate Quality").strip()
    if "\n" in workload_visibility_value:
        workload_visibility_value = workload_visibility_value.split("\n")[0].strip()
    if not workload_visibility_value:
        workload_visibility_value = "N/A"

    # Escalations
    escalations_text = extract_between(text, "Details If applicable", "People")
    # Split by lines, remove empty, and ignore the first word/letter
    esc_lines = [line.strip() for line in escalations_text.splitlines() if line.strip()]
    if len(esc_lines) > 1:
        has_escalation = esc_lines[1].strip().lower() in ("yes", "true", "y", "1")
        details = esc_lines[2] if len(esc_lines) > 2 else ""
    else:
        has_escalation = ""
        details = ""

    # Non Adherence
    nonadherence_block = extract_between(text, "Time To Resolve", "Timesheet Timelines*")
    nonadherence_lines = [line.strip() for line in nonadherence_block.splitlines() if line.strip()]
    nonadherence_value = nonadherence_lines[1] if len(nonadherence_lines) > 1 else ""
    count = int(nonadherence_lines[2]) if len(nonadherence_lines) > 2 and nonadherence_lines[2].isdigit() else 0
    impact = nonadherence_lines[3] if len(nonadherence_lines) > 3 else ""
    time_to_resolve = nonadherence_lines[4] if len(nonadherence_lines) > 4 else ""

    # Innovation
    innovation_block = extract_between(text, "Value Added", "Risk")
    innovation_lines = [line.strip() for line in innovation_block.splitlines() if line.strip()]
    if len(innovation_lines) > 1:
        details = " ".join(innovation_lines[1:-1]) if len(innovation_lines) > 2 else innovation_lines[1]
        value_added = innovation_lines[-1]
    else:
        details = ""
        value_added = ""

    # Risk
    risk_block = extract_between(text, "Details of the risk", "Showcase")
    risk_lines = [line.strip() for line in risk_block.splitlines() if line.strip()]
    if len(risk_lines) > 1:
        risk_value = risk_lines[1]
        risk_details = risk_lines[2] if len(risk_lines) > 2 else ""
    else:
        risk_value = ""
        risk_details = ""

    # Showcases
    showcases_block = extract_between(text, "Showcase", "Query & Feedback - Click Here")
    showcases_lines = [line.strip() for line in showcases_block.splitlines() if line.strip()]
    showcases = []
    idx = 0
    while idx < len(showcases_lines):
        if showcases_lines[idx].isdigit():
            seq_no = int(showcases_lines[idx])
            detail = ""
            # Check if next line exists and is not another sequence number
            if idx + 1 < len(showcases_lines) and not showcases_lines[idx + 1].isdigit():
                detail = showcases_lines[idx + 1]
                idx += 1
            showcases.append({"sequenceNo": seq_no, "detail": detail})
        idx += 1

    # JSON Structure
    return {
        "teamId": team_data["teamId"],
        "managerId": team_data["managerId"],
        "clientName": team_data["client"],
        "opcoId": team_data["opcoId"],
        "startDate": start_date,
        "endDate": end_date,
        "milestones": milestones,
        "workloadVisibility": {
            "value": workload_visibility_value,
            "ragStatusId": rag_default
        },
        "adequateQuality": {
            "value": extract_between_strip(text, "Quality of Deliverables", "Escalations"),
            "ragStatusId": rag_default
        },
        "escalations": {
            "hasEscalation": has_escalation,
            "details": details,
            "ragStatusId": rag_default
        },
        "trainings": {
            "trainingDetails": training_details,
            "totalHours": total_hours,
            "ragStatusId": rag_default
        },
        "billability": {
            "billedResources": billed_resources,
            "unbilledResources": unbilled_resources,
            "leavesBilled": leaves_billed,
            "leavesUnbilled": leaves_unbilled,
            "holidays": holidays,
            "overallBillabilityPercent": overall_billability_percent,
            "ragStatusId": rag_default
        },
        "improvements": improvements,
        "nonAdherence": {
            "nonAdherenceValue": nonadherence_value,
            "count": count,
            "impact": impact,
            "timeToResolve": time_to_resolve,
            "ragStatusId": rag_default
        },
        "timesheets": {
            "clientDefaulters": client_defaulters,
            "erpDefaulters": erp_defaulters,
            "ragStatusId": rag_default
        },
        "innovation": {
            "details": details,
            "valueAdded": value_added,
            "ragStatusId": rag_default
        },
        "risk": {
            "riskValue": risk_value,
            "details": risk_details,
            "ragStatusId": rag_default
        },
        "showcases": showcases
    }




def process_all_dashboards(input_folder="dashboards", output_folder="dashboards/output"):
    os.makedirs(output_folder, exist_ok=True)
    for file in os.listdir(input_folder):
        if file.endswith(".pdf"):
            path = os.path.join(input_folder, file)
            print(f"Processing {file}...")
            data = parse_pdf(path)
            if data:
                output_path = os.path.join(output_folder, file.replace(".pdf", "_request.json"))
                with open(output_path, "w") as f:
                    json.dump(data, f, indent=2)
                print(f"Saved → {output_path}")
            else:
                print(f"Skipped → {file}")


if __name__ == "__main__":
    process_all_dashboards()