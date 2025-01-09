package main;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CoffeeMachine machine = new CoffeeMachine();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n---Coffee Machine Menu---");
            System.out.println("Available coffees and prices: " + machine.getDrinksPrices());
            System.out.println("Coins in the machine: " + machine.getCoins());
            System.out.println("Ingredients: " + machine.getIngredients());
            System.out.println("1. Add coins");
            System.out.println("2. Buy coffee");
            System.out.println("3. Refill ingredients");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.print("Enter coin type (5, 10, 20, 50, 1lv, 2lv): ");
                        String coinType = scanner.next();
                        System.out.print("Enter quantity of coins: ");
                        int quantity = scanner.nextInt();
                        machine.addCoins(coinType, quantity);
                        break;
                    case 2:
                        System.out.print("Enter type of coffee (espresso, latte, mocha): ");
                        String type = scanner.next();
                        if (machine.makeCoffee(type)) {
                            System.out.println("\nEnjoy your " + type + "!");
                            int remainingCoins = machine.getCoins();
                            if (remainingCoins > 0) {
                                Map<String, Integer> change = machine.getChange(remainingCoins);
                                System.out.println("Returning change: ");
                                change.forEach((coin, count) -> System.out.println(count + " x " + coin));
                            }
                        }
                        break;
                    case 3:
                        System.out.print("Enter amount of coffee to refill: ");
                        int coffee = scanner.nextInt();
                        System.out.print("Enter amount of milk to refill: ");
                        int milk = scanner.nextInt();
                        System.out.print("Enter amount of chocolate to refill: ");
                        int chocolate = scanner.nextInt();
                        machine.refillIngredients(coffee, milk, chocolate);
                        break;
                    case 4:
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (InputMismatchException e) {
                System.out.println("\nInvalid input. Please enter a number [1-4].");
                scanner.next(); // Clear the invalid input
            }
        }
    }
}