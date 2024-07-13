from flask import Flask, request, jsonify
import time

app = Flask(__name__)

@app.route('/producer', methods=['POST'])
def send_value():
    data = request.get_json()
    value = data.get('number')
    timestamp = time.time()
    print(f"Received value: {value} at {timestamp}")
    return jsonify({'status': 'success', 'timestamp': timestamp}), 200

if __name__ == '__main__':
    app.run(host='localhost', port=8080, debug=True, threaded=True)