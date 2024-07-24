import unittest
from unittest.mock import patch, MagicMock
import numpy as np
import cv2
import requests
import mediapipe as mp
from collections import deque
from camera_main import (calculate_angle, determine_note, combination_result, map_values, display_note_box, send_notes_to_server, process_video)


class TestPoseEstimation(unittest.TestCase):

    def test_calculate_angle(self):
        # Test cases: (point1, point2, point3, expected_angle)
        test_cases = [
            ([0, 0], [0, 1], [1, 1],  90),
            ([0, 0], [1, 0], [2, 0], 180),
            ([0, 1], [0, 0], [0, 1],   0),
        ]

        for i, (a, b, c, expected) in enumerate(test_cases):
            result = calculate_angle(a, b, c)
            assert np.isclose(result, expected, atol=1e-2), f"Test case {i+1} failed: expected {expected}, got {result}"
            print(f"Test case {i+1} passed: expected {expected}, got {result}")

    def test_determine_note(self):
        # Test cases: (angle_elbow, angle_shoulder, expected_note)
        test_cases = [
            (140, 150, "0"),
            (140, 30, "1"),
            (100, 200, "404"),
            (50, 100, "4")
        ]

        for i, (elbow, shoulder, expected) in enumerate(test_cases):
            result = determine_note(elbow, shoulder)
            assert result == expected, f"Test case {i+1} failed: expected {expected}, got {result}"
            print(f"Test case {i+1} passed: expected {expected}, got {result}")

    def test_combination_result(self):
        # Test cases: (manuale, tempo, expected_result)
        test_cases = [
            (0, 0, 0),
            (3, 4, 14),
            (1, 5, 5),
            (5, 5, 25),
            (6, 2, None)  # Invalid input test
        ]

        for i, (manuale, tempo, expected) in enumerate(test_cases):
            result = combination_result(manuale, tempo)
            assert result == expected, f"Test case {i+1} failed: expected {expected}, got {result}"
            print(f"Test case {i+1} passed: expected {expected}, got {result}")

    def test_map_values(self):
        # Test cases: (noteManuale, noteTempo, expected_mappedManuale, expected_mappedTempo)
        test_cases = [
            ("3", "3", "ADD", "PLUS"),
            ("0", "0", "ON/OFF", "ON/OFF"),
            ("6", "6", "???", "???"),
            ("4", "5", "MIN", "DEF"),
            ("5", "2", "MAX", "MINUS")
        ]

        for i, (noteManuale, noteTempo, expected_mManuale, expected_mTempo) in enumerate(test_cases):
            mapped_mManuale, mapped_mTempo = map_values(noteManuale, noteTempo)
            assert mapped_mManuale == expected_mManuale and mapped_mTempo == expected_mTempo, f"Test case {i+1} failed: expected ({expected_mManuale}, {expected_mTempo}), got ({mapped_mManuale}, {mapped_mTempo})"
            print(f"Test case {i+1} passed: expected ({expected_mManuale}, {expected_mTempo}), got ({mapped_mManuale}, {mapped_mTempo})")

    def test_display_note_box_2(self):
        # Create a blank image (black background)
        image = np.zeros((100, 300, 3), dtype=np.uint8)

        # Test inputs
        noteManuale = "ADD"
        noteTempo = "PLUS"

        # Call the function
        display_note_box(image, noteManuale, noteTempo)

        # Define the expected color for the text and rectangle
        expected_color = (245, 117, 16)  # Color used for the rectangle

        # Check the rectangle area (top-left corner should be the rectangle color)
        assert (image[10, 10] == expected_color).all(), "Rectangle was not drawn correctly"

        # Check some pixels in the expected text positions for non-zero values (indicating text presence)
        assert (image[20, 150] != [0, 0, 0]).any(), "Manuale text was not drawn correctly"
        assert (image[50, 150] != [0, 0, 0]).any(), "Tempo text was not drawn correctly"

        print("display_note_box test passed")

    def test_display_note_box(self):
        image = np.zeros((100, 300, 3), dtype=np.uint8)
        display_note_box(image, 'ADD', 'MINUS')
        self.assertIsNotNone(image)

    @patch('camera_main.requests.post')
    def test_send_notes_to_server(self, mock_post):
        mock_post.return_value = MagicMock()
        url = 'http://example.com'
        command = 1
        send_notes_to_server(url, command)
        mock_post.assert_called_once_with(url, json={'number': command})

if __name__ == '__main__':
    unittest.main()