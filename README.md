# 🚗 Smart Parking System

> Vision-based Smart Parking solution for metropolitan cities using **IoT + AI + Cloud + Microservices + Multimodal LLMs**.

<p align="center">
  <img src="resources/Smart_Parking_SystemV2.png" alt="Smart Parking System Flowchart" width="700"/>
</p>

---

### 🧠 Overview
A **vision-based smart parking system** that detects available parking spots in real time using camera modules and updates availability on a cloud-hosted web portal.  
The system integrates **multimodal Large Language Models (LLMs)** — capable of understanding both **images and text** — to provide **intelligent anomaly detection and reporting** within the smart parking network.

---

### ⚡ Key Highlights
- 🚘 Real-time parking detection using **YOLO (Object Detection)**  
- 🧩 **Decentralized edge processing** on Raspberry Pi and ESP32-CAM  
- ☁️ **Microservices architecture** using Spring Boot, Python & Kafka  
- 🧠 **Multimodal LLM-powered anomaly reporting** (vision + text) using **Spring AI + OpenAI API**  
- 🔗 **Event-driven pipeline** with Kafka for reliable communication  
- 🗄️ **MongoDB & AWS S3** for scalable cloud storage  
- 🌐 **Angular-based web portal** with live map integration  

---

### 📋 Contents
- [Problem Statement](#problem-statement)
- [Solution Proposed](#solution-proposed)
- [Segments Involved](#segments-involved)
- [Technical Stack](#technical-stack)
- [Multimodal LLM Integration](#multimodal-llm-integration)
- [Flowchart of System](#flowchart-of-system)
- [Gallery](#gallery)

---

## 🧩 Problem Statement
- Parking congestion remains a major issue in metropolitan areas, leading to wasted time, fuel, and frustration.  
- Vehicles circling in search of vacant spots increase **traffic congestion** and **pollution**.  
- Manual monitoring and static systems fail to adapt dynamically.  
- Lack of intelligent anomaly detection (e.g., faulty cameras, misidentified vehicles) limits system reliability.  

---

## 💡 Solution Proposed
- **ESP32-CAM modules** capture real-time parking images.  
- **Raspberry Pi servers** run YOLO-based vehicle detection locally.  
- Processed data is sent to a **cloud-hosted microservice ecosystem** via Kafka.  
- A **multimodal LLM (vision + text)** evaluates visual + contextual inputs for **anomaly detection** — identifying issues such as misclassified slots, obstructed cameras, or inconsistent data trends.  
- The **Angular web portal** shows real-time parking availability and system health to users.  

---

## 🧱 Segments Involved
- **Edge Computing:** Raspberry Pi devices for on-site processing and transmission.  
- **IoT Layer:** ESP32-CAM modules for live video/image capture.  
- **Cloud & AI Layer:** Python + Spring Boot microservices with LLM reasoning.  
- **Frontend Layer:** Angular-based dashboard with Google Maps visualization.  

---

## ⚙️ Flowchart of System
<p align="center">
  <img src="resources/Smart_Parking_SystemV2.png" alt="System Flowchart" width="700"/>
</p>

---

## 🧰 Technical Stack

### 🖥️ Processing Server
- Raspberry Pi 4B (Raspbian 32-bit)
- TCP Socket-based image streaming  
- Python (OpenCV, PIL)  
- Auto-run service on boot  

### 📸 ESP32-CAM Setup
- Arduino IDE with ESP32 board  
- TCP Protocol  
- Wi-Fi-based image transfer  

### 🌐 Frontend
- Angular Framework  
- Google Maps API  
- Live occupancy visualization  

### 🧩 Microservices Architecture

| Service | Description | Tech Stack |
|----------|--------------|-------------|
| `camera-service` | YOLO-based vehicle detection and image upload | Python, OpenCV, Kafka, AWS S3 |
| `ai-service` | **Multimodal LLM-powered anomaly detection (vision + text)** | Java, Spring Boot, Spring AI (OpenAI API), Kafka, AWS S3 |
| `datafetch-service` | Fetches real-time data for the frontend | Java, Spring Boot, MongoDB |
| `db-writes-service` | Writes occupancy and anomaly data to DB | Java, Spring Boot, Kafka, MongoDB |

---

## 🤖 Multimodal LLM Integration
### 🔍 What it does
The **AI service** uses a **multimodal Large Language Model (LLM)** capable of processing **both image data and text metadata**.  
This enables **contextual anomaly detection** beyond what traditional object detection models can achieve.

### 🧩 Core Capabilities
- **Visual + Text Reasoning:** Combines YOLO’s image detections with metadata for intelligent system analysis.  
- **Anomaly Reporting:** Detects unusual situations like:
  - Camera misalignment or obstruction  
  - False positives / missed detections  
  - Data drift or inconsistent updates  
- **Human-like Insight Generation:** Generates descriptive text reports (e.g., *“Camera 3 feed likely obstructed — object detection confidence dropped by 45%”*)  
- **Kafka Event Triggers:** Sends anomaly summaries as structured Kafka messages for further analysis.  

### 🧠 Powered by
- **Spring AI**  
- **OpenAI Multimodal API**  
- Integrated into the `ai-service` microservice  

---

## ☁️ Storage
- **AWS S3** → Image storage and anomaly snapshot archiving  
- **MongoDB** → Real-time structured data + anomaly logs  

---

## 🖼️ Gallery

### 🔹 ESP32-CAM Module
<p align="center">
  <img src="https://user-images.githubusercontent.com/63254914/145011057-12974ce8-c2bc-4f2a-84cf-5da65a002274.jpg" width="300"/>
  <img src="https://user-images.githubusercontent.com/63254914/145011091-269a53a6-e352-4ec6-abb7-d028695f939a.jpg" width="300"/>
  <img src="https://user-images.githubusercontent.com/63254914/145020788-d5f42cb8-4607-4d28-a4bd-d8d94790621f.jpeg" width="300"/>
</p>

### 🔹 Object Detection (YOLO)
<p align="center">
  <img src="backend-microservices/camera-service/images/processed-images/2025-11-13_14.29.08_bangalore_6.JPG" width="700"/>
</p>

### 🔹 Website Interface
<p align="center">
  <img src="resources/websiteV2_1.png" width="700"/>
  <img src="resources/websiteV2_2.png" width="700"/>
  <img src="resources/websiteV2_3.png" width="700"/>
  <img src="resources/websiteV2_4.png" width="700"/>
  <img src="resources/websiteV2_5.png" width="700"/>
  <img src="resources/websiteV2_6.png" width="700"/>
</p>

---

## 🚀 Future Improvements
- Integration with mobile app for navigation to nearest parking spot  
- Real-time heatmap of parking demand  
- License plate recognition (LPR) integration  
- **Extended multimodal LLMs for predictive analytics and maintenance scheduling**  

---

## 👩‍💻 Contributors
- [@greesh02](https://github.com/greesh02)
- [@YourTeamMembersHere]

---

## 📄 License
This project is licensed under the **MIT License** — free to use and modify with attribution.

---
