package main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CoffeeMachine {
    private int coffee;
    private int milk;
    private int chocolate;
    private int coins;
    private Map<String, Integer> prices;

    public CoffeeMachine() {
        loadIngredients();
        loadPrices();
        this.coins = 0;
    }

    public String getDrinksPrices() {
        return "Espresso: " + prices.get("espresso") + ", Latte: " + prices.get("latte") + ", Mocha: " + prices.get("mocha");
    }

    public int getCoins() {
        return this.coins;
    }

    public String getIngredients() {
        return "Coffee: " + this.coffee + ", Milk: " + this.milk + ", Chocolate: " + this.chocolate;
    }

    private void loadIngredients() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("src/resources/ingredients.json")) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            this.coffee = ((Long) jsonObject.get("coffee")).intValue();
            this.milk = ((Long) jsonObject.get("milk")).intValue();
            this.chocolate = ((Long) jsonObject.get("chocolate")).intValue();
        } catch (IOException | ParseException e) {
            System.out.println("Error loading ingredients: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadPrices() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("src/resources/prices.json")) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            this.prices = new HashMap<>();
            for (Object key : jsonObject.keySet()) {
                String drink = (String) key;
                Long priceLong = (Long) jsonObject.get(drink);
                this.prices.put(drink, priceLong.intValue());
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error loading prices: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void saveIngredients() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("coffee", this.coffee);
        jsonObject.put("milk", this.milk);
        jsonObject.put("chocolate", this.chocolate);

        try (FileWriter file = new FileWriter("src/resources/ingredients.json")) {
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.out.println("Error saving ingredients: " + e.getMessage());
        }
    }

    public void addCoins(String coinType, int quantity) {
        int coinValue = 0;
        switch (coinType) {
            case "5":
                coinValue = 5;
                break;
            case "10":
                coinValue = 10;
                break;
            case "20":
                coinValue = 20;
                break;
            case "50":
                coinValue = 50;
                break;
            case "1lv":
                coinValue = 100;
                break;
            case "2lv":
                coinValue = 200;
                break;
            default:
                System.out.println("Invalid coin type. Please insert 5, 10, 20, 50, 1lv, or 2lv coins.");
                return;
        }
        this.coins += coinValue * quantity;
    }

    public boolean makeCoffee(String type) {
        int cost = 0;
        switch (type.toLowerCase()) {
            case "espresso":
                cost = prices.get("espresso");
                if (coffee >= 50 && coins >= cost) {
                    coffee -= 50;
                    coins -= cost;
                    saveIngredients();
                    return true;
                } else {
                    printInsufficientIngredients(coffee < 50, false, false, coins < cost);
                }
                break;
            case "latte":
                cost = prices.get("latte");
                if (coffee >= 30 && milk >= 20 && coins >= cost) {
                    coffee -= 30;
                    milk -= 20;
                    coins -= cost;
                    saveIngredients();
                    return true;
                } else {
                    printInsufficientIngredients(coffee < 30, milk < 20, false, coins < cost);
                }
                break;
            case "mocha":
                cost = prices.get("mocha");
                if (coffee >= 30 && milk >= 20 && chocolate >= 10 && coins >= cost) {
                    coffee -= 30;
                    milk -= 20;
                    chocolate -= 10;
                    coins -= cost;
                    saveIngredients();
                    return true;
                } else {
                    printInsufficientIngredients(coffee < 30, milk < 20, chocolate < 10, coins < cost);
                }
                break;
            default:
                System.out.println("Unknown coffee type.");
                return false;
        }
        return false;
    }
    
    private void printInsufficientIngredients(boolean coffee, boolean milk, boolean chocolate, boolean coins) {
        if (coffee) {
            System.out.println("Not enough coffee.");
        }
        if (milk) {
            System.out.println("Not enough milk.");
        }
        if (chocolate) {
            System.out.println("Not enough chocolate.");
        }
        if (coins) {
            System.out.println("Not enough coins.");
        }
    }

    public void refillIngredients(int coffee, int milk, int chocolate) {
        this.coffee += coffee;
        this.milk += milk;
        this.chocolate += chocolate;
        saveIngredients();
    }

    public Map<String, Integer> getChange(int amount) {
        int[] coinTypes = {200, 100, 50, 20, 10, 5};
        Map<String, Integer> change = new HashMap<>();
        for (int coin : coinTypes) {
            int count = 0;
            while (amount >= coin) {
                amount -= coin;
                count++;
            }
            if (count > 0) {
                String coinType = coin == 200 ? "2lv" : coin == 100 ? "1lv" : String.valueOf(coin);
                change.put(coinType, count);
            }
        }
        this.coins = 0; // Reset coins after giving change
        return change;
    }
}