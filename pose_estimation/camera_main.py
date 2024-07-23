import cv2
import mediapipe as mp
import numpy as np
import requests
from collections import deque
import threading

mp_drawing = mp.solutions.drawing_utils
mp_pose = mp.solutions.pose

def calculate_angle(a, b, c):
    a = np.array(a) # First
    b = np.array(b) # Mid
    c = np.array(c) # End
    
    radians = np.arctan2(c[1]-b[1], c[0]-b[0]) - np.arctan2(a[1]-b[1], a[0]-b[0])
    angle = np.abs(radians * 180.0 / np.pi)
    
    if angle > 180.0:
        angle = 360 - angle
        
    return angle

NOTE_LOOKUP_TABLE = {
    ((135, 180), (135, 180)): "0",
    ((135, 180), (  0,  45)): "1",
    ((135, 180), ( 45,  90)): "2",
    (( 90, 135), (  0, 180)): "3",
    (( 45,  90), (  0, 180)): "4",
    ((  0,  45), (  0, 180)): "5",
}

def determine_note(angle_elbow, angle_shoulder):
    for ((elbow_min, elbow_max), (shoulder_min, shoulder_max)), note in NOTE_LOOKUP_TABLE.items():
        if elbow_min <= angle_elbow < elbow_max and shoulder_min <= angle_shoulder < shoulder_max:
            return note
    return "404"

def combination_result(manuale, tempo):
    manuale = int(manuale)
    tempo = int(tempo)

    if manuale == 0 and tempo == 0:
        return 0
    
    if 1 <= manuale <= 5 and 1 <= tempo <= 5:
        return (manuale - 1) * 5 + tempo

def map_values(noteManuale, noteTempo):
    manuale_mapping = {
        '3': 'ADD',
        '2': 'DEL',
        '5': 'MAX',
        '4': 'MIN'
    }

    tempo_mapping = {
        '3': 'PLUS',
        '2': 'MINUS',
        '5': 'DEF'
    }

    mapped_noteManuale = manuale_mapping.get(noteManuale, '???')
    mapped_noteTempo = tempo_mapping.get(noteTempo, '???')

    return ('ON/OFF' if noteManuale == '0' and noteTempo == '0' else mapped_noteManuale,
            'ON/OFF' if noteManuale == '0' and noteTempo == '0' else mapped_noteTempo)

def send_notes_to_server(url, command):
    try:
        requests.post(url, json={'number': command})
    except requests.exceptions.RequestException as e:
        print(f"Error sending notes to server: {e}")

def display_note_box(image, noteManuale, noteTempo):
    cv2.rectangle(image, (0, 0), (300, 73), (245, 117, 16), -1)

    cv2.putText(image, 'Manuale:', (10, 20),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 0), 1, cv2.LINE_AA)
    cv2.putText(image, noteManuale, (150, 20),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 0), 1, cv2.LINE_AA)
    
    cv2.putText(image, 'Tempo:', (10, 50),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 0), 1, cv2.LINE_AA)
    cv2.putText(image, noteTempo, (150, 50),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 0), 1, cv2.LINE_AA)

def process_video(camera_id):
    cap = cv2.VideoCapture(camera_id)
    url = 'http://10.10.35.129:8080/producer'
    command_history = deque(maxlen=25)
    command_allowed = False
    last_command = 100
    
    with mp_pose.Pose(min_detection_confidence=0.5, min_tracking_confidence=0.5) as pose:
        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break

            image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            image.flags.writeable = False

            results = pose.process(image)

            image.flags.writeable = True
            image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

            try:
                landmarks = results.pose_landmarks.landmark

                hipLeft = [landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].x, landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].y]
                hipRight = [landmarks[mp_pose.PoseLandmark.RIGHT_HIP.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_HIP.value].y]
                shoulderLeft = [landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].x, landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].y]
                shoulderRight = [landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].y]
                elbowLeft = [landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].x, landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].y]
                elbowRight = [landmarks[mp_pose.PoseLandmark.RIGHT_ELBOW.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_ELBOW.value].y]
                wristLeft = [landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].x, landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].y]
                wristRight = [landmarks[mp_pose.PoseLandmark.RIGHT_WRIST.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_WRIST.value].y]

                angleElbowLeft = calculate_angle(shoulderLeft, elbowLeft, wristLeft)
                angleElbowRight = calculate_angle(shoulderRight, elbowRight, wristRight)
                angleShoulderLeft = calculate_angle(hipLeft, shoulderLeft, elbowLeft)
                angleShoulderRight = calculate_angle(hipRight, shoulderRight, elbowRight)

                noteManuale = determine_note(angleElbowRight, angleShoulderRight)
                noteTempo = determine_note(angleElbowLeft, angleShoulderLeft)
                command = combination_result(noteManuale, noteTempo)
                
                if command in {0, 2, 3, 5, 6, 11, 16, 21}:
                    command_history.append(command)

                    if len(command_history) == 25 and all(cmd == 0 for cmd in command_history) and last_command != command:
                        command_allowed = not command_allowed
                        last_command = 0

                        if command_allowed:
                            print("ready")
                            threading.Thread(target=send_notes_to_server, args=(url, 0)).start()
                        else:
                            print("end")
                            threading.Thread(target=send_notes_to_server, args=(url, 26)).start()
                        command_history.clear()

                    if command_allowed:
                        if len(command_history) == 25 and all(cmd == command_history[0] for cmd in command_history) and last_command != command:
                            command_history.clear()
                            threading.Thread(target=send_notes_to_server, args=(url, command)).start()
                            last_command = command
                            print(command)
                else:
                    last_command = command
                    
            except Exception as e:
                noteManuale, noteTempo = "NoN", "NoN"

            mapped_noteManuale, mapped_noteTempo = map_values(noteManuale, noteTempo)
            display_note_box(image, mapped_noteManuale, mapped_noteTempo)

            if results.pose_landmarks:
                mp_drawing.draw_landmarks(
                    image, results.pose_landmarks, mp_pose.POSE_CONNECTIONS,
                    mp_drawing.DrawingSpec(color=(245, 117, 66), thickness=2, circle_radius=2),
                    mp_drawing.DrawingSpec(color=(245, 66, 230), thickness=2, circle_radius=2)
                )

            cv2.imshow('Mediapipe Feed', image)

            if cv2.waitKey(10) & 0xFF == ord('q'):
                break

        cap.release()
        cv2.destroyAllWindows()

if __name__ == "__main__":
    print("Please write 0 for web-camera and 1 for external camera:")
    choice = int(input())
    if choice==1:
        process_video(1)
    else:
        process_video(0)

