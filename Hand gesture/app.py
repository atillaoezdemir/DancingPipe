import csv
import copy
import argparse
import itertools
from collections import Counter
from collections import deque

import cv2 as cv
import numpy as np
import mediapipe as mp

from utils import CvFpsCalc
from model import KeyPointClassifier
from model import PointHistoryClassifier

import os

def get_args():
    """
    Parses command line arguments for the hand gesture recognition script.

    Returns:
        argparse.Namespace: A namespace object containing the parsed arguments.
    """
    parser = argparse.ArgumentParser()

    # Device ID for the camera (default is 0 for the first available camera)
    parser.add_argument("--device", type=int, default=0)
    # Width of the video capture frame (default is 960 pixels)
    parser.add_argument("--width", help='Capture width', type=int, default=960)
    # Height of the video capture frame (default is 540 pixels)
    parser.add_argument("--height", help='Capture height', type=int, default=540)

    # Flag to use static image mode for the hand tracking model (default is False)
    parser.add_argument('--use_static_image_mode', action='store_true')
    # Minimum detection confidence for the hand tracking model (default is 0.7)
    parser.add_argument("--min_detection_confidence", help='Minimum detection confidence', type=float, default=0.7)
    # Minimum tracking confidence for the hand tracking model (default is 0.5)
    parser.add_argument("--min_tracking_confidence", help='Minimum tracking confidence', type=float, default=0.5)

    args = parser.parse_args()

    return args

def main():
    """
    Main function to perform hand gesture recognition using the webcam feed.
    """
    # Argument parsing
    args = get_args()

    # Extract arguments
    cap_device = args.device
    cap_width = args.width
    cap_height = args.height
    use_static_image_mode = args.use_static_image_mode
    min_detection_confidence = args.min_detection_confidence
    min_tracking_confidence = args.min_tracking_confidence

    use_brect = True  # Flag to use bounding rectangle for drawing

    # Camera preparation
    cap = cv.VideoCapture(cap_device)
    cap.set(cv.CAP_PROP_FRAME_WIDTH, cap_width)
    cap.set(cv.CAP_PROP_FRAME_HEIGHT, cap_height)

    # Load MediaPipe Hands model
    mp_hands = mp.solutions.hands
    hands = mp_hands.Hands(
        static_image_mode=use_static_image_mode,
        max_num_hands=2,
        min_detection_confidence=min_detection_confidence,
        min_tracking_confidence=min_tracking_confidence,
    )

    # Initialize classifiers
    keypoint_classifier = KeyPointClassifier()
    point_history_classifier = PointHistoryClassifier()

    # Load label files for classifiers
    with open('model/keypoint_classifier/keypoint_classifier_label.csv', encoding='utf-8-sig') as f:
        keypoint_classifier_labels = csv.reader(f)
        keypoint_classifier_labels = [row[0] for row in keypoint_classifier_labels]
    
    with open('model/point_history_classifier/point_history_classifier_label.csv', encoding='utf-8-sig') as f:
        point_history_classifier_labels = csv.reader(f)
        point_history_classifier_labels = [row[0] for row in point_history_classifier_labels]

    # Initialize FPS calculator
    cvFpsCalc = CvFpsCalc(buffer_len=10)

    # Initialize coordinate history buffers
    history_length = 16
    point_history = deque(maxlen=history_length)
    finger_gesture_history = deque(maxlen=history_length)

    mode = 0

    while True:
        fps = cvFpsCalc.get()

        # Process key input (ESC to exit)
        key = cv.waitKey(10)
        if key == 27:  # ESC key
            break
        number, mode = select_mode(key, mode)

        # Capture frame from camera
        ret, image = cap.read()
        if not ret:
            break
        image = cv.flip(image, 1)  # Mirror the image for a natural view
        debug_image = copy.deepcopy(image)

        # Convert the image to RGB and process with MediaPipe Hands model
        image = cv.cvtColor(image, cv.COLOR_BGR2RGB)
        image.flags.writeable = False
        results = hands.process(image)
        image.flags.writeable = True

        if results.multi_hand_landmarks is not None:
            for hand_landmarks, handedness in zip(results.multi_hand_landmarks, results.multi_handedness):
                # Calculate bounding rectangle and landmark list
                brect = calc_bounding_rect(debug_image, hand_landmarks)
                landmark_list = calc_landmark_list(debug_image, hand_landmarks)

                # Pre-process landmarks and point history
                pre_processed_landmark_list = pre_process_landmark(landmark_list)
                pre_processed_point_history_list = pre_process_point_history(debug_image, point_history)

                # Log data to CSV
                logging_csv(number, mode, pre_processed_landmark_list, pre_processed_point_history_list)

                # Classify hand sign
                hand_sign_id = keypoint_classifier(pre_processed_landmark_list)
                if hand_sign_id == 2:  # Point gesture detected
                    point_history.append(landmark_list[8])
                else:
                    point_history.append([0, 0])

                # Classify finger gesture based on point history
                finger_gesture_id = 0
                point_history_len = len(pre_processed_point_history_list)
                if point_history_len == (history_length * 2):
                    finger_gesture_id = point_history_classifier(pre_processed_point_history_list)

                # Update finger gesture history
                finger_gesture_history.append(finger_gesture_id)
                most_common_fg_id = Counter(finger_gesture_history).most_common()

                # Draw results on the image
                debug_image = draw_bounding_rect(use_brect, debug_image, brect)
                debug_image = draw_landmarks(debug_image, landmark_list)
                debug_image = draw_info_text(
                    debug_image,
                    brect,
                    handedness,
                    keypoint_classifier_labels[hand_sign_id],
                    point_history_classifier_labels[most_common_fg_id[0][0]],
                )
        else:
            point_history.append([0, 0])

        # Draw point history and additional information on the image
        debug_image = draw_point_history(debug_image, point_history)
        debug_image = draw_info(debug_image, fps, mode, number)

        # Display the image
        cv.imshow('Hand Gesture Recognition', debug_image)

    # Release resources
    cap.release()
    cv.destroyAllWindows()

