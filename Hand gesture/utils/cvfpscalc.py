from collections import deque
import cv2 as cv


class CvFpsCalc(object):
    """
    A class to calculate frames per second (FPS) using OpenCV.

    Attributes:
        buffer_len (int): The length of the buffer to store time differences.
    """

    def __init__(self, buffer_len=1):
        """
        Initializes the CvFpsCalc object.

        Args:
            buffer_len (int): The length of the buffer to store time differences.
        """
        self._start_tick = cv.getTickCount()
        self._freq = 1000.0 / cv.getTickFrequency()
        self._difftimes = deque(maxlen=buffer_len)

    def get(self):
        """
        Calculates and returns the current FPS.

        Returns:
            float: The current FPS value, rounded to two decimal places.
        """
        current_tick = cv.getTickCount()
        different_time = (current_tick - self._start_tick) * self._freq
        self._start_tick = current_tick

        # Append the time difference to the buffer
        self._difftimes.append(different_time)

        # Calculate FPS
        fps = 1000.0 / (sum(self._difftimes) / len(self._difftimes))
        fps_rounded = round(fps, 2)

        return fps_rounded
