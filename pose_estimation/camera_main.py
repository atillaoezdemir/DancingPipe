import cv2
import mediapipe as mp
import numpy as np
import requests
from collections import deque
import threading

# Initialize MediaPipe tools
mp_drawing = mp.solutions.drawing_utils
mp_pose = mp.solutions.pose

def calculate_angle(a, b, c):
    """
    Calculates the angle between three points in 2D space.
    
    Arguments:
    a, b, c -- lists containing the coordinates of the points (x, y).
    
    Returns:
    angle -- the angle in degrees between the lines ab and bc.
    """
    a = np.array(a)  # Convert the first point to a NumPy array
    b = np.array(b)  # Convert the middle point (vertex) to a NumPy array
    c = np.array(c)  # Convert the end point to a NumPy array
    
    # Calculate the angle between lines `ab` and `bc` using arctan2
    # `arctan2` computes the angle of the line from the point `b` to `a` and from `b` to `c`
    angle_ab = np.arctan2(a[1] - b[1], a[0] - b[0])  # Angle of line `ab`
    angle_bc = np.arctan2(c[1] - b[1], c[0] - b[0])  # Angle of line `bc`
    
    # Find the difference between the two angles to get the angle between lines `ab` and `bc`
    radians = angle_bc - angle_ab
    
    # Convert the angle from radians to degrees
    angle = np.abs(radians * 180.0 / np.pi)
    
    # Adjust angle to be in the range [0, 180] degrees
    if angle > 180.0:
        angle = 360 - angle
        
    return angle

# Dictionary to map ranges of angles to specific note values or labels
NOTE_LOOKUP_TABLE = {
    # Angle ranges for different notes
    ((135, 180), (135, 180)): "0",  # Note "0" corresponds to angles between 135 and 180 degrees for both ranges
    ((135, 180), (0, 45)): "1",    # Note "1" corresponds to angles between 135 and 180 degrees and 0 to 45 degrees
    ((135, 180), (45, 90)): "2",   # ---
    ((90, 135), (0, 180)): "3",    # ---
    ((45, 90), (0, 180)): "4",     # ---
    ((0, 45), (0, 180)): "5",      # ---
}

def determine_note(angle_elbow, angle_shoulder):
    """
    Determines the note based on elbow and shoulder angles.
    
    Arguments:
    angle_elbow -- angle at the elbow joint.
    angle_shoulder -- angle at the shoulder joint.
    
    Returns:
    note -- the corresponding note as a string.
    """
        # Iterate over each entry in the NOTE_LOOKUP_TABLE dictionary
    for ((elbow_min, elbow_max), (shoulder_min, shoulder_max)), note in NOTE_LOOKUP_TABLE.items():
        # Check if the provided angles fall within the defined ranges
        if elbow_min <= angle_elbow < elbow_max and shoulder_min <= angle_shoulder < shoulder_max:
            return note  # Return the corresponding note if ranges match
    
    # Return "404" if no matching note is found
    return "404"

def combination_result(manuale, tempo):
    """
    Calculates a combined result based on two integer inputs: `manuale` and `tempo`.
    
    Arguments:
    manuale -- an integer representing a manual input (expected in the range 0-5).
    tempo -- an integer representing a tempo input (expected in the range 0-5).
    
    Returns:
    result -- an integer calculated as (manuale - 1) * 5 + tempo, or 0 if both inputs are 0.
    """
    # Convert inputs to int
    manuale = int(manuale)
    tempo = int(tempo)

    # Special case: if both inputs are zero, return 0
    if manuale == 0 and tempo == 0:
        return 0
    # Ensure inputs are within the valid range
    if 1 <= manuale <= 5 and 1 <= tempo <= 5:
        # Calculate the result based on the given formula
        return (manuale - 1) * 5 + tempo

def map_values(noteManuale, noteTempo):
    """
    Maps 'manuale' and 'tempo' note values to corresponding commands.
    
    Arguments:
    noteManuale -- the note for 'manuale'.
    noteTempo -- the note for 'tempo'.
    
    Returns:
    mapped_noteManuale -- the command for 'manuale'.
    mapped_noteTempo -- the command for 'tempo'.
    """
    # Define mappings from note values to commands for manaule and tempo
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

    # Map the provided note values to their corresponding commands
    mapped_noteManuale = manuale_mapping.get(noteManuale, '???')
    mapped_noteTempo = tempo_mapping.get(noteTempo, '???')

    # Return on/off if manuale and tempo is 0, because is a start pose
    return ('ON/OFF' if noteManuale == '0' and noteTempo == '0' else mapped_noteManuale,
            'ON/OFF' if noteManuale == '0' and noteTempo == '0' else mapped_noteTempo)

def send_command_to_server(url, command):
    """
    Sends a command to the server at the specified URL.
    
    Arguments:
    url -- the URL of the server.
    command -- the command to be sent to the server.
    """
    try:
        # Send a POST request to the server with the command in JSON format
        requests.post(url, json={'number': command})
    except requests.exceptions.RequestException as e:
        # Print error message if an exception occurs
        print(f"Error sending notes to server: {e}")


