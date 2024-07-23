#!/usr/bin/env python
# -*- coding: utf-8 -*-
import numpy as np
import tensorflow as tf


class KeyPointClassifier(object):
    """
    A classifier for keypoints using a TensorFlow Lite model.

    Attributes:
        model_path (str): Path to the TensorFlow Lite model file.
        num_threads (int): Number of threads to use for inference.
    """

    def __init__(
        self,
        model_path='model/keypoint_classifier/keypoint_classifier.tflite',
        num_threads=1,
    ):
        """
        Initializes the KeyPointClassifier with the given parameters.

        Args:
            model_path (str): Path to the TensorFlow Lite model file.
            num_threads (int): Number of threads to use for model inference.
        """
        # Load the TensorFlow Lite model
        self.interpreter = tf.lite.Interpreter(model_path=model_path,
        num_threads=num_threads)

        # Allocate tensors for the model
        self.interpreter.allocate_tensors()
        self.input_details = self.interpreter.get_input_details()
        self.output_details = self.interpreter.get_output_details()

    def __call__(
        self,
        landmark_list,
    ):
        """
        Performs inference on the given landmark list and returns the classification result.

        Args:
            landmark_list (list): List of landmark data to classify.

        Returns:
            int: The index of the classification result.
        """
        # Set the input tensor with the landmark data
        input_details_tensor_index = self.input_details[0]['index']
        self.interpreter.set_tensor(
            input_details_tensor_index,
            np.array([landmark_list], dtype=np.float32))
        # Perform inference
        self.interpreter.invoke()

        # Get the output tensor and classify the result
        output_details_tensor_index = self.output_details[0]['index']
        result = self.interpreter.get_tensor(output_details_tensor_index)

        # Determine the index of the maximum score
        result_index = np.argmax(np.squeeze(result))

        return result_index
