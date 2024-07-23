#!/usr/bin/env python
# -*- coding: utf-8 -*-
import numpy as np
import tensorflow as tf


class PointHistoryClassifier(object):
    """
    A classifier for point history using a TensorFlow Lite model.

    Attributes:
        model_path (str): Path to the TensorFlow Lite model file.
        score_th (float): Threshold for classification score to consider a result valid.
        invalid_value (int): Value to return if the classification score is below the threshold.
        num_threads (int): Number of threads to use for inference.
    """

    def __init__(
        self,
        model_path='model/point_history_classifier/point_history_classifier.tflite',
        score_th=0.5,
        invalid_value=0,
        num_threads=1,
    ):
        """
        Initializes the PointHistoryClassifier with the given parameters.

        Args:
            model_path (str): Path to the TensorFlow Lite model file.
            score_th (float): Classification score threshold.
            invalid_value (int): Value to return for scores below the threshold.
            num_threads (int): Number of threads to use for model inference.
        """
        # Load the TensorFlow Lite model
        self.interpreter = tf.lite.Interpreter(model_path=model_path,
        num_threads=num_threads)

        # Allocate tensors for the model
        self.interpreter.allocate_tensors()
        self.input_details = self.interpreter.get_input_details()
        self.output_details = self.interpreter.get_output_details()

        self.score_th = score_th
        self.invalid_value = invalid_value

    def __call__(
        self,
        point_history,
    ):
        """
        Performs inference on the given point history and returns the classification result.

        Args:
            point_history (list): List of point history data to classify. 

        Returns:
            int: The index of the classification result if it meets the score threshold, 
            otherwise returns the `invalid_value`.
        """
        # Set the input tensor with the point history data
        input_details_tensor_index = self.input_details[0]['index']
        self.interpreter.set_tensor(
            input_details_tensor_index,
            np.array([point_history], dtype=np.float32))
        # Perform inference
        self.interpreter.invoke()

        # Get the output tensor and classify the result
        output_details_tensor_index = self.output_details[0]['index']
        result = self.interpreter.get_tensor(output_details_tensor_index)

        # Determine the index of the maximum score
        result_index = np.argmax(np.squeeze(result))

        # If the score is below the threshold, return the invalid value
        if np.squeeze(result)[result_index] < self.score_th:
            result_index = self.invalid_value

        return result_index
