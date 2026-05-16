
USE virtual_hospital;


SELECT * FROM Patient;
SELECT * FROM Doctor;
SELECT * FROM Appointment;
SELECT * FROM Bill;
SELECT * FROM Payment;

-- Dashboard counters
SELECT COUNT(*) AS total_patients FROM Patient;
SELECT COUNT(*) AS total_doctors FROM Doctor;
SELECT COUNT(*) AS total_appointments FROM Appointment;
SELECT COUNT(*) AS total_bills FROM Bill;




SELECT 
    a.appointment_id,
    a.appointment_date,
    a.appointment_time,
    a.status,
    p.full_name AS patient_name,
    d.full_name AS doctor_name,
    d.specialty
FROM Appointment a
JOIN Patient p ON a.patient_id = p.patient_id
JOIN Doctor d ON a.doctor_id = d.doctor_id
ORDER BY a.appointment_date, a.appointment_time;


SELECT 
    v.visit_id,
    v.visit_date,
    p.full_name AS patient_name,
    d.full_name AS doctor_name,
    v.notes
FROM Visit v
JOIN Patient p ON v.patient_id = p.patient_id
JOIN Doctor d ON v.doctor_id = d.doctor_id
ORDER BY v.visit_date;


SELECT 
    b.bill_id,
    b.bill_date,
    b.total_amount,
    b.status,
    p.full_name AS patient_name
FROM Bill b
JOIN Patient p ON b.patient_id = p.patient_id
WHERE b.status = 'Unpaid';


SELECT 
    lt.test_id,
    lt.test_name,
    lt.test_date,
    lt.status,
    lr.result_details,
    lr.result_date
FROM Laboratory_Test lt
LEFT JOIN Lab_Result lr ON lt.test_id = lr.test_id;


SELECT 
    ad.admission_id,
    p.full_name AS patient_name,
    r.room_number,
    r.room_type,
    ad.admission_date,
    ad.discharge_date
FROM Admission ad
JOIN Patient p ON ad.patient_id = p.patient_id
JOIN Room r ON ad.room_id = r.room_id;


SELECT 
    pr.prescription_id,
    p.full_name AS patient_name,
    d.full_name AS doctor_name,
    m.medication_name,
    m.dosage_form,
    pr.instruction
FROM Prescription pr
JOIN Visit v ON pr.visit_id = v.visit_id
JOIN Patient p ON v.patient_id = p.patient_id
JOIN Doctor d ON v.doctor_id = d.doctor_id
JOIN Prescription_Medication pm ON pr.prescription_id = pm.prescription_id
JOIN Medication m ON pm.medication_id = m.medication_id;


DELETE FROM Payment WHERE payment_id = 1001;
DELETE FROM Bill WHERE bill_id = 1001;
DELETE FROM Appointment WHERE appointment_id = 1001;
DELETE FROM Doctor WHERE doctor_id = 1001;
DELETE FROM Patient WHERE patient_id = 1001;



INSERT INTO Patient
(patient_id, full_name, gender, date_of_birth, phone, address, medical_history)
VALUES
(1001, 'Test Patient', 'Male', '1999-03-07', '0599000001', 'Nablus', 'None');


INSERT INTO Doctor
(doctor_id, full_name, specialty, phone, email, department_id)
VALUES
(1001, 'Test Doctor', 'General Medicine', '0599000002', 'test.doctor@hospital.ps', 1);


INSERT INTO Appointment
(appointment_id, appointment_date, appointment_time, status, patient_id, doctor_id)
VALUES
(1001, '2026-05-10', '09:30:00', 'Scheduled', 1001, 1001);


INSERT INTO Bill
(bill_id, bill_date, total_amount, status, patient_id)
VALUES
(1001, '2026-05-10', 200.00, 'Unpaid', 1001);


INSERT INTO Payment
(payment_id, payment_date, amount_paid, payment_method, bill_id)
VALUES
(1001, '2026-05-10', 200.00, 'Cash', 1001);


UPDATE Bill
SET status =
    CASE 
        WHEN (SELECT COALESCE(SUM(amount_paid), 0)
              FROM Payment
              WHERE bill_id = 1001) >= total_amount
        THEN 'Paid'
        ELSE 'Unpaid'
    END
WHERE bill_id = 1001;


SELECT * FROM Patient WHERE patient_id = 1001;
SELECT * FROM Doctor WHERE doctor_id = 1001;
SELECT * FROM Appointment WHERE appointment_id = 1001;
SELECT * FROM Bill WHERE bill_id = 1001;
SELECT * FROM Payment WHERE payment_id = 1001;



UPDATE Patient
SET 
    full_name = 'Updated Test Patient',
    gender = 'Male',
    date_of_birth = '1999-03-07',
    phone = '0599111111',
    address = 'Ramallah',
    medical_history = 'Updated medical history'
WHERE patient_id = 1001;


UPDATE Doctor
SET
    full_name = 'Updated Test Doctor',
    specialty = 'Emergency Specialist',
    phone = '0599222222',
    email = 'updated.doctor@hospital.ps',
    department_id = 4
WHERE doctor_id = 1001;


UPDATE Appointment
SET
    appointment_date = '2026-05-11',
    appointment_time = '11:00:00',
    status = 'Completed',
    patient_id = 1001,
    doctor_id = 1001
WHERE appointment_id = 1001;


UPDATE Bill
SET
    bill_date = '2026-05-11',
    total_amount = 250.00,
    status = 'Unpaid',
    patient_id = 1001
WHERE bill_id = 1001;


SELECT * FROM Patient WHERE patient_id = 1001;
SELECT * FROM Doctor WHERE doctor_id = 1001;
SELECT * FROM Appointment WHERE appointment_id = 1001;
SELECT * FROM Bill WHERE bill_id = 1001;


DELETE FROM Payment WHERE payment_id = 1001;
DELETE FROM Bill WHERE bill_id = 1001;
DELETE FROM Appointment WHERE appointment_id = 1001;
DELETE FROM Doctor WHERE doctor_id = 1001;
DELETE FROM Patient WHERE patient_id = 1001;


SELECT * FROM Patient WHERE patient_id = 1001;
SELECT * FROM Doctor WHERE doctor_id = 1001;
SELECT * FROM Appointment WHERE appointment_id = 1001;
SELECT * FROM Bill WHERE bill_id = 1001;
SELECT * FROM Payment WHERE payment_id = 1001;

