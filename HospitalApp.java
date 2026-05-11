import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class HospitalApp extends JFrame {

    private final Color NAVY = new Color(31, 45, 61);
    private final Color BLUE = new Color(52, 152, 219);
    private final Color GREEN = new Color(46, 204, 113);
    private final Color RED = new Color(231, 76, 60);
    private final Color ORANGE = new Color(243, 156, 18);
    private final Color BG = new Color(245, 247, 250);

    private JLabel patientCountLabel;
    private JLabel doctorCountLabel;
    private JLabel appointmentCountLabel;
    private JLabel billCountLabel;

    private DefaultTableModel patientModel;
    private DefaultTableModel doctorModel;
    private DefaultTableModel appointmentModel;
    private DefaultTableModel billModel;
    private DefaultTableModel paymentModel;
    private DefaultTableModel reportModel;

    private JTable patientTable;
    private JTable doctorTable;
    private JTable appointmentTable;
    private JTable billTable;
    private JTable paymentTable;
    private JTable reportTable;

    private JTextField pId, pName, pGender, pDob, pPhone, pAddress, pHistory;
    private JTextField dId, dName, dSpecialty, dPhone, dEmail, dDepartmentId;
    private JTextField aId, aDate, aTime, aStatus, aPatientId, aDoctorId;
    private JTextField bId, bDate, bAmount, bStatus, bPatientId;
    private JTextField payId, payDate, payAmount, payMethod, payBillId;

    private JLabel reportTitle;

    public HospitalApp() {
        setTitle("Virtual Hospital Management System");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabs.addTab("Dashboard", createDashboardPanel());
        tabs.addTab("Patients", createPatientsPanel());
        tabs.addTab("Doctors", createDoctorsPanel());
        tabs.addTab("Appointments", createAppointmentsPanel());
        tabs.addTab("Billing & Payments", createBillingPanel());
        tabs.addTab("Reports", createReportsPanel());

        add(tabs, BorderLayout.CENTER);

        refreshDashboard();
        loadPatients();
        loadDoctors();
        loadAppointments();
        loadBills();
        loadPayments();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Virtual Hospital Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = mainPanel();

        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 20));
        cards.setOpaque(false);

        patientCountLabel = new JLabel("0");
        doctorCountLabel = new JLabel("0");
        appointmentCountLabel = new JLabel("0");
        billCountLabel = new JLabel("0");

        cards.add(createCard("Patients", patientCountLabel, BLUE));
        cards.add(createCard("Doctors", doctorCountLabel, GREEN));
        cards.add(createCard("Appointments", appointmentCountLabel, ORANGE));
        cards.add(createCard("Bills", billCountLabel, RED));

        JButton refresh = createButton("Refresh Dashboard", BLUE);
        refresh.addActionListener(e -> refreshDashboard());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(refresh);

        panel.add(cards, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(NAVY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createPatientsPanel() {
        JPanel panel = mainPanel();

        patientModel = createModel("ID", "Full Name", "Gender", "Date of Birth", "Phone", "Address", "Medical History");
        patientTable = createTable(patientModel);

        pId = field();
        pName = field();
        pGender = field();
        pDob = field();
        pPhone = field();
        pAddress = field();
        pHistory = field();

        JPanel form = new JPanel(new GridLayout(4, 4, 10, 10));
        form.setBackground(Color.WHITE);
        addField(form, "Patient ID", pId);
        addField(form, "Full Name", pName);
        addField(form, "Gender", pGender);
        addField(form, "DOB ex: 3/7/1993", pDob);
        addField(form, "Phone", pPhone);
        addField(form, "Address", pAddress);
        addField(form, "Medical History", pHistory);

        JPanel buttons = buttonPanel();
        JButton add = createButton("Add Patient", GREEN);
        JButton update = createButton("Update Patient", ORANGE);
        JButton delete = createButton("Delete Patient", RED);
        JButton load = createButton("Load Patients", BLUE);
        JButton clear = createButton("Clear", NAVY);

        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(load);
        buttons.add(clear);

        add.addActionListener(e -> addPatient());
        update.addActionListener(e -> updatePatient());
        delete.addActionListener(e -> deletePatient());
        load.addActionListener(e -> loadPatients());
        clear.addActionListener(e -> clearPatientFields());

        patientTable.getSelectionModel().addListSelectionListener(e -> fillPatientFields());

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);
        top.add(box("Patient Information", form), BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDoctorsPanel() {
        JPanel panel = mainPanel();

        doctorModel = createModel("ID", "Full Name", "Specialty", "Phone", "Email", "Department ID");
        doctorTable = createTable(doctorModel);

        dId = field();
        dName = field();
        dSpecialty = field();
        dPhone = field();
        dEmail = field();
        dDepartmentId = field();

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
        form.setBackground(Color.WHITE);
        addField(form, "Doctor ID", dId);
        addField(form, "Full Name", dName);
        addField(form, "Specialty", dSpecialty);
        addField(form, "Phone", dPhone);
        addField(form, "Email", dEmail);
        addField(form, "Department ID", dDepartmentId);

        JPanel buttons = buttonPanel();
        JButton add = createButton("Add Doctor", GREEN);
        JButton update = createButton("Update Doctor", ORANGE);
        JButton delete = createButton("Delete Doctor", RED);
        JButton load = createButton("Load Doctors", BLUE);
        JButton clear = createButton("Clear", NAVY);

        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(load);
        buttons.add(clear);

        add.addActionListener(e -> addDoctor());
        update.addActionListener(e -> updateDoctor());
        delete.addActionListener(e -> deleteDoctor());
        load.addActionListener(e -> loadDoctors());
        clear.addActionListener(e -> clearDoctorFields());

        doctorTable.getSelectionModel().addListSelectionListener(e -> fillDoctorFields());

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);
        top.add(box("Doctor Information", form), BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = mainPanel();

        appointmentModel = createModel("ID", "Date", "Time", "Status", "Patient ID", "Doctor ID");
        appointmentTable = createTable(appointmentModel);

        aId = field();
        aDate = field();
        aTime = field();
        aStatus = field();
        aPatientId = field();
        aDoctorId = field();

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
        form.setBackground(Color.WHITE);
        addField(form, "Appointment ID", aId);
        addField(form, "Date yyyy-mm-dd", aDate);
        addField(form, "Time hh:mm:ss", aTime);
        addField(form, "Status", aStatus);
        addField(form, "Patient ID", aPatientId);
        addField(form, "Doctor ID", aDoctorId);

        JPanel buttons = buttonPanel();
        JButton add = createButton("Add Appointment", GREEN);
        JButton update = createButton("Update Appointment", ORANGE);
        JButton delete = createButton("Delete Appointment", RED);
        JButton load = createButton("Load Appointments", BLUE);
        JButton clear = createButton("Clear", NAVY);

        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(load);
        buttons.add(clear);

        add.addActionListener(e -> addAppointment());
        update.addActionListener(e -> updateAppointment());
        delete.addActionListener(e -> deleteAppointment());
        load.addActionListener(e -> loadAppointments());
        clear.addActionListener(e -> clearAppointmentFields());

        appointmentTable.getSelectionModel().addListSelectionListener(e -> fillAppointmentFields());

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);
        top.add(box("Appointment Information", form), BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBillingPanel() {
        JPanel panel = mainPanel();

        billModel = createModel("Bill ID", "Date", "Total Amount", "Status", "Patient ID", "Patient Name");
        paymentModel = createModel("Payment ID", "Date", "Amount Paid", "Method", "Bill ID");

        billTable = createTable(billModel);
        paymentTable = createTable(paymentModel);

        bId = field();
        bDate = field();
        bAmount = field();
        bStatus = field();
        bPatientId = field();

        payId = field();
        payDate = field();
        payAmount = field();
        payMethod = field();
        payBillId = field();

        JPanel billForm = new JPanel(new GridLayout(3, 4, 10, 10));
        billForm.setBackground(Color.WHITE);
        addField(billForm, "Bill ID", bId);
        addField(billForm, "Date yyyy-mm-dd", bDate);
        addField(billForm, "Total Amount", bAmount);
        addField(billForm, "Status", bStatus);
        addField(billForm, "Patient ID", bPatientId);

        JPanel billButtons = buttonPanel();
        JButton addBill = createButton("Add Bill", GREEN);
        JButton updateBill = createButton("Update Bill", ORANGE);
        JButton deleteBill = createButton("Delete Bill", RED);
        JButton loadBills = createButton("Load Bills", BLUE);

        billButtons.add(addBill);
        billButtons.add(updateBill);
        billButtons.add(deleteBill);
        billButtons.add(loadBills);

        addBill.addActionListener(e -> addBill());
        updateBill.addActionListener(e -> updateBill());
        deleteBill.addActionListener(e -> deleteBill());
        loadBills.addActionListener(e -> loadBills());

        billTable.getSelectionModel().addListSelectionListener(e -> fillBillFields());

        JPanel paymentForm = new JPanel(new GridLayout(3, 4, 10, 10));
        paymentForm.setBackground(Color.WHITE);
        addField(paymentForm, "Payment ID", payId);
        addField(paymentForm, "Date yyyy-mm-dd", payDate);
        addField(paymentForm, "Amount Paid", payAmount);
        addField(paymentForm, "Method", payMethod);
        addField(paymentForm, "Bill ID", payBillId);

        JPanel paymentButtons = buttonPanel();
        JButton addPayment = createButton("Add Payment", GREEN);
        JButton loadPayments = createButton("Load Payments", BLUE);

        paymentButtons.add(addPayment);
        paymentButtons.add(loadPayments);

        addPayment.addActionListener(e -> addPayment());
        loadPayments.addActionListener(e -> loadPayments());

        JPanel topForms = new JPanel(new GridLayout(1, 2, 15, 15));
        topForms.setOpaque(false);

        JPanel billBox = new JPanel(new BorderLayout(8, 8));
        billBox.setOpaque(false);
        billBox.add(box("Bill Information", billForm), BorderLayout.CENTER);
        billBox.add(billButtons, BorderLayout.SOUTH);

        JPanel paymentBox = new JPanel(new BorderLayout(8, 8));
        paymentBox.setOpaque(false);
        paymentBox.add(box("Payment Information", paymentForm), BorderLayout.CENTER);
        paymentBox.add(paymentButtons, BorderLayout.SOUTH);

        topForms.add(billBox);
        topForms.add(paymentBox);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(billTable),
                new JScrollPane(paymentTable));
        split.setResizeWeight(0.5);

        panel.add(topForms, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = mainPanel();

        reportTitle = new JLabel("Choose a report");
        reportTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        reportTitle.setForeground(NAVY);

        reportModel = createModel();
        reportTable = createTable(reportModel);

        JPanel buttons = buttonPanel();

        JButton appointmentReport = createButton("Appointments Report", BLUE);
        JButton unpaidBills = createButton("Unpaid Bills", RED);
        JButton visitHistory = createButton("Visit History", GREEN);
        JButton labResults = createButton("Lab Results", ORANGE);
        JButton admissions = createButton("Admissions", NAVY);
        JButton medications = createButton("Prescription Medications", BLUE);

        buttons.add(appointmentReport);
        buttons.add(unpaidBills);
        buttons.add(visitHistory);
        buttons.add(labResults);
        buttons.add(admissions);
        buttons.add(medications);

        appointmentReport.addActionListener(e -> loadReport(
                "Appointments with Patient and Doctor",
                "SELECT a.appointment_id, a.appointment_date, a.appointment_time, a.status, " +
                        "p.full_name AS patient_name, d.full_name AS doctor_name, d.specialty " +
                        "FROM Appointment a " +
                        "JOIN Patient p ON a.patient_id = p.patient_id " +
                        "JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                        "ORDER BY a.appointment_date, a.appointment_time"
        ));

        unpaidBills.addActionListener(e -> loadReport(
                "Unpaid Bills",
                "SELECT b.bill_id, b.bill_date, b.total_amount, b.status, p.full_name AS patient_name " +
                        "FROM Bill b JOIN Patient p ON b.patient_id = p.patient_id " +
                        "WHERE b.status = 'Unpaid'"
        ));

        visitHistory.addActionListener(e -> loadReport(
                "Patient Visit History",
                "SELECT v.visit_id, v.visit_date, p.full_name AS patient_name, d.full_name AS doctor_name, v.notes " +
                        "FROM Visit v " +
                        "JOIN Patient p ON v.patient_id = p.patient_id " +
                        "JOIN Doctor d ON v.doctor_id = d.doctor_id " +
                        "ORDER BY v.visit_date"
        ));

        labResults.addActionListener(e -> loadReport(
                "Lab Tests and Results",
                "SELECT lt.test_id, lt.test_name, lt.test_date, lt.status, lr.result_details, lr.result_date " +
                        "FROM Laboratory_Test lt " +
                        "LEFT JOIN Lab_Result lr ON lt.test_id = lr.test_id"
        ));

        admissions.addActionListener(e -> loadReport(
                "Admissions and Rooms",
                "SELECT ad.admission_id, p.full_name AS patient_name, r.room_number, r.room_type, " +
                        "ad.admission_date, ad.discharge_date " +
                        "FROM Admission ad " +
                        "JOIN Patient p ON ad.patient_id = p.patient_id " +
                        "JOIN Room r ON ad.room_id = r.room_id"
        ));

        medications.addActionListener(e -> loadReport(
                "Prescription Medications",
                "SELECT pr.prescription_id, p.full_name AS patient_name, d.full_name AS doctor_name, " +
                        "m.medication_name, m.dosage_form, pr.instruction " +
                        "FROM Prescription pr " +
                        "JOIN Visit v ON pr.visit_id = v.visit_id " +
                        "JOIN Patient p ON v.patient_id = p.patient_id " +
                        "JOIN Doctor d ON v.doctor_id = d.doctor_id " +
                        "JOIN Prescription_Medication pm ON pr.prescription_id = pm.prescription_id " +
                        "JOIN Medication m ON pm.medication_id = m.medication_id"
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(reportTitle, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        return panel;
    }

    private void refreshDashboard() {
        patientCountLabel.setText(String.valueOf(countRows("Patient")));
        doctorCountLabel.setText(String.valueOf(countRows("Doctor")));
        appointmentCountLabel.setText(String.valueOf(countRows("Appointment")));
        billCountLabel.setText(String.valueOf(countRows("Bill")));
    }

    private int countRows(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            showError(e);
        }

        return 0;
    }

    private void loadPatients() {
        patientModel.setRowCount(0);

        String sql = "SELECT patient_id, full_name, gender, date_of_birth, phone, address, medical_history FROM Patient ORDER BY patient_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patientModel.addRow(new Object[]{
                        rs.getInt("patient_id"),
                        rs.getString("full_name"),
                        rs.getString("gender"),
                        rs.getDate("date_of_birth"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("medical_history")
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void addPatient() {
        String sql = "INSERT INTO Patient VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(pId.getText().trim()));
            ps.setString(2, pName.getText().trim());
            ps.setString(3, pGender.getText().trim());
            setNullableDate(ps, 4, pDob.getText().trim());
            ps.setString(5, pPhone.getText().trim());
            ps.setString(6, pAddress.getText().trim());
            ps.setString(7, pHistory.getText().trim());

            ps.executeUpdate();
            loadPatients();
            refreshDashboard();
            clearPatientFields();
            success("Patient added successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void updatePatient() {
        String sql = "UPDATE Patient SET full_name=?, gender=?, date_of_birth=?, phone=?, address=?, medical_history=? WHERE patient_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pName.getText().trim());
            ps.setString(2, pGender.getText().trim());
            setNullableDate(ps, 3, pDob.getText().trim());
            ps.setString(4, pPhone.getText().trim());
            ps.setString(5, pAddress.getText().trim());
            ps.setString(6, pHistory.getText().trim());
            ps.setInt(7, Integer.parseInt(pId.getText().trim()));

            ps.executeUpdate();
            loadPatients();
            clearPatientFields();
            success("Patient updated successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deletePatient() {
        if (!confirm("Delete this patient?")) return;

        String sql = "DELETE FROM Patient WHERE patient_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(pId.getText().trim()));
            ps.executeUpdate();
            loadPatients();
            refreshDashboard();
            clearPatientFields();
            success("Patient deleted successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadDoctors() {
        doctorModel.setRowCount(0);

        String sql = "SELECT doctor_id, full_name, specialty, phone, email, department_id FROM Doctor ORDER BY doctor_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                doctorModel.addRow(new Object[]{
                        rs.getInt("doctor_id"),
                        rs.getString("full_name"),
                        rs.getString("specialty"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getInt("department_id")
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void addDoctor() {
        String sql = "INSERT INTO Doctor VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(dId.getText().trim()));
            ps.setString(2, dName.getText().trim());
            ps.setString(3, dSpecialty.getText().trim());
            ps.setString(4, dPhone.getText().trim());
            ps.setString(5, dEmail.getText().trim());
            ps.setInt(6, Integer.parseInt(dDepartmentId.getText().trim()));

            ps.executeUpdate();
            loadDoctors();
            refreshDashboard();
            clearDoctorFields();
            success("Doctor added successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateDoctor() {
        String sql = "UPDATE Doctor SET full_name=?, specialty=?, phone=?, email=?, department_id=? WHERE doctor_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dName.getText().trim());
            ps.setString(2, dSpecialty.getText().trim());
            ps.setString(3, dPhone.getText().trim());
            ps.setString(4, dEmail.getText().trim());
            ps.setInt(5, Integer.parseInt(dDepartmentId.getText().trim()));
            ps.setInt(6, Integer.parseInt(dId.getText().trim()));

            ps.executeUpdate();
            loadDoctors();
            clearDoctorFields();
            success("Doctor updated successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteDoctor() {
        if (!confirm("Delete this doctor?")) return;

        String sql = "DELETE FROM Doctor WHERE doctor_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(dId.getText().trim()));
            ps.executeUpdate();
            loadDoctors();
            refreshDashboard();
            clearDoctorFields();
            success("Doctor deleted successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadAppointments() {
        appointmentModel.setRowCount(0);

        String sql = "SELECT appointment_id, appointment_date, appointment_time, status, patient_id, doctor_id FROM Appointment ORDER BY appointment_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                appointmentModel.addRow(new Object[]{
                        rs.getInt("appointment_id"),
                        rs.getDate("appointment_date"),
                        rs.getTime("appointment_time"),
                        rs.getString("status"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id")
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void addAppointment() {
        String sql = "INSERT INTO Appointment VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(aId.getText().trim()));
            ps.setDate(2, parseDate(aDate.getText().trim()));
            ps.setTime(3, java.sql.Time.valueOf(aTime.getText().trim()));
            ps.setString(4, aStatus.getText().trim());
            ps.setInt(5, Integer.parseInt(aPatientId.getText().trim()));
            ps.setInt(6, Integer.parseInt(aDoctorId.getText().trim()));

            ps.executeUpdate();
            loadAppointments();
            refreshDashboard();
            clearAppointmentFields();
            success("Appointment added successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateAppointment() {
        String sql = "UPDATE Appointment SET appointment_date=?, appointment_time=?, status=?, patient_id=?, doctor_id=? WHERE appointment_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, parseDate(aDate.getText().trim()));
            ps.setTime(2, java.sql.Time.valueOf(aTime.getText().trim()));
            ps.setString(3, aStatus.getText().trim());
            ps.setInt(4, Integer.parseInt(aPatientId.getText().trim()));
            ps.setInt(5, Integer.parseInt(aDoctorId.getText().trim()));
            ps.setInt(6, Integer.parseInt(aId.getText().trim()));

            ps.executeUpdate();
            loadAppointments();
            clearAppointmentFields();
            success("Appointment updated successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteAppointment() {
        if (!confirm("Delete this appointment?")) return;

        String sql = "DELETE FROM Appointment WHERE appointment_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(aId.getText().trim()));
            ps.executeUpdate();
            loadAppointments();
            refreshDashboard();
            clearAppointmentFields();
            success("Appointment deleted successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadBills() {
        billModel.setRowCount(0);

        String sql = "SELECT b.bill_id, b.bill_date, b.total_amount, b.status, b.patient_id, p.full_name " +
                "FROM Bill b JOIN Patient p ON b.patient_id = p.patient_id ORDER BY b.bill_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                billModel.addRow(new Object[]{
                        rs.getInt("bill_id"),
                        rs.getDate("bill_date"),
                        rs.getBigDecimal("total_amount"),
                        rs.getString("status"),
                        rs.getInt("patient_id"),
                        rs.getString("full_name")
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void addBill() {
        String sql = "INSERT INTO Bill VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(bId.getText().trim()));
            ps.setDate(2, parseDate(bDate.getText().trim()));
            ps.setBigDecimal(3, new java.math.BigDecimal(bAmount.getText().trim()));
            ps.setString(4, bStatus.getText().trim());
            ps.setInt(5, Integer.parseInt(bPatientId.getText().trim()));

            ps.executeUpdate();
            loadBills();
            refreshDashboard();
            success("Bill added successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateBill() {
        String sql = "UPDATE Bill SET bill_date=?, total_amount=?, status=?, patient_id=? WHERE bill_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, parseDate(bDate.getText().trim()));
            ps.setBigDecimal(2, new java.math.BigDecimal(bAmount.getText().trim()));
            ps.setString(3, bStatus.getText().trim());
            ps.setInt(4, Integer.parseInt(bPatientId.getText().trim()));
            ps.setInt(5, Integer.parseInt(bId.getText().trim()));

            ps.executeUpdate();
            loadBills();
            success("Bill updated successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteBill() {
        if (!confirm("Delete this bill?")) return;

        String sql = "DELETE FROM Bill WHERE bill_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(bId.getText().trim()));
            ps.executeUpdate();
            loadBills();
            refreshDashboard();
            success("Bill deleted successfully.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadPayments() {
        paymentModel.setRowCount(0);

        String sql = "SELECT payment_id, payment_date, amount_paid, payment_method, bill_id FROM Payment ORDER BY payment_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                paymentModel.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getDate("payment_date"),
                        rs.getBigDecimal("amount_paid"),
                        rs.getString("payment_method"),
                        rs.getInt("bill_id")
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void addPayment() {
        String sql = "INSERT INTO Payment VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int billId = Integer.parseInt(payBillId.getText().trim());

            ps.setInt(1, Integer.parseInt(payId.getText().trim()));
            ps.setDate(2, parseDate(payDate.getText().trim()));
            ps.setBigDecimal(3, new java.math.BigDecimal(payAmount.getText().trim()));
            ps.setString(4, payMethod.getText().trim());
            ps.setInt(5, billId);

            ps.executeUpdate();
            updateBillStatus(conn, billId);

            loadPayments();
            loadBills();
            success("Payment added successfully. Bill status updated.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateBillStatus(Connection conn, int billId) throws SQLException {
        String sql = "UPDATE Bill SET status = " +
                "CASE WHEN (SELECT COALESCE(SUM(amount_paid), 0) FROM Payment WHERE bill_id = ?) >= total_amount " +
                "THEN 'Paid' ELSE 'Unpaid' END " +
                "WHERE bill_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ps.setInt(2, billId);
            ps.executeUpdate();
        }
    }

    private void loadReport(String title, String sql) {
        reportTitle.setText(title);
        reportModel.setRowCount(0);
        reportModel.setColumnCount(0);

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                reportModel.addColumn(meta.getColumnLabel(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];

                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }

                reportModel.addRow(row);
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private JPanel mainPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private DefaultTableModel createModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(214, 234, 248));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230));

        JTableHeader header = table.getTableHeader();
        header.setBackground(NAVY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, center);

        return table;
    }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 7, 5, 7)
        ));
        return f;
    }

    private void addField(JPanel panel, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(NAVY);
        panel.add(l);
        panel.add(field);
    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);
        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        return btn;
    }

    private JPanel box(String title, JPanel content) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                new EmptyBorder(10, 10, 10, 10)
        ));
        box.add(content, BorderLayout.CENTER);
        return box;
    }

    private void setNullableDate(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.DATE);
            return;
        }

        try {
            ps.setDate(index, parseDate(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new SQLException("Invalid date. Use one of these formats: 1993-03-07 or 3/7/1993");
        }
    }

    private java.sql.Date parseDate(String value) {
        DateTimeFormatter[] formats = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("M-d-yyyy"),
                DateTimeFormatter.ofPattern("MM-dd-yyyy")
        };

        for (DateTimeFormatter format : formats) {
            try {
                LocalDate localDate = LocalDate.parse(value, format);
                return java.sql.Date.valueOf(localDate);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Invalid date format");
    }

    private void fillPatientFields() {
        int row = patientTable.getSelectedRow();
        if (row < 0) return;
        row = patientTable.convertRowIndexToModel(row);

        pId.setText(String.valueOf(patientModel.getValueAt(row, 0)));
        pName.setText(String.valueOf(patientModel.getValueAt(row, 1)));
        pGender.setText(String.valueOf(patientModel.getValueAt(row, 2)));
        pDob.setText(String.valueOf(patientModel.getValueAt(row, 3)));
        pPhone.setText(String.valueOf(patientModel.getValueAt(row, 4)));
        pAddress.setText(String.valueOf(patientModel.getValueAt(row, 5)));
        pHistory.setText(String.valueOf(patientModel.getValueAt(row, 6)));
    }

    private void fillDoctorFields() {
        int row = doctorTable.getSelectedRow();
        if (row < 0) return;
        row = doctorTable.convertRowIndexToModel(row);

        dId.setText(String.valueOf(doctorModel.getValueAt(row, 0)));
        dName.setText(String.valueOf(doctorModel.getValueAt(row, 1)));
        dSpecialty.setText(String.valueOf(doctorModel.getValueAt(row, 2)));
        dPhone.setText(String.valueOf(doctorModel.getValueAt(row, 3)));
        dEmail.setText(String.valueOf(doctorModel.getValueAt(row, 4)));
        dDepartmentId.setText(String.valueOf(doctorModel.getValueAt(row, 5)));
    }

    private void fillAppointmentFields() {
        int row = appointmentTable.getSelectedRow();
        if (row < 0) return;
        row = appointmentTable.convertRowIndexToModel(row);

        aId.setText(String.valueOf(appointmentModel.getValueAt(row, 0)));
        aDate.setText(String.valueOf(appointmentModel.getValueAt(row, 1)));
        aTime.setText(String.valueOf(appointmentModel.getValueAt(row, 2)));
        aStatus.setText(String.valueOf(appointmentModel.getValueAt(row, 3)));
        aPatientId.setText(String.valueOf(appointmentModel.getValueAt(row, 4)));
        aDoctorId.setText(String.valueOf(appointmentModel.getValueAt(row, 5)));
    }

    private void fillBillFields() {
        int row = billTable.getSelectedRow();
        if (row < 0) return;
        row = billTable.convertRowIndexToModel(row);

        bId.setText(String.valueOf(billModel.getValueAt(row, 0)));
        bDate.setText(String.valueOf(billModel.getValueAt(row, 1)));
        bAmount.setText(String.valueOf(billModel.getValueAt(row, 2)));
        bStatus.setText(String.valueOf(billModel.getValueAt(row, 3)));
        bPatientId.setText(String.valueOf(billModel.getValueAt(row, 4)));
    }

    private void clearPatientFields() {
        pId.setText("");
        pName.setText("");
        pGender.setText("");
        pDob.setText("");
        pPhone.setText("");
        pAddress.setText("");
        pHistory.setText("");
    }

    private void clearDoctorFields() {
        dId.setText("");
        dName.setText("");
        dSpecialty.setText("");
        dPhone.setText("");
        dEmail.setText("");
        dDepartmentId.setText("");
    }

    private void clearAppointmentFields() {
        aId.setText("");
        aDate.setText("");
        aTime.setText("");
        aStatus.setText("");
        aPatientId.setText("");
        aDoctorId.setText("");
    }

    private boolean confirm(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    private void success(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            HospitalApp app = new HospitalApp();
            app.setVisible(true);
        });
    }
}
