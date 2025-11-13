# Smart Parking System
Vision based Smart Parking system 
Technologies Used -> ESP32-CAMs, Raspberry Pi, microservices architecture (SpringBoot and Python), kafka,  angular, AWS S3 bucket. 


### Contents
- [Problem statement](#Problem-statement)
- [Solution proposed](#Solution-proposed)
- [Segments involved](#Segments-involved)
- [Technical stack](#Technical-stack)
- [Technical explanation](#Technical-explanation)
- [Flowchart of system](#Flowchart-of-system)
- [Gallery](#Gallery)

## Problem statement
- Parking has always been an issue in metropolitan cities.  Let it be a shop or a public place, parking wastes time, fuel and sometimes breaks sweat to get it done.
- Drivers tend to circle around the place searching for parking spots. This increases traffic congestions, wastes fuel and increases pollution. It escalates to a risky scale during festival times.
- Unable to find proper parking spots, some vehicles are parked in narrow spaces causing traffic jams.

## Solution proposed
- The project proposes to install low cost camera modules in multiple parking lots across the city, which streams live image to the corresponding remote server 
- The remote server processes the data from the camera module and decides on the number of vacant parking spaces available in the parking lot
- The remote server updates the number of vacant parking slots and number of filled parking slots in a cloud database
- The number of vacant parking slots and their location is displayed in a web application accessible to general public and free to use.
- The database is updated continuously , ensuring a pristine user experience

## Segments involved
- Decentralized server (Raspberry Pi) for image processing, computation and network management.
- ESP32-CAM hardware setup for wireless image transmission and reception by server.
- Object detection and updation of database
- Cloud Deployed and completely scalable Website and Cloud Database management.

## Flowchart of system
![Romain-flowchart](resources/Smart_Parking_SystemV2.png)

## Technical stack
- Processing server
  - Raspberry Pi 4B 
  - Raspbian 32-bit OS
  - SSH access - PuTTy, FileZilla
  - Auto run on boot-up
  - TCP Sockets
  - PIL Library
- ESP32-CAM setup
  - Arduino IDE
  - ESP32 board 
  - socket library
  - TCP Protocol
- UI
  - Angular
  - Google Maps API services
- Microservices
  - camera-service (Object detection with YOLO) -> Python, kafka, OpenCV, AWS S3, PyTest
  - ai-service -> Java, SpringBoot, kafka, AWS S3, Spring AI(OpenAI LLM API integrated)
  - datafetch-service -> Java, SpringBoot, kafka, AWS S3, MongoDB
  - db-writes-service -> Java, SpringBoot, kafka, AWS S3, MongoDB
- Storage
  - AWS S3 -> storing images
  - Mongo DB -> storing realtime data
  





## Gallery
### ESPCAM module
![cam_1_cropped](https://user-images.githubusercontent.com/63254914/145011057-12974ce8-c2bc-4f2a-84cf-5da65a002274.jpg)
![cam2_cropped](https://user-images.githubusercontent.com/63254914/145011091-269a53a6-e352-4ec6-abb7-d028695f939a.jpg)
![cam3_cropped](https://user-images.githubusercontent.com/63254914/145020788-d5f42cb8-4607-4d28-a4bd-d8d94790621f.jpeg)

### Object detection
![occ1_crop](https://user-images.githubusercontent.com/63254914/145021342-a76fa8c2-bea5-4c9d-b4ab-78943e9291c7.png)
![occ3_crop](https://user-images.githubusercontent.com/63254914/145022138-48e320c8-b803-41e9-8c4d-e3bca6af1675.png)
![occ2_crop](https://user-images.githubusercontent.com/63254914/145021366-a41d5e67-8bc0-436d-9eba-92378afdb756.png)

### Website

![website](resources/websiteV2_1.png)
![website](resources/websiteV2_2.png)
![website](resources/websiteV2_3.png)
![website](resources/websiteV2_4.png)
![website](resources/websiteV2_5.png)
![website](resources/websiteV2_6.png)

