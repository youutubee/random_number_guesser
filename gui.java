import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class gui extends JFrame {
    private static final String EXPENSES_DIRECTORY = "Expenses/";
    private static final String EXPENSES_FILENAME = EXPENSES_DIRECTORY + "Shrijan_expenses.json";
    private static final String INCOMES_FILENAME = "Shrijan_incomes.json";
    private static final String USER_INFO_FILENAME = "user_information.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final String[] FRIENDS = {"Keshav", "Vaibhav", "Praveen", "Ayush", "Harsh"};
    private static final String[] FRIENDS_EXPENSE_FILES = {"Keshav_expenses.json", "Vaibhav_expenses.json",
            "Praveen_expenses.json", "Ayush_expenses.json", "Harsh_expenses.json"};
    private static final String[] FRIENDS_INCOME_FILES = {"Keshav_incomes.json", "Vaibhav_incomes.json",
            "Praveen_incomes.json", "Ayush_incomes.json", "Harsh_incomes.json"};
    private static final Gson[] FRIENDS_EXPENSE_GSON = new Gson[FRIENDS.length];
    private static final Gson[] FRIENDS_INCOME_GSON = new Gson[FRIENDS.length];

    private List<Expense> expenses;
    private List<Income> incomes;

    private JButton addExpenseButton;
    private JButton addIncomeButton;
    private JButton splitBillButton;
    private JButton displayExpensesButton;
    private JButton displayIncomesButton;
    private JButton exitButton;

    public gui() {
        super("Expense Tracker");
        initializeData();
        initializeComponents();
        setupLayout();
        setupActions();
    }

    private void initializeData() {
        // Create Expenses directory if it doesn't exist
        File expensesDir = new File(EXPENSES_DIRECTORY);
        if (!expensesDir.exists()) {
            expensesDir.mkdir();
        }

        for (int i = 0; i < FRIENDS.length; i++) {
            FRIENDS_EXPENSE_GSON[i] = new GsonBuilder().setPrettyPrinting().create();
            FRIENDS_INCOME_GSON[i] = new GsonBuilder().setPrettyPrinting().create();
        }
        expenses = loadExpenses();
        incomes = loadIncomes();
    }

    private void initializeComponents() {
        addExpenseButton = new JButton("Add an Expense");
        addIncomeButton = new JButton("Add an Income");
        splitBillButton = new JButton("Split Bill");
        displayExpensesButton = new JButton("Display Expenses");
        displayIncomesButton = new JButton("Display Incomes");
        exitButton = new JButton("Exit");
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(6, 1, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(addExpenseButton);
        mainPanel.add(addIncomeButton);
        mainPanel.add(splitBillButton);
        mainPanel.add(displayExpensesButton);
        mainPanel.add(displayIncomesButton);
        mainPanel.add(exitButton);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupActions() {
        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });

        addIncomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addIncome();
            }
        });

        splitBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitBill();
            }
        });

        displayExpensesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayExpenses();
            }
        });

        displayIncomesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayIncomes();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void addExpense() {
        double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter the amount:"));

        String[] categories = {"Home", "Bills", "Health", "Education", "Transportation", "Food"};
        String category = (String) JOptionPane.showInputDialog(null, "Select a category:", "Expense Category",
                JOptionPane.PLAIN_MESSAGE, null, categories, categories[0]);

        Expense newExpense = new Expense(amount, category);
        expenses.add(newExpense);
        saveExpenses(expenses);
        JOptionPane.showMessageDialog(null, "Expense added successfully.");
    }

    private void addIncome() {
        double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter the amount:"));

        String[] incomeCategories = {"Refund", "Grant", "Salary", "Sale", "Award", "Coupon"};
        String category = (String) JOptionPane.showInputDialog(null, "Select an income category:", "Income Category",
                JOptionPane.PLAIN_MESSAGE, null, incomeCategories, incomeCategories[0]);

        Income newIncome = new Income(amount, category);
        incomes.add(newIncome);
        saveIncomes(incomes);
        JOptionPane.showMessageDialog(null, "Income added successfully.");
    }

    private void splitBill() {
        double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter the amount to split:"));
        String selectedFriendsInput = JOptionPane.showInputDialog(getFriendsPanel());
        if (selectedFriendsInput == null) return; // User canceled operation

        List<String> selectedFriends = parseSelectedFriends(selectedFriendsInput);

        double splitAmount = amount / (selectedFriends.size() + 1); // Including yourself
        for (String friend : selectedFriends) {
            int index = Arrays.asList(FRIENDS).indexOf(friend);
            if (index != -1) {
                List<Expense> friendExpenses = loadFriendExpenses(index);
                friendExpenses.add(new Expense(splitAmount, "Shared"));
                saveFriendExpenses(index, friendExpenses);
            }
        }
        expenses.add(new Expense(splitAmount, "Shared"));
        saveExpenses(expenses);
        JOptionPane.showMessageDialog(null, "Split amount added to expenses.");
    }

    private JPanel getFriendsPanel() {
        JPanel friendsPanel = new JPanel();
        friendsPanel.setLayout(new GridLayout(FRIENDS.length, 1));
        for (int i = 0; i < FRIENDS.length; i++) {
            JLabel friendLabel = new JLabel((i + 1) + ". " + FRIENDS[i]);
            friendsPanel.add(friendLabel);
        }
        return friendsPanel;
    }

    private List<String> parseSelectedFriends(String input) {
        List<String> selectedFriends = new ArrayList<>();
        String[] numbers = input.split(",");
        for (String number : numbers) {
            String trimmedNumber = number.trim();
            int index = Integer.parseInt(trimmedNumber) - 1;
            if (index >= 0 && index < FRIENDS.length) {
                selectedFriends.add(FRIENDS[index]);
            }
        }
        return selectedFriends;
    }

    private void displayExpenses() {
        StringBuilder stringBuilder = new StringBuilder("Expenses:\n");
        if (!expenses.isEmpty()) {
            for (Expense expense : expenses) {
                stringBuilder.append(expense).append("\n");
            }
        } else {
            stringBuilder.append("No expenses recorded.");
        }
        JOptionPane.showMessageDialog(null, stringBuilder.toString());
    }

    private void displayIncomes() {
        StringBuilder stringBuilder = new StringBuilder("Incomes:\n");
        if (!incomes.isEmpty()) {
            for (Income income : incomes) {
                stringBuilder.append(income).append("\n");
            }
        } else {
            stringBuilder.append("No incomes recorded.");
        }
        JOptionPane.showMessageDialog(null, stringBuilder.toString());
    }

    private void saveExpenses(List<Expense> expenses) {
        try (FileWriter writer = new FileWriter(EXPENSES_FILENAME)) {
            gson.toJson(expenses, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Expense> loadExpenses() {
        try (FileReader reader = new FileReader(EXPENSES_FILENAME)) {
            Expense[] expensesArray = gson.fromJson(reader, Expense[].class);
            if (expensesArray != null) {
                return new ArrayList<>(Arrays.asList(expensesArray));
            }
        } catch (IOException e) {
            // If file does not exist or cannot be read, return an empty list
        }
        return new ArrayList<>();
    }

    private void saveFriendExpenses(int index, List<Expense> expenses) {
        String friendExpensesFilename = EXPENSES_DIRECTORY + FRIENDS_EXPENSE_FILES[index];
        try (FileWriter writer = new FileWriter(friendExpensesFilename)) {
            FRIENDS_EXPENSE_GSON[index].toJson(expenses, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Expense> loadFriendExpenses(int index) {
        String friendExpensesFilename = EXPENSES_DIRECTORY + FRIENDS_EXPENSE_FILES[index];
        try (FileReader reader = new FileReader(friendExpensesFilename)) {
            Expense[] expensesArray = FRIENDS_EXPENSE_GSON[index].fromJson(reader, Expense[].class);
            if (expensesArray != null) {
                return new ArrayList<>(Arrays.asList(expensesArray));
            }
        } catch (IOException e) {
            // If file does not exist or cannot be read, return an empty list
        }
        return new ArrayList<>();
    }

    private void saveIncomes(List<Income> incomes) {
        try (FileWriter writer = new FileWriter(INCOMES_FILENAME)) {
            gson.toJson(incomes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Income> loadIncomes() {
        try (FileReader reader = new FileReader(INCOMES_FILENAME)) {
            Income[] incomesArray = gson.fromJson(reader, Income[].class);
            if (incomesArray != null) {
                return new ArrayList<>(Arrays.asList(incomesArray));
            }
        } catch (IOException e) {
            // If file does not exist or cannot be read, return an empty list
        }
        return new ArrayList<>();
    }

    private void saveUserInfo(UserInfo userInfo) {
        try (FileWriter writer = new FileWriter(USER_INFO_FILENAME)) {
            gson.toJson(userInfo, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UserInfo loadUserInfo() {
        try (FileReader reader = new FileReader(USER_INFO_FILENAME)) {
            return gson.fromJson(reader, UserInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new gui().setVisible(true);
            }
        });
    }
}
