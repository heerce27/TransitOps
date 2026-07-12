package com.transitops.service;

import com.transitops.model.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseService {

    private final List<Expense> expenses;

    public ExpenseService() {

        expenses = new ArrayList<>();

        expenses.add(new Expense(
                "Van-05",
                "Maintenance",
                2500,
                "2026-07-10",
                "Oil Change"
        ));

        expenses.add(new Expense(
                "Truck-01",
                "Toll Charges",
                500,
                "2026-07-11",
                "Highway Toll"
        ));
    }

    public List<Expense> getAllExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public boolean deleteExpense(String vehicle,
                                 String description) {

        for (Expense expense : expenses) {

            if (expense.getVehicle()
                    .equalsIgnoreCase(vehicle)
                    &&
                    expense.getDescription()
                    .equalsIgnoreCase(description)) {

                expenses.remove(expense);
                return true;
            }
        }

        return false;
    }

    public double calculateTotalExpense() {

        double total = 0;

        for (Expense expense : expenses) {
            total += expense.getAmount();
        }

        return total;
    }
}