def select_mode(key, mode):
    """
    Determines the mode based on the key pressed and returns the updated mode and number.

    Args:
        key (int): ASCII code of the key pressed. Represents the input from the keyboard.
        mode (int): Current mode value that will be updated based on the key pressed.

    Returns:
        tuple: A tuple containing:
            - number (int): The number corresponding to the key if it is a digit (0-9); otherwise -1.
            - mode (int): The updated mode based on the key pressed:
                - Mode 0: Set when 'n' (ASCII code 110) is pressed.
                - Mode 1: Set when 'k' (ASCII code 107) is pressed.
                - Mode 2: Set when 'h' (ASCII code 104) is pressed.
                - Any other key does not change the mode.
    """
    number = -1
    if 48 <= key <= 57:  # ASCII codes for digits 0 through 9 ('0' = 48, '9' = 57)
        number = key - 48
    if key == 110:  # ASCII code for 'n'
        mode = 0  # Set mode to 0 when 'n' is pressed
    elif key == 107:  # ASCII code for 'k'
        mode = 1  # Set mode to 1 when 'k' is pressed
    elif key == 104:  # ASCII code for 'h'
        mode = 2  # Set mode to 2 when 'h' is pressed
    return number, mode


def calc_bounding_rect(image, landmarks):
    """
    Calculates the bounding rectangle that encloses all the landmarks in the image.

    Args:
        image (numpy.ndarray): The image on which landmarks are drawn, used to determine the dimensions.
        landmarks (object): An object containing landmark data with attributes x and y (normalized coordinates).

    Returns:
        list: A list containing the coordinates of the bounding rectangle in the form [x_min, y_min, x_max, y_max].
            - [x_min, y_min]: Coordinates of the top-left corner of the bounding rectangle.
            - [x_max, y_max]: Coordinates of the bottom-right corner of the bounding rectangle.
    """
    image_width, image_height = image.shape[1], image.shape[0]

    landmark_array = np.empty((0, 2), int)

    for _, landmark in enumerate(landmarks.landmark):
        # Convert normalized coordinates (0 to 1) to pixel values
        landmark_x = min(int(landmark.x * image_width), image_width - 1)
        landmark_y = min(int(landmark.y * image_height), image_height - 1)

        landmark_point = [np.array((landmark_x, landmark_y))]

        landmark_array = np.append(landmark_array, landmark_point, axis=0)

    # Calculate bounding rectangle
    x, y, w, h = cv.boundingRect(landmark_array)

    return [x, y, x + w, y + h]


