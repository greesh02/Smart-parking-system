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
  



## Flowchart of system
![Romain-flowchart](resources/Smart_Parking_SystemV2.png)

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

![website_mob_2](https://user-images.githubusercontent.com/63254914/155846893-39bd9e97-aaf2-4f90-971f-ae4239446b59.png)

![145020947-693ba4a2-7d6b-4620-a0fc-46d5aa149523](https://user-images.githubusercontent.com/63254914/155846844-b46c9ca2-ffcb-4321-aebf-84957cf8c861.png)

![mobile_website1](https://user-images.githubusercontent.com/63254914/145021395-9cbd8341-47b8-42f1-af28-5f8a757e90a2.png)
![145020963-99ef73c6-62bb-476d-b5e7-9f995055ff43](https://user-images.githubusercontent.com/63254914/155846803-f3462eb9-0093-4eb5-99dd-189adf3f2ddc.png)



