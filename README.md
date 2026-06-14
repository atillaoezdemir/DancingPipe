# Dancing Pipes 🎹

> **This project is no longer actively maintained.**
> It was developed as an academic project at [THWS](https://www.thws.de/) during Summer Semester 2024 and is archived here for reference.

A multimodal interaction system that enables non-expert users to play a pipe organ using intuitive body gestures and hand movements captured via camera. The system translates recognized gestures into MIDI signals and sends them to a real pipe organ in real-time.

---

## Architecture

![System Architecture](Documentation/Diagrams/Processing%20command%20from%20camera.jpg)

The system consists of four main components that communicate over a network:

| Component | Description | Tech |
|---|---|---|
| **Pose Estimation** | Captures body & hand gestures via camera | Python, MediaPipe, OpenCV |
| **Server** | Central hub — routes commands between components | Java 17, Spring Boot, Gradle |
| **Frontend** | Web UI for system control & visualization | Angular, TypeScript |
| **Organ Client (MIDI)** | Sends MIDI signals to the pipe organ | Java 8+, Maven, javax.sound.midi |

---

## Project Structure

```
DancingPipes/
├── pose_estimation/       # Camera client — body & hand gesture recognition
│   ├── camera_main.py     # Main entry point
│   └── README.md
├── server/organServer/    # Spring Boot server
│   └── README.md
├── frontend/OrganUI/      # Angular web frontend
│   └── README.md
├── MIDI/OrganPlayer/      # Organ client with MIDI sequencer
│   └── README.md
├── Hand gesture/          # Hand gesture classification models & training
├── API/                   # API client scripts (Python)
├── Documentation/         # Project documentation & diagrams
│   ├── Diagrams/
│   ├── Documentation.pdf
│   └── Documentation.docx
└── README.md
```

---

## Getting Started

Each component has its own setup guide:

1. [Pose Estimation (Camera Client)](./pose_estimation/README.md)
2. [Server](./server/organServer/README.md)
3. [Frontend](./frontend/OrganUI/README.md)
4. [Organ Client (MIDI Sequencer)](./MIDI/OrganPlayer/README.md)

---

## Supervisors

- **Prof. Dr. Frank-Michael Schleif** — THWS
- **Prof. Dr. Hannes Ritschel** — Würzburg University of Music