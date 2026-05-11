
DROP DATABASE IF EXISTS virtual_hospital;
CREATE DATABASE virtual_hospital;
USE virtual_hospital;
SHOW DATABASES;


CREATE TABLE Department (
    department_id INT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL,
    location VARCHAR(100)
);

CREATE TABLE Doctor (
    doctor_id INT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    department_id INT NOT NULL,
    FOREIGN KEY (department_id) REFERENCES Department(department_id)
);

CREATE TABLE Patient (
    patient_id INT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    gender VARCHAR(10),
    date_of_birth DATE,
    phone VARCHAR(20),
    address VARCHAR(200),
    medical_history TEXT
);

CREATE TABLE Appointment (
    appointment_id INT PRIMARY KEY,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(30),
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

CREATE TABLE Visit (
    visit_id INT PRIMARY KEY,
    visit_date DATE NOT NULL,
    notes TEXT,
    appointment_id INT UNIQUE,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

CREATE TABLE Diagnosis (
    diagnosis_id INT PRIMARY KEY,
    diagnosis_name VARCHAR(100) NOT NULL,
    description TEXT,
    visit_id INT NOT NULL,
    FOREIGN KEY (visit_id) REFERENCES Visit(visit_id)
);


CREATE TABLE Treatment (
    treatment_id INT PRIMARY KEY,
    treatment_name VARCHAR(100) NOT NULL,
    description TEXT,
    cost DECIMAL(10,2),
    visit_id INT NOT NULL,
    FOREIGN KEY (visit_id) REFERENCES Visit(visit_id)
);

CREATE TABLE Prescription (
    prescription_id INT PRIMARY KEY,
    prescription_date DATE NOT NULL,
    instruction TEXT,
    visit_id INT NOT NULL,
    FOREIGN KEY (visit_id) REFERENCES Visit(visit_id)
);

CREATE TABLE Medication (
    medication_id INT PRIMARY KEY,
    medication_name VARCHAR(100) NOT NULL,
    dosage_form VARCHAR(50),
    price DECIMAL(10,2)
);

CREATE TABLE Prescription_Medication (
    prescription_id INT NOT NULL,
    medication_id INT NOT NULL,
    PRIMARY KEY (prescription_id, medication_id),
    FOREIGN KEY (prescription_id) REFERENCES Prescription(prescription_id),
    FOREIGN KEY (medication_id) REFERENCES Medication(medication_id)
);

CREATE TABLE Laboratory_Test (
    test_id INT PRIMARY KEY,
    test_name VARCHAR(100) NOT NULL,
    test_date DATE NOT NULL,
    status VARCHAR(30),
    visit_id INT NOT NULL,
    FOREIGN KEY (visit_id) REFERENCES Visit(visit_id)
);

CREATE TABLE Lab_Result (
    result_id INT PRIMARY KEY,
    result_details TEXT,
    result_date DATE NOT NULL,
    test_id INT UNIQUE NOT NULL,
    FOREIGN KEY (test_id) REFERENCES Laboratory_Test(test_id)
);

CREATE TABLE Bill (
    bill_id INT PRIMARY KEY,
    bill_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(30),
    patient_id INT NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

CREATE TABLE Payment (
    payment_id INT PRIMARY KEY,
    payment_date DATE NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50),
    bill_id INT NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES Bill(bill_id)
);

CREATE TABLE Room (
    room_id INT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL,
    room_type VARCHAR(50),
    status VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS Admission (
    admission_id INT PRIMARY KEY,
    admission_date DATE NOT NULL,
    discharge_date DATE,
    patient_id INT NOT NULL,
    room_id INT NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id)
);

USE virtual_hospital;

INSERT INTO Department VALUES
(1, 'Cardiology', 'Nablus'),
(2, 'Neurology', 'Ramallah'),
(3, 'Pediatrics', 'Jenin'),
(4, 'Emergency', 'Hebron');

INSERT INTO Doctor VALUES
(1, 'Ahmad Abu Khalil', 'Cardiologist', '0597000001', 'ahmad@hospital.ps', 1),
(2, 'Lina Barghouti', 'Neurologist', '0597000002', 'lina@hospital.ps', 2),
(3, 'Omar Masri', 'Pediatrician', '0597000003', 'omar@hospital.ps', 3),
(4, 'Khaled Nassar', 'Emergency Specialist', '0597000004', 'khaled@hospital.ps', 4);

INSERT INTO Patient VALUES
(1, 'Sara Abu Ahmad', 'Female', '2000-06-12', '0598111111', 'Nablus', 'None'),
(2, 'Yousef Darwish', 'Male', '1995-03-20', '0598222222', 'Ramallah', 'Diabetes'),
(3, 'Maya Qassem', 'Female', '2014-11-05', '0598333333', 'Jenin', 'Asthma'),
(4, 'Ali Hamdan', 'Male', '1988-01-18', '0598444444', 'Hebron', 'Hypertension');

INSERT INTO Appointment VALUES
(1, '2026-04-01', '09:30:00', 'Completed', 1, 1),
(2, '2026-04-01', '11:00:00', 'Completed', 2, 2),
(3, '2026-04-02', '10:00:00', 'Scheduled', 3, 3),
(4, '2026-04-02', '12:30:00', 'Completed', 4, 4);

INSERT INTO Visit VALUES
(1, '2026-04-01', 'Heart checkup', 1, 1, 1),
(2, '2026-04-01', 'Headache diagnosis', 2, 2, 2),
(3, '2026-04-02', 'Emergency case', 4, 4, 4);


INSERT INTO Diagnosis VALUES
(1, 'High Blood Pressure', 'Needs monitoring', 1),
(2, 'Migraine', 'Chronic headache', 2),
(3, 'Chest Pain', 'Emergency condition', 3); 

INSERT INTO Treatment VALUES
(1, 'Blood Pressure Treatment', 'Medication + diet', 120.00, 1),
(2, 'Pain Relief Therapy', 'Medication', 80.00, 2),
(3, 'Emergency Care', 'Immediate treatment', 300.00, 3);

INSERT INTO Medication VALUES
(1, 'Panadol', 'Tablet', 5.00),
(2, 'Aspirin', 'Tablet', 3.00),
(3, 'Insulin', 'Injection', 50.00),
(4, 'Ventolin', 'Inhaler', 25.00);


INSERT INTO Prescription VALUES
(1, '2026-04-01', 'Twice daily after food', 1),
(2, '2026-04-01', 'Once daily', 2),
(3, '2026-04-02', 'Emergency use', 3);

INSERT INTO Prescription_Medication VALUES
(1, 1),
(1, 2),
(2, 2),
(3, 1);

INSERT INTO Laboratory_Test VALUES
(1, 'Blood Test', '2026-04-01', 'Completed', 1),
(2, 'CT Scan', '2026-04-01', 'Completed', 2),
(3, 'ECG', '2026-04-02', 'Completed', 3);

INSERT INTO Bill VALUES
(1, '2026-04-01', 150.00, 'Paid', 1),
(2, '2026-04-01', 100.00, 'Unpaid', 2),
(3, '2026-04-02', 300.00, 'Paid', 4);

INSERT INTO Payment VALUES
(1, '2026-04-01', 150.00, 'Cash', 1),
(2, '2026-04-02', 300.00, 'Card', 3);

INSERT INTO Room VALUES
(1, '101', 'Single', 'Occupied'),
(2, '102', 'Double', 'Available'),
(3, '103', 'ICU', 'Occupied');

INSERT INTO Admission VALUES
(1, '2026-04-02', '2026-04-06', 4, 3);


SHOW TABLES;
SELECT * FROM Patient;


