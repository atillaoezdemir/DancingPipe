from flask import Flask, request, jsonify
import time

app = Flask(__name__)

@app.route('/send_value', methods=['POST'])
def send_value():
    data = request.get_json()
    value = data.get('command')
    timestamp = time.time()
    print(f"Received value: {value} at {timestamp}")
    return jsonify({'status': 'success', 'timestamp': timestamp}), 200

if __name__ == '__main__':
    app.run(debug=True, threaded=True)