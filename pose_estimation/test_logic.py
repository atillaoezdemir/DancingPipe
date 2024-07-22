import unittest
from unittest.mock import patch
import requests
import nbformat
from nbconvert import PythonExporter

# Load Jupyter Notebook and extract the functions
with open("pose_estimation/Pose_estimation_main.ipynb") as f:
    nb = nbformat.read(f, as_version=4)

python_exporter = PythonExporter()
(source_code, _) = python_exporter.from_notebook_node(nb)

exec(source_code)

class TestCombinationResult(unittest.TestCase):
    def test_combination_result(self):
        # Test with both manuale and tempo as 0
        self.assertEqual(combination_result(0, 0), 0)
        
        # Test with valid ranges for manuale and tempo
        self.assertEqual(combination_result(1, 1), 1)
        self.assertEqual(combination_result(2, 2), 7)
        self.assertEqual(combination_result(5, 5), 25)
        
        # Test with out of range values
        self.assertIsNone(combination_result(6, 3))
        self.assertIsNone(combination_result(3, 6))
        self.assertIsNone(combination_result(-1, 3))
        self.assertIsNone(combination_result(3, -1))

class TestSendNotesToServer(unittest.TestCase):
    @patch('requests.post')
    def test_send_notes_to_server_success(self, mock_post):
        url = 'http://10.10.35.129:8080/producer'
        command = 0
        
        # Set up the mock to return a successful response
        mock_post.return_value.status_code = 200
        
        send_notes_to_server(url, command)
        
        # Assert that requests.post was called once with the correct parameters
        mock_post.assert_called_once_with(url, json={'number': command})
    
    @patch('requests.post')
    def test_send_notes_to_server_failure(self, mock_post):
        url = 'http://10.10.35.129:8080/producer'
        command = 0
        
        # Set up the mock to raise an exception
        mock_post.side_effect = requests.exceptions.RequestException
        
        send_notes_to_server(url, command)
        
        # Assert that requests.post was called once with the correct parameters
        mock_post.assert_called_once_with(url, json={'number': command})

class TestMapValues(unittest.TestCase):
    def test_map_values(self):
        # Test mappings
        self.assertEqual(map_values('3', '3'), ('ADD', 'PLUS'))
        self.assertEqual(map_values('2', '2'), ('DEL', 'MINUS'))
        self.assertEqual(map_values('5', '5'), ('MAX', 'DEF'))
        self.assertEqual(map_values('4', '5'), ('MIN', 'DEF'))
        
        # Test ON/OFF condition
        self.assertEqual(map_values('0', '0'), ('ON/OFF', 'ON/OFF'))
        
        # Test unmapped values
        self.assertEqual(map_values('1', '1'), ('???', '???'))

if __name__ == '__main__':
    unittest.main()