def calc_landmark_list(image, landmarks):
    """
    Converts landmarks from normalized coordinates to pixel coordinates.

    Args:
        image (numpy.ndarray): The image on which landmarks are drawn, used to determine the dimensions.
        landmarks (object): An object containing landmark data with attributes x and y (normalized coordinates).

    Returns:
        list: A list of landmark coordinates in pixel values, where each coordinate is in the form [x, y].
            - Each coordinate is a list [x, y] where x and y are pixel values.
    """
    image_width, image_height = image.shape[1], image.shape[0]

    landmark_point = []

    for _, landmark in enumerate(landmarks.landmark):
        # Convert normalized coordinates to pixel values
        landmark_x = min(int(landmark.x * image_width), image_width - 1)
        landmark_y = min(int(landmark.y * image_height), image_height - 1)
        
        landmark_point.append([landmark_x, landmark_y])

    return landmark_point


def pre_process_landmark(landmark_list):
    """
    Pre-processes the list of landmarks for further analysis.

    This includes converting landmarks to relative coordinates based on the first landmark,
    normalizing them to the range [0, 1], and flattening the list into a one-dimensional array.

    Args:
        landmark_list (list): A list of landmark coordinates in pixel values, where each coordinate is in the form [x, y].

    Returns:
        list: A flattened and normalized list of relative landmark coordinates.
            - The list is in the form of [x1, y1, x2, y2, ..., xn, yn] where each coordinate is normalized.
    """
    temp_landmark_list = copy.deepcopy(landmark_list)

    # Convert to relative coordinates based on the first landmark
    base_x, base_y = 0, 0
    for index, landmark_point in enumerate(temp_landmark_list):
        if index == 0:
            base_x, base_y = landmark_point[0], landmark_point[1]

        temp_landmark_list[index][0] = temp_landmark_list[index][0] - base_x
        temp_landmark_list[index][1] = temp_landmark_list[index][1] - base_y

    # Flatten the list of coordinates (convert 2D list to 1D list)
    temp_landmark_list = list(itertools.chain.from_iterable(temp_landmark_list))

    # Normalize coordinates to the range [0, 1]
    max_value = max(list(map(abs, temp_landmark_list)))
    
    def normalize_(n):
        return n / max_value

    temp_landmark_list = list(map(normalize_, temp_landmark_list))

    return temp_landmark_list


def pre_process_point_history(image, point_history):
    """
    Converts a list of point history into relative coordinates and normalizes them.

    The coordinates of points in `point_history` are converted into relative values with respect 
    to the first point and then normalized according to the width and height of the image. 
    
    Args:
        image (numpy.ndarray): The image on which the points were recorded. Used to get the image dimensions.
        point_history (list): A list of point history in the format [[x1, y1], [x2, y2], ...], where each point is in pixels.

    Returns:
        list: A one-dimensional list of normalized point coordinates in the format [x1, y1, x2, y2, ...].
            - Coordinates are normalized by the image dimensions and converted to relative values.
    """
    image_width, image_height = image.shape[1], image.shape[0]

    temp_point_history = copy.deepcopy(point_history)

    # Convert to relative coordinates
    base_x, base_y = 0, 0
    for index, point in enumerate(temp_point_history):
        if index == 0:
            base_x, base_y = point[0], point[1]

        temp_point_history[index][0] = (temp_point_history[index][0] - base_x) / image_width
        temp_point_history[index][1] = (temp_point_history[index][1] - base_y) / image_height

    # Convert to a one-dimensional list
    temp_point_history = list(itertools.chain.from_iterable(temp_point_history))

    return temp_point_history


def logging_csv(number, mode, landmark_list, point_history_list):
    """
    Logs data to a CSV file depending on the current mode and number.

    Based on the value of `mode`, this function writes either keypoint data (if `mode` is 1) 
    or point history data (if `mode` is 2) to the respective CSV file.

    Args:
        number (int): The number corresponding to the key (0-9). Written to the file along with the data.
        mode (int): The operation mode that determines the data format to log:
            - Mode 0: No logging is performed.
            - Mode 1: Logs keypoint data to 'keypoint.csv'.
            - Mode 2: Logs point history data to 'point_history.csv'.
        landmark_list (list): List of keypoint coordinates in the format [x1, y1, x2, y2, ...].
            Used if `mode` is 1.
        point_history_list (list): List of point history coordinates in the format [x1, y1, x2, y2, ...].
            Used if `mode` is 2.

    Returns:
        None
    """
    if mode == 0:
        pass  # No logging is performed for mode 0
    if mode == 1 and (0 <= number <= 9):
        csv_path = 'model/keypoint_classifier/keypoint.csv'
        with open(csv_path, 'a', newline="") as f:
            writer = csv.writer(f)
            writer.writerow([number, *landmark_list])
    if mode == 2 and (0 <= number <= 9):
        csv_path = 'model/point_history_classifier/point_history.csv'
        with open(csv_path, 'a', newline="") as f:
            writer = csv.writer(f)
            writer.writerow([number, *point_history_list])

    return


