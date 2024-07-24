import cv2

cap = cv2.VideoCapture(2)

if not cap.isOpened():
    print("dont work")
    exit()

while True:
    ret, frame = cap.read()
    if not ret:
        print("error")
        break

    cv2.imshow('Frame', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()