def display_note_box(image, noteManuale, noteTempo):
    """
    Displays a note information box on the image.
    
    Arguments:
    image -- the image on which the information is displayed.
    noteManuale -- the command for 'manuale'.
    noteTempo -- the command for 'tempo'.
    """

    # Draw a filled rectangle for the note information box
    cv2.rectangle(image, (0, 0), (300, 73), (245, 117, 16), -1)

    # Add text labels and corresponding notes to the image
    cv2.putText(image, 'Manuale:', (10, 20),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 0), 1, cv2.LINE_AA)
    cv2.putText(image, noteManuale, (150, 20),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 0), 1, cv2.LINE_AA)
    cv2.putText(image, 'Tempo:', (10, 50),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 0), 1, cv2.LINE_AA)
    cv2.putText(image, noteTempo, (150, 50),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 0), 1, cv2.LINE_AA)

def process_video(camera_id):
    """
    Processes video stream from the camera and sends commands to the server based on movements.
    
    Arguments:
    camera_id -- the ID of the camera (0 for webcam, 1 for external camera).
    """
    cap = cv2.VideoCapture(camera_id) # Start the camera
    url = 'http://10.10.35.129:8080/producer' # Define the url for the server
    command_history = deque(maxlen=25) # History of comands in form of deque
    command_allowed = False
    last_command = 100
    
    # Initialize MediaPipe Pose module
    with mp_pose.Pose(min_detection_confidence=0.5, min_tracking_confidence=0.5) as pose:
        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break # Exit loop if no frame

            # Convert image to RGB (Red Green Blue)
            image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            image.flags.writeable = False

            # Process the image to get pose landmarks
            results = pose.process(image)

            image.flags.writeable = True
            image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

            try:
                # Extract pose landmarks
                landmarks = results.pose_landmarks.landmark

                # Get keypoint coordinates
                hipLeft = [landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].x, landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].y]
                hipRight = [landmarks[mp_pose.PoseLandmark.RIGHT_HIP.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_HIP.value].y]
                shoulderLeft = [landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].x, landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].y]
                shoulderRight = [landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].y]
                elbowLeft = [landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].x, landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].y]
                elbowRight = [landmarks[mp_pose.PoseLandmark.RIGHT_ELBOW.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_ELBOW.value].y]
                wristLeft = [landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].x, landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].y]
                wristRight = [landmarks[mp_pose.PoseLandmark.RIGHT_WRIST.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_WRIST.value].y]

                # Calculate angles
                angleElbowLeft = calculate_angle(shoulderLeft, elbowLeft, wristLeft)
                angleElbowRight = calculate_angle(shoulderRight, elbowRight, wristRight)
                angleShoulderLeft = calculate_angle(hipLeft, shoulderLeft, elbowLeft)
                angleShoulderRight = calculate_angle(hipRight, shoulderRight, elbowRight)

                # Determine notes and command
                noteManuale = determine_note(angleElbowRight, angleShoulderRight)
                noteTempo = determine_note(angleElbowLeft, angleShoulderLeft)
                command = combination_result(noteManuale, noteTempo)
                
                # Check if the command in allowed list
                if command in {0, 2, 3, 5, 6, 11, 16, 21}:
                    #add the allowed command to history
                    command_history.append(command)

                    # Check if the last 25 commands are all zero to toggle command state
                    if len(command_history) == 25 and all(cmd == 0 for cmd in command_history) and last_command != command:
                        command_allowed = not command_allowed
                        last_command = 0

                        # Send commands based on the toggle state
                        if command_allowed:
                            print("ready")
                            threading.Thread(target=send_command_to_server, args=(url, 0)).start()
                        else:
                            print("end")
                            threading.Thread(target=send_command_to_server, args=(url, 26)).start()
                        command_history.clear()

                    # Send command if it filled out the history and allowed
                    if command_allowed:
                        if len(command_history) == 25 and all(cmd == command_history[0] for cmd in command_history) and last_command != command:
                            command_history.clear()
                            threading.Thread(target=send_command_to_server, args=(url, command)).start()
                            last_command = command
                            print(command)
                else:
                    last_command=command
            except Exception as e:
                # Default values in case of error
                noteManuale, noteTempo = "NoN", "NoN"

            # Display note information on the image
            mapped_noteManuale, mapped_noteTempo = map_values(noteManuale, noteTempo)
            display_note_box(image, mapped_noteManuale, mapped_noteTempo)

            if results.pose_landmarks:
                # Draw pose landmarks and connections
                mp_drawing.draw_landmarks(
                    image, results.pose_landmarks, mp_pose.POSE_CONNECTIONS,
                    mp_drawing.DrawingSpec(color=(245, 117, 66), thickness=2, circle_radius=2),
                    mp_drawing.DrawingSpec(color=(245, 66, 230), thickness=2, circle_radius=2)
                )

            # Show image in a window
            cv2.imshow('Mediapipe Feed', image)

            if cv2.waitKey(10) & 0xFF == ord('q'):
                break

        # Release resources
        cap.release()
        cv2.destroyAllWindows()

if __name__ == "__main__":
    """
    Main block to start video processing.
    Prompts the user to choose a camera and starts the video processing.
    """
    print("Please write 0 for web-camera and 1 for external camera:")
    choice = int(input())
    if choice == 1:
        process_video(1)
    else:
        process_video(0)