def draw_landmarks(image, landmark_point):
    """
    Draws landmarks on the image.

    This function draws lines connecting landmarks to represent the hand structure, including 
    thumb, index finger, middle finger, ring finger, and little finger. It also draws circles 
    at each landmark point for better visualization.

    Args:
        image (numpy.ndarray): The image on which the landmarks will be drawn.
        landmark_point (list): List of landmark coordinates in the format [[x1, y1], [x2, y2], ...].

    Returns:
        numpy.ndarray: The image with the landmarks drawn on it.
    """
    if len(landmark_point) > 0:
        # Thumb
        cv.line(image, tuple(landmark_point[2]), tuple(landmark_point[3]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[2]), tuple(landmark_point[3]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[3]), tuple(landmark_point[4]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[3]), tuple(landmark_point[4]),
                (255, 255, 255), 2)

        # Index finger
        cv.line(image, tuple(landmark_point[5]), tuple(landmark_point[6]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[5]), tuple(landmark_point[6]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[6]), tuple(landmark_point[7]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[6]), tuple(landmark_point[7]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[7]), tuple(landmark_point[8]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[7]), tuple(landmark_point[8]),
                (255, 255, 255), 2)

        # Middle finger
        cv.line(image, tuple(landmark_point[9]), tuple(landmark_point[10]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[9]), tuple(landmark_point[10]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[10]), tuple(landmark_point[11]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[10]), tuple(landmark_point[11]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[11]), tuple(landmark_point[12]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[11]), tuple(landmark_point[12]),
                (255, 255, 255), 2)

        # Ring finger
        cv.line(image, tuple(landmark_point[13]), tuple(landmark_point[14]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[13]), tuple(landmark_point[14]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[14]), tuple(landmark_point[15]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[14]), tuple(landmark_point[15]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[15]), tuple(landmark_point[16]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[15]), tuple(landmark_point[16]),
                (255, 255, 255), 2)

        # Little finger
        cv.line(image, tuple(landmark_point[17]), tuple(landmark_point[18]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[17]), tuple(landmark_point[18]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[18]), tuple(landmark_point[19]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[18]), tuple(landmark_point[19]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[19]), tuple(landmark_point[20]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[19]), tuple(landmark_point[20]),
                (255, 255, 255), 2)

        # Palm
        cv.line(image, tuple(landmark_point[0]), tuple(landmark_point[1]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[0]), tuple(landmark_point[1]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[1]), tuple(landmark_point[2]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[1]), tuple(landmark_point[2]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[2]), tuple(landmark_point[5]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[2]), tuple(landmark_point[5]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[5]), tuple(landmark_point[9]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[5]), tuple(landmark_point[9]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[9]), tuple(landmark_point[13]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[9]), tuple(landmark_point[13]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[13]), tuple(landmark_point[17]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[13]), tuple(landmark_point[17]),
                (255, 255, 255), 2)
        cv.line(image, tuple(landmark_point[17]), tuple(landmark_point[0]),
                (0, 0, 0), 6)
        cv.line(image, tuple(landmark_point[17]), tuple(landmark_point[0]),
                (255, 255, 255), 2)

    # Key Points
    for index, landmark in enumerate(landmark_point):
        if index == 0:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 1:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 2:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 3:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 4:
            cv.circle(image, (landmark[0], landmark[1]), 8, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 8, (0, 0, 0), 1)
        if index == 5:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 6:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 7:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 8:
            cv.circle(image, (landmark[0], landmark[1]), 8, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 8, (0, 0, 0), 1)
        if index == 9:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 10:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 11:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 12:
            cv.circle(image, (landmark[0], landmark[1]), 8, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 8, (0, 0, 0), 1)
        if index == 13:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 14:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 15:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 16:
            cv.circle(image, (landmark[0], landmark[1]), 8, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 8, (0, 0, 0), 1)
        if index == 17:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 18:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 19:
            cv.circle(image, (landmark[0], landmark[1]), 5, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 5, (0, 0, 0), 1)
        if index == 20:
            cv.circle(image, (landmark[0], landmark[1]), 8, (255, 255, 255),
                      -1)
            cv.circle(image, (landmark[0], landmark[1]), 8, (0, 0, 0), 1)

    return image

