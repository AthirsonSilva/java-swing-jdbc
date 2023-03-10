package com.uninove.persistence.view;

import com.uninove.persistence.repository.ClientRepository;
import com.uninove.persistence.entity.Client;
import com.uninove.persistence.entity.Gender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

public class RegistrationApplication extends JFrame implements ActionListener {
    JButton saveButton;
    JLabel nameLabel, phoneLabel, genderLabel, incomeLabel;
    JTextField nameText;
    JFormattedTextField phoneText, incomeText;
    MaskFormatter phoneMask;
    ButtonGroup groupGender;
    JRadioButton maleRadioButton, femaleRadioButton;
    JTable clientsTable;
    DefaultTableModel clientsTableModel;

    public RegistrationApplication() {
        setTitle("Registration");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setBounds(400, 200, 800, 600);

        nameLabel = new JLabel("Name:");
        nameLabel.setBounds(300, 10, 50, 30);
        add(nameLabel);

        phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(10, 10, 50, 30);
        add(phoneLabel);
        try {
            phoneMask = new MaskFormatter("+## (##) # ####-####");
            phoneMask.setPlaceholderCharacter('_');
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        phoneText = new JFormattedTextField(phoneMask);
        phoneText.setBounds(10, 40, 250, 30);
        add(phoneText);

        nameText = new JTextField(40);
        nameText.setBounds(280, 40, 500, 30);
        add(nameText);

        genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(12, 80, 150, 30);
        add(genderLabel);

        maleRadioButton = new JRadioButton(Gender.MALE.name());
        maleRadioButton.setBounds(10, 110, 70, 30);
        femaleRadioButton = new JRadioButton(Gender.FEMALE.name());
        femaleRadioButton.setBounds(80, 110, 100, 30);
        groupGender = new ButtonGroup();
        groupGender.add(maleRadioButton);
        groupGender.add(femaleRadioButton);
        add(maleRadioButton);
        add(femaleRadioButton);

        incomeLabel = new JLabel("Income:");
        incomeLabel.setBounds(180, 80, 150, 30);
        add(incomeLabel);

        incomeText = new JFormattedTextField();
        incomeText.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#0.00"))));
        incomeText.setBounds(180, 110, 425, 30);
        incomeText.setToolTipText("Utilize semicolon to separate the decimal part");
        add(incomeText);

        saveButton = new JButton("Save");
        saveButton.setBounds(660, 110, 120, 30);
        saveButton.addActionListener(this);
        add(saveButton);

        String[] cols = {"ID", "Name", "Phone", "Gender", "Income"};
        clientsTableModel = new DefaultTableModel(cols, 3);
        clientsTable = new JTable(clientsTableModel);
        clientsTableModel.setNumRows(0);
        clientsTable.setBounds(5, 150, 780, 410);
        add(clientsTable);

        JScrollPane scrollPane = new JScrollPane(clientsTable,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(5, 150, 780, 410);
        add(scrollPane);
    }

    public static void main(String[] args) {
        RegistrationApplication registrationApplication = new RegistrationApplication();

        registrationApplication.populateTable();
        registrationApplication.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message;

        if (e.getSource() == saveButton) {
            double income;
            String name, phone;
            Gender gender;

            name = nameText.getText();
            phone = phoneText.getText();

            if (maleRadioButton.isSelected()) {
                gender = Gender.MALE;
            } else if (femaleRadioButton.isSelected()) {
                gender = Gender.FEMALE;
            } else {
                gender = null;
            }

            income = Double.parseDouble(incomeText.getText().replace(",", "."));
            assert gender != null;
            Client client = Client
                    .builder()
                    .name(name)
                    .phone(phone)
                    .gender(gender.name())
                    .income(income)
                    .build();

            ClientRepository clientRepository = new ClientRepository();
            int result = clientRepository.connect();

            if (result == 2) {
                message = "Connection driver not found";
            } else if (result == 3) {
                message = "Connection data base not found";
            } else {
                int clientWasSaved = clientRepository.save(client);
                if (clientWasSaved == 1) {
                    message = "Client's data saved successfully!";
                    cleanInputFields();
                } else if (clientWasSaved == 2) {
                    message = "You is trying to save a client with an ID that already exists";
                } else {
                    message = "Client's data saved successfully!";

                    populateTable();
                }
                clientRepository.disconnect();
            }
            JOptionPane.showMessageDialog(null, message);
        }
    }

    private void populateTable() {
        ClientRepository clientRepository = new ClientRepository();
        int result = clientRepository.connect();

        if (result == 2) {
            JOptionPane.showMessageDialog(null, "Connection driver not found");
        } else if (result == 3) {
            JOptionPane.showMessageDialog(null, "Connection data base not found");
        } else {
            ArrayList<Client> clients = clientRepository.fetchClients();
            clientsTableModel.setNumRows(0);

            clients.forEach(client -> clientsTableModel.addRow(new Object[]{
                    client.getId(),
                    client.getName(),
                    client.getPhone(),
                    client.getGender(),
                    client.getIncome()
            }));

            clientRepository.disconnect();
        }
    }

    private void cleanInputFields() {
        nameText.setText("");
        phoneText.setText("");
        groupGender.clearSelection();
        incomeText.setText("");
    }
}