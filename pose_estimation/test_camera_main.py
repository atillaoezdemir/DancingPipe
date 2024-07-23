import unittest
from unittest.mock import patch, MagicMock
import numpy as np
import cv2
import requests
import mediapipe as mp
from collections import deque
from camera_main import (calculate_angle, determine_note, combination_result, map_values, display_note_box, send_notes_to_server, process_video)

class TestPoseEstimation(unittest.TestCase):

    def test_combination_result(self):
        result = combination_result(1, 2)
        self.assertEqual(result, 2)
        result = combination_result(0, 0)
        self.assertEqual(result, 0)

    def test_map_values(self):
        manuale, tempo = map_values('3', '2')
        self.assertEqual(manuale, 'ADD')
        self.assertEqual(tempo, 'MINUS')

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