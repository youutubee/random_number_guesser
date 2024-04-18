import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class trying {
    private static final String EXPENSES_FILENAME = "Shrijan_expenses.json";
    private static final String INCOMES_FILENAME = "Shrijan_incomes.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final String[] FRIENDS = {"Keshav", "Vaibhav", "Praveen", "Ayush", "Harsh"};
    private static final String[] FRIENDS_EXPENSE_FILES = {"Keshav_expenses.json", "Vaibhav_expenses.json",
            "Praveen_expenses.json", "Ayush_expenses.json", "Harsh_expenses.json"};
    private static final String[] FRIENDS_INCOME_FILES = {"Keshav_incomes.json", "Vaibhav_incomes.json",
            "Praveen_incomes.json", "Ayush_incomes.json", "Harsh_incomes.json"};
    private static final Gson[] FRIENDS_EXPENSE_GSON = new Gson[FRIENDS.length];
    private static final Gson[] FRIENDS_INCOME_GSON = new Gson[FRIENDS.length];

    public static void main(String[] args) {
        for (int i = 0; i < FRIENDS.length; i++) {
            FRIENDS_EXPENSE_GSON[i] = new GsonBuilder().setPrettyPrinting().create();
            FRIENDS_INCOME_GSON[i] = new GsonBuilder().setPrettyPrinting().create();
        }
        List<Expense> expenses = loadExpenses();
        List<Income> incomes = loadIncomes();
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Add an Expense");
        System.out.println("2. Add an Income");
        System.out.println("3. Split Bill");
        System.out.println("4. Display Expenses");
        System.out.println("5. Display Incomes");
        System.out.println("6. Exit");

        while (true) {
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addExpense(scanner, expenses);
                    break;
                case 2:
                    addIncome(scanner, incomes);
                    break;
                case 3:
                    splitBill(scanner, expenses);
                    break;
                case 4:
                    displayExpenses(expenses);
                    break;
                case 5:
                    displayIncomes(incomes);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter again.");
            }
        }
    }

    private static void addExpense(Scanner scanner, List<Expense> expenses) {
        System.out.print("Enter the amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.println("Select a category:");
        System.out.println("1. Home");
        System.out.println("2. Bills");
        System.out.println("3. Health");
        System.out.println("4. Education");
        System.out.println("5. Transportation");
        System.out.println("6. Food");

        System.out.print("Enter the category number: ");
        int categoryChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        String category;
        switch (categoryChoice) {
            case 1:
                category = "Home";
                break;
            case 2:
                category = "Bills";
                break;
            case 3:
                category = "Health";
                break;
            case 4:
                category = "Education";
                break;
            case 5:
                category = "Transportation";
                break;
            case 6:
                category = "Food";
                break;
            default:
                System.out.println("Invalid category choice");
                return;
        }
        Expense newExpense = new Expense(amount, category);
        expenses.add(newExpense);
        saveExpenses(expenses);
        System.out.println("Expense added successfully.");
    }

    private static void addIncome(Scanner scanner, List<Income> incomes) {
        System.out.print("Enter the amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.println("Select an income category:");
        System.out.println("1. Refund");
        System.out.println("2. Grant");
        System.out.println("3. Salary");
        System.out.println("4. Sale");
        System.out.println("5. Award");
        System.out.println("6. Coupon");

        System.out.print("Enter the category number: ");
        int categoryChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        String category;
        switch (categoryChoice) {
            case 1:
                category = "Refund";
                break;
            case 2:
                category = "Grant";
                break;
            case 3:
                category = "Salary";
                break;
            case 4:
                category = "Sale";
                break;
            case 5:
                category = "Award";
                break;
            case 6:
                category = "Coupon";
                break;
            default:
                System.out.println("Invalid category choice");
                return;
        }
        Income newIncome = new Income(amount, category);
        incomes.add(newIncome);
        saveIncomes(incomes);
        System.out.println("Income added successfully.");
    }

    private static void splitBill(Scanner scanner, List<Expense> expenses) {
        System.out.print("Enter the amount to split: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        double splitAmount = amount / (FRIENDS.length + 1); // Including yourself
        for (int i = 0; i < FRIENDS.length; i++) {
            List<Expense> friendExpenses = loadFriendExpenses(i);
            friendExpenses.add(new Expense(splitAmount, "Shared"));
            saveFriendExpenses(i, friendExpenses);
            System.out.println("Split amount added to " + FRIENDS[i] + "'s expenses.");
        }
        expenses.add(new Expense(splitAmount, "Shared"));
        saveExpenses(expenses);
        System.out.println("Split amount added to your expenses.");
    }

    private static void saveExpenses(List<Expense> expenses) {
        try (FileWriter writer = new FileWriter(EXPENSES_FILENAME)) {
            gson.toJson(expenses, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Expense> loadExpenses() {
        try (FileReader reader = new FileReader(EXPENSES_FILENAME)) {
            Expense[] expensesArray = gson.fromJson(reader, Expense[].class);
            if (expensesArray != null) {
                return new ArrayList<>(List.of(expensesArray));
            }
        } catch (IOException e) {
            // If file does not exist or cannot be read, return an empty list
        }
        return new ArrayList<>();
    }

    private static void saveFriendExpenses(int index, List<Expense> expenses) {
        try (FileWriter writer = new FileWriter(FRIENDS_EXPENSE_FILES[index])) {
            FRIENDS_EXPENSE_GSON[index].toJson(expenses, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Expense> loadFriendExpenses(int index) {
        try (FileReader reader = new FileReader(FRIENDS_EXPENSE_FILES[index])) {
            Expense[] expensesArray = FRIENDS_EXPENSE_GSON[index].fromJson(reader, Expense[].class);
            if (expensesArray != null) {
                return new ArrayList<>(List.of(expensesArray));
            }
        } catch (IOException e) {
            // If file does not exist or cannot be read, return an empty list
        }
        return new ArrayList<>();
    }

    private static void saveIncomes(List<Income> incomes) {
        try (FileWriter writer = new FileWriter(INCOMES_FILENAME)) {
            gson.toJson(incomes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Income> loadIncomes() {
        try (FileReader reader = new FileReader(INCOMES_FILENAME)) {
            Income[] incomesArray = gson.fromJson(reader, Income[].class);
            if (incomesArray != null) {
                return new ArrayList<>(List.of(incomesArray));
            }
        } catch (IOException e) {
            // If file does not exist or cannot be read, return an empty list
        }
        return new ArrayList<>();
    }

    private static void displayExpenses(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }
        System.out.println("Expenses:");
        for (Expense expense : expenses) {
            System.out.println(expense);
        }
    }

    private static void displayIncomes(List<Income> incomes) {
        if (incomes.isEmpty()) {
            System.out.println("No incomes recorded.");
            return;
        }
        System.out.println("Incomes:");
        for (Income income : incomes) {
            System.out.println(income);
        }
    }
}