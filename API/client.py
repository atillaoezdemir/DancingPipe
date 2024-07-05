import requests
import time

url = 'http://127.0.0.1:5000/send_value'

while True:
    value = 42
    response = requests.post(url, json={'value': value})
    print(response.json())
    time.sleep(0.250)