def draw_bounding_rect(use_brect, image, brect):
    """
    Draws a bounding rectangle around the detected hand area if specified.

    Args:
        use_brect (bool): Flag to determine whether to draw the bounding rectangle.
        image (numpy.ndarray): The image on which to draw the bounding rectangle.
        brect (list): The coordinates of the bounding rectangle in the format [x1, y1, x2, y2].

    Returns:
        numpy.ndarray: The image with the bounding rectangle drawn, if `use_brect` is True.
    """
    if use_brect:
        # Draw the outer rectangle around the detected hand area
        cv.rectangle(image, (brect[0], brect[1]), (brect[2], brect[3]),
                     (0, 0, 0), 1)

    return image


def draw_info_text(image, brect, handedness, hand_sign_text,
                   finger_gesture_text):
    """
    Draws text information on the image, including the handedness, hand sign, and finger gestures.

    Args:
        image (numpy.ndarray): The image on which to draw the information text.
        brect (list): The coordinates of the bounding rectangle in the format [x1, y1, x2, y2].
        handedness (object): Object containing the classification label for handedness.
        hand_sign_text (str): The text representing the detected hand sign.
        finger_gesture_text (str): The text representing the detected finger gestures.

    Returns:
        numpy.ndarray: The image with the information text drawn on it.
    """
    # Draw background for text
    cv.rectangle(image, (brect[0], brect[1]), (brect[2], brect[1] - 22),
                 (0, 0, 0), -1)

    # Draw handedness and hand sign text
    info_text = handedness.classification[0].label[0:]
    if hand_sign_text != "":
        info_text = info_text + ':' + hand_sign_text
    cv.putText(image, info_text, (brect[0] + 5, brect[1] - 4),
               cv.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1, cv.LINE_AA)

    # Draw finger gesture text if provided
    if finger_gesture_text != "":
        cv.putText(image, "Finger Gesture:" + finger_gesture_text, (10, 60),
                   cv.FONT_HERSHEY_SIMPLEX, 1.0, (0, 0, 0), 4, cv.LINE_AA)
        cv.putText(image, "Finger Gesture:" + finger_gesture_text, (10, 60),
                   cv.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 2,
                   cv.LINE_AA)

    return image


def draw_point_history(image, point_history):
    """
    Draws a history of points on the image, with varying sizes and colors.

    Args:
        image (numpy.ndarray): The image on which to draw the point history.
        point_history (list): A list of point coordinates in the format [[x1, y1], [x2, y2], ...].

    Returns:
        numpy.ndarray: The image with the point history drawn on it.
    """
    for index, point in enumerate(point_history):
        if point[0] != 0 and point[1] != 0:
            # Draw each point with increasing size and a green color
            cv.circle(image, (point[0], point[1]), 1 + int(index / 2),
                      (152, 251, 152), 2)

    return image


def draw_info(image, fps, mode, number):
    """
    Draws various information on the image including FPS, mode, and number.

    Args:
        image (numpy.ndarray): The image on which to draw the information.
        fps (float): The current frames per second.
        mode (int): The current mode (1 for logging key points, 2 for logging point history).
        number (int): The number associated with the current mode (0-9).

    Returns:
        numpy.ndarray: The image with the information drawn on it.
    """
    # Draw FPS
    cv.putText(image, "FPS:" + str(fps), (10, 30), cv.FONT_HERSHEY_SIMPLEX,
               1.0, (0, 0, 0), 4, cv.LINE_AA)
    cv.putText(image, "FPS:" + str(fps), (10, 30), cv.FONT_HERSHEY_SIMPLEX,
               1.0, (255, 255, 255), 2, cv.LINE_AA)

    # Draw mode information
    mode_string = ['Logging Key Point', 'Logging Point History']
    if 1 <= mode <= 2:
        cv.putText(image, "MODE:" + mode_string[mode - 1], (10, 90),
                   cv.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1,
                   cv.LINE_AA)
        # Draw number information if valid
        if 0 <= number <= 9:
            cv.putText(image, "NUM:" + str(number), (10, 110),
                       cv.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1,
                       cv.LINE_AA)

    return image


if __name__ == '__main__':
